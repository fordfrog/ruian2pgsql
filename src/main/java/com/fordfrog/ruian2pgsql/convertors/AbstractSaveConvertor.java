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

import com.fordfrog.ruian2pgsql.Config;
import com.fordfrog.ruian2pgsql.utils.XMLUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Abstract implementation of Convertor with support for saving item data.
 *
 * @param <T> class of objects that will be saved to the database
 *
 * @author fordfrog
 */
public abstract class AbstractSaveConvertor<T> implements Convertor {

    /**
     * Class of objects that will be saved to the database.
     */
    private final Class<T> clazz;
    /**
     * Namespace of the processed element.
     */
    private final String namespace;
    /**
     * Local name of the processed element.
     */
    private final String localName;
    /**
     * Database connection.
     */
    private final Connection connection;
    /**
     * Prepared statement for checking whether item exists.
     */
    private final PreparedStatement pstmExists;
    /**
     * Prepared statement for insertion of new item.
     */
    private final PreparedStatement pstmInsert;
    /**
     * Prepared statement for update of existing item.
     */
    private final PreparedStatement pstmUpdate;

    /**
     * Creates new instance of AbstractSaveConvertor.
     *
     * @param clazz          {@link #clazz}
     * @param namespace      {@link #namespace}
     * @param localName      {@link #localName}
     * @param con            database connection
     * @param sqlExists      SQL statement for testing whether item exists, if
     *                       item
     *                       does not exist, it must return no row
     * @param sqlInsert      SQL statement for insertion of item to database
     * @param sqlUpdate      SQL statement for updating of item in database
     * @param sqlInsertNoGis SQL statement for insertion of item to database
     *                       when --no-gis is used
     * @param sqlUpdateNoGis SQL statement for updating of item in database when
     *                       --no-gis is used
     *
     * @throws SQLException Thrown if problem occurred while preparing
     *                      statements.
     */
    public AbstractSaveConvertor(Class<T> clazz, final String namespace,
            final String localName, final Connection con,
            final String sqlExists, final String sqlInsert,
            final String sqlUpdate, final String sqlInsertNoGis,
            final String sqlUpdateNoGis) throws SQLException {
        this.clazz = clazz;
        this.namespace = namespace;
        this.localName = localName;
        this.connection = con;
        this.pstmExists =
                sqlExists == null ? null : con.prepareStatement(sqlExists);

        String sqlInsertAdj = Config.isNoGis() ? sqlInsertNoGis : sqlInsert;
        String sqlUpdateAdj = Config.isNoGis() ? sqlUpdateNoGis : sqlUpdate;

        if (Config.isMysqlDriver()) {
            sqlInsertAdj = fixSql(sqlInsertAdj);
            sqlUpdateAdj = fixSql(sqlUpdateAdj);
        }

        if (sqlInsertAdj != null) {
            sqlInsertAdj = formatGeometry(sqlInsertAdj);
        }

        if (sqlUpdateAdj != null) {
            sqlUpdateAdj = formatGeometry(sqlUpdateAdj);
        }

        this.pstmInsert = sqlInsertAdj == null
                ? null : con.prepareStatement(sqlInsertAdj);
        this.pstmUpdate = sqlUpdateAdj == null
                ? null : con.prepareStatement(sqlUpdateAdj);
    }

    /**
     * Getter for {@link #connection}.
     *
     * @return {@link #connection}
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Processes current elements and its sub-elements. On each element
     * {@link #processElement(javax.xml.stream.XMLStreamReader, java.lang.Object, java.io.Writer)}
     * is called. After the element is processed,
     * {@link #saveData(java.sql.Connection, java.lang.Object)} is called.
     *
     * @param reader XML stream reader
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws SQLException       Thrown if problem occurred while communicating
     *                            with database.
     */
    @Override
    public void convert(final XMLStreamReader reader) throws XMLStreamException,
            SQLException {
        final T item;

        try {
            item = clazz.newInstance();
        } catch (final IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException("Failed to instantiate class "
                    + clazz.getName(), ex);
        }

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    processElement(reader, item);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(namespace, localName, reader)) {
                        saveData(item);

                        return;
                    }

                    break;
            }
        }
    }

    /**
     * Fixes SQL statement.
     *
     * @param sql SQL statement
     *
     * @return fixed SQL statement
     */
    protected String fixSql(final String sql) {
        if (sql == null) {
            return sql;
        }

        if (Config.isMysqlDriver()) {
            return sql.replace("timezone('utc', now())", "current_timestamp");
        } else {
            return sql;
        }
    }

    /**
     * Formats geometry in SQL statement.
     *
     * @param sql SQL statement
     *
     * @return fixed SQL statement
     */
    protected String formatGeometry(final String sql) {
        if (sql == null) {
            return sql;
        }

	String geomFunction;
        if (Config.isConvertToEWKT()) {
            geomFunction = "ST_GeomFromEWKT";
        } else {
            geomFunction = "ST_GeomFromGML";
        }

	String newsql;
        if (Config.getDestinationSrid() != null) {
	    newsql = sql.replace("%FUNCTION%(?)", "ST_Transform(" + geomFunction + "(?)," + Config.getDestinationSrid() + ")");
        } else {
	    newsql = sql.replace("%FUNCTION%", geomFunction);
        }

	return newsql;
    }

    /**
     * Saves the item data into database. If item already exists,
     * {@link #updateItem(java.sql.Connection, java.lang.Object)} is called,
     * otherwise {@link #insertItem(java.sql.Connection, java.lang.Object)} is
     * called. Testing for whether item exists is performed via
     * {@link #exists(java.sql.Connection, java.lang.Object)}.
     *
     * @param item item to be saved
     *
     * @throws SQLException Thrown if problem occurred while saving item into
     *                      database.
     */
    protected void saveData(final T item) throws SQLException {
        if (Config.isDryRun()) {
            return;
        }

        if (exists(item)) {
            updateItem(item);
        } else {
            insertItem(item);
        }
    }

    /**
     * Checks whether item already exists.
     * {@link #fillExists(java.sql.PreparedStatement, java.lang.Object)} is
     * called to get the prepared statement parameter(s) filled.
     *
     * @param item item to be checked
     *
     * @return true if item exists, otherwise false
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    protected boolean exists(final T item) throws SQLException {
        pstmExists.clearParameters();
        fillExists(pstmExists, item);

        try (final ResultSet rs = pstmExists.executeQuery()) {
            return rs.next();
        }
    }

    /**
     * Inserts item into database. Before statement execution,
     * {@link #fill(java.sql.PreparedStatement, java.lang.Object, boolean)} is
     * called to get prepared statement parameters filled.
     *
     * @param item item to be saved
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    protected void insertItem(final T item) throws SQLException {
        pstmInsert.clearParameters();
        fill(pstmInsert, item, false);
        pstmInsert.execute();
    }

    /**
     * Updates item in database. Before statement execution,
     * {@link #fill(java.sql.PreparedStatement, java.lang.Object, boolean)} is
     * called to get prepared statement parameters filled.
     *
     * @param item item to be saved
     *
     * @throws SQLException
     */
    protected void updateItem(final T item) throws SQLException {
        pstmUpdate.clearParameters();
        fill(pstmUpdate, item, true);
        pstmUpdate.execute();
    }

    /**
     * Processes elements of the main element.
     *
     * @param reader XML stream reader
     * @param item   item of the main element
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws SQLException       Thrown if problem occurred while communicating
     *                            with database.
     */
    protected abstract void processElement(XMLStreamReader reader, T item)
            throws XMLStreamException, SQLException;

    /**
     * Fills prepared statement parameters for testing whether item already
     * exists in database.
     *
     * @param pstm prepared statement
     * @param item item of the element
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    protected abstract void fillExists(PreparedStatement pstm, T item)
            throws SQLException;

    /**
     * Fills prepared statement parameters for insertion or update.
     *
     * @param pstm   prepared statement
     * @param item   item of the element
     * @param update true if UPDATE statement will be performed, false if INSERT
     *               statement will be performed
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    protected abstract void fill(PreparedStatement pstm, T item,
            boolean update) throws SQLException;
}
