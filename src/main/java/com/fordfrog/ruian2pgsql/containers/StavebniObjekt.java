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
 * Container for StavebniObjekt element.
 *
 * @author fordfrog
 */
public class StavebniObjekt implements ItemWithDefinicniBod, ItemWithHranice {

    private Integer kod;
    private Boolean nespravny;
    private Long identifikacniParcelaId;
    private Integer momcKod;
    private Integer cobceKod;
    private Long budovaId;
    private Integer[] cislaDomovni;
    private Date dokonceni;
    private Integer jeVytahKod;
    private Boolean zmenaGrafiky;
    private Integer druhKonstrukceKod;
    private Boolean zmenaDetailu;
    private Integer obestavenyProstor;
    private Integer pocetBytu;
    private Integer pocetPodlazi;
    private Integer podlahovaPlocha;
    private Boolean pripojElEnergie;
    private Integer pripojKanalSitKod;
    private Integer pripojPlynKod;
    private Integer pripojVodovodKod;
    private Integer typKod;
    private Integer zastavenaPlocha;
    private Integer zpusobVytapeniKod;
    private Integer zpusobVyuzitiKod;
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

    public Boolean getNespravny() {
        return nespravny;
    }

    public void setNespravny(final Boolean nespravny) {
        this.nespravny = nespravny;
    }

    public Long getIdentifikacniParcelaId() {
        return identifikacniParcelaId;
    }

    public void setIdentifikacniParcelaId(final Long identifikacniParcelaId) {
        this.identifikacniParcelaId = identifikacniParcelaId;
    }

    public Integer getMomcKod() {
        return momcKod;
    }

    public void setMomcKod(final Integer momcKod) {
        this.momcKod = momcKod;
    }

    public Integer getCobceKod() {
        return cobceKod;
    }

    public void setCobceKod(final Integer cobceKod) {
        this.cobceKod = cobceKod;
    }

    public Long getBudovaId() {
        return budovaId;
    }

    public void setBudovaId(final Long budovaId) {
        this.budovaId = budovaId;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Integer[] getCislaDomovni() {
        return cislaDomovni;
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setCislaDomovni(final Integer[] cislaDomovni) {
        this.cislaDomovni = cislaDomovni;
    }

    public void addCisloDomovni(final Integer cisloDomovni) {
        final Integer[] newArray;

        if (cislaDomovni == null) {
            newArray = new Integer[1];
        } else {
            newArray = new Integer[cislaDomovni.length + 1];
            System.arraycopy(cislaDomovni, 0, newArray, 0, cislaDomovni.length);
        }

        newArray[newArray.length - 1] = cisloDomovni;
        cislaDomovni = newArray;
    }

    @SuppressWarnings("ReturnOfDateField")
    public Date getDokonceni() {
        return dokonceni;
    }

    @SuppressWarnings("AssignmentToDateFieldFromParameter")
    public void setDokonceni(final Date dokonceni) {
        this.dokonceni = dokonceni;
    }

    public Integer getJeVytahKod() {
        return jeVytahKod;
    }

    public void setJeVytahKod(final Integer jeVytahKod) {
        this.jeVytahKod = jeVytahKod;
    }

    public Boolean getZmenaGrafiky() {
        return zmenaGrafiky;
    }

    public void setZmenaGrafiky(final Boolean zmenaGrafiky) {
        this.zmenaGrafiky = zmenaGrafiky;
    }

    public Integer getDruhKonstrukceKod() {
        return druhKonstrukceKod;
    }

    public void setDruhKonstrukceKod(final Integer druhKonstrukceKod) {
        this.druhKonstrukceKod = druhKonstrukceKod;
    }

    public Boolean getZmenaDetailu() {
        return zmenaDetailu;
    }

    public void setZmenaDetailu(final Boolean zmenaDetailu) {
        this.zmenaDetailu = zmenaDetailu;
    }

    public Integer getObestavenyProstor() {
        return obestavenyProstor;
    }

    public void setObestavenyProstor(final Integer obestavenyProstor) {
        this.obestavenyProstor = obestavenyProstor;
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

    public Integer getPodlahovaPlocha() {
        return podlahovaPlocha;
    }

    public void setPodlahovaPlocha(final Integer podlahovaPlocha) {
        this.podlahovaPlocha = podlahovaPlocha;
    }

    public Boolean getPripojElEnergie() {
        return pripojElEnergie;
    }

    public void setPripojElEnergie(final Boolean pripojElEnergie) {
        this.pripojElEnergie = pripojElEnergie;
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

    public Integer getTypKod() {
        return typKod;
    }

    public void setTypKod(final Integer typKod) {
        this.typKod = typKod;
    }

    public Integer getZastavenaPlocha() {
        return zastavenaPlocha;
    }

    public void setZastavenaPlocha(final Integer zastavenaPlocha) {
        this.zastavenaPlocha = zastavenaPlocha;
    }

    public Integer getZpusobVytapeniKod() {
        return zpusobVytapeniKod;
    }

    public void setZpusobVytapeniKod(final Integer zpusobVytapeniKod) {
        this.zpusobVytapeniKod = zpusobVytapeniKod;
    }

    public Integer getZpusobVyuzitiKod() {
        return zpusobVyuzitiKod;
    }

    public void setZpusobVyuzitiKod(final Integer zpusobVyuzitiKod) {
        this.zpusobVyuzitiKod = zpusobVyuzitiKod;
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
