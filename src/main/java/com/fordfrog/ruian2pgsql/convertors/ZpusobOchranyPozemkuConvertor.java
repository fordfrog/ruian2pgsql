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

import com.fordfrog.ruian2pgsql.containers.ZpusobOchranyPozemku;
import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.XMLUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Convertor for ZpusobOchranyPozemku element.
 *
 * @author fordfrog
 */
public class ZpusobOchranyPozemkuConvertor
        extends AbstractSaveConvertor<ZpusobOchranyPozemku> {

    /**
     * Namespace of the element sub-elements.
     */
    private static final String NAMESPACE = Namespaces.COMMON_TYPY;
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT =
            "INSERT INTO rn_zpusob_ochrany_pozemku "
            + "(kod, zpusob_ochrany_kod, parcela_id, id_trans_ruian, "
            + "rizeni_id) VALUES (?, ?, ?, ?, ?)";
    /**
     * Id of parent Parcela.
     */
    private Long parcelaId;

    /**
     * Creates new instance of ZpusobOchranyPozemkuConvertor.
     *
     * @param con database connection
     *
     * @throws SQLException Thrown if problem occurred while communicating with
     *                      database.I
     */
    public ZpusobOchranyPozemkuConvertor(final Connection con)
            throws SQLException {
        super(ZpusobOchranyPozemku.class, NAMESPACE, "ZpusobOchrany", con, null,
                SQL_INSERT, null);
    }

    @Override
    protected void fill(final PreparedStatement pstm,
            final ZpusobOchranyPozemku item, final boolean update)
            throws SQLException {
        pstm.setInt(1, item.getKod());
        pstm.setInt(2, item.getZpusobOchranyKod());
        pstm.setLong(3, parcelaId);
        pstm.setLong(4, item.getIdTransRuian());
        pstm.setLong(5, item.getRizeniId());
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final ZpusobOchranyPozemku item) throws SQLException {
        // we do not set any parameters as we always return empty result set
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final ZpusobOchranyPozemku item) throws XMLStreamException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "IdTransakce":
                        item.setIdTransRuian(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "Kod":
                        item.setKod(Integer.parseInt(reader.getElementText()));
                        break;
                    case "TypOchranyKod":
                        item.setZpusobOchranyKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "RizeniId":
                        item.setRizeniId(
                                Long.parseLong(reader.getElementText()));
                        break;
                    default:
                        XMLUtils.processUnsupported(reader);
                }

                break;
            default:
                XMLUtils.processUnsupported(reader);
        }
    }

    @Override
    protected void saveData(final ZpusobOchranyPozemku item)
            throws SQLException {
        insertItem(item);
    }

    /**
     * Getter for {@link #parcelaId}.
     *
     * @return {@link #parcelaId}
     */
    public Long getParcelaId() {
        return parcelaId;
    }

    /**
     * Setter for {@link #parcelaId}.
     *
     * @param parcelaId {@link #parcelaId}
     */
    public void setParcelaId(final Long parcelaId) {
        this.parcelaId = parcelaId;
    }
}
