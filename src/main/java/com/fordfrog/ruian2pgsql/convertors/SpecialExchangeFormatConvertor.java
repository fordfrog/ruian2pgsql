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

import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.XMLUtils;
import java.sql.Connection;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Special exchange format convertor.
 *
 * @author fordfrog
 */
public class SpecialExchangeFormatConvertor extends AbstractConvertor {

    /**
     * Namespace of SpecialniVymennyFormat and its sub-elements.
     */
    private static final String NAMESPACE = Namespaces.SPECIALNI_VYMENNY_FORMAT_TYPY;
    /**
     * Data convertor instance.
     */
    private final SpecialniDataConvertor dataConvertor;
    /**
     * Hlavicka convertor instance.
     */
    private final SpecialniHlavickaConvertor hlavickaConvertor;

    /**
     * Creates new instance of ExchangeFormatConvertor.
     *
     * @param con {@link #connection}
     *
     * @throws SQLException Thrown if problem occurred while initializing
     *                      convertors.
     */
    public SpecialExchangeFormatConvertor(final Connection con) throws SQLException {
        super(NAMESPACE, "SpecialniVymennyFormat");

        dataConvertor = new SpecialniDataConvertor(con);
        hlavickaConvertor = new SpecialniHlavickaConvertor(con);
    }

    @Override
    protected void processElement(final XMLStreamReader reader)
            throws XMLStreamException, SQLException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "Data":
                        dataConvertor.convert(reader);
                        break;
                    case "Hlavicka":
                        hlavickaConvertor.convert(reader);
                        break;
                    default:
                        XMLUtils.processUnsupported(reader);
                }

                break;
            default:
                XMLUtils.processUnsupported(reader);
        }
    }
}
