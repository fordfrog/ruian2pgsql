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

import java.nio.file.Path;

/**
 * Runtime configuration.
 *
 * @author fordfrog
 */
public class Config {

    /**
     * Path to input directory.
     */
    private static Path inputDirPath;
    /**
     * Database connection URL.
     */
    private static String dbConnectionUrl;
    /**
     * Flag determining whether tables should be (re)created.
     */
    private static boolean createTables;
    /**
     * Flag determining whether all transaction ids should be reset so that all
     * records would be updated.
     */
    private static boolean resetTransactionIds;
    /**
     * Flag determining whether invalid GML should be ignored.
     */
    private static boolean ignoreInvalidGML;
    /**
     * Path to file where runtime messages should be logged.
     */
    private static Path logFilePath;

    /**
     * Getter for {@link #inputDirPath}.
     *
     * @return {@link #inputDirPath}
     */
    public static Path getInputDirPath() {
        return inputDirPath;
    }

    /**
     * Setter for {@link #inputDirPath}.
     *
     * @param inputDirPath {@link #inputDirPath}
     */
    public static void setInputDirPath(final Path inputDirPath) {
        Config.inputDirPath = inputDirPath;
    }

    /**
     * Getter for {@link #dbConnectionUrl}.
     *
     * @return {@link #dbConnectionUrl}
     */
    public static String getDbConnectionUrl() {
        return dbConnectionUrl;
    }

    /**
     * Setter for {@link #dbConnectionUrl}.
     *
     * @param dbConnectionUrl {@link #dbConnectionUrl}
     */
    public static void setDbConnectionUrl(final String dbConnectionUrl) {
        Config.dbConnectionUrl = dbConnectionUrl;
    }

    /**
     * Getter for {@link #createTables}.
     *
     * @return {@link #createTables}
     */
    public static boolean isCreateTables() {
        return createTables;
    }

    /**
     * Setter for {@link #createTables}.
     *
     * @param createTables {@link #createTables}
     */
    public static void setCreateTables(final boolean createTables) {
        Config.createTables = createTables;
    }

    /**
     * Getter for {@link #resetTransactionIds}.
     *
     * @return {@link #resetTransactionIds}
     */
    public static boolean isResetTransactionIds() {
        return resetTransactionIds;
    }

    /**
     * Setter for {@link #resetTransactionIds}.
     *
     * @param resetTransactionIds {@link #resetTransactionIds}
     */
    public static void setResetTransactionIds(
            final boolean resetTransactionIds) {
        Config.resetTransactionIds = resetTransactionIds;
    }

    /**
     * Getter for {@link #logFilePath}.
     *
     * @return {@link #logFilePath}
     */
    public static Path getLogFilePath() {
        return logFilePath;
    }

    /**
     * Setter for {@link #logFilePath}.
     *
     * @param logFilePath {@link #logFilePath}
     */
    public static void setLogFilePath(final Path logFilePath) {
        Config.logFilePath = logFilePath;
    }

    /**
     * Getter for {@link #ignoreInvalidGML}.
     *
     * @return {@link #ignoreInvalidGML}
     */
    public static boolean isIgnoreInvalidGML() {
        return ignoreInvalidGML;
    }

    /**
     * Setter for {@link #ignoreInvalidGML}.
     *
     * @param ignoreInvalidGML {@link #ignoreInvalidGML}
     */
    public static void setIgnoreInvalidGML(boolean ignoreInvalidGML) {
        Config.ignoreInvalidGML = ignoreInvalidGML;
    }

    /**
     * Creates new instance of Config.
     */
    private Config() {
    }
}
