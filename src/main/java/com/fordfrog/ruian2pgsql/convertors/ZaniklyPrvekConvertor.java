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
import com.fordfrog.ruian2pgsql.containers.ZaniklyPrvek;
import com.fordfrog.ruian2pgsql.utils.Log;
import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.XMLUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Convertor for ZaniklyPrvek element.
 *
 * @author fordfrog
 */
public class ZaniklyPrvekConvertor extends AbstractSaveConvertor<ZaniklyPrvek> {

    /**
     * Namespace of the element.
     */
    private static final String NAMESPACE = Namespaces.VYMENNY_FORMAT_TYPY;
    /**
     * SQL statement for marking AdresniMisto as deleted.
     */
    private static final String SQL_UPDATE_ADRESNI_MISTO =
            "UPDATE rn_adresni_misto SET deleted = true, "
            + "item_timestamp = timezone('utc', now()), id_trans_ruian = ? "
            + "WHERE kod = ? AND id_trans_ruian < ?";
    /**
     * SQL statement for marking CastObce as deleted.
     */
    private static final String SQL_UPDATE_CAST_OBCE =
            "UPDATE rn_cast_obce SET deleted = true, "
            + "item_timestamp = timezone('utc', now()), id_trans_ruian = ? "
            + "WHERE kod = ? AND id_trans_ruian < ?";
    /**
     * SQL statement for marking BonitDilyParcel as deleted.
     */
    private static final String SQL_UPDATE_BONIT_DILY_PARCEL =
            "UPDATE rn_bonit_dily_parcel SET deleted = true "
            + "WHERE parcela_id = ?";
    /**
     * SQL statement for marking DetailniTEA as deleted.
     */
    private static final String SQL_UPDATE_DETAILNI_TEA =
            "UPDATE rn_detailni_tea SET deleted = true WHERE stavobj_kod = ?";
    /**
     * SQL statement for marking Parcela as deleted.
     */
    private static final String SQL_UPDATE_PARCELA = "UPDATE rn_parcela "
            + "SET deleted = true, item_timestamp = timezone('utc', now()), "
            + "id_trans_ruian = ? WHERE id = ? AND id_trans_ruian < ?";
    /**
     * SQL statement for marking StavebniObjekt as deleted.
     */
    private static final String SQL_UPDATE_STAVEBNI_OBJEKT =
            "UPDATE rn_stavebni_objekt SET deleted = true, "
            + "item_timestamp = timezone('utc', now()), id_trans_ruian = ? "
            + "WHERE kod = ? AND id_trans_ruian < ?";
    /**
     * SQL statement for marking Ulice as deleted.
     */
    private static final String SQL_UPDATE_ULICE = "UPDATE rn_ulice "
            + "SET deleted = true, item_timestamp = timezone('utc', now()), "
            + "id_trans_ruian = ? WHERE kod = ? AND id_trans_ruian < ?";
    /**
     * SQL statement for marking ZpusobOchranyObjektu as deleted.
     */
    private static final String SQL_UPDATE_ZPUSOB_OCHRANY_OBJEKTU =
            "UPDATE rn_zpusob_ochrany_objektu SET deleted = true "
            + "WHERE stavobj_kod = ?";
    /**
     * SQL statement for marking ZpusobOchranyPozemku as deleted.
     */
    private static final String SQL_UPDATE_ZPUSOB_OCHRANY_POZEMKU =
            "UPDATE rn_zpusob_ochrany_pozemku SET deleted = true "
            + "WHERE parcela_id = ?";
    /**
     * Prepared statement for marking AdresniMisto as deleted.
     */
    private final PreparedStatement pstmUpdateAdresniMisto;
    /**
     * Prepared statement for marking BonitDilyParcel as deleted.
     */
    private final PreparedStatement pstmUpdateBonitDilyParcel;
    /**
     * Prepared statement for marking CastObce as deleted.
     */
    private final PreparedStatement pstmUpdateCastObce;
    /**
     * Prepared statement for marking DetailniTEA as deleted.
     */
    private final PreparedStatement pstmUpdateDetailniTea;
    /**
     * Prepared statement for marking Parcela as deleted.
     */
    private final PreparedStatement pstmUpdateParcela;
    /**
     * Prepared statement for marking StavebniObjekt as deleted.
     */
    private final PreparedStatement pstmUpdateStavebniObjekt;
    /**
     * Prepared statement for marking Ulice as deleted.
     */
    private final PreparedStatement pstmUpdateUlice;
    /**
     * Prepared statement for marking ZpusobOchranyObjektu as deleted.
     */
    private final PreparedStatement pstmUpdateZpusobOchranyObjektu;
    /**
     * Prepared statement for marking ZpusobOchranyPozemku as deleted.
     */
    private final PreparedStatement pstmUpdateZpusobOchranyPozemku;

    /**
     * Creates new instance of ZaniklyPrvekConvertor.
     *
     * @param con database connection
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    public ZaniklyPrvekConvertor(final Connection con) throws SQLException {
        super(ZaniklyPrvek.class, NAMESPACE, "ZaniklyPrvek", con, null, null,
                null, null, null);

        pstmUpdateAdresniMisto =
                con.prepareStatement(fixSql(SQL_UPDATE_ADRESNI_MISTO));
        pstmUpdateBonitDilyParcel =
                con.prepareStatement(fixSql(SQL_UPDATE_BONIT_DILY_PARCEL));
        pstmUpdateCastObce = con.prepareStatement(fixSql(SQL_UPDATE_CAST_OBCE));
        pstmUpdateDetailniTea =
                con.prepareStatement(fixSql(SQL_UPDATE_DETAILNI_TEA));
        pstmUpdateParcela = con.prepareStatement(fixSql(SQL_UPDATE_PARCELA));
        pstmUpdateStavebniObjekt =
                con.prepareStatement(fixSql(SQL_UPDATE_STAVEBNI_OBJEKT));
        pstmUpdateUlice = con.prepareStatement(fixSql(SQL_UPDATE_ULICE));
        pstmUpdateZpusobOchranyObjektu =
                con.prepareStatement(fixSql(SQL_UPDATE_ZPUSOB_OCHRANY_OBJEKTU));
        pstmUpdateZpusobOchranyPozemku =
                con.prepareStatement(fixSql(SQL_UPDATE_ZPUSOB_OCHRANY_POZEMKU));
    }

    @Override
    protected void fill(final PreparedStatement pstm, final ZaniklyPrvek item,
            final boolean update) throws SQLException {
        // not used
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final ZaniklyPrvek item) throws SQLException {
        // not used
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final ZaniklyPrvek item) throws XMLStreamException, SQLException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "IdTransakce":
                        item.setIdTransakce(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "PrvekId":
                        item.setPrvekId(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "TypPrvkuKod":
                        item.setTypPrvkuKod(reader.getElementText());
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
     * Instead of saving data, it removes specified item from database.
     *
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    @Override
    protected void saveData(final ZaniklyPrvek item) throws SQLException {
        switch (item.getTypPrvkuKod()) {
            case "AD": // AdresniMisto
                deleteAdresniMisto(item);
                break;
            case "CO": // CastObce
                deleteCastObce(item);
                break;
            case "SO": // StavebniObjekt
                deleteStavebniObjekt(item);
                break;
            case "PA": // Parcela
                deleteParcela(item);
                break;
            case "UL": // Ulice
                deleteUlice(item);
                break;
            default:
                Log.write("Ignoring unsupported TypPrvkuKod '"
                        + item.getTypPrvkuKod() + " 'of ZaniklyPrvek");
        }
    }

    /**
     * Deletes AdresniMisto item.
     *
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteAdresniMisto(final ZaniklyPrvek item)
            throws SQLException {
        if (Config.isDryRun()) {
            return;
        }

        pstmUpdateAdresniMisto.clearParameters();
        pstmUpdateAdresniMisto.setLong(1, item.getIdTransakce());
        pstmUpdateAdresniMisto.setInt(2, item.getPrvekId().intValue());
        pstmUpdateAdresniMisto.setLong(3, item.getIdTransakce());
        pstmUpdateAdresniMisto.execute();
    }

    /**
     * Deletes CastObce item.
     *
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteCastObce(final ZaniklyPrvek item)
            throws SQLException {
        if (Config.isDryRun()) {
            return;
        }

        pstmUpdateCastObce.clearParameters();
        pstmUpdateCastObce.setLong(1, item.getIdTransakce());
        pstmUpdateCastObce.setInt(2, item.getPrvekId().intValue());
        pstmUpdateCastObce.setLong(3, item.getIdTransakce());
        pstmUpdateCastObce.execute();
    }

    /**
     * Deletes StavebniObjekt item.
     *
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteStavebniObjekt(final ZaniklyPrvek item)
            throws SQLException {
        if (Config.isDryRun()) {
            return;
        }

        pstmUpdateStavebniObjekt.clearParameters();
        pstmUpdateStavebniObjekt.setLong(1, item.getIdTransakce());
        pstmUpdateStavebniObjekt.setInt(2, item.getPrvekId().intValue());
        pstmUpdateStavebniObjekt.setLong(3, item.getIdTransakce());
        pstmUpdateStavebniObjekt.execute();

        pstmUpdateDetailniTea.clearParameters();
        pstmUpdateDetailniTea.setInt(1, item.getPrvekId().intValue());
        pstmUpdateDetailniTea.execute();

        pstmUpdateZpusobOchranyObjektu.clearParameters();
        pstmUpdateZpusobOchranyObjektu.setInt(1, item.getPrvekId().intValue());
        pstmUpdateZpusobOchranyObjektu.execute();
    }

    /**
     * Deletes Parcela item.
     *
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteParcela(final ZaniklyPrvek item) throws SQLException {
        if (Config.isDryRun()) {
            return;
        }

        pstmUpdateParcela.clearParameters();
        pstmUpdateParcela.setLong(1, item.getIdTransakce());
        pstmUpdateParcela.setInt(2, item.getPrvekId().intValue());
        pstmUpdateParcela.setLong(3, item.getIdTransakce());
        pstmUpdateParcela.execute();

        pstmUpdateZpusobOchranyPozemku.clearParameters();
        pstmUpdateZpusobOchranyPozemku.setLong(1, item.getPrvekId());
        pstmUpdateZpusobOchranyPozemku.execute();

        pstmUpdateBonitDilyParcel.clearParameters();
        pstmUpdateBonitDilyParcel.setLong(1, item.getPrvekId());
        pstmUpdateBonitDilyParcel.execute();
    }

    /**
     * Deletes Ulice item.
     *
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteUlice(final ZaniklyPrvek item) throws SQLException {
        if (Config.isDryRun()) {
            return;
        }

        pstmUpdateUlice.clearParameters();
        pstmUpdateUlice.setLong(1, item.getIdTransakce());
        pstmUpdateUlice.setInt(2, item.getPrvekId().intValue());
        pstmUpdateUlice.setLong(3, item.getIdTransakce());
        pstmUpdateUlice.execute();
    }
}
