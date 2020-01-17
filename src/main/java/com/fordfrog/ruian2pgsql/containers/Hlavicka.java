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

import java.util.Date;

/**
 * Hlavicka container.
 *
 * @author fordfrog
 */
public class Hlavicka {

    private String verzeVfr;
    private String typZaznamu;
    private String typDavky;
    private String typSouboru;
    private Date datum;
    private Integer transakceOdId;
    private Date transakceOdZapsano;
    private Integer transakceDoId;
    private Date transakceDoZapsano;
    private String predchoziSoubor;
    private String plnySoubor;
    private String metadata;
    private Date platnostDatKIsui;
    private Date platnostDatKIskn;

    public String getVerzeVfr() {
        return verzeVfr;
    }

    public void setVerzeVfr(final String verzeVfr) {
        this.verzeVfr = verzeVfr;
    }

    public String getTypZaznamu() {
        return typZaznamu;
    }

    public void setTypZaznamu(final String typZaznamu) {
        this.typZaznamu = typZaznamu;
    }

    public String getTypDavky() {
        return typDavky;
    }

    public void setTypDavky(final String typDavky) {
        this.typDavky = typDavky;
    }

    public String getTypSouboru() {
        return typSouboru;
    }

    public void setTypSouboru(final String typSouboru) {
        this.typSouboru = typSouboru;
    }

    @SuppressWarnings("ReturnOfDateField")
    public Date getDatum() {
        return datum;
    }

    @SuppressWarnings("AssignmentToDateFieldFromParameter")
    public void setDatum(final Date datum) {
        this.datum = datum;
    }

    public Integer getTransakceOdId() {
        return transakceOdId;
    }

    public void setTransakceOdId(final Integer transakceOdId) {
        this.transakceOdId = transakceOdId;
    }

    @SuppressWarnings("ReturnOfDateField")
    public Date getTransakceOdZapsano() {
        return transakceOdZapsano;
    }

    @SuppressWarnings("AssignmentToDateFieldFromParameter")
    public void setTransakceOdZapsano(final Date transakceOdZapsano) {
        this.transakceOdZapsano = transakceOdZapsano;
    }

    public Integer getTransakceDoId() {
        return transakceDoId;
    }

    public void setTransakceDoId(final Integer transakceDoId) {
        this.transakceDoId = transakceDoId;
    }

    @SuppressWarnings("ReturnOfDateField")
    public Date getTransakceDoZapsano() {
        return transakceDoZapsano;
    }

    @SuppressWarnings("AssignmentToDateFieldFromParameter")
    public void setTransakceDoZapsano(final Date transakceDoZapsano) {
        this.transakceDoZapsano = transakceDoZapsano;
    }

    public String getPredchoziSoubor() {
        return predchoziSoubor;
    }

    public void setPredchoziSoubor(final String predchoziSoubor) {
        this.predchoziSoubor = predchoziSoubor;
    }

    public String getPlnySoubor() {
        return plnySoubor;
    }

    public void setPlnySoubor(final String plnySoubor) {
        this.plnySoubor = plnySoubor;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(final String metadata) {
        this.metadata = metadata;
    }

    public Date getPlatnostDatKIsui() {
        return platnostDatKIsui;
    }
    public void setPlatnostDatKIsui(final Date platnostDatKIsui) {
        this.platnostDatKIsui = platnostDatKIsui;
    }

    public Date getPlatnostDatKIskn() {
        return platnostDatKIskn;
    }

    public void setPlatnostDatKIskn(final Date platnostDatKIskn) {
        this.platnostDatKIskn = platnostDatKIskn;
    }
}
