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

import com.fordfrog.ruian2pgsql.containers.BonitovanyDil;
import com.fordfrog.ruian2pgsql.utils.Namespaces;
import com.fordfrog.ruian2pgsql.utils.Utils;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Convertor for BonitovanyDil element.
 *
 * @author fordfrog
 */
public class BonitovanyDilConvertor
        extends AbstractSaveConvertor<BonitovanyDil> {

    /**
     * Namespace of the element sub-elements.
     */
    private static final String NAMESPACE = Namespaces.COMMON_TYPY;
    /**
     * SQL statement for checking whether the item exists. We always insert the
     * items as new because they are children of Parcela element and their live
     * is bound to the parent element.
     */
    private static final String SQL_EXISTS =
            "SELECT 1 FROM rn_bonit_dily_parcel WHERE parcela_id IS NULL";
    /**
     * SQL statement for insertion of new item.
     */
    private static final String SQL_INSERT = "INSERT INTO rn_bonit_dily_parcel "
            + "(parcela_id, bpej_kod, vymera, id_trans_ruian, rizeni_id) "
            + "VALUES (?, ?, ?, ?, ?)";
    /**
     * Id of parent Parcela.
     */
    private final long parcelaId;

    /**
     * Creates new instance of BonitovanyDilConvertor.
     *
     * @param parcelaId {@link #parcelaId}
     */
    public BonitovanyDilConvertor(final long parcelaId) {
        super(BonitovanyDil.class, NAMESPACE, "BonitovanyDil", SQL_EXISTS,
                SQL_INSERT, null);

        this.parcelaId = parcelaId;
    }

    @Override
    protected void fill(final PreparedStatement pstm, final BonitovanyDil item,
            final boolean update) throws SQLException {
        pstm.setLong(1, parcelaId);
        pstm.setInt(2, item.getBpejKod());
        pstm.setInt(3, item.getVymera());
        pstm.setLong(4, item.getIdTransRuian());
        pstm.setLong(5, item.getRizeniId());
    }

    @Override
    protected void fillExists(final PreparedStatement pstm,
            final BonitovanyDil item) throws SQLException {
        // we do not set any parameters as we always return empty result set
    }

    @Override
    protected void processElement(final XMLStreamReader reader,
            final Connection con, final BonitovanyDil item,
            final Writer logFile) throws XMLStreamException {
        switch (reader.getNamespaceURI()) {
            case NAMESPACE:
                switch (reader.getLocalName()) {
                    case "BonitovanaJednotkaKod":
                        item.setBpejKod(
                                Integer.parseInt(reader.getElementText()));
                        break;
                    case "IdTransakce":
                    case "IdTranskace":
                        item.setIdTransRuian(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "RizeniId":
                        item.setRizeniId(
                                Long.parseLong(reader.getElementText()));
                        break;
                    case "Vymera":
                        item.setVymera(
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
