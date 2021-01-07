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

    private static final String SQL_STANDARD_ITEM_UPDATE_TEMPLATE =
            "UPDATE %s SET deleted = true, "
            + "item_timestamp = timezone('utc', now()), id_trans_ruian = ? "
            + "WHERE kod = ? AND id_trans_ruian <= ?";
    /**
     * SQL statement for marking Stat as deleted.
     */
    private static final String SQL_UPDATE_STAT =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_stat");
    /**
     * SQL statement for marking RegionSoudrznosti as deleted.
     */
    private static final String SQL_UPDATE_REGION_SOUDRZNOSTI =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_region_soudrznosti");
    /**
     * SQL statement for marking Kraj as deleted.
     */
    private static final String SQL_UPDATE_KRAJ =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_kraj_1960");
    /**
     * SQL statement for marking Vusc as deleted.
     */
    private static final String SQL_UPDATE_VUSC =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_vusc");
    /**
     * SQL statement for marking Okres as deleted.
     */
    private static final String SQL_UPDATE_OKRES =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_okres");
    /**
     * SQL statement for marking Orp as deleted.
     */
    private static final String SQL_UPDATE_ORP =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_orp");
    /**
     * SQL statement for marking Pou as deleted.
     */
    private static final String SQL_UPDATE_POU =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_pou");
    /**
     * SQL statement for marking Obec as deleted.
     */
    private static final String SQL_UPDATE_OBEC =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_obec");
    /**
     * SQL statement for marking SpravniObvod as deleted.
     */
    private static final String SQL_UPDATE_SPRAVNI_OBVOD =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_spravni_obvod");
    /**
     * SQL statement for marking Mop as deleted.
     */
    private static final String SQL_UPDATE_MOP =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_mop");
    /**
     * SQL statement for marking Momc as deleted.
     */
    private static final String SQL_UPDATE_MOMC =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_momc");
    /**
     * SQL statement for marking KatastralniUzemi as deleted.
     */
    private static final String SQL_UPDATE_KATASTRALNI_UZEMI =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_katastralni_uzemi");
    /**
     * SQL statement for marking Zsj as deleted.
     */
    private static final String SQL_UPDATE_ZSJ =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_zsj");
    /**
     * SQL statement for marking VO as deleted.
     */
    private static final String SQL_UPDATE_VO =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_vo");
    /**
     * SQL statement for marking AdresniMisto as deleted.
     */
    private static final String SQL_UPDATE_ADRESNI_MISTO =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_adresni_misto");
    /**
     * SQL statement for marking CastObce as deleted.
     */
    private static final String SQL_UPDATE_CAST_OBCE =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_cast_obce");
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
            + "id_trans_ruian = ? WHERE id = ? AND id_trans_ruian <= ?";
    /**
     * SQL statement for marking StavebniObjekt as deleted.
     */
    private static final String SQL_UPDATE_STAVEBNI_OBJEKT =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_stavebni_objekt");
    /**
     * SQL statement for marking Ulice as deleted.
     */
    private static final String SQL_UPDATE_ULICE =
            String.format(SQL_STANDARD_ITEM_UPDATE_TEMPLATE, "rn_ulice");
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
     * Prepared statement for marking Stat as deleted.
     */
    private final PreparedStatement pstmUpdateStat;
    /**
     * Prepared statement for marking RegionSoudrznosti as deleted.
     */
    private final PreparedStatement pstmUpdateRegionSoudrznosti;
    /**
     * Prepared statement for marking Kraj as deleted.
     */
    private final PreparedStatement pstmUpdateKraj;
    /**
     * Prepared statement for marking Vusc as deleted.
     */
    private final PreparedStatement pstmUpdateVusc;
    /**
     * Prepared statement for marking Okres as deleted.
     */
    private final PreparedStatement pstmUpdateOkres;
    /**
     * Prepared statement for marking Orp as deleted.
     */
    private final PreparedStatement pstmUpdateOrp;
    /**
     * Prepared statement for marking Pou as deleted.
     */
    private final PreparedStatement pstmUpdatePou;
    /**
     * Prepared statement for marking Obec as deleted.
     */
    private final PreparedStatement pstmUpdateObec;
    /**
     * Prepared statement for marking SpravniObvod as deleted.
     */
    private final PreparedStatement pstmUpdateSpravniObvod;
    /**
     * Prepared statement for marking Mop as deleted.
     */
    private final PreparedStatement pstmUpdateMop;
    /**
     * Prepared statement for marking Momc as deleted.
     */
    private final PreparedStatement pstmUpdateMomc;
    /**
     * Prepared statement for marking KatastralniUzemi as deleted.
     */
    private final PreparedStatement pstmUpdateKatastralniUzemi;
    /**
     * Prepared statement for marking Zsj as deleted.
     */
    private final PreparedStatement pstmUpdateZsj;
    /**
     * Prepared statement for marking VO as deleted.
     */
    private final PreparedStatement pstmUpdateVo;
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

        pstmUpdateStat = con.prepareStatement(fixSql(SQL_UPDATE_STAT));
        pstmUpdateRegionSoudrznosti =
                con.prepareStatement(fixSql(SQL_UPDATE_REGION_SOUDRZNOSTI));
        pstmUpdateKraj = con.prepareStatement(fixSql(SQL_UPDATE_KRAJ));
        pstmUpdateVusc = con.prepareStatement(fixSql(SQL_UPDATE_VUSC));
        pstmUpdateOkres = con.prepareStatement(fixSql(SQL_UPDATE_OKRES));
        pstmUpdateOrp = con.prepareStatement(fixSql(SQL_UPDATE_ORP));
        pstmUpdatePou = con.prepareStatement(fixSql(SQL_UPDATE_POU));
        pstmUpdateObec = con.prepareStatement(fixSql(SQL_UPDATE_OBEC));
        pstmUpdateSpravniObvod =
                con.prepareStatement(fixSql(SQL_UPDATE_SPRAVNI_OBVOD));
        pstmUpdateMop = con.prepareStatement(fixSql(SQL_UPDATE_MOP));
        pstmUpdateMomc = con.prepareStatement(fixSql(SQL_UPDATE_MOMC));
        pstmUpdateKatastralniUzemi =
                con.prepareStatement(fixSql(SQL_UPDATE_KATASTRALNI_UZEMI));
        pstmUpdateZsj = con.prepareStatement(fixSql(SQL_UPDATE_ZSJ));
        pstmUpdateVo = con.prepareStatement(fixSql(SQL_UPDATE_VO));
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
            case "ST": // Stat
                deleteItem(pstmUpdateStat, item);
                break;
            case "RS": // RegionSoudrznosti
                deleteItem(pstmUpdateRegionSoudrznosti, item);
                break;
            case "KR": // Kraj
                deleteItem(pstmUpdateKraj, item);
                break;
            case "VC": // Vusc
                deleteItem(pstmUpdateVusc, item);
                break;
            case "OK": // Okres
                deleteItem(pstmUpdateOkres, item);
                break;
            case "OP": // Orp
                deleteItem(pstmUpdateOrp, item);
                break;
            case "PU": // Pou
                deleteItem(pstmUpdatePou, item);
                break;
            case "OB": // Obec
                deleteItem(pstmUpdateObec, item);
                break;
            case "SP": // SpravniObvod
                deleteItem(pstmUpdateSpravniObvod, item);
                break;
            case "MP": // Mop
                deleteItem(pstmUpdateMop, item);
                break;
            case "MC": // Momc
                deleteItem(pstmUpdateMomc, item);
                break;
            case "KU": //KatastralniUzemi
                deleteItem(pstmUpdateKatastralniUzemi, item);
                break;
            case "ZJ": // Zsj
                deleteItem(pstmUpdateZsj, item);
                break;
            case "VO": // Vo
                deleteItem(pstmUpdateVo, item);
                break;
            case "AD": // AdresniMisto
                deleteItem(pstmUpdateAdresniMisto, item);
                break;
            case "CO": // CastObce
                deleteItem(pstmUpdateCastObce, item);
                break;
            case "SO": // StavebniObjekt
                deleteStavebniObjekt(item);
                break;
            case "PA": // Parcela
                deleteParcela(item);
                break;
            case "UL": // Ulice
                deleteItem(pstmUpdateUlice, item);
                break;
            default:
                Log.write("Ignoring unsupported TypPrvkuKod '"
                        + item.getTypPrvkuKod() + " 'of ZaniklyPrvek");
        }
    }
    /**
     * Deletes item with standard (3 parameters) prepared statement.
     *
     * @param pstm Prepared statement used to mark item as deleted
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteItem(final PreparedStatement pstm, final ZaniklyPrvek item)
            throws SQLException {
        if (Config.isDryRun()) {
            return;
        }
        pstm.clearParameters();
        pstm.setLong(1, item.getIdTransakce());
        pstm.setInt(2, item.getPrvekId().intValue());
        pstm.setLong(3, item.getIdTransakce());
        pstm.execute();
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
        pstmUpdateParcela.setLong(2, item.getPrvekId());
        pstmUpdateParcela.setLong(3, item.getIdTransakce());
        pstmUpdateParcela.execute();

        pstmUpdateZpusobOchranyPozemku.clearParameters();
        pstmUpdateZpusobOchranyPozemku.setLong(1, item.getPrvekId());
        pstmUpdateZpusobOchranyPozemku.execute();

        pstmUpdateBonitDilyParcel.clearParameters();
        pstmUpdateBonitDilyParcel.setLong(1, item.getPrvekId());
        pstmUpdateBonitDilyParcel.execute();
    }
}
