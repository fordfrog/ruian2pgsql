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
import com.fordfrog.ruian2pgsql.containers.Parcela;
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
 * Convertor for Parcela element.
 *
 * @author fordfrog
 */
public class ParcelaConvertor extends AbstractSaveConvertor<Parcela> {

    /**
     * Namespace of the element.
     */
    private static final String NAMESPACE = Namespaces.PARCELA_INT_TYPY;
    /**
     * SQL statement for checking whether the item already exists.
     */
    private static final String SQL_EXISTS =
            "SELECT 1 FROM rn_parcela WHERE id = ?";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT = "INSERT INTO rn_parcela "
            + "(nespravny, katuz_kod, druh_pozemku_kod, druh_cislovani_kod, "
            + "kmenove_cislo, poddeleni_cisla, vymera_parcely, id_trans_ruian, "
            + "zpusob_vyu_poz_kod, rizeni_id, plati_od, definicni_bod, "
            + "hranice, id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
            + "%FUNCTION%(?), %FUNCTION%(?), ?)";
    /**
     * SQL statement for update of existing item.
     */
    private static final String SQL_UPDATE = "UPDATE rn_parcela "
            + "SET nespravny = ?, katuz_kod = ?, druh_pozemku_kod = ?, "
            + "druh_cislovani_kod = ?, kmenove_cislo = ?, poddeleni_cisla = ?, "
            + "vymera_parcely = ?, id_trans_ruian = ?, "
            + "zpusob_vyu_poz_kod = ?, rizeni_id = ?, plati_od = ?, "
            + "definicni_bod = %FUNCTION%(?), hranice = %FUNCTION%(?), "
            + "item_timestamp = timezone('utc', now()), deleted = false "
            + "WHERE id = ? AND id_trans_ruian <= ?";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT_NO_GIS = "INSERT INTO rn_parcela "
            + "(nespravny, katuz_kod, druh_pozemku_kod, druh_cislovani_kod, "
            + "kmenove_cislo, poddeleni_cisla, vymera_parcely, id_trans_ruian, "
            + "zpusob_vyu_poz_kod, rizeni_id, plati_od, id) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    /**
     * SQL statement for update of existing item.
     */
    private static final String SQL_UPDATE_NO_GIS = "UPDATE rn_parcela "
            + "SET nespravny = ?, katuz_kod = ?, druh_pozemku_kod = ?, "
            + "druh_cislovani_kod = ?, kmenove_cislo = ?, poddeleni_cisla = ?, "
            + "vymera_parcely = ?, id_trans_ruian = ?, "
            + "zpusob_vyu_poz_kod = ?, rizeni_id = ?, plati_od = ?, "
            + "item_timestamp = timezone('utc', now()), deleted = false "
            + "WHERE id = ? AND id_trans_ruian <= ?";
    /**
     * SQL statement for deletion of BonitovaneDily.
     */
    private static final String SQL_DELETE_BONITOVANE_DILY =
            "DELETE FROM rn_bonit_dily_parcel WHERE parcela_id = ?";
    /**
     * SQL statement for deletion of ZpusobyOchranyPozemku.
     */
    private static final String SQL_DELETE_ZPUSOBY_OCHRANY_POZEMKU =
            "DELETE FROM rn_zpusob_ochrany_pozemku WHERE parcela_id = ?";
    /**
     * Prepared statement for deletion of BonitovaneDily.
     */
    private final PreparedStatement pstmDeleteBonitovaneDily;
    /**
     * Prepared statement for deletion of ZpusobyOchranyPozemku.
     */
    private final PreparedStatement pstmDeleteZpusobyOchranyPozemku;
    /**
     * Convertor for BonitovaneDily.
     */
    private final Convertor convertorBonitovaneDily;
    /**
     * Convertor for ZpusobyOchranyPozemku.
     */
    private final Convertor convertorZpusobyOchranyPozemku;
    /**
     * Convertor for BonitovanyDil.
     */
    private final BonitovanyDilConvertor bonitovanyDilConvertor;
    /**
     * Convertor for ZpusobOchranyPozemku.
     */
    private final ZpusobOchranyPozemkuConvertor zpusobOchranyPozemkuConvertor;

    /**
     * Creates new instance of ParcelaConvertor.
     *
     * @param con database connection
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    public ParcelaConvertor(final Connection con) throws SQLException {
        super(Parcela.class, Namespaces.VYMENNY_FORMAT_TYPY, "Parcela", con,
                SQL_EXISTS, SQL_INSERT, SQL_UPDATE, SQL_INSERT_NO_GIS,
                SQL_UPDATE_NO_GIS);

        pstmDeleteBonitovaneDily =
                con.prepareStatement(SQL_DELETE_BONITOVANE_DILY);
        pstmDeleteZpusobyOchranyPozemku =
                con.prepareStatement(SQL_DELETE_ZPUSOBY_OCHRANY_POZEMKU);

        bonitovanyDilConvertor = new BonitovanyDilConvertor(con);
        zpusobOchranyPozemkuConvertor = new ZpusobOchranyPozemkuConvertor(con);

        convertorBonitovaneDily = new CollectionConvertor(
                Namespaces.PARCELA_INT_TYPY, "BonitovaneDily",
                Namespaces.COMMON_TYPY, "BonitovanyDil",
                bonitovanyDilConvertor);
        convertorZpusobyOchranyPozemku = new CollectionConvertor(
                Namespaces.PARCELA_INT_TYPY, "ZpusobyOchranyPozemku",
                Namespaces.COMMON_TYPY, "ZpusobOchrany",
                zpusobOchranyPozemkuConvertor);
    }

    @Override
    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    protected void fill(final PreparedStatement pstm, final Parcela item,
            final boolean update) throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        int index = 1;
        pstmEx.setBoolean(index++, item.getNespravny());
        pstm.setInt(index++, item.getKatuzKod());
        pstm.setInt(index++, item.getDruhPozemkuKod());
        pstm.setInt(index++, item.getDruhCislovaniKod());
        pstm.setInt(index++, item.getKmenoveCislo());
        pstmEx.setInt(index++, item.getPoddeleniCisla());
        pstm.setLong(index++, item.getVymeraParcely());
        pstm.setLong(index++, item.getIdTransRuian());
        pstmEx.setInt(index++, item.getZpusobVyuPozKod());
        pstm.setLong(index++, item.getRizeniId());
        pstmEx.setDate(index++, item.getPlatiOd());

        if (!Config.isNoGis()) {
            pstm.setString(index++, item.getDefinicniBod());
            pstm.setString(index++, item.getHranice());
        }

        pstm.setLong(index++, item.getId());

        if (update) {
            pstm.setLong(index++, item.getIdTransRuian());
        }
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final Parcela item) throws SQLException {
        pstm.setLong(1, item.getId());
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Parcela item) throws XMLStreamException,
            SQLException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "BonitovaneDily":
                        bonitovanyDilConvertor.setParcelaId(item.getId());
                        convertorBonitovaneDily.convert(reader);
                        break;
                    case "DruhCislovaniKod":
                        item.setDruhCislovaniKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "DruhPozemkuKod":
                        item.setDruhPozemkuKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "Geometrie":
                        Utils.processGeometrie(
                                reader, getConnection(), item, NAMESPACE);
                        break;
                    case "Id":
                        item.setId(Long.parseLong(reader.getElementText()));
                        deleteBonitovateDily(item.getId());
                        deleteZpusobyOchranyPozemku(item.getId());
                        break;
                    case "IdTransakce":
                        item.setIdTransRuian(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "KatastralniUzemi":
                        item.setKatuzKod(Utils.getKatastralniUzemiKod(
                                reader, NAMESPACE));
                        break;
                    case "KmenoveCislo":
                        item.setKmenoveCislo(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "Nespravny":
                        item.setNespravny(
                                Boolean.valueOf(reader.getElementText()));
                        break;
                    case "PlatiOd":
                        item.setPlatiOd(
                                Utils.parseTimestamp(reader.getElementText()));
                        break;
                    case "PododdeleniCisla":
                        item.setPoddeleniCisla(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "RizeniId":
                        item.setRizeniId(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "VymeraParcely":
                        item.setVymeraParcely(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "ZpusobyOchranyPozemku":
                        zpusobOchranyPozemkuConvertor.setParcelaId(
                                item.getId());
                        convertorZpusobyOchranyPozemku.convert(reader);
                        break;
                    case "ZpusobyVyuzitiPozemku":
                        item.setZpusobVyuPozKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    default:
                        XMLUtils.processUnsupported(reader);
                }

                break;
            default:
                XMLUtils.processUnsupported(reader);
        }
    }

    /**
     * Deletes BonitovaneDily that belong to this Parcela.
     *
     * @param parcelaId Parcela id
     *
     * @throws SQLException Thrown if problem occurred while deleting the items.
     */
    private void deleteBonitovateDily(final Long parcelaId)
            throws SQLException {
        if (Config.isDryRun()) {
            return;
        }

        pstmDeleteBonitovaneDily.clearParameters();
        pstmDeleteBonitovaneDily.setLong(1, parcelaId);
        pstmDeleteBonitovaneDily.execute();
    }

    /**
     * Deletes ZpusobyOchranyPozemku that belong to this Parcela.
     *
     * @param parcelaId Parcela id
     *
     * @throws SQLException Thrown if problem occurred while deleting the items.
     */
    private void deleteZpusobyOchranyPozemku(final Long parcelaId)
            throws SQLException {
        if (Config.isDryRun()) {
            return;
        }

        pstmDeleteZpusobyOchranyPozemku.clearParameters();
        pstmDeleteZpusobyOchranyPozemku.setLong(1, parcelaId);
        pstmDeleteZpusobyOchranyPozemku.execute();
    }
}
