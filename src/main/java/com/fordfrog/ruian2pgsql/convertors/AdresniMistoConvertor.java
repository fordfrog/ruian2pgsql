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
import com.fordfrog.ruian2pgsql.containers.AdresniMisto;
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
 * Convertor for AdresniMisto element.
 *
 * @author fordfrog
 */
public class AdresniMistoConvertor extends AbstractSaveConvertor<AdresniMisto> {

    /**
     * Namespace of the element.
     */
    private static final String NAMESPACE = Namespaces.ADR_MISTO_INT_TYPY;
    /**
     * SQL statement for checking whether the item exists.
     */
    private static final String SQL_EXISTS =
            "SELECT 1 FROM rn_adresni_misto WHERE kod = ?";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT = "INSERT INTO rn_adresni_misto "
            + "(nespravny, adrp_psc, ulice_kod, vo_kod, stavobj_kod, cislo_domovni, "
            + "cislo_orientacni_hodnota, cislo_orientacni_pismeno, "
            + "id_trans_ruian, plati_od, zmena_grafiky, nz_id_globalni, "
            + "definicni_bod, zachranka, hasici, kod) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, %FUNCTION%(?), "
            + "%FUNCTION%(?), %FUNCTION%(?), ?)";
    /**
     * SQL statement for update of existing item.
     */
    private static final String SQL_UPDATE = "UPDATE rn_adresni_misto "
            + "SET nespravny = ?, adrp_psc = ?, ulice_kod = ?, vo_kod = ?, "
            + "stavobj_kod = ?, cislo_domovni = ?, "
            + "cislo_orientacni_hodnota = ?, cislo_orientacni_pismeno = ?, "
            + "id_trans_ruian = ?, plati_od = ?, zmena_grafiky = ?, "
            + "nz_id_globalni = ?, definicni_bod = %FUNCTION%(?), "
            + "zachranka = %FUNCTION%(?), hasici = %FUNCTION%(?), "
            + "item_timestamp = timezone('utc', now()), deleted = false "
            + "WHERE kod = ? AND id_trans_ruian <= ?";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT_NO_GIS =
            "INSERT INTO rn_adresni_misto "
            + "(nespravny, adrp_psc, ulice_kod, vo_kod, stavobj_kod, cislo_domovni, "
            + "cislo_orientacni_hodnota, cislo_orientacni_pismeno, "
            + "id_trans_ruian, plati_od, zmena_grafiky, nz_id_globalni, kod) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    /**
     * SQL statement for update of existing item.
     */
    private static final String SQL_UPDATE_NO_GIS = "UPDATE rn_adresni_misto "
            + "SET nespravny = ?, adrp_psc = ?, ulice_kod = ?, vo_kod = ?, "
            + "stavobj_kod = ?, cislo_domovni = ?, "
            + "cislo_orientacni_hodnota = ?, cislo_orientacni_pismeno = ?, "
            + "id_trans_ruian = ?, plati_od = ?, zmena_grafiky = ?, "
            + "nz_id_globalni = ?, item_timestamp = timezone('utc', now()), "
            + "deleted = false "
            + "WHERE kod = ? AND id_trans_ruian <= ?";

    /**
     * Creates new instance of AdresniMistoConvertor.
     *
     * @param con database connection
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    public AdresniMistoConvertor(final Connection con) throws SQLException {
        super(AdresniMisto.class, Namespaces.VYMENNY_FORMAT_TYPY,
                "AdresniMisto", con, SQL_EXISTS, SQL_INSERT, SQL_UPDATE,
                SQL_INSERT_NO_GIS, SQL_UPDATE_NO_GIS);
    }

    @Override
    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    protected void fill(final PreparedStatement pstm, final AdresniMisto item,
            final boolean update) throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        int index = 1;
        pstmEx.setBoolean(index++, item.getNespravny());
        pstmEx.setInt(index++, item.getAdrpPsc());
        pstmEx.setInt(index++, item.getUliceKod());
        pstmEx.setInt(index++, item.getVoKod());
        pstm.setInt(index++, item.getStavobjKod());
        pstm.setInt(index++, item.getCisloDomovni());
        pstmEx.setInt(index++, item.getCisloOrientacniHodnota());
        pstm.setString(index++, item.getCisloOrientacniPismeno());
        pstm.setLong(index++, item.getIdTransRuian());
        pstmEx.setDate(index++, item.getPlatiOd());
        pstmEx.setBoolean(index++, item.getZmenaGrafiky());
        pstm.setLong(index++, item.getNzIdGlobalni());

        if (!Config.isNoGis()) {
            pstm.setString(index++, item.getDefinicniBod());
            pstm.setString(index++, item.getZachranka());
            pstm.setString(index++, item.getHasici());
        }

        pstm.setInt(index++, item.getKod());

        if (update) {
            pstm.setLong(index++, item.getIdTransRuian());
        }
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final AdresniMisto item) throws SQLException {
        pstm.setInt(1, item.getKod());
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final AdresniMisto item) throws XMLStreamException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "CisloDomovni":
                        item.setCisloDomovni(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "CisloOrientacni":
                        item.setCisloOrientacniHodnota(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "CisloOrientacniPismeno":
                        item.setCisloOrientacniPismeno(reader.getElementText());
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
                    case "Kod":
                        item.setKod(Integer.parseInt(reader.getElementText()));
                        break;
                    case "Nespravny":
                        item.setNespravny(
                                Boolean.valueOf(reader.getElementText()));
                        break;
                    case "PlatiOd":
                        item.setPlatiOd(
                                Utils.parseTimestamp(reader.getElementText()));
                        break;
                    case "Psc":
                        item.setAdrpPsc(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "StavebniObjekt":
                        item.setStavobjKod(
                                Utils.getStavebniObjektKod(reader, NAMESPACE));
                        break;
                    case "Ulice":
                        item.setUliceKod(Utils.getUliceKod(reader, NAMESPACE));
                        break;
                    case "VOKod":
                        item.setVoKod(Integer.parseInt(reader.getElementText()));
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
