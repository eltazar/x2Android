/**
 * 
 */

package it.wm.perdue.businessLogic;

import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.Date;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class Coupon implements HasID {
    @SerializedName("idofferta")
    private int     id                 = -1;
    @SerializedName("offerta_title")
    private String  titolo             = null;
    @SerializedName("offerta_titolo_breve")
    private String  titoloBreve        = null;
    @SerializedName("offerta_foto_big")
    private URL     urlImmagine        = null;
    @SerializedName("coupon_valore_acquisto")
    private float   valoreAcquisto     = 0;
    @SerializedName("coupon_valore_facciale")
    private float   valoreFacciale     = 0;
    @SerializedName("offerta_sconto_va")
    private float   sconto             = 0;
    @SerializedName("offerta_sconto_per")
    private String  scontoPer          = null; // ????????
    @SerializedName("offerta_descrizione_estesa")
    private String  descrizione        = null;
    @SerializedName("offerta_descrizione_breve")
    private String  descrizioneBreve   = null;
    @SerializedName("offerta_condizioni_sintetiche")
    private String  condizioni         = null;
    @SerializedName("coupon_periodo_dal")
    private Date    couponPeriodoDal   = null; // ?????????
    @SerializedName("offerta_pediodo_dal")
    private Date    inizioValidita     = null;
    @SerializedName("offerta_periodo_al")
    private Date    fineValidita       = null;
    @SerializedName("idesercente")
    private int     idEsercente        = -1;
    @SerializedName("esercente_nome")
    private String  nomeEsercente      = null;
    @SerializedName("esercente_indirizzo")
    private String  indirizzoEsercente = null;
    @SerializedName("esercente_comune")
    private boolean esercenteComune    = false;
    
    public int getID() {
        return id;
    }
    
    public String getTitolo() {
        return titolo;
    }
    
    public String getTitoloBreve() {
        return titoloBreve;
    }
    
    public URL getUrlImmagine() {
        return urlImmagine;
    }
    
    public float getValoreAcquisto() {
        return valoreAcquisto;
    }
    
    public float getValoreFacciale() {
        return valoreFacciale;
    }
    
    public float getSconto() {
        return sconto;
    }
    
    public String getScontoPer() {
        return scontoPer;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    public String getDescrizioneBreve() {
        return descrizioneBreve;
    }
    
    public String getCondizioni() {
        return condizioni;
    }
    
    public Date getCouponPeriodoDal() {
        return couponPeriodoDal;
    }
    
    public Date getInizioValidita() {
        return inizioValidita;
    }
    
    public Date getFineValidita() {
        return fineValidita;
    }
    
    public int getIdEsercente() {
        return idEsercente;
    }
    
    public String getNomeEsercente() {
        return nomeEsercente;
    }
    
    public String getIndirizzoEsercente() {
        return indirizzoEsercente;
    }
    
    public boolean isEsercenteComune() {
        return esercenteComune;
    }
    
}
