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
     * Whether geometries should be converted to EWKT before they are stored in
     * database.
     */
    private static boolean convertToEWKT;
    /**
     * Whether debug information should be output.
     */
    private static boolean debug;
    /**
     * If set to true, input files are processed but no data is stored in
     * database.
     */
    private static boolean dryRun;
    /**
     * If set to true, no geometries are parsed.
     */
    private static boolean noGis;
    /**
     * Path to file where runtime messages should be logged.
     */
    private static Path logFilePath;
    /**
     * SRID of coordinate system to which souldb be geometries transformed.
     */
    private static Integer destinationSrid;
    /**
     * Precision used for linear approximation of curved objects.
     */
    private static double linearPrecision = 0.01;

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
     * Getter for {@link #destinationSrid}.
     *
     * @return {@link #destinationSrid}
     */
    public static Integer getDestinationSrid() {
        return destinationSrid;
    }

    /**
     * Setter for {@link #destinationSrid}.
     *
     * @param destinationSrid {@link #destinationSrid}
     */
    public static void setDestinationSrid(final Integer destinationSrid) {
        Config.destinationSrid = destinationSrid;
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
    public static void setIgnoreInvalidGML(final boolean ignoreInvalidGML) {
        Config.ignoreInvalidGML = ignoreInvalidGML;
    }

    /**
     * Getter for {@link #convertToEWKT}.
     *
     * @return {@link #convertToEWKT}
     */
    public static boolean isConvertToEWKT() {
        return convertToEWKT;
    }

    /**
     * Setter for {@link #convertToEWKT}.
     *
     * @param convertToEWKT {@link #convertToEWKT}
     */
    public static void setConvertToEWKT(final boolean convertToEWKT) {
        Config.convertToEWKT = convertToEWKT;
    }

    /**
     * Getter for {@link #debug}.
     *
     * @return {@link #debug}
     */
    public static boolean isDebug() {
        return debug;
    }

    /**
     * Setter for {@link #debug}.
     *
     * @param debug {@link #debug}
     */
    public static void setDebug(final boolean debug) {
        Config.debug = debug;
    }

    /**
     * Getter for {@link #dryRun}.
     *
     * @return {@link #dryRun}
     */
    public static boolean isDryRun() {
        return dryRun;
    }

    /**
     * Setter for {@link #dryRun}.
     *
     * @param dryRun {@link #dryRun}
     */
    public static void setDryRun(final boolean dryRun) {
        Config.dryRun = dryRun;
    }

    /**
     * Getter for {@link #noGis}.
     *
     * @return {@link #noGis}
     */
    public static boolean isNoGis() {
        return noGis;
    }

    /**
     * Setter for {@link #noGis}.
     *
     * @param noGis {@link #noGis}
     */
    public static void setNoGis(final boolean noGis) {
        Config.noGis = noGis;
    }

    /**
     * Returns true if MySQL driver is used, otherwise false.
     *
     * @return true if MySQL driver is used, otherwise false
     */
    public static boolean isMysqlDriver() {
        return dbConnectionUrl != null
                && dbConnectionUrl.startsWith("jdbc:mysql:");
    }

    /**
     * Getter for {@link #linearPrecision}.
     *
     * @return {@link #linearPrecision}
     */
    public static double getLinearPrecision() {
        return linearPrecision;
    }

    /**
     * Creates new instance of Config.
     */
    private Config() {
    }
}
