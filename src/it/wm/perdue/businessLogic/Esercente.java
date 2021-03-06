
package it.wm.perdue.businessLogic;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class Esercente implements HasID, Serializable{
    @SerializedName("IDesercente")
    private int      id             = -1;
    @SerializedName("Insegna_Esercente")
    private String   insegna        = null;
    @SerializedName("Indirizzo_Esercente")
    private String   indirizzo      = null;
    @SerializedName("Citta_Esercente")
    private String   citta          = null;
    @SerializedName("Zona_Esercente")
    private String   zona           = null;
    @SerializedName("Telefono_Esercente")
    private String   telefono       = null;
    @SerializedName("Email_Esercente")
    private String   email          = null;
    @SerializedName("Url_Esercente")
    private String   url            = null; // TODO: cambiare classe in URL
    @SerializedName("Giorno_chiusura_Esercente")
    private String   giornoChiusura = null;
    @SerializedName("Giorni")
    private String[] giorni         = null;
    @SerializedName("Tipo_Teser")
    private String   tipologia      = null;
    @SerializedName("Note_Varie_CE")
    private String   noteVarie      = null; // TODO: riguardarsi cosa contiene
                                             // e
                                             // cambiare nome. --> dovrebbe
                                             // contenere la condizione dello
                                             // sconto
    @SerializedName("Ulteriori_Informazioni")
    private boolean  ulterioriInfo  = false;
    @SerializedName("Esercente_Virtuale")
    private boolean  virtuale       = false;
    @SerializedName("Latitudine")
    private double   latitude       = 0.0;
    @SerializedName("Longitudine")
    private double   longitude      = 0.0;
    @SerializedName("Distanza")
    private double   distanza       = 0.0;
    
    public Esercente(String insegna, double latitude, double longitude, String indirizzo) {
        // TODO Auto-generated constructor stub
        this.latitude = latitude;
        this.longitude = longitude;
        this.insegna = insegna;
        this.indirizzo = indirizzo;
    }

    public String[] getGiorni() {
        return giorni;
    }
    
    public String getGiorniString() {
        String giorniString = "".concat(giorni[0]);
        for (int i = 0; i < giorni.length; i++)
            if (i != 0)
                giorniString = giorniString.concat(" - " + giorni[i]);
        return giorniString;
    }
    
    public int getID() {
        return id;
    }
    
    public String getInsegna() {
        return insegna;
    }
    
    public String getIndirizzo() {
        return indirizzo;
    }
    
    public String getCitta() {
        return citta;
    }
    
    public String getZona() {
        return zona;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getGiornoChiusura() {
        return giornoChiusura;
    }
    
    public String getTipologia() {
        return tipologia;
    }
    
    public String getNoteVarie() {
        return noteVarie;
    }
    
    public boolean isUlterioriInfo() {
        return ulterioriInfo;
    }
    
    public boolean isVirtuale() {
        return virtuale;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public double getDistanza() {
        return distanza;
    }
}
