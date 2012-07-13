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
 * Container for DetailniTEA information.
 *
 * @author fordfrog
 */
public class DetailniTEA {

    private Integer kod;
    private Integer stavobjKod;
    private Integer adresniMistoKod;
    private boolean nespravny;
    private Integer pocetBytu;
    private Integer pocetPodlazi;
    private Integer druhKonstrukceKod;
    private Integer pripojKanalSitKod;
    private Integer pripojPlynKod;
    private Integer pripojVodovodKod;
    private Boolean pripojElEnergie;
    private Integer zpusobVytapeniKod;
    private Long idTransRuian;
    private Date platiOd;
    private Long nzIdGlobalni;

    public Integer getKod() {
        return kod;
    }

    public void setKod(final Integer kod) {
        this.kod = kod;
    }

    public Integer getStavobjKod() {
        return stavobjKod;
    }

    public void setStavobjKod(final Integer stavobjKod) {
        this.stavobjKod = stavobjKod;
    }

    public Integer getAdresniMistoKod() {
        return adresniMistoKod;
    }

    public void setAdresniMistoKod(final Integer adresniMistoKod) {
        this.adresniMistoKod = adresniMistoKod;
    }

    public boolean isNespravny() {
        return nespravny;
    }

    public void setNespravny(final boolean nespravny) {
        this.nespravny = nespravny;
    }

    public Integer getPocetBytu() {
        return pocetBytu;
    }

    public void setPocetBytu(final Integer pocetBytu) {
        this.pocetBytu = pocetBytu;
    }

    public Integer getPocetPodlazi() {
        return pocetPodlazi;
    }

    public void setPocetPodlazi(final Integer pocetPodlazi) {
        this.pocetPodlazi = pocetPodlazi;
    }

    public Integer getDruhKonstrukceKod() {
        return druhKonstrukceKod;
    }

    public void setDruhKonstrukceKod(final Integer druhKonstrukceKod) {
        this.druhKonstrukceKod = druhKonstrukceKod;
    }

    public Integer getPripojKanalSitKod() {
        return pripojKanalSitKod;
    }

    public void setPripojKanalSitKod(final Integer pripojKanalSitKod) {
        this.pripojKanalSitKod = pripojKanalSitKod;
    }

    public Integer getPripojPlynKod() {
        return pripojPlynKod;
    }

    public void setPripojPlynKod(final Integer pripojPlynKod) {
        this.pripojPlynKod = pripojPlynKod;
    }

    public Integer getPripojVodovodKod() {
        return pripojVodovodKod;
    }

    public void setPripojVodovodKod(final Integer pripojVodovodKod) {
        this.pripojVodovodKod = pripojVodovodKod;
    }

    public Boolean getPripojElEnergie() {
        return pripojElEnergie;
    }

    public void setPripojElEnergie(final Boolean pripojElEnergie) {
        this.pripojElEnergie = pripojElEnergie;
    }

    public Integer getZpusobVytapeniKod() {
        return zpusobVytapeniKod;
    }

    public void setZpusobVytapeniKod(final Integer zpusobVytapeniKod) {
        this.zpusobVytapeniKod = zpusobVytapeniKod;
    }

    public Long getIdTransRuian() {
        return idTransRuian;
    }

    public void setIdTransRuian(final Long idTransRuian) {
        this.idTransRuian = idTransRuian;
    }

    @SuppressWarnings("ReturnOfDateField")
    public Date getPlatiOd() {
        return platiOd;
    }

    @SuppressWarnings("AssignmentToDateFieldFromParameter")
    public void setPlatiOd(final Date platiOd) {
        this.platiOd = platiOd;
    }

    public Long getNzIdGlobalni() {
        return nzIdGlobalni;
    }

    public void setNzIdGlobalni(final Long nzIdGlobalni) {
        this.nzIdGlobalni = nzIdGlobalni;
    }
}
