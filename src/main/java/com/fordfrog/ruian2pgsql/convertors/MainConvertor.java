/**
 * Copyright 2012 Miroslav Šulc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.fordfrog.ruian2pgsql.convertors;

import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.Utils;
import com.fordfrog.ruian2pgsql.utils.XMLStringUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Converts RÚIAN data into PostgreSQL database.
 *
 * @author fordfrog
 */
public class MainConvertor {

    /**
     * Creates new instance of MainConvertor.
     */
    private MainConvertor() {
    }

    /**
     * Converts all files with .xml.gz and .xml extensions from specified
     * directory into database.
     *
     * @param inputDirPath        path to directory that contains input files
     * @param dbConnectionUrl     database connection URL
     * @param createTables        whether data tables should be (re)created
     * @param resetTransactionIds reset transaction ids in all tables before the
     *                            import
     * @param logFile             log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws SQLException       Thrown if problem occurred while communicating
     *                            with database.
     */
    public static void convert(final Path inputDirPath,
            final String dbConnectionUrl, final boolean createTables,
            final boolean resetTransactionIds, final Writer logFile)
            throws XMLStreamException, SQLException {
        final long startTimestamp = System.currentTimeMillis();

        try (final Connection con =
                        DriverManager.getConnection(dbConnectionUrl)) {
            con.setAutoCommit(false);

            if (createTables) {
                Utils.printToLog(logFile, "Initializing database schema...");
                runSQLFromResource(con, "/sql/schema.sql");
            }

            Utils.printToLog(logFile, "Recreating RÚIAN statistics view...");
            runSQLFromResource(con, "/sql/ruian_stats.sql");

            if (resetTransactionIds) {
                Utils.printToLog(logFile, "Resetting transaction ids...");
                runSQLFromResource(con, "/sql/reset_transaction_ids.sql");
            }

            final boolean multipointBug = Utils.checkMultipointBug(con);

            if (multipointBug) {
                Utils.printToLog(logFile, "Installed version of Postgis is "
                        + "affected by multipoint bug "
                        + "http://trac.osgeo.org/postgis/ticket/1928, enabling "
                        + "workaround...");
                XMLStringUtil.setMultipointBugWorkaround(true);
            }

            for (final Path file : getInputFiles(inputDirPath)) {
                processFile(con, file, logFile);
            }

            con.commit();

            Utils.printToLog(logFile, "Total duration: "
                    + (System.currentTimeMillis() - startTimestamp) + " ms");
        }
    }

    /**
     * Runs SQL statements from specified resource.
     *
     * @param con          database connection
     * @param resourceName name of the resource from which to read the SQL
     *                     statements
     */
    private static void runSQLFromResource(final Connection con,
            final String resourceName) {
        final StringBuilder sbSQL = new StringBuilder(10_240);

        try (final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                        MainConvertor.class.getResourceAsStream(
                        resourceName), "UTF-8"));
                final Statement stm = con.createStatement()) {
            String line = reader.readLine();

            while (line != null) {
                sbSQL.append(line);

                if (line.endsWith(";")) {
                    stm.execute(sbSQL.toString());
                    sbSQL.setLength(0);
                } else {
                    sbSQL.append('\n');
                }

                line = reader.readLine();
            }
        } catch (final SQLException ex) {
            throw new RuntimeException(
                    "Statement failed: " + sbSQL.toString(), ex);
        } catch (final IOException ex) {
            throw new RuntimeException(
                    "Failed to read SQL statements from resource", ex);
        }
    }

    /**
     * Reads input files from input directory and sorts them in ascending order.
     *
     * @param inputDirPath input directory
     *
     * @return sorted list of input files
     */
    private static List<Path> getInputFiles(final Path inputDirPath) {
        final List<Path> result = new ArrayList<>(10);

        try (final DirectoryStream<Path> files =
                        Files.newDirectoryStream(inputDirPath)) {
            final Iterator<Path> filesIterator = files.iterator();

            while (filesIterator.hasNext()) {
                final Path file = filesIterator.next();

                if (!Files.isDirectory(file)) {
                    result.add(file);
                }
            }
        } catch (final IOException ex) {
            throw new RuntimeException(
                    "Failed to read content of input directory", ex);
        }

        Collections.sort(result, new Comparator<Path>() {
            @Override
            public int compare(final Path o1, final Path o2) {
                return o1.getFileName().toString().compareTo(
                        o2.getFileName().toString());
            }
        });

        return result;
    }

    /**
     * Processes single input file.
     *
     * @param con     database connection
     * @param file    file path
     * @param logFile log file writer
     *
     * @throws UnsupportedEncodingException Thrown if UTF-8 encoding is not
     *                                      supported.
     * @throws XMLStreamException           Thrown if problem occurred while
     *                                      reading XML stream.
     * @throws SQLException                 Thrown if problem occurred while
     *                                      communicating with database.
     */
    private static void processFile(final Connection con, final Path file,
            final Writer logFile) throws XMLStreamException, SQLException {
        final String fileName = file.toString();

        if (fileName.endsWith(".xml.gz") || fileName.endsWith(".xml")) {
            final long startTimestamp = System.currentTimeMillis();

            Utils.printToLog(logFile, "Processing file " + file);
            Utils.flushLog(logFile);

            try (final InputStream inputStream = Files.newInputStream(file)) {
                if (fileName.endsWith(".gz")) {
                    readInputStream(
                            con, new GZIPInputStream(inputStream), logFile);
                } else {
                    readInputStream(con, inputStream, logFile);
                }
            } catch (final IOException ex) {
                throw new RuntimeException("Failed to read input file", ex);
            }

            Utils.printToLog(logFile, "File processed in "
                    + (System.currentTimeMillis() - startTimestamp) + " ms");
            Utils.flushLog(logFile);
        } else {
            Utils.printToLog(logFile,
                    "Unsupported file extension, ignoring file " + file);
        }
    }

    /**
     * Reads input stream and processes the XML content.
     *
     * @param con         database connection
     * @param inputStream input stream containing XML data
     * @param logFile     log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws SQLException       Thrown if problem occurred while communicating
     *                            with database.
     */
    private static void readInputStream(final Connection con,
            final InputStream inputStream, final Writer logFile)
            throws XMLStreamException, SQLException {
        final XMLInputFactory xMLInputFactory = XMLInputFactory.newInstance();

        final XMLStreamReader reader;

        try {
            reader = xMLInputFactory.createXMLStreamReader(
                    new InputStreamReader(inputStream, "UTF-8"));
        } catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException("UTF-8 encoding is not supported", ex);
        }

        while (reader.hasNext()) {
            final int event = reader.next();

            if (event == XMLStreamReader.START_ELEMENT) {
                processElement(reader, con, logFile);
            }
        }
    }

    /**
     * Processes elements and its sub-elements.
     *
     * @param reader  XML stream reader
     * @param con     database connection
     * @param logFile log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws SQLException       Thrown if problem occurred while communicating
     *                            with database.
     */
    private static void processElement(final XMLStreamReader reader,
            final Connection con, final Writer logFile)
            throws XMLStreamException, SQLException {
        switch (reader.getNamespaceURI()) {
            case Namespaces.VYMENNY_FORMAT_TYPY:
                switch (reader.getLocalName()) {
                    case "VymennyFormat":
                        new ExchangeFormatConvertor(con).
                                convert(reader, logFile);
                        break;
                    default:
                        Utils.processUnsupported(reader, logFile);
                }
                break;
            default:
                Utils.processUnsupported(reader, logFile);
        }
    }
}
