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

import com.fordfrog.ruian2pgsql.containers.Parcela;
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
            + "ST_GeomFromGML(?), ST_GeomFromGML(?), ?)";
    /**
     * SQL statement for update of existing item.
     */
    private static final String SQL_UPDATE = "UPDATE rn_parcela "
            + "SET nespravny = ?, katuz_kod = ?, druh_pozemku_kod = ?, "
            + "druh_cislovani_kod = ?, kmenove_cislo = ?, poddeleni_cisla = ?, "
            + "vymera_parcely = ?, id_trans_ruian = ?, "
            + "zpusob_vyu_poz_kod = ?, rizeni_id = ?, plati_od = ?, "
            + "definicni_bod = ST_GeomFromGML(?), hranice = ST_GeomFromGML(?) "
            + "WHERE id = ? AND plati_od < ?";

    /**
     * Creates new instance of ParcelaConvertor.
     */
    public ParcelaConvertor() {
        super(Parcela.class, Namespaces.VYMENNY_FORMAT_TYPY, "Parcela",
                SQL_EXISTS, SQL_INSERT, SQL_UPDATE);
    }

    @Override
    protected void fill(final PreparedStatement pstm, final Parcela item,
            final boolean update) throws SQLException {
        final PreparedStatementEx pstmEx = new PreparedStatementEx(pstm);
        pstmEx.setBoolean(1, item.getNespravny());
        pstm.setInt(2, item.getKatuzKod());
        pstm.setInt(3, item.getDruhPozemkuKod());
        pstm.setInt(4, item.getDruhCislovaniKod());
        pstm.setInt(5, item.getKmenoveCislo());
        pstmEx.setInt(6, item.getPoddeleniCisla());
        pstm.setLong(7, item.getVymeraParcely());
        pstm.setLong(8, item.getIdTransRuian());
        pstmEx.setInt(9, item.getZpusobVyuPozKod());
        pstm.setLong(10, item.getRizeniId());
        pstmEx.setDate(11, item.getPlatiOd());
        pstm.setString(12, item.getDefinicniBod());
        pstm.setString(13, item.getHranice());
        pstm.setLong(14, item.getId());

        if (update) {
            pstmEx.setDate(15, item.getPlatiOd());
        }
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final Parcela item) throws SQLException {
        pstm.setLong(1, item.getId());
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Connection con, final Parcela item, final Writer logFile)
            throws XMLStreamException, SQLException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "BonitovaneDily":
                        new CollectionConvertor(Namespaces.PARCELA_INT_TYPY,
                                "BonitovaneDily", Namespaces.COMMON_TYPY,
                                "BonitovanyDil",
                                new BonitovanyDilConvertor(item.getId())).
                                convert(reader, con, logFile);
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
                                reader, con, item, NAMESPACE, logFile);
                        break;
                    case "Id":
                        item.setId(Long.parseLong(reader.getElementText()));
                        deleteBonitovateDily(con, item.getId());
                        deleteZpusobyOchranyPozemku(con, item.getId());
                        break;
                    case "IdTransakce":
                        item.setIdTransRuian(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "KatastralniUzemi":
                        item.setKatuzKod(Utils.getKatastralniUzemiKod(
                                reader, NAMESPACE, logFile));
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
                        new CollectionConvertor(Namespaces.PARCELA_INT_TYPY,
                                "ZpusobyOchranyPozemku", Namespaces.COMMON_TYPY,
                                "ZpusobOchrany",
                                new ZpusobOchranyPozemkuConvertor(item.getId())).
                                convert(reader, con, logFile);
                        break;
                    case "ZpusobyVyuzitiPozemku":
                        item.setZpusobVyuPozKod(
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

    /**
     * Deletes BonitovaneDily that belong to this Parcela.
     *
     * @param con       database connection
     * @param parcelaId Parcela id
     *
     * @throws SQLException Thrown if problem occurred while deleting the items.
     */
    private void deleteBonitovateDily(final Connection con,
            final Long parcelaId) throws SQLException {
        try (final PreparedStatement pstm = con.prepareStatement(
                        "DELETE FROM rn_bonit_dily_parcel "
                        + "WHERE parcela_id = ?")) {
            pstm.setLong(1, parcelaId);
            pstm.execute();
        }
    }

    /**
     * Deletes ZpusobyOchranyPozemku that belong to this Parcela.
     *
     * @param con       database connection
     * @param parcelaId Parcela id
     *
     * @throws SQLException Thrown if problem occurred while deleting the items.
     */
    private void deleteZpusobyOchranyPozemku(final Connection con,
            final Long parcelaId) throws SQLException {
        try (final PreparedStatement pstm = con.prepareStatement(
                        "DELETE FROM rn_zpusob_ochrany_pozemku "
                        + "WHERE parcela_id = ?")) {
            pstm.setLong(1, parcelaId);
            pstm.execute();
        }
    }
}
