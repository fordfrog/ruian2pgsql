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
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Convertor for collection of elements.
 *
 * @author fordfrog
 */
public class CollectionConvertor extends AbstractConvertor {

    /**
     * Namespace of the collection element.
     */
    private static final String NAMESPACE = Namespaces.VYMENNY_FORMAT_TYPY;
    /**
     * Item namespace.
     */
    private final String itemNamespace;
    /**
     * Local name of the collection item element.
     */
    private final String itemName;
    /**
     * Convertor that can convert the item elements.
     */
    private final Convertor convertor;

    /**
     * Creates new instance of CollectionConvertor.
     *
     * @param localName local name of the collection main element.
     * @param itemName  {@link #itemName}
     * @param convertor {@link #convertor}
     */
    public CollectionConvertor(final String localName, final String itemName,
            final Convertor convertor) {
        super(NAMESPACE, localName);

        this.itemNamespace = NAMESPACE;
        this.itemName = itemName;
        this.convertor = convertor;
    }

    /**
     * Creates new instance of CollectionConvertor.
     *
     * @param namespace     namespace of the element
     * @param localName     local name of the collection main element.
     * @param itemNamespace item namespace
     * @param itemName      {@link #itemName}
     * @param convertor     {@link #convertor}
     */
    public CollectionConvertor(final String namespace, final String localName,
            final String itemNamespace, final String itemName,
            final Convertor convertor) {
        super(namespace, localName);

        this.itemNamespace = itemNamespace;
        this.itemName = itemName;
        this.convertor = convertor;
    }

    @Override
    protected void processElement(final XMLStreamReader reader)
            throws XMLStreamException, SQLException {
        if (itemNamespace.equals(reader.getNamespaceURI())
                && itemName.equals(reader.getLocalName())) {
            convertor.convert(reader);
        } else {
            XMLUtils.processUnsupported(reader);
        }
    }
}
