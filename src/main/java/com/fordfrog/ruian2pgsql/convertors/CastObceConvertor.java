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

import com.fordfrog.ruian2pgsql.containers.CastObce;
import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.PreparedStatementEx;
import com.fordfrog.ruian2pgsql.utils.Utils;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Convertor for CastObce element.
 *
 * @author fordfrog
 */
public class CastObceConvertor extends AbstractSaveConvertor<CastObce> {

    /**
     * Namespace of CastObce.
     */
    private static final String NAMESPACE = Namespaces.CAST_OBCE_INT_TYPY;
    /**
     * SQL statement for testing whether item already exist.
     */
    private static final String SQL_EXISTS =
            "SELECT 1 FROM rn_cast_obce WHERE kod = ?";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT = "INSERT INTO rn_cast_obce "
            + "(nazev, nespravny, obec_kod, mluv_char_pad_2, mluv_char_pad_3, "
            + "mluv_char_pad_4, mluv_char_pad_5, mluv_char_pad_6, "
            + "mluv_char_pad_7, id_trans_ruian, zmena_grafiky, plati_od, "
            + "nz_id_globalni, definicni_bod, hranice, kod) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    /**
     * SQL statement for update of existing item.
     */
    private static final String SQL_UPDATE = "UPDATE rn_cast_obce "
            + "SET nazev = ?, nespravny = ?, obec_kod = ?, "
            + "mluv_char_pad_2 = ?, mluv_char_pad_3 = ?, mluv_char_pad_4 = ?, "
            + "mluv_char_pad_5 = ?, mluv_char_pad_6 = ?, mluv_char_pad_7 = ?, "
            + "id_trans_ruian = ?, zmena_grafiky = ?, plati_od = ?, "
            + "nz_id_globalni = ?, definicni_bod = ?, hranice = ? "
            + "WHERE kod = ? AND plati_od < ?";

    /**
     * Creates new instance of CastObceConvertor.
     */
    public CastObceConvertor() {
        super(CastObce.class, Namespaces.VYMENNY_FORMAT_TYPY, "CastObce",
                SQL_EXISTS, SQL_INSERT, SQL_UPDATE);
    }

    @Override
    protected void fill(final PreparedStatement pstm, final CastObce item,
            final boolean update) throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        pstm.setString(1, item.getNazev());
        pstm.setBoolean(2, item.isNespravny());
        pstm.setInt(3, item.getObecKod());
        pstm.setString(4, item.getMluvCharPad2());
        pstm.setString(5, item.getMluvCharPad3());
        pstm.setString(6, item.getMluvCharPad4());
        pstm.setString(7, item.getMluvCharPad5());
        pstm.setString(8, item.getMluvCharPad6());
        pstm.setString(9, item.getMluvCharPad7());
        pstm.setLong(10, item.getIdTransRuian());
        pstmEx.setBoolean(11, item.getZmenaGrafiky());
        pstmEx.setDate(12, item.getPlatiOd());
        pstm.setLong(13, item.getNzIdGlobalni());
        pstm.setObject(14, item.getDefinicniBod());
        pstm.setObject(15, item.getHranice());
        pstm.setInt(16, item.getKod());

        if (update) {
            pstmEx.setDate(17, item.getPlatiOd());
        }
    }

    @Override
    protected void fillExists(final PreparedStatement pstm, final CastObce item)
            throws SQLException {
        pstm.setInt(1, item.getKod());
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Connection con, final CastObce item, final Writer logFile)
            throws IOException, XMLStreamException {
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
                    case "MluvnickeCharakteristiky":
                        Utils.processMluvnickeCharakteristiky(
                                reader, item, NAMESPACE, logFile);
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
                        Utils.printWarningIgnoringElement(logFile, reader);
                }

                break;
            default:
                Utils.printWarningIgnoringElement(logFile, reader);
        }
    }
}
