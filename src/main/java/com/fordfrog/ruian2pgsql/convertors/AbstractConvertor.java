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
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Abstract implementation of Convertor.
 *
 * @author fordfrog
 */
public abstract class AbstractConvertor implements Convertor {

    /**
     * Namespace of the processed element.
     */
    private final String namespace;
    /**
     * Local name of the processed element.
     */
    private final String localName;

    /**
     * Creates new instance of AbstractConvertor.
     *
     * @param namespace {@link #namespace}
     * @param localName {@link #localName}
     */
    public AbstractConvertor(final String namespace, final String localName) {
        this.namespace = namespace;
        this.localName = localName;
    }

    /**
     * Processes current elements and its sub-elements. On each element
     * {@link #processElement(javax.xml.stream.XMLStreamReader, java.sql.Connection, java.io.Writer)}
     * is called.
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
    @Override
    public void convert(final XMLStreamReader reader, final Connection con,
            final Writer logFile) throws XMLStreamException, IOException,
            SQLException {
        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    processElement(reader, con, logFile);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (Utils.isEndElement(namespace, localName, reader)) {
                        return;
                    }

                    break;
            }
        }
    }

    /**
     * Processes current XML element.
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
    protected abstract void processElement(final XMLStreamReader reader,
            final Connection con, final Writer logFile) throws IOException,
            XMLStreamException, SQLException;
}
