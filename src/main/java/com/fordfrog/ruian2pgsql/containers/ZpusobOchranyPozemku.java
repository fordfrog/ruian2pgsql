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
package com.fordfrog.ruian2pgsql.containers;

/**
 * Container for ZpusobOchranyPozemku information.
 *
 * @author fordfrog
 */
public class ZpusobOchranyPozemku {

    private Integer kod;
    private Integer zpusobOchranyKod;
    private Long parcelaId;
    private Long idTransRuian;
    private Long rizeniId;

    public Integer getKod() {
        return kod;
    }

    public void setKod(final Integer kod) {
        this.kod = kod;
    }

    public Integer getZpusobOchranyKod() {
        return zpusobOchranyKod;
    }

    public void setZpusobOchranyKod(final Integer zpusobOchranyKod) {
        this.zpusobOchranyKod = zpusobOchranyKod;
    }

    public Long getParcelaId() {
        return parcelaId;
    }

    public void setParcelaId(final Long parcelaId) {
        this.parcelaId = parcelaId;
    }

    public Long getIdTransRuian() {
        return idTransRuian;
    }

    public void setIdTransRuian(final Long idTransRuian) {
        this.idTransRuian = idTransRuian;
    }

    public Long getRizeniId() {
        return rizeniId;
    }

    public void setRizeniId(final Long rizeniId) {
        this.rizeniId = rizeniId;
    }
}
