
package it.wm.perdue.businessLogic;

import com.google.gson.annotations.SerializedName;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class EsercenteRistorazione extends Esercente {
    @SerializedName("Fasciaprezzo_Esercente")
    private String  fasciaPrezzo   = null;
    @SerializedName("Ambiente_Esercente")
    private String  ambiente       = null;
    @SerializedName("Subtipo_STeser")
    private String  sottoTipologia = null;
    @SerializedName("Specialita_CE")
    private String  specialita     = null;
    @SerializedName("Lunedi_mat_CE")
    private boolean lunMattina     = false;
    @SerializedName("Lunedi_sera_CE")
    private boolean lunSera        = false;
    @SerializedName("Martedi_mat_CE")
    private boolean marMattina     = false;
    @SerializedName("Martedi_sera_CE")
    private boolean marSera        = false;
    @SerializedName("Mecoledi_mat_CE")
    private boolean merMattina     = false;
    @SerializedName("Mecoledi_sera_CE")
    private boolean merSera        = false;
    @SerializedName("Giovedi_mat_CE")
    private boolean gioMattina     = false;
    @SerializedName("Giovedi_sera_CE")
    private boolean gioSera        = false;
    @SerializedName("Venerdi_mat_CE")
    private boolean venMattina     = false;
    @SerializedName("Venerdi_sera_CE")
    private boolean venSera        = false;
    @SerializedName("Sabato_mat_CE")
    private boolean sabMattina     = false;
    @SerializedName("Sabato_sera_CE")
    private boolean sabSera        = false;
    @SerializedName("Domenica_mat_CE")
    private boolean domMattina     = false;
    @SerializedName("Domenica_sera_CE")
    private boolean domSera        = false;

    public String getFasciaPrezzo() {
        return fasciaPrezzo;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public String getSottoTipologia() {
        return sottoTipologia;
    }

    public String getSpecialita() {
        return specialita;
    }

    public boolean isLunMattina() {
        return lunMattina;
    }

    public boolean isLunSera() {
        return lunSera;
    }

    public boolean isMarMattina() {
        return marMattina;
    }

    public boolean isMarSera() {
        return marSera;
    }

    public boolean isMerMattina() {
        return merMattina;
    }

    public boolean isMerSera() {
        return merSera;
    }

    public boolean isGioMattina() {
        return gioMattina;
    }

    public boolean isGioSera() {
        return gioSera;
    }

    public boolean isVenMattina() {
        return venMattina;
    }

    public boolean isVenSera() {
        return venSera;
    }

    public boolean isSabMattina() {
        return sabMattina;
    }

    public boolean isSabSera() {
        return sabSera;
    }

    public boolean isDomMattina() {
        return domMattina;
    }

    public boolean isDomSera() {
        return domSera;
    }

}
