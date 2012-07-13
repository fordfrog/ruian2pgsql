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
import org.postgresql.geometric.PGpath;
import org.postgresql.geometric.PGpoint;

/**
 * Container for Obec information.
 *
 * @author fordfrog
 */
public class Obec implements ItemWithDefinicniBod, ItemWithHranice,
        ItemWithMluvCharPad {

    private Integer kod;
    private String nazev;
    private boolean nespravny;
    private Integer okresKod;
    private Integer pouKod;
    private String nutsLau;
    private String mluvCharPad2;
    private String mluvCharPad3;
    private String mluvCharPad4;
    private String mluvCharPad5;
    private String mluvCharPad6;
    private String mluvCharPad7;
    private Boolean zmenaGrafiky;
    private Integer cleneniSmRozsahKod;
    private Integer cleneniSmTypKod;
    private Integer statusKod;
    private String vlajkaText;
    private byte[] vlajkaObrazek;
    private String znakText;
    private byte[] znakObrazek;
    private Long idTransRuian;
    private Date platiOd;
    private Long nzIdGlobalni;
    private PGpoint definicniBod;
    private PGpath hranice;

    public Integer getKod() {
        return kod;
    }

    public void setKod(final Integer kod) {
        this.kod = kod;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(final String nazev) {
        this.nazev = nazev;
    }

    public boolean isNespravny() {
        return nespravny;
    }

    public void setNespravny(final boolean nespravny) {
        this.nespravny = nespravny;
    }

    public Integer getOkresKod() {
        return okresKod;
    }

    public void setOkresKod(final Integer okresKod) {
        this.okresKod = okresKod;
    }

    public Integer getPouKod() {
        return pouKod;
    }

    public void setPouKod(final Integer pouKod) {
        this.pouKod = pouKod;
    }

    public String getNutsLau() {
        return nutsLau;
    }

    public void setNutsLau(final String nutsLau) {
        this.nutsLau = nutsLau;
    }

    @Override
    public String getMluvCharPad2() {
        return mluvCharPad2;
    }

    @Override
    public void setMluvCharPad2(final String mluvCharPad2) {
        this.mluvCharPad2 = mluvCharPad2;
    }

    @Override
    public String getMluvCharPad3() {
        return mluvCharPad3;
    }

    @Override
    public void setMluvCharPad3(final String mluvCharPad3) {
        this.mluvCharPad3 = mluvCharPad3;
    }

    @Override
    public String getMluvCharPad4() {
        return mluvCharPad4;
    }

    @Override
    public void setMluvCharPad4(final String mluvCharPad4) {
        this.mluvCharPad4 = mluvCharPad4;
    }

    @Override
    public String getMluvCharPad5() {
        return mluvCharPad5;
    }

    @Override
    public void setMluvCharPad5(final String mluvCharPad5) {
        this.mluvCharPad5 = mluvCharPad5;
    }

    @Override
    public String getMluvCharPad6() {
        return mluvCharPad6;
    }

    @Override
    public void setMluvCharPad6(final String mluvCharPad6) {
        this.mluvCharPad6 = mluvCharPad6;
    }

    @Override
    public String getMluvCharPad7() {
        return mluvCharPad7;
    }

    @Override
    public void setMluvCharPad7(final String mluvCharPad7) {
        this.mluvCharPad7 = mluvCharPad7;
    }

    public Boolean getZmenaGrafiky() {
        return zmenaGrafiky;
    }

    public void setZmenaGrafiky(final Boolean zmenaGrafiky) {
        this.zmenaGrafiky = zmenaGrafiky;
    }

    public Integer getCleneniSmRozsahKod() {
        return cleneniSmRozsahKod;
    }

    public void setCleneniSmRozsahKod(final Integer cleneniSmRozsahKod) {
        this.cleneniSmRozsahKod = cleneniSmRozsahKod;
    }

    public Integer getCleneniSmTypKod() {
        return cleneniSmTypKod;
    }

    public void setCleneniSmTypKod(final Integer cleneniSmTypKod) {
        this.cleneniSmTypKod = cleneniSmTypKod;
    }

    public Integer getStatusKod() {
        return statusKod;
    }

    public void setStatusKod(final Integer statusKod) {
        this.statusKod = statusKod;
    }

    public String getVlajkaText() {
        return vlajkaText;
    }

    public void setVlajkaText(final String vlajkaText) {
        this.vlajkaText = vlajkaText;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public byte[] getVlajkaObrazek() {
        return vlajkaObrazek;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setVlajkaObrazek(final byte[] vlajkaObrazek) {
        this.vlajkaObrazek = vlajkaObrazek;
    }

    public String getZnakText() {
        return znakText;
    }

    public void setZnakText(final String znakText) {
        this.znakText = znakText;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public byte[] getZnakObrazek() {
        return znakObrazek;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setZnakObrazek(final byte[] znakObrazek) {
        this.znakObrazek = znakObrazek;
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

    @Override
    public PGpoint getDefinicniBod() {
        return definicniBod;
    }

    @Override
    public void setDefinicniBod(final PGpoint definicniBod) {
        this.definicniBod = definicniBod;
    }

    @Override
    public PGpath getHranice() {
        return hranice;
    }

    @Override
    public void setHranice(final PGpath hranice) {
        this.hranice = hranice;
    }
}
