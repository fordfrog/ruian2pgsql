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

import com.fordfrog.ruian2pgsql.containers.Ulice;
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
 * Convertor for Ulice element.
 *
 * @author fordfrog
 */
public class UliceConvertor extends AbstractSaveConvertor<Ulice> {

    private static final String NAMESPACE = Namespaces.ULICE_INT_TYPY;
    private static final String SQL_EXISTS =
            "SELECT 1 FROM rn_ulice WHERE kod = ?";
    private static final String SQL_INSERT = "INSERT INTO rn_ulice "
            + "(nazev, nespravny, obec_kod, id_trans_ruian, plati_od, "
            + "nz_id_globalni, zmena_grafiky, definicni_cary, kod) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ST_GeomFromGML(?), ?)";
    private static final String SQL_UPDATE = "UPDATE rn_ulice "
            + "SET nazev = ?, nespravny = ?, obec_kod = ?, id_trans_ruian = ?, "
            + "plati_od = ?, nz_id_globalni = ?, zmena_grafiky = ?, "
            + "definicni_cary = ST_GeomFromGML(?) WHERE kod = ? AND plati_od < ?";

    public UliceConvertor() {
        super(Ulice.class, Namespaces.VYMENNY_FORMAT_TYPY, "Ulice", SQL_EXISTS,
                SQL_INSERT, SQL_UPDATE);
    }

    @Override
    protected void fill(final PreparedStatement pstm, final Ulice item,
            final boolean update) throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        pstm.setString(1, item.getNazev());
        pstmEx.setBoolean(2, item.getNespravny());
        pstm.setInt(3, item.getObecKod());
        pstm.setLong(4, item.getIdTransRuian());
        pstmEx.setDate(5, item.getPlatiOd());
        pstm.setLong(6, item.getNzIdGlobalni());
        pstmEx.setBoolean(7, item.getZmenaGrafiky());
        pstm.setString(8, item.getDefinicniCary());
        pstm.setInt(9, item.getKod());

        if (update) {
            pstmEx.setDate(10, item.getPlatiOd());
        }
    }

    @Override
    protected void fillExists(final PreparedStatement pstm, final Ulice item)
            throws SQLException {
        pstm.setInt(1, item.getKod());
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Connection con, final Ulice item, final Writer logFile)
            throws XMLStreamException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "Geometrie":
                        Utils.processGeometrie(
                                reader, item, NAMESPACE, logFile);
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
                        item.setKod(Integer.parseInt(reader.getElementText()));
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
                    default:
                        Utils.processUnsupported(reader, logFile);
                }

                break;
            default:
                Utils.processUnsupported(reader, logFile);
        }
    }
}
