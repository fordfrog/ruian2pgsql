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

import com.fordfrog.ruian2pgsql.Config;
import com.fordfrog.ruian2pgsql.containers.Zsj;
import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.PreparedStatementEx;
import com.fordfrog.ruian2pgsql.utils.Utils;
import com.fordfrog.ruian2pgsql.utils.XMLUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Convertor for Zsj element.
 *
 * @author fordfrog
 */
public class ZsjConvertor extends AbstractSaveConvertor<Zsj> {

    /**
     * Namespace of the element.
     */
    private static final String NAMESPACE = Namespaces.ZSJ_INT_TYPY;
    /**
     * SQL statement for checking whether the item already exists.
     */
    private static final String SQL_EXISTS =
            "SELECT 1 from rn_zsj WHERE kod = ?";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT = "INSERT INTO rn_zsj "
            + "(nazev, nespravny, katuz_kod, charakter_zsj_kod, "
            + "mluv_char_pad_2, mluv_char_pad_3, mluv_char_pad_4, "
            + "mluv_char_pad_5, mluv_char_pad_6, mluv_char_pad_7, "
            + "vymera, plati_od, zmena_grafiky, nz_id_globalni, "
            + "id_trans_ruian, definicni_bod, hranice, datum_vzniku, kod) VALUES (?, ?, ?, "
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, %FUNCTION%(?), "
            + "%FUNCTION%(?), ?, ?)";
    /**
     * SQL statement for update of existing item.
     */
    private static final String SQL_UPDATE = "UPDATE rn_zsj "
            + "SET nazev = ?, nespravny = ?, katuz_kod = ?, "
            + "charakter_zsj_kod = ?, mluv_char_pad_2 = ?, "
            + "mluv_char_pad_3 = ?, mluv_char_pad_4 = ?, mluv_char_pad_5 = ?, "
            + "mluv_char_pad_6 = ?, mluv_char_pad_7 = ?, vymera = ?, "
            + "plati_od = ?, zmena_grafiky = ?, nz_id_globalni = ?, "
            + "id_trans_ruian = ?, definicni_bod = %FUNCTION%(?), "
            + "hranice = %FUNCTION%(?), datum_vzniku = ?, "
            + "item_timestamp = timezone('utc', now()), deleted = false "
            + "WHERE kod = ? AND id_trans_ruian <= ?";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT_NO_GIS = "INSERT INTO rn_zsj "
            + "(nazev, nespravny, katuz_kod, charakter_zsj_kod, "
            + "mluv_char_pad_2, mluv_char_pad_3, mluv_char_pad_4, "
            + "mluv_char_pad_5, mluv_char_pad_6, mluv_char_pad_7, "
            + "vymera, plati_od, zmena_grafiky, nz_id_globalni, "
            + "id_trans_ruian, datum_vzniku, kod) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    /**
     * SQL statement for update of existing item.
     */
    private static final String SQL_UPDATE_NO_GIS = "UPDATE rn_zsj "
            + "SET nazev = ?, nespravny = ?, katuz_kod = ?, "
            + "charakter_zsj_kod = ?, mluv_char_pad_2 = ?, "
            + "mluv_char_pad_3 = ?, mluv_char_pad_4 = ?, mluv_char_pad_5 = ?, "
            + "mluv_char_pad_6 = ?, mluv_char_pad_7 = ?, vymera = ?, "
            + "plati_od = ?, zmena_grafiky = ?, nz_id_globalni = ?, "
            + "id_trans_ruian = ?, datum_vzniku = ?, item_timestamp = timezone('utc', now()), "
            + "deleted = false "
            + "WHERE kod = ? AND id_trans_ruian <= ?";

    /**
     * Creates new instance of ZsjConvertor.
     *
     * @param con database connection
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    public ZsjConvertor(final Connection con) throws SQLException {
        super(Zsj.class, Namespaces.VYMENNY_FORMAT_TYPY, "Zsj", con, SQL_EXISTS,
                SQL_INSERT, SQL_UPDATE, SQL_INSERT_NO_GIS, SQL_UPDATE_NO_GIS);
    }

    @Override
    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    protected void fill(final PreparedStatement pstm, final Zsj item,
            final boolean update) throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        int index = 1;
        pstm.setString(index++, item.getNazev());
        pstmEx.setBoolean(index++, item.getNespravny());
        pstm.setInt(index++, item.getKatuzKod());
        pstm.setInt(index++, item.getCharakterZsjKod());
        pstm.setString(index++, item.getMluvCharPad2());
        pstm.setString(index++, item.getMluvCharPad3());
        pstm.setString(index++, item.getMluvCharPad4());
        pstm.setString(index++, item.getMluvCharPad5());
        pstm.setString(index++, item.getMluvCharPad6());
        pstm.setString(index++, item.getMluvCharPad7());
        pstmEx.setLong(index++, item.getVymera());
        pstmEx.setDate(index++, item.getPlatiOd());
        pstmEx.setBoolean(index++, item.getZmenaGrafiky());
        pstm.setLong(index++, item.getNzIdGlobalni());
        pstm.setLong(index++, item.getIdTransRuian());

        if (!Config.isNoGis()) {
            pstm.setString(index++, item.getDefinicniBod());
            pstm.setString(index++, item.getHranice());
        }

        pstmEx.setDate(index++, item.getDatumVzniku());
        pstm.setInt(index++, item.getKod());

        if (update) {
            pstm.setLong(index++, item.getIdTransRuian());
        }
    }

    @Override
    protected void fillExists(final PreparedStatement pstm, final Zsj item)
            throws SQLException {
        pstm.setInt(1, item.getKod());
    }

    @Override
    protected void processElement(final XMLStreamReader reader, final Zsj item)
            throws XMLStreamException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "CharakterZsjKod":
                        item.setCharakterZsjKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "DatumVzniku":
                        item.setDatumVzniku(
                                Utils.parseTimestamp(reader.getElementText()));
                        break;
                    case "Geometrie":
                        Utils.processGeometrie(
                                reader, getConnection(), item, NAMESPACE);
                        break;
                    case "GlobalniIdNavrhuZmeny":
                        item.setNzIdGlobalni(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "IdTransakce":
                        item.setIdTransRuian(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "KatastralniUzemi":
                        item.setKatuzKod(Utils.getKatastralniUzemiKod(
                                reader, NAMESPACE));
                        break;
                    case "Kod":
                        item.setKod(Integer.parseInt(reader.getElementText()));
                        break;
                    case "MluvnickeCharakteristiky":
                        Utils.processMluvnickeCharakteristiky(
                                reader, item, NAMESPACE);
                        break;
                    case "Nazev":
                        item.setNazev(reader.getElementText());
                        break;
                    case "Nespravny":
                        item.setNespravny(
                                Boolean.valueOf(reader.getElementText()));
                        break;
                    case "PlatiOd":
                        item.setPlatiOd(
                                Utils.parseTimestamp(reader.getElementText()));
                        break;
                    case "Vymera":
                        item.setVymera(Long.parseLong(reader.getElementText()));
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
