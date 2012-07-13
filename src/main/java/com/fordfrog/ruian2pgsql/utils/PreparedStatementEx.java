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
package com.fordfrog.ruian2pgsql.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Prepared statement helper wrapper.
 *
 * @author fordfrog
 */
public class PreparedStatementEx {

    /**
     * Calendar with UTC time zone for storing dates in database.
     */
    private static final Calendar TZ_CALENDAR =
            Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    /**
     * Wrapped prepared statement.
     */
    private final PreparedStatement preparedStatement;

    /**
     * Creates new instance of PreparedStatementEx.
     *
     * @param preparedStatement {@link #preparedStatement}
     */
    public PreparedStatementEx(final PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    /**
     * Sets boolean value or boolean null to the prepared statement.
     *
     * @param parameterIndex parameter index
     * @param value          boolean value or null
     *
     * @throws SQLException Thrown if problem occurred while setting the value.
     */
    public void setBoolean(final int parameterIndex, final Boolean value)
            throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, Types.BOOLEAN);
        } else {
            preparedStatement.setBoolean(parameterIndex, value);
        }
    }

    /**
     * Sets integer value or integer null to the prepared statement.
     *
     * @param parameterIndex parameter index
     * @param value          integer value or null
     *
     * @throws SQLException Thrown if problem occurred while setting the value.
     */
    public void setInt(final int parameterIndex, final Integer value)
            throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, Types.INTEGER);
        } else {
            preparedStatement.setInt(parameterIndex, value);
        }
    }

    /**
     * Sets long value or long null to the prepared statement.
     *
     * @param parameterIndex parameter index
     * @param value          long value or null
     *
     * @throws SQLException Thrown if problem occurred while setting the value.
     */
    public void setLong(final int parameterIndex, final Long value)
            throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, Types.BIGINT);
        } else {
            preparedStatement.setLong(parameterIndex, value);
        }
    }

    /**
     * Sets date value in UTC time zone to the prepared statement.
     *
     * @param parameterIndex parameter index
     * @param date           date value or null
     *
     * @throws SQLException Thrown if problem occurred while setting the value.
     */
    public void setDate(final int parameterIndex, final Date date)
            throws SQLException {
        if (date == null) {
            preparedStatement.setNull(parameterIndex, Types.DATE);
        } else {
            preparedStatement.setDate(parameterIndex,
                    new java.sql.Date(date.getTime()), TZ_CALENDAR);
        }
    }

    /**
     * Sets timestamp value in UTC time zone to the prepared statement.
     *
     * @param parameterIndex parameter index
     * @param timestamp      timestamp value or null
     *
     * @throws SQLException Thrown if problem occurred while setting the value.
     */
    public void setTimestamp(final int parameterIndex, final Date timestamp)
            throws SQLException {
        if (timestamp == null) {
            preparedStatement.setNull(parameterIndex, Types.DATE);
        } else {
            preparedStatement.setTimestamp(parameterIndex,
                    new Timestamp(timestamp.getTime()), TZ_CALENDAR);
        }
    }

    /**
     * Sets integer array value or null to the prepared statement.
     *
     * @param parameterIndex parameter index
     * @param array          integer array or null
     *
     * @throws SQLException Thrown if problem occurred while setting the value.
     */
    public void setIntArray(final int parameterIndex, final Integer[] array)
            throws SQLException {
        if (array == null) {
            preparedStatement.setArray(parameterIndex, null);
        } else {
            preparedStatement.setArray(parameterIndex,
                    preparedStatement.getConnection().
                    createArrayOf("int", array));
        }
    }
}
