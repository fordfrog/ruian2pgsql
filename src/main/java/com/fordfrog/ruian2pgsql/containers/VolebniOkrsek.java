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
 * Container for VolebniOkrsek information.
 *
 * @author fordfrog
 */
public class VolebniOkrsek implements ItemWithDefinicniBod, ItemWithHranice {

    private Integer kod;
    private Integer cislo;
    private Boolean nespravny;
    private Integer obecKod;
    private Integer momcKod;
    private String poznamka;
    private Date platiOd;
    private Date platiDo;
    private Long idTransRuian;
    private Long nzIdGlobalni;
    private String definicniBod;
    private String hranice;

    public Integer getKod() {
        return kod;
    }

    public void setKod(final Integer kod) {
        this.kod = kod;
    }

    public Integer getCislo() {
        return this.cislo;
    }

    public void setCislo(int cislo) {
        this.cislo = cislo;
    }

    public Boolean getNespravny() {
        return nespravny;
    }

    public void setNespravny(final Boolean nespravny) {
        this.nespravny = nespravny;
    }

    public Integer getObecKod() {
        return obecKod;
    }

    public void setObecKod(final Integer obecKod) {
        this.obecKod = obecKod;
    }

    public Integer getMomcKod() {
        if (momcKod==null) return 0;
        return momcKod;
    }

    public void setMomcKod(final Integer momcKod) {
        this.momcKod = momcKod;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(final String poznamka) {
        this.poznamka = poznamka;
    }

    @SuppressWarnings("ReturnOfDateField")
    public Date getPlatiOd() {
        return platiOd;
    }

    @SuppressWarnings("AssignmentToDateFieldFromParameter")
    public void setPlatiOd(final Date platiOd) {
        this.platiOd = platiOd;
    }

    @SuppressWarnings("ReturnOfDateField")
    public Date getPlatiDo() {
        return platiDo;
    }

    @SuppressWarnings("AssignmentToDateFieldFromParameter")
    public void setPlatiDo(final Date platiDo) {
        this.platiDo = platiDo;
    }

    public Long getIdTransRuian() {
        return idTransRuian;
    }

    public void setIdTransRuian(final Long idTransRuian) {
        this.idTransRuian = idTransRuian;
    }

    public Long getNzIdGlobalni() {
        return nzIdGlobalni;
    }

    public void setNzIdGlobalni(final Long nzIdGlobalni) {
        this.nzIdGlobalni = nzIdGlobalni;
    }

    @Override
    public String getDefinicniBod() {
        return definicniBod;
    }

    @Override
    public void setDefinicniBod(final String definicniBod) {
        this.definicniBod = definicniBod;
    }

    @Override
    public String getHranice() {
        return hranice;
    }

    @Override
    public void setHranice(final String hranice) {
        this.hranice = hranice;
    }
}
