
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
    
    public EsercenteRistorazione(String insegna, double latitude, double longitude,String indirizzo) {
        super(insegna, latitude, longitude, indirizzo);
    }
    
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
    
    public String getPranzoString() {
        String pranzo = "<font color=\"red\">Pranzo: </font>";
        
        if (this.isLunMattina())
            pranzo = pranzo.concat("Lun ");
        if (this.isMarMattina())
            pranzo = pranzo.concat("Mar ");
        if (this.isMerMattina())
            pranzo = pranzo.concat("Merc ");
        if (this.isGioMattina())
            pranzo = pranzo.concat("Gio ");
        if (this.isVenMattina())
            pranzo = pranzo.concat("Ven ");
        if (this.isSabMattina())
            pranzo = pranzo.concat("Sab ");
        if (this.isDomMattina())
            pranzo = pranzo.concat("Dom ");
        
        if (pranzo.length() == 33)
            pranzo = null;
        
        return pranzo;
    }
    
    public String getCenaString() {
        
        String cena = "<font color=\"red\">Cena: </font>";
        
        if (this.isLunSera())
            cena = cena.concat("Lun ");
        if (this.isMarSera())
            cena = cena.concat("Mar ");
        if (this.isMerMattina())
            cena = cena.concat("Merc ");
        if (this.isGioSera())
            cena = cena.concat("Gio ");
        if (this.isVenSera())
            cena = cena.concat("Ven ");
        if (this.isSabSera())
            cena = cena.concat("Sab ");
        if (this.isDomSera())
            cena = cena.concat("Dom ");
        
        if (cena.length() == 31)
            cena = null;
        
        return cena;
    }
}
