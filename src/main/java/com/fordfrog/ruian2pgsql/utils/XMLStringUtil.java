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
     * Creates new instance of XMLStringUtil.
     */
    private XMLStringUtil() {
    }

    /**
     * Creates string from XML element and its sub-elements. The method is GML
     * specific.
     *
     * @param reader XML stream reader
     *
     * @return element tree as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading or
     *                            writing XML stream.
     */
    public static String createString(final XMLStreamReader reader)
            throws XMLStreamException {
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

        return stripDeclaration(stringWriter.toString());
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
                    writer.writeCharacters(reader.getText());
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
}
