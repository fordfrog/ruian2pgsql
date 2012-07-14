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

import com.fordfrog.ruian2pgsql.containers.KatastralniUzemi;
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
 * Convertor for KatastralniUzemi element.
 *
 * @author fordfrog
 */
public class KatastralniUzemiConvertor
        extends AbstractSaveConvertor<KatastralniUzemi> {

    /**
     * Namespace of the element.
     */
    private static final String NAMESPACE = Namespaces.KAT_UZ_INT_TYPY;
    /**
     * SQL statement for testing whether the item already exists.
     */
    private static final String SQL_EXISTS =
            "SELECT 1 FROM rn_katastralni_uzemi WHERE kod = ?";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT = "INSERT INTO rn_katastralni_uzemi "
            + "(nazev, nespravny, obec_kod, ma_dkm, mluv_char_pad_2, "
            + "mluv_char_pad_3, mluv_char_pad_4, mluv_char_pad_5, "
            + "mluv_char_pad_6, mluv_char_pad_7, id_trans_ruian, plati_od, "
            + "nz_id_globalni, rizeni_id, definicni_bod, hranice, kod) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    /**
     * SQL statement for updating of existing item.
     */
    private static final String SQL_UPDATE = "UPDATE rn_katastralni_uzemi "
            + "SET nazev = ?, nespravny = ?, obec_kod = ?, ma_dkm = ?, "
            + "mluv_char_pad_2 = ?, mluv_char_pad_3 = ?, mluv_char_pad_4 = ?, "
            + "mluv_char_pad_5 = ?, mluv_char_pad_6 = ?, mluv_char_pad_7 = ?, "
            + "id_trans_ruian = ?, plati_od = ?, nz_id_globalni = ?, "
            + "rizeni_id = ?, definicni_bod = ?, hranice = ? "
            + "WHERE kod = ? AND plati_od < ?";

    /**
     * Creates new instance of KatastralniUzemiConvertor.
     */
    public KatastralniUzemiConvertor() {
        super(KatastralniUzemi.class, Namespaces.VYMENNY_FORMAT_TYPY,
                "KatastralniUzemi", SQL_EXISTS, SQL_INSERT, SQL_UPDATE);
    }

    @Override
    protected void fill(final PreparedStatement pstm,
            final KatastralniUzemi item, final boolean update)
            throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        pstm.setString(1, item.getNazev());
        pstmEx.setBoolean(2, item.getNespravny());
        pstm.setInt(3, item.getObecKod());
        pstm.setBoolean(4, item.isMaDkm());
        pstm.setString(5, item.getMluvCharPad2());
        pstm.setString(6, item.getMluvCharPad3());
        pstm.setString(7, item.getMluvCharPad4());
        pstm.setString(8, item.getMluvCharPad5());
        pstm.setString(9, item.getMluvCharPad6());
        pstm.setString(10, item.getMluvCharPad7());
        pstm.setLong(11, item.getIdTransRuian());
        pstmEx.setDate(12, item.getPlatiOd());
        pstm.setLong(13, item.getNzIdGlobalni());
        pstmEx.setLong(14, item.getRizeniId());
        pstm.setObject(15, item.getDefinicniBod());
        pstm.setObject(16, item.getHranice());
        pstm.setInt(17, item.getKod());

        if (update) {
            pstmEx.setDate(18, item.getPlatiOd());
        }
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final KatastralniUzemi item) throws SQLException {
        pstm.setInt(1, item.getKod());
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Connection con, final KatastralniUzemi item,
            final Writer logFile) throws IOException, XMLStreamException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "ExistujeDigitalniMapa":
                        item.setMaDkm("A".equals(reader.getElementText()));
                        break;
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
                    case "RizeniId":
                        item.setRizeniId(
                                Long.parseLong(reader.getElementText()));
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
