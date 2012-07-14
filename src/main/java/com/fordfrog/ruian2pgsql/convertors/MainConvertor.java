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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
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
     * @param inputDirPath    path to directory that contains input files
     * @param dbConnectionUrl database connection URL
     * @param createTables    whether data tables should be (re)created
     * @param logFilePath     log file path
     *
     * @throws IOException        Thrown if I/O problem occurred
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws SQLException       Thrown if problem occurred while communicating
     *                            with database.
     */
    public static void convert(final Path inputDirPath,
            final String dbConnectionUrl, final boolean createTables,
            final Path logFilePath)
            throws IOException, XMLStreamException, SQLException {
        try (final Connection con =
                        DriverManager.getConnection(dbConnectionUrl);
                final BufferedWriter logFile = Files.newBufferedWriter(
                        logFilePath, Charset.forName("UTF-8"))) {
            con.setAutoCommit(false);

            if (createTables) {
                initDatabase(con);
            }

            for (final Path file : getInputFiles(inputDirPath)) {
                processFile(con, file, logFile);
            }

            con.commit();
        }
    }

    /**
     * Initializes database tables.
     *
     * @param con database connection
     *
     * @throws IOException Thrown if problem occurred while reading SQL schema
     *                     file.
     */
    private static void initDatabase(final Connection con) throws IOException {
        final StringBuilder sbSQL = new StringBuilder(10240);

        try (final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                        MainConvertor.class.getResourceAsStream(
                        "/sql/schema.sql")));
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
            throw new RuntimeException("Statement failed: " + sbSQL.toString());
        }
    }

    /**
     * Reads input files from input directory and sorts them in ascending order.
     *
     * @param inputDirPath input directory
     *
     * @return sorted list of input files
     *
     * @throws IOException Thrown if problem occurred while reading files from
     *                     input directory.
     */
    private static List<Path> getInputFiles(final Path inputDirPath)
            throws IOException {
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
     * @throws IOException                  Thrown if I/O problem occurred.
     * @throws UnsupportedEncodingException Thrown if UTF-8 encoding is not
     *                                      supported.
     * @throws XMLStreamException           Thrown if problem occurred while
     *                                      reading XML stream.
     * @throws SQLException                 Thrown if problem occurred while
     *                                      communicating with database.
     */
    private static void processFile(final Connection con, final Path file,
            final Writer logFile) throws IOException,
            UnsupportedEncodingException, XMLStreamException, SQLException {
        final String fileName = file.toString();

        if (fileName.endsWith(".xml.gz")) {
            Utils.printToLog(logFile, "Processing file " + file);

            try (final GZIPInputStream gZIPInputStream =
                            new GZIPInputStream(Files.newInputStream(file))) {
                readInputStream(con, gZIPInputStream, logFile);
            }
        } else if (fileName.endsWith(".xml")) {
            Utils.printToLog(logFile, "Processing file " + file);

            try (final InputStream inputStream = Files.newInputStream(file)) {
                readInputStream(con, inputStream, logFile);
            }
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
     * @throws IOException                  Thrown if I/O problem occurred.
     * @throws UnsupportedEncodingException Thrown if UTF-8 encoding is not
     *                                      supported.
     * @throws XMLStreamException           Thrown if problem occurred while
     *                                      reading XML stream.
     * @throws SQLException                 Thrown if problem occurred while
     *                                      communicating with database.
     */
    private static void readInputStream(final Connection con,
            final InputStream inputStream, final Writer logFile)
            throws UnsupportedEncodingException, XMLStreamException,
            SQLException, IOException {
        final XMLInputFactory xMLInputFactory = XMLInputFactory.newInstance();

        final XMLStreamReader reader =
                xMLInputFactory.createXMLStreamReader(
                new InputStreamReader(inputStream, "UTF-8"));

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
     * @throws IOException        Thrown if I/O problem occurred.
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws SQLException       Thrown if problem occurred while communicating
     *                            with database.
     */
    private static void processElement(final XMLStreamReader reader,
            final Connection con, final Writer logFile)
            throws XMLStreamException, SQLException, IOException {
        switch (reader.getNamespaceURI()) {
            case Namespaces.VYMENNY_FORMAT_TYPY:
                switch (reader.getLocalName()) {
                    case "VymennyFormat":
                        new ExchangeFormatConvertor().
                                convert(reader, con, logFile);
                        break;
                    default:
                        Utils.printWarningIgnoringElement(logFile, reader);
                }
                break;
            default:
                Utils.printWarningIgnoringElement(logFile, reader);
        }
    }
}
