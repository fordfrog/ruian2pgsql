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
package com.fordfrog.ruian2pgsql;

import com.fordfrog.ruian2pgsql.convertors.MainConvertor;
import com.fordfrog.ruian2pgsql.utils.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Objects;
import javax.xml.stream.XMLStreamException;

/**
 * Main class.
 *
 * @author fordfrog
 */
public class App {

    /**
     * Main method.
     *
     * @param args application arguments.
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws SQLException       Thrown if problem occurred while communicating
     *                            with database.
     */
    @SuppressWarnings("AssignmentToForLoopParameter")
    public static void main(final String[] args) throws XMLStreamException,
            SQLException {
        if (args.length == 0) {
            printUsage();

            return;
        }

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--convert-to-ewkt":
                    Config.setConvertToEWKT(true);
                    break;
                case "--create-tables":
                    Config.setCreateTables(true);
                    break;
                case "--db-connection-url":
                    i++;
                    Config.setDbConnectionUrl(args[i]);
                    break;
                case "--dry-run":
                    Config.setDryRun(true);
                    break;
                case "--debug":
                    Config.setDebug(true);
                    break;
                case "--ignore-invalid-gml":
                    Config.setIgnoreInvalidGML(true);
                    break;
                case "--input-dir":
                    i++;
                    Config.setInputDirPath(Paths.get(args[i]));
                    break;
                case "--log-file":
                    i++;
                    Config.setLogFilePath(Paths.get(args[i]));
                    break;
                case "--no-gis":
                    Config.setNoGis(true);
                    break;
                case "--reset-transaction-ids":
                    Config.setResetTransactionIds(true);
                    break;
                case "--dest-srid":
                    i++;
                    Config.setDestinationSrid(Integer.valueOf(args[i]));
                    break;
                default:
                    throw new RuntimeException(
                            "Unsupported command line switch: " + args[i]);
            }
        }

        Objects.requireNonNull(Config.getDbConnectionUrl(),
                "--db-connection-url must be set (example: "
                + "jdbc:postgresql://localhost/ruian?user=ruian"
                + "&password=p4ssw0rd)");
        Objects.requireNonNull(
                Config.getInputDirPath(), "--input-dir must be set");

        try (@SuppressWarnings("UseOfSystemOutOrSystemErr")
                final Writer logFile = new OutputStreamWriter(
                        Config.getLogFilePath() == null ? System.out
                        : Files.newOutputStream(Config.getLogFilePath()),
                        "UTF-8")) {
            Log.setLogWriter(logFile);
            MainConvertor.convert();
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to create log writer", ex);
        }
    }

    /**
     * Prints application usage information.
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private static void printUsage() {
        try (final InputStream inputStream =
                        App.class.getResourceAsStream("/usage.txt");
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"))) {
            String line = reader.readLine();

            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (final IOException ex) {
            throw new RuntimeException(
                    "Failed to output usage information", ex);
        }
    }
}
