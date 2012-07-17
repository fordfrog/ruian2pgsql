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

import com.fordfrog.ruian2pgsql.containers.AdresniMisto;
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
 * Convertor for AdresniMisto element.
 *
 * @author fordfrog
 */
public class AdresniMistoConvertor extends AbstractSaveConvertor<AdresniMisto> {

    private static final String NAMESPACE = Namespaces.ADR_MISTO_INT_TYPY;
    private static final String SQL_EXISTS =
            "SELECT 1 FROM rn_adresni_misto WHERE kod = ?";
    private static final String SQL_INSERT = "INSERT INTO rn_adresni_misto "
            + "(nespravny, adrp_psc, ulice_kod, stavobj_kod, cislo_domovni, "
            + "cislo_orientacni_hodnota, cislo_orientacni_pismeno, "
            + "id_trans_ruian, plati_od, zmena_grafiky, nz_id_globalni, "
            + "definicni_bod, zachranka, hasici, kod) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromGML(?), "
            + "ST_GeomFromGML(?), ST_GeomFromGML(?), ?)";
    private static final String SQL_UPDATE = "UPDATE rn_adresni_misto "
            + "SET nespravny = ?, adrp_psc = ?, ulice_kod = ?, "
            + "stavobj_kod = ?, cislo_domovni = ?, "
            + "cislo_orientacni_hodnota = ?, cislo_orientacni_pismeno = ?, "
            + "id_trans_ruian = ?, plati_od = ?, zmena_grafiky = ?, "
            + "nz_id_globalni = ?, definicni_bod = ST_GeomFromGML(?), "
            + "zachranka = ST_GeomFromGML(?), hasici = ST_GeomFromGML(?), "
            + "item_timestamp = timezone('utc', now()), deleted = false "
            + "WHERE kod = ? AND id_trans_ruian < ?";

    public AdresniMistoConvertor() {
        super(AdresniMisto.class, Namespaces.VYMENNY_FORMAT_TYPY,
                "AdresniMisto", SQL_EXISTS, SQL_INSERT, SQL_UPDATE);
    }

    @Override
    protected void fill(final PreparedStatement pstm, final AdresniMisto item,
            final boolean update) throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        pstmEx.setBoolean(1, item.getNespravny());
        pstmEx.setInt(2, item.getAdrpPsc());
        pstmEx.setInt(3, item.getUliceKod());
        pstm.setInt(4, item.getStavobjKod());
        pstm.setInt(5, item.getCisloDomovni());
        pstmEx.setInt(6, item.getCisloOrientacniHodnota());
        pstm.setString(7, item.getCisloOrientacniPismeno());
        pstm.setLong(8, item.getIdTransRuian());
        pstmEx.setDate(9, item.getPlatiOd());
        pstmEx.setBoolean(10, item.getZmenaGrafiky());
        pstm.setLong(11, item.getNzIdGlobalni());
        pstm.setString(12, item.getDefinicniBod());
        pstm.setString(13, item.getZachranka());
        pstm.setString(14, item.getHasici());
        pstm.setInt(15, item.getKod());

        if (update) {
            pstm.setLong(16, item.getIdTransRuian());
        }
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final AdresniMisto item) throws SQLException {
        pstm.setInt(1, item.getKod());
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Connection con, final AdresniMisto item, final Writer logFile)
            throws XMLStreamException {
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
                        item.setStavobjKod(Utils.getStavebniObjektKod(
                                reader, NAMESPACE, logFile));
                        break;
                    case "Ulice":
                        item.setUliceKod(
                                Utils.getUliceKod(reader, NAMESPACE, logFile));
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
