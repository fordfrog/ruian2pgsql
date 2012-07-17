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

import com.fordfrog.ruian2pgsql.containers.SpravniObvod;
import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.PreparedStatementEx;
import com.fordfrog.ruian2pgsql.utils.Utils;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Convertor for SpravniObvod element.
 *
 * @author fordfrog
 */
public class SpravniObvodConvertor extends AbstractSaveConvertor<SpravniObvod> {

    /**
     * Namespace of the element.
     */
    private static final String NAMESPACE = Namespaces.SPRAV_OBV_INT_TYPY;
    /**
     * SQL statement for checking whether the item already exists.
     */
    private static final String SQL_EXISTS =
            "SELECT 1 FROM rn_spravni_obvod WHERE kod = ?";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT = "INSERT INTO rn_spravni_obvod "
            + "(nazev, nespravny, obec_kod, spravni_momc_kod, id_trans_ruian, "
            + "plati_od, nz_id_globalni, zmena_grafiky, definicni_bod, "
            + "hranice, kod) VALUES (?, ?, ?, ?, ?, ?, ?, ?, "
            + "ST_GeomFromGML(?), ST_GeomFromGML(?), ?)";
    /**
     * SQL statement for update of existing item.
     */
    private static final String SQL_UPDATE = "UPDATE rn_spravni_obvod "
            + "SET nazev = ?, nespravny = ?, obec_kod = ?, "
            + "spravni_momc_kod = ?, id_trans_ruian = ?, plati_od = ?, "
            + "nz_id_globalni = ?, zmena_grafiky = ?, "
            + "definicni_bod = ST_GeomFromGML(?), hranice = ST_GeomFromGML(?) "
            + "WHERE kod = ? AND id_trans_ruian < ?";

    /**
     * Creates new instance of SpravniObvodConvertor.
     */
    public SpravniObvodConvertor() {
        super(SpravniObvod.class, Namespaces.VYMENNY_FORMAT_TYPY,
                "SpravniObvod", SQL_EXISTS, SQL_INSERT, SQL_UPDATE);
    }

    @Override
    protected void fill(final PreparedStatement pstm, final SpravniObvod item,
            final boolean update) throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        pstm.setString(1, item.getNazev());
        pstmEx.setBoolean(2, item.getNespravny());
        pstm.setInt(3, item.getObecKod());
        pstm.setInt(4, item.getSpravniMomcKod());
        pstm.setLong(5, item.getIdTransRuian());
        pstmEx.setDate(6, item.getPlatiOd());
        pstm.setLong(7, item.getNzIdGlobalni());
        pstmEx.setBoolean(8, item.getZmenaGrafiky());
        pstm.setString(9, item.getDefinicniBod());
        pstm.setString(10, item.getHranice());
        pstm.setInt(11, item.getKod());

        if (update) {
            pstm.setLong(12, item.getIdTransRuian());
        }
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final SpravniObvod item) throws SQLException {
        pstm.setInt(1, item.getKod());
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Connection con, final SpravniObvod item, final Writer logFile)
            throws XMLStreamException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "Geometrie":
                        Utils.processGeometrie(
                                reader, con, item, NAMESPACE, logFile);
                        break;
                    case "GlobalniIdNavrhuZmeny":
                        item.setNzIdGlobalni(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "IdTransakce":
                        item.setIdTransRuian(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "Kod":
                        item.setKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "Nazev":
                        item.setNazev(reader.getElementText());
                        break;
                    case "Nespravny":
                        item.setNespravny(
                                Boolean.valueOf(reader.getElementText()));
                        break;
                    case "Obec":
                        item.setObecKod(
                                Utils.getObecKod(reader, NAMESPACE, logFile));
                        break;
                    case "PlatiOd":
                        item.setPlatiOd(
                                Utils.parseTimestamp(reader.getElementText()));
                        break;
                    case "SpravniMomcKod":
                        item.setSpravniMomcKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    default:
                        Utils.processUnsupported(reader, logFile);
                }

                break;
            default:
                Utils.processUnsupported(reader, logFile);
        }
    }
}
