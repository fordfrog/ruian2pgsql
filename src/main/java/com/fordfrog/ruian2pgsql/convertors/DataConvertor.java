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
     * Convertor for AdresniMista.
     */
    private final Convertor convertorAdresniMista;
    /**
     * Convertor for CastiObci.
     */
    private final Convertor convertorCastiObci;
    /**
     * Convertor for KatastralniUzemi.
     */
    private final Convertor convertorKatastralniUzemi;
    /**
     * Convertor for Kraje.
     */
    private final Convertor convertorKraje;
    /**
     * Convertor for Momc.
     */
    private final Convertor convertorMomc;
    /**
     * Convertor for Mop.
     */
    private final Convertor convertorMop;
    /**
     * Convertor for Obce.
     */
    private final Convertor convertorObce;
    /**
     * Convertor for Okresy.
     */
    private final Convertor convertorOkresy;
    /**
     * Convertor for Orp.
     */
    private final Convertor convertorOrp;
    /**
     * Convertor for Parcely.
     */
    private final Convertor convertorParcely;
    /**
     * Convertor for Pou.
     */
    private final Convertor convertorPou;
    /**
     * Convertor for RegionySoudrznosti.
     */
    private final Convertor convertorRegionySoudrznosti;
    /**
     * Convertor for SpravniObvody.
     */
    private final Convertor convertorSpravniObvody;
    /**
     * Convertor for StavebniObjekty.
     */
    private final Convertor convertorStavebniObjekty;
    /**
     * Convertor for Staty.
     */
    private final Convertor convertorStaty;
    /**
     * Convertor for Ulice.
     */
    private final Convertor convertorUlice;
    /**
     * Convertor for VolebniOkrsek.
     */
    private final Convertor convertorVolebniOkrsek;
    /**
     * Convertor for Vusc.
     */
    private final Convertor convertorVusc;
    /**
     * Convertor for ZaniklePrvky.
     */
    private final Convertor convertorZaniklePrvky;
    /**
     * Convertor for Zsj.
     */
    private final Convertor convertorZsj;

    /**
     * Creates new instance of DataConvertor.
     *
     * @param con database connection
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    public DataConvertor(final Connection con) throws SQLException {
        super(NAMESPACE, "Data");

        convertorAdresniMista = new CollectionConvertor(
                "AdresniMista", "AdresniMisto", new AdresniMistoConvertor(con));
        convertorCastiObci = new CollectionConvertor(
                "CastiObci", "CastObce", new CastObceConvertor(con));
        convertorKatastralniUzemi = new CollectionConvertor("KatastralniUzemi",
                "KatastralniUzemi", new KatastralniUzemiConvertor(con));
        convertorKraje = new CollectionConvertor(
                "Kraje", "Kraj", new KrajConvertor(con));
        convertorMomc =
                new CollectionConvertor("Momc", "Momc", new MomcConvertor(con));
        convertorMop =
                new CollectionConvertor("Mop", "Mop", new MopConvertor(con));
        convertorObce =
                new CollectionConvertor("Obce", "Obec", new ObecConvertor(con));
        convertorOkresy = new CollectionConvertor(
                "Okresy", "Okres", new OkresConvertor(con));
        convertorOrp =
                new CollectionConvertor("Orp", "Orp", new OrpConvertor(con));
        convertorParcely = new CollectionConvertor(
                "Parcely", "Parcela", new ParcelaConvertor(con));
        convertorPou =
                new CollectionConvertor("Pou", "Pou", new PouConvertor(con));
        convertorRegionySoudrznosti = new CollectionConvertor(
                "RegionySoudrznosti", "RegionSoudrznosti",
                new RegionSoudrznostiConvertor(con));
        convertorSpravniObvody = new CollectionConvertor("SpravniObvody",
                "SpravniObvod", new SpravniObvodConvertor(con));
        convertorStavebniObjekty = new CollectionConvertor("StavebniObjekty",
                "StavebniObjekt", new StavebniObjektConvertor(con));
        convertorStaty = new CollectionConvertor(
                "Staty", "Stat", new StatConvertor(con));
        convertorUlice = new CollectionConvertor(
                "Ulice", "Ulice", new UliceConvertor(con));
        convertorVolebniOkrsek = new CollectionConvertor(
                "VolebniOkrsek", "VO", new VOConvertor(con));
        convertorVusc =
                new CollectionConvertor("Vusc", "Vusc", new VuscConvertor(con));
        convertorZaniklePrvky = new CollectionConvertor(
                "ZaniklePrvky", "ZaniklyPrvek", new ZaniklyPrvekConvertor(con));
        convertorZsj =
                new CollectionConvertor("Zsj", "Zsj", new ZsjConvertor(con));

    }

    @Override
    protected void processElement(final XMLStreamReader reader)
            throws XMLStreamException, SQLException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "AdresniMista":
                        convertorAdresniMista.convert(reader);
                        break;
                    case "CastiObci":
                        convertorCastiObci.convert(reader);
                        break;
                    case "KatastralniUzemi":
                        convertorKatastralniUzemi.convert(reader);
                        break;
                    case "Kraje":
                        convertorKraje.convert(reader);
                        break;
                    case "Momc":
                        convertorMomc.convert(reader);
                        break;
                    case "Mop":
                        convertorMop.convert(reader);
                        break;
                    case "Obce":
                        convertorObce.convert(reader);
                        break;
                    case "Okresy":
                        convertorOkresy.convert(reader);
                        break;
                    case "Orp":
                        convertorOrp.convert(reader);
                        break;
                    case "Parcely":
                        convertorParcely.convert(reader);
                        break;
                    case "Pou":
                        convertorPou.convert(reader);
                        break;
                    case "RegionySoudrznosti":
                        convertorRegionySoudrznosti.convert(reader);
                        break;
                    case "SpravniObvody":
                        convertorSpravniObvody.convert(reader);
                        break;
                    case "Staty":
                        convertorStaty.convert(reader);
                        break;
                    case "StavebniObjekty":
                        convertorStavebniObjekty.convert(reader);
                        break;
                    case "Ulice":
                        convertorUlice.convert(reader);
                        break;
                    case "VolebniOkrsek":
                        convertorVolebniOkrsek.convert(reader);
                        break;
                    case "Vusc":
                        convertorVusc.convert(reader);
                        break;
                    case "ZaniklePrvky":
                        convertorZaniklePrvky.convert(reader);
                        break;
                    case "Zsj":
                        convertorZsj.convert(reader);
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
