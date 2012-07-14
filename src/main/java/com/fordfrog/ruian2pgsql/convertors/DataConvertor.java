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
import com.fordfrog.ruian2pgsql.utils.Utils;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Data convertor.
 *
 * @author fordfrog
 */
public class DataConvertor extends AbstractConvertor {

    /**
     * Namespace of the Data element and its sub-elements.
     */
    private static final String NAMESPACE = Namespaces.VYMENNY_FORMAT_TYPY;

    /**
     * Creates new instance of DataConvertor.
     */
    public DataConvertor() {
        super(NAMESPACE, "Data");
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Connection con, final Writer logFile)
            throws XMLStreamException, SQLException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                Convertor convertor = null;

                switch (reader.getLocalName()) {
                    case "AdresniMista":
                        convertor = new CollectionConvertor("AdresniMista",
                                "AdresniMisto", new AdresniMistoConvertor());
                        break;
                    case "CastiObci":
                        convertor = new CollectionConvertor("CastiObci",
                                "CastObce", new CastObceConvertor());
                        break;
                    case "KatastralniUzemi":
                        convertor = new CollectionConvertor("KatastralniUzemi",
                                "KatastralniUzemi",
                                new KatastralniUzemiConvertor());
                        break;
                    case "Kraje":
                        convertor = new CollectionConvertor(
                                "Kraje", "Kraj", new KrajConvertor());
                        break;
                    case "Momc":
                        convertor = new CollectionConvertor(
                                "Momc", "Momc", new MomcConvertor());
                        break;
                    case "Mop":
                        convertor = new CollectionConvertor(
                                "Mop", "Mop", new MopConvertor());
                        break;
                    case "Obce":
                        convertor = new CollectionConvertor(
                                "Obce", "Obec", new ObecConvertor());
                        break;
                    case "Okresy":
                        convertor = new CollectionConvertor(
                                "Okresy", "Okres", new OkresConvertor());
                        break;
                    case "Orp":
                        convertor = new CollectionConvertor(
                                "Orp", "Orp", new OrpConvertor());
                        break;
                    case "Parcely":
                        convertor = new CollectionConvertor(
                                "Parcely", "Parcela", new ParcelaConvertor());
                        break;
                    case "Pou":
                        convertor = new CollectionConvertor(
                                "Pou", "Pou", new PouConvertor());
                        break;
                    case "RegionySoudrznosti":
                        convertor = new CollectionConvertor(
                                "RegionySoudrznosti", "RegionSoudrznosti",
                                new RegionSoudrznostiConvertor());
                        break;
                    case "SpravniObvody":
                        convertor = new CollectionConvertor("SpravniObvody",
                                "SpravniObvod", new SpravniObvodConvertor());
                        break;
                    case "StavebniObjekty":
                        convertor = new CollectionConvertor("StavebniObjekty",
                                "StavebniObjekt",
                                new StavebniObjektConvertor());
                        break;
                    case "Staty":
                        convertor = new CollectionConvertor(
                                "Staty", "Stat", new StatConvertor());
                        break;
                    case "Ulice":
                        convertor = new CollectionConvertor(
                                "Ulice", "Ulice", new UliceConvertor());
                        break;
                    case "Vusc":
                        convertor = new CollectionConvertor(
                                "Vusc", "Vusc", new VuscConvertor());
                        break;
                    case "Zsj":
                        convertor = new CollectionConvertor(
                                "Zsj", "Zsj", new ZsjConvertor());
                        break;
                    default:
                        Utils.printWarningIgnoringElement(logFile, reader);
                }

                if (convertor != null) {
                    convertor.convert(reader, con, logFile);
                }

                break;
            default:
                Utils.printWarningIgnoringElement(logFile, reader);
        }
    }
}
