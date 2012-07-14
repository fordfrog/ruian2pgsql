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

import com.fordfrog.ruian2pgsql.containers.DetailniTEA;
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
 * Convertor for DetailniTEA element.
 *
 * @author fordfrog
 */
public class DetailniTEAConvertor extends AbstractSaveConvertor<DetailniTEA> {

    /**
     * Namespace of the element.
     */
    private static final String NAMESPACE = Namespaces.STAV_OBJ_INT_TYPY;
    /**
     * SQL statement for checking whether the item exists. We always insert the
     * items as new because they are children of StavebniObjekt element and
     * their live is bound to the parent element.
     */
    private static final String SQL_EXISTS =
            "SELECT 1 FROM rn_detailni_tea WHERE stavobj_kod IS NULL";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT = "INSERT INTO rn_detailni_tea "
            + "(kod, stavobj_kod, adresni_misto_kod, nespravny, pocet_bytu,"
            + " pocet_podlazi, druh_konstrukce_kod, pripoj_kanal_sit_kod, "
            + "pripoj_plyn_kod, pripoj_vodovod_kod, pripoj_el_energie, "
            + "zpusob_vytapeni_kod, id_trans_ruian, plati_od, nz_id_globalni) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    /**
     * Id of parent StavebniObjekt.
     */
    private final int stavebniObjektId;

    /**
     * Creates new instance of ZpusobOchranyObjektuConvertor.
     *
     * @param stavebniObjektId {@link #stavebniObjektId}
     */
    public DetailniTEAConvertor(final int stavebniObjektId) {
        super(DetailniTEA.class, NAMESPACE, "DetailniTEA", SQL_EXISTS,
                SQL_INSERT, null);

        this.stavebniObjektId = stavebniObjektId;
    }

    @Override
    protected void fill(final PreparedStatement pstm, final DetailniTEA item,
            final boolean update) throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        pstmEx.setInt(1, item.getKod());
        pstm.setInt(2, stavebniObjektId);
        pstmEx.setInt(3, item.getAdresniMistoKod());
        pstmEx.setBoolean(4, item.getNespravny());
        pstmEx.setInt(5, item.getPocetBytu());
        pstmEx.setInt(6, item.getPocetPodlazi());
        pstmEx.setInt(7, item.getDruhKonstrukceKod());
        pstmEx.setInt(8, item.getPripojKanalSitKod());
        pstmEx.setInt(9, item.getPripojPlynKod());
        pstmEx.setInt(10, item.getPripojVodovodKod());
        pstmEx.setBoolean(11, item.getPripojElEnergie());
        pstmEx.setInt(12, item.getZpusobVytapeniKod());
        pstmEx.setLong(13, item.getIdTransRuian());
        pstmEx.setDate(14, item.getPlatiOd());
        pstmEx.setLong(15, item.getNzIdGlobalni());
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final DetailniTEA item) throws SQLException {
        // we do not set any parameters as we always return empty result set
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Connection con, final DetailniTEA item, final Writer logFile)
            throws XMLStreamException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "AdresniMistoKod":
                        item.setAdresniMistoKod(Utils.getAdresniMistoKod(
                                reader, NAMESPACE, logFile));
                        break;
                    case "DruhKonstrukceKod":
                        item.setDruhKonstrukceKod(
                                Integer.parseInt(reader.getElementText()));
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
                    case "PocetBytu":
                        item.setPocetBytu(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "PocetPodlazi":
                        item.setPocetPodlazi(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "PripojeniKanalizaceKod":
                        item.setPripojKanalSitKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "PripojeniPlynKod":
                        item.setPripojPlynKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "PripojeniVodovodKod":
                        item.setPripojVodovodKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "ZpusobVytapeniKod":
                        item.setZpusobVytapeniKod(
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
