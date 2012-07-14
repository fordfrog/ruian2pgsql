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

import com.fordfrog.ruian2pgsql.containers.ZaniklyPrvek;
import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.Utils;
import java.io.Writer;
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

    public ZaniklyPrvekConvertor() {
        super(ZaniklyPrvek.class, NAMESPACE, "ZaniklyPrvek", null, null, null);
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
            final Connection con, final ZaniklyPrvek item, final Writer logFile)
            throws XMLStreamException, SQLException {
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
                        Utils.printWarningIgnoringElement(logFile, reader);
                }

                break;
            default:
                Utils.printWarningIgnoringElement(logFile, reader);
        }
    }

    /**
     * Instead of saving data, it removes specified item from database.
     *
     * @param con  database connection
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    @Override
    protected void saveData(final Connection con, final ZaniklyPrvek item,
            final Writer logFile) throws SQLException {
        switch (item.getTypPrvkuKod()) {
            case "AD": // AdresniMisto
                deleteAdresniMisto(con, item);
                break;
            case "SO": // StavebniObjekt
                deleteStavebniObjekt(con, item);
                break;
            case "PA": // Parcela
                deleteParcela(con, item);
                break;
            case "UL": // Ulice
                deleteUlice(con, item);
                break;
            default:
                Utils.printToLog(logFile, "Ignoring unsupported TypPrvkuKod '"
                        + item.getTypPrvkuKod() + " 'of ZaniklyPrvek");
        }
    }

    /**
     * Deletes AdresniMisto item.
     *
     * @param con  database connection
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteAdresniMisto(final Connection con,
            final ZaniklyPrvek item) throws SQLException {
        try (final PreparedStatement pstm = con.prepareStatement(
                        "DELETE FROM rn_adresni_misto "
                        + "WHERE kod = ? AND id_trans_ruian < ?")) {
            pstm.setInt(1, item.getPrvekId().intValue());
            pstm.setLong(2, item.getIdTransakce());
            pstm.execute();
        }
    }

    /**
     * Deletes StavebniObjekt item.
     *
     * @param con  database connection
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteStavebniObjekt(final Connection con,
            final ZaniklyPrvek item) throws SQLException {
        try (final PreparedStatement pstm = con.prepareStatement(
                        "DELETE FROM rn_stavebni_objekt "
                        + "WHERE kod = ? AND id_trans_ruian < ?")) {
            pstm.setInt(1, item.getPrvekId().intValue());
            pstm.setLong(2, item.getIdTransakce());
            pstm.execute();
        }
    }

    /**
     * Deletes Parcela item.
     *
     * @param con  database connection
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteParcela(final Connection con, final ZaniklyPrvek item)
            throws SQLException {
        try (final PreparedStatement pstm = con.prepareStatement(
                        "DELETE FROM rn_parcela "
                        + "WHERE id = ? AND id_trans_ruian < ?")) {
            pstm.setInt(1, item.getPrvekId().intValue());
            pstm.setLong(2, item.getIdTransakce());
            pstm.execute();
        }
    }

    /**
     * Deletes Ulice item.
     *
     * @param con  database connection
     * @param item item
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.
     */
    private void deleteUlice(final Connection con, final ZaniklyPrvek item)
            throws SQLException {
        try (final PreparedStatement pstm = con.prepareStatement(
                        "DELETE FROM rn_ulice "
                        + "WHERE kod = ? AND id_trans_ruian < ?")) {
            pstm.setInt(1, item.getPrvekId().intValue());
            pstm.setLong(2, item.getIdTransakce());
            pstm.execute();
        }
    }
}
