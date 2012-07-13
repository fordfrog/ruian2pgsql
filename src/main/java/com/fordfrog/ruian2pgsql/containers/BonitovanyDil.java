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
 * Container for BonitovanyDil information.
 *
 * @author fordfrog
 */
public class BonitovanyDil {

    private Long parcelaId;
    private Integer bpejKod;
    private Integer vymera;
    private Long idTransRuian;
    private Long rizeniId;

    public Long getParcelaId() {
        return parcelaId;
    }

    public void setParcelaId(final Long parcelaId) {
        this.parcelaId = parcelaId;
    }

    public Integer getBpejKod() {
        return bpejKod;
    }

    public void setBpejKod(final Integer bpejKod) {
        this.bpejKod = bpejKod;
    }

    public Integer getVymera() {
        return vymera;
    }

    public void setVymera(final Integer vymera) {
        this.vymera = vymera;
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
