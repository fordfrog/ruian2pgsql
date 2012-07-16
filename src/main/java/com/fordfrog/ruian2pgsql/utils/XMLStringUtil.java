/**
 * Copyright 2012 Miroslav Šulc To change this template, choose Tools |
 * Templates and open the template in the editor.
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

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Reads element and its subelements from XML stream reader and converts it to
 * string.
 *
 * @author fordfrog
 */
public class XMLStringUtil {

    /**
     * Whether to ignore invalid GML strings.
     */
    private static boolean ignoreInvalidGML;

    /**
     * Creates new instance of XMLStringUtil.
     */
    private XMLStringUtil() {
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
        XMLStringUtil.ignoreInvalidGML = ignoreInvalidGML;
    }

    /**
     * Creates GML string from XML element and its sub-elements.
     *
     * @param reader XML stream reader
     * @param con    database connection
     *
     * @return element tree as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading or
     *                            writing XML stream.
     */
    public static String createGMLString(final XMLStreamReader reader,
            final Connection con) throws XMLStreamException {
        final XMLOutputFactory xMLOutputFactory =
                XMLOutputFactory.newInstance();
        final StringWriter stringWriter = new StringWriter(1024);
        final XMLStreamWriter writer =
                xMLOutputFactory.createXMLStreamWriter(stringWriter);

        writer.writeStartDocument();
        writer.setPrefix("gml", Namespaces.GML);

        writeElementTree(reader, writer, true);

        writer.writeEndDocument();
        writer.close();

        final String result = stripDeclaration(stringWriter.toString());

        if (ignoreInvalidGML) {
            return isValidGML(con, result) ? result : null;
        } else {
            return result;
        }
    }

    /**
     * Writes the element tree.
     *
     * @param reader       XML stream reader
     * @param writer       XML stream writer
     * @param setNamespace whether GML namespace should be set on the element
     *
     * @throws XMLStreamException Thrown if problem occurred while reading or
     *                            writing XML stream.
     */
    private static void writeElementTree(final XMLStreamReader reader,
            final XMLStreamWriter writer, final boolean setNamespace)
            throws XMLStreamException {
        final String namespace = reader.getNamespaceURI();
        final String localName = reader.getLocalName();

        writer.writeStartElement(namespace, localName);

        if (setNamespace) {
            writer.writeAttribute("xmlns:gml", Namespaces.GML);
        }

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            final String attrNamespace = reader.getAttributeNamespace(i);

            if (attrNamespace == null) {
                writer.writeAttribute(reader.getAttributeLocalName(i),
                        reader.getAttributeValue(i));
            } else {
                writer.writeAttribute(reader.getAttributeNamespace(i),
                        reader.getAttributeLocalName(i),
                        reader.getAttributeValue(i));
            }
        }

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    writeElementTree(reader, writer, false);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    writer.writeEndElement();
                    return;
                case XMLStreamReader.CHARACTERS:
                    writer.writeCharacters(reader.getText().
                            replaceAll("(\\d+(?:\\.\\d+)?)", "-$1"));
                    break;
                default:
                    throw new RuntimeException(
                            "Unsupported XML event " + event);
            }
        }
    }

    /**
     * Strips XML declaration from the string.
     *
     * @param string XML string
     *
     * @return XML string without XML declaration
     */
    private static String stripDeclaration(final String string) {
        final int pos = string.indexOf("?>");

        if (pos == -1) {
            return string;
        } else {
            return string.substring(pos + 2).trim();
        }
    }

    /**
     * Checks whether specified GML string is valid for ST_GeomFromGML()
     * function.
     *
     * @param con database connection
     * @param gml GML string
     *
     * @return true if GML string is valid, otherwise false
     */
    private static boolean isValidGML(final Connection con, final String gml) {
        try (final PreparedStatement pstm = con.prepareStatement(
                        "SELECT st_geomfromgml(?)")) {
            final Savepoint savepoint = con.setSavepoint("gml_check");

            pstm.setString(1, gml);

            try (final ResultSet rs = pstm.executeQuery()) {
                con.releaseSavepoint(savepoint);
            } catch (final SQLException ex) {
                con.rollback(savepoint);

                return false;
            }
        } catch (final SQLException ex) {
            throw new RuntimeException("Failed to validate GML string", ex);
        }

        return true;
    }
}
