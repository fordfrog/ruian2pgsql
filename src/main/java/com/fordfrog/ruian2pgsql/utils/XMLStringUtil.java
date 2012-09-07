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

import com.fordfrog.ruian2pgsql.Config;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.MessageFormat;
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
     * Whether to enable multipoint bug workaround.
     */
    private static boolean multipointBugWorkaround;

    /**
     * Creates new instance of XMLStringUtil.
     */
    private XMLStringUtil() {
    }

    /**
     * Getter for {@link #multipointBugWorkaround}.
     *
     * @return {@link #multipointBugWorkaround}
     */
    public static boolean isMultipointBugWorkaround() {
        return multipointBugWorkaround;
    }

    /**
     * Setter for {@link #multipointBugWorkaround}.
     *
     * @param multipointBugWorkaround {@link #multipointBugWorkaround}
     */
    public static void setMultipointBugWorkaround(
            final boolean multipointBugWorkaround) {
        XMLStringUtil.multipointBugWorkaround = multipointBugWorkaround;
    }

    /**
     * Creates GML string from XML element and its sub-elements.
     *
     * @param reader  XML stream reader
     * @param con     database connection
     * @param logFile log file writer
     *
     * @return element tree as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading or
     *                            writing XML stream.
     */
    public static String createGMLString(final XMLStreamReader reader,
            final Connection con, final Writer logFile)
            throws XMLStreamException {
        final XMLOutputFactory xMLOutputFactory =
                XMLOutputFactory.newInstance();
        final StringWriter stringWriter = new StringWriter(1_024);
        final XMLStreamWriter writer =
                xMLOutputFactory.createXMLStreamWriter(stringWriter);

        writer.writeStartDocument();
        writer.setPrefix("gml", Namespaces.GML);

        writeElementTree(reader, writer, true, false);

        writer.writeEndDocument();
        writer.close();

        final String result = stripDeclaration(stringWriter.toString());

        if (Config.isIgnoreInvalidGML()) {
            return isValidGML(con, result, logFile) ? result : null;
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
     * @param isMultipoint whether any of the parents is gml:Multipoint element
     *
     * @throws XMLStreamException Thrown if problem occurred while reading or
     *                            writing XML stream.
     */
    private static void writeElementTree(final XMLStreamReader reader,
            final XMLStreamWriter writer, final boolean setNamespace,
            final boolean isMultipoint) throws XMLStreamException {
        final String namespace = reader.getNamespaceURI();
        final String localName = reader.getLocalName();
        final boolean curIsMultipoint = Namespaces.GML.equals(namespace)
                && "MultiPoint".equals(localName);
        final boolean isIgnorePointMembers = multipointBugWorkaround
                && isMultipoint && Namespaces.GML.equals(namespace)
                && "pointMembers".equals(localName);
        final boolean isMultipointPoint = multipointBugWorkaround
                && isMultipoint && Namespaces.GML.equals(namespace)
                && "Point".equals(localName);

        if (isIgnorePointMembers) {
            // we do not write pointMembers, instead pointMember is written
            // along with the Point itself
        } else if (isMultipointPoint) {
            writer.writeStartElement(Namespaces.GML, "pointMember");
            writeStartElement(reader, writer, setNamespace);
        } else {
            writeStartElement(reader, writer, setNamespace);
        }

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    writeElementTree(reader, writer, false,
                            curIsMultipoint || isMultipoint);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (!isIgnorePointMembers) {
                        writer.writeEndElement();
                    }

                    if (isMultipointPoint) {
                        writer.writeEndElement();
                    }
                    return;
                case XMLStreamReader.CHARACTERS:
                    writer.writeCharacters(reader.getText());
                    break;
                default:
                    throw new RuntimeException(
                            "Unsupported XML event " + event);
            }
        }
    }

    /**
     * Writes start element and its attributes.
     *
     * @param reader       XML stream reader
     * @param writer       XML stream writer
     * @param setNamespace whether GML namespace should be set
     *
     * @throws XMLStreamException Thrown if problem occurred while working with
     *                            XML streams.
     */
    private static void writeStartElement(final XMLStreamReader reader,
            final XMLStreamWriter writer, final boolean setNamespace)
            throws XMLStreamException {
        writer.writeStartElement(
                reader.getNamespaceURI(), reader.getLocalName());

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
     * @param con     database connection
     * @param gml     GML string
     * @param logFile log file writer
     *
     * @return true if GML string is valid, otherwise false
     */
    private static boolean isValidGML(final Connection con, final String gml,
            final Writer logFile) {
        try (final PreparedStatement pstm = con.prepareStatement(
                        "SELECT st_geomfromgml(?)")) {
            final Savepoint savepoint = con.setSavepoint("gml_check");

            pstm.setString(1, gml);

            try (final ResultSet rs = pstm.executeQuery()) {
                con.releaseSavepoint(savepoint);
            } catch (final SQLException ex) {
                Utils.printToLog(
                        logFile, MessageFormat.format("Invalid GML: {0}", gml));
                con.rollback(savepoint);

                return false;
            }
        } catch (final SQLException ex) {
            throw new RuntimeException(
                    "Failed to validate GML string: " + gml, ex);
        }

        return true;
    }
}
