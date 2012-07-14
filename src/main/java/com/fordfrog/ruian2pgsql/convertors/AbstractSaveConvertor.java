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

import com.fordfrog.ruian2pgsql.utils.Utils;
import java.io.Writer;
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
     * SQL statement for testing whether item exists. If item does not exist, it
     * must return no row.
     */
    private final String sqlExists;
    /**
     * SQL statement for insertion of item to database.
     */
    private final String sqlInsert;
    /**
     * SQL statement for updating of item in database.
     */
    private final String sqlUpdate;

    /**
     * Creates new instance of AbstractSaveConvertor.
     *
     * @param clazz     {@link #clazz}
     * @param namespace {@link #namespace}
     * @param localName {@link #localName}
     * @param sqlExists {@link #sqlExists}
     * @param sqlInsert {@link #sqlInsert}
     * @param sqlUpdate {@link #sqlUpdate}
     */
    public AbstractSaveConvertor(Class<T> clazz, final String namespace,
            final String localName, final String sqlExists,
            final String sqlInsert, final String sqlUpdate) {
        this.clazz = clazz;
        this.namespace = namespace;
        this.localName = localName;
        this.sqlExists = sqlExists;
        this.sqlInsert = sqlInsert;
        this.sqlUpdate = sqlUpdate;
    }

    /**
     * Processes current elements and its sub-elements. On each element
     * {@link #processElement(javax.xml.stream.XMLStreamReader, java.lang.Object, java.io.Writer)}
     * is called. After the element is processed,
     * {@link #saveData(java.sql.Connection, java.lang.Object)} is called.
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
    @Override
    public void convert(final XMLStreamReader reader, final Connection con,
            final Writer logFile) throws XMLStreamException, SQLException {
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
                    processElement(reader, con, item, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (Utils.isEndElement(namespace, localName, reader)) {
                        saveData(con, item);

                        return;
                    }

                    break;
            }
        }
    }

    /**
     * Saves the item data into database. If item already exists,
     * {@link #updateItem(java.sql.Connection, java.lang.Object)} is called,
     * otherwise {@link #insertItem(java.sql.Connection, java.lang.Object)} is
     * called. Testing for whether item exists is performed via
     * {@link #exists(java.sql.Connection, java.lang.Object)}.
     *
     * @param con  database connection
     * @param item item to be saved
     *
     * @throws SQLException Thrown if problem occurred while saving item into
     *                      database.
     */
    protected void saveData(final Connection con, final T item)
            throws SQLException {
        if (exists(con, item)) {
            updateItem(con, item);
        } else {
            insertItem(con, item);
        }
    }

    /**
     * Checks whether item already exists.
     * {@link #fillExists(java.sql.PreparedStatement, java.lang.Object)} is
     * called to get the prepared statement parameter(s) filled.
     *
     * @param con  database connection
     * @param item item to be checked
     *
     * @return true if item exists, otherwise false
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    protected boolean exists(final Connection con, final T item)
            throws SQLException {
        try (final PreparedStatement pstm = con.prepareStatement(sqlExists)) {
            fillExists(pstm, item);

            try (final ResultSet rs = pstm.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Inserts item into database. Before statement execution,
     * {@link #fill(java.sql.PreparedStatement, java.lang.Object, boolean)} is
     * called to get prepared statement parameters filled.
     *
     * @param con  database connection
     * @param item item to be saved
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    protected void insertItem(final Connection con, final T item)
            throws SQLException {
        try (final PreparedStatement pstm = con.prepareStatement(sqlInsert)) {
            fill(pstm, item, false);
            pstm.execute();
        }
    }

    /**
     * Updates item in database. Before statement execution,
     * {@link #fill(java.sql.PreparedStatement, java.lang.Object, boolean)} is
     * called to get prepared statement parameters filled.
     *
     * @param con  database connection
     * @param item item to be saved
     *
     * @throws SQLException
     */
    protected void updateItem(final Connection con, final T item)
            throws SQLException {
        try (final PreparedStatement pstm = con.prepareStatement(sqlUpdate)) {
            fill(pstm, item, true);
            pstm.execute();
        }
    }

    /**
     * Processes elements of the main element.
     *
     * @param reader  XML stream reader
     * @param con     database connection
     * @param item    item of the main element
     * @param logFile log file writer
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     * @throws SQLException       Thrown if problem occurred while communicating
     *                            with database.
     */
    protected abstract void processElement(XMLStreamReader reader,
            Connection con, T item, Writer logFile)
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
