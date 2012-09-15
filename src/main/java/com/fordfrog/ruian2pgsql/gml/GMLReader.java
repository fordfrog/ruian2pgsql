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
package com.fordfrog.ruian2pgsql.gml;

import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.XMLUtils;
import java.sql.Connection;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Reads GML into string.
 *
 * @author fordfrog
 */
public class GMLReader {

    /**
     * Processes GML element(s).
     *
     * @param reader       XML stream reader
     * @param con          database connection
     * @param endNamespace namespace of DefinicniBod element
     * @param endLocalName local name of DefinicniBod element
     *
     * @return GML geometry as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    public static String readGML(final XMLStreamReader reader,
            final Connection con, final String endNamespace,
            final String endLocalName) throws XMLStreamException {
        String definicniBod = null;

        while (reader.hasNext()) {
            final int event = reader.next();

            switch (event) {
                case XMLStreamReader.START_ELEMENT:
                    definicniBod = readGMLElement(reader, con);
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (XMLUtils.isSameElement(
                            endNamespace, endLocalName, reader)) {
                        return definicniBod;
                    }
            }
        }

        return definicniBod;
    }

    /**
     * Processes sub-elements of an element containing GML.
     *
     * @param reader XML stream reader
     * @param con    database connection
     *
     * @return GML geometry as string
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static String readGMLElement(final XMLStreamReader reader,
            final Connection con) throws XMLStreamException {
        String result = null;

        switch (reader.getNamespaceURI()) {
            case Namespaces.GML:
                result = GMLUtils.createGMLString(reader, con);
                break;
            default:
                XMLUtils.processUnsupported(reader);
        }

        return result;
    }

    /**
     * Creates new instance of GMLReader.
     */
    private GMLReader() {
    }
}
