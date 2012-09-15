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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * XML utilities.
 *
 * @author fordfrog
 */
public class XMLUtils {

    /**
     * Checks whether XML stream reader namespace and local name match the one
     * specified in the call.
     *
     * @param namespace namespace
     * @param localName local name
     * @param reader    XML stream reader
     *
     * @return true if namespace and local name match, otherwise false
     */
    public static boolean isEndElement(final String namespace,
            final String localName, final XMLStreamReader reader) {
        return namespace.equals(reader.getNamespaceURI())
                && localName.equals(reader.getLocalName());
    }

    /**
     * Processes unsupported element and its subelements.
     *
     * @param reader XML stream reader
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static void processUnsupported(final XMLStreamReader reader)
            throws XMLStreamException {
        processUnsupported(reader, 0);
    }

    /**
     * Strips XML declaration from the string.
     *
     * @param string XML string
     *
     * @return XML string without XML declaration
     */
    public static String stripDeclaration(final String string) {
        final int pos = string.indexOf("?>");

        if (pos == -1) {
            return string;
        } else {
            return string.substring(pos + 2).trim();
        }
    }

    /**
     * Processes unsupported element and its subelements.
     *
     * @param reader XML stream reader
     * @param indent indentation count
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void processUnsupported(final XMLStreamReader reader,
            final int indent) throws XMLStreamException {
        final String namespace = reader.getNamespaceURI();
        final String localName = reader.getLocalName();
        final StringBuilder sbString = new StringBuilder(namespace.length()
                + localName.length() + 50);

        for (int i = 0; i < indent; i++) {
            sbString.append("  ");
        }

        sbString.append("Warning: Ignoring unsupported element ");
        sbString.append(namespace);
        sbString.append(' ');
        sbString.append(localName);

        Log.write(sbString.toString());

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    processUnsupported(reader, indent + 1);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isEndElement(
                            namespace, localName, reader)) {
                        return;
                    }
            }
        }
    }

    /**
     * Creates new instance of XMLUtils.
     */
    private XMLUtils() {
    }
}
