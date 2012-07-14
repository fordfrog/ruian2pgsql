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
 * Container for ZaniklyPrvek information.
 *
 * @author fordfrog
 */
public class ZaniklyPrvek {

    private String typPrvkuKod;
    private Long prvekId;
    private Long idTransakce;

    public String getTypPrvkuKod() {
        return typPrvkuKod;
    }

    public void setTypPrvkuKod(final String typPrvkuKod) {
        this.typPrvkuKod = typPrvkuKod;
    }

    public Long getPrvekId() {
        return prvekId;
    }

    public void setPrvekId(final Long prvekId) {
        this.prvekId = prvekId;
    }

    public Long getIdTransakce() {
        return idTransakce;
    }

    public void setIdTransakce(final Long idTransakce) {
        this.idTransakce = idTransakce;
    }
}
