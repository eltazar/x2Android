/**
 * 
 */

package it.wm.perdue.businessLogic;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class Coupon implements HasID {
    @SerializedName("idofferta")
    private int     id                 = -1;
    @SerializedName("offerta_title")
    private String  titolo             = null; //questo è un po più lungo e va dove ci pare
    @SerializedName("offerta_titolo_breve")
    private String  titoloBreve        = null; //questo va nell'header della view
    @SerializedName("offerta_foto_big")
    private String     urlImmagine        = null;
    @SerializedName("coupon_valore_acquisto")
    private float   valoreAcquisto     = 0; //prezzo di acquisto del coupon
    @SerializedName("coupon_valore_facciale")
    private float   valoreFacciale     = 0; //prezzo non scontato, pieno
    @SerializedName("offerta_sconto_va")
    private float   sconto             = 0; //questoi è quanti euro si risparmia
    @SerializedName("offerta_sconto_per")
    private String  scontoPer          = null; // questa è la percentuale di risparmio
    @SerializedName("offerta_descrizione_estesa")
    private String  descrizioneEstesa        = null; // questo va in "per saperne di più", e praticamente e è null non va mostrata la cella
    @SerializedName("offerta_descrizione_breve") 
    private String  descrizioneBreve   = null; //questo in dettaglio offerta
    @SerializedName("offerta_condizioni_sintetiche") // questo in termini e condizioni
    private String  condizioni         = null;
    @SerializedName("coupon_periodo_dal")
    private Date    couponPeriodoDal   = null; // bho, forse inizio dell'offerta
    @SerializedName("offerta_pediodo_dal")
    private Date    inizioValidita     = null;
    @SerializedName("offerta_periodo_al")
    private Date    fineValidita       = null; //quando scade l'offerta
    @SerializedName("idesercente")
    private int     idEsercente        = -1;
    @SerializedName("esercente_nome")
    private String  nomeEsercente      = null;
    @SerializedName("esercente_indirizzo")
    private String  indirizzoEsercente = null;
    @SerializedName("IdTipologia_Esercente")
    private int  idTipoEsercente = -1;
    //@SerializedName("IDcontratto_Contresercente")
    //private int  idContrattoControes = -1;
    @SerializedName("esercente_comune")
    private String esercenteComune    = null;
    
    public int getID() {
        return id;
    }
    
    public String getTitolo() {
        return titolo;
    }
    /*
    public int getIdContrattoControes(){
        return idContrattoControes;
    }*/
    
    public int idTipoEsercente(){
        return idTipoEsercente;
    }
    
    public String getTitoloBreve() {
        return titoloBreve;
    }
    
    public String getUrlImmagine() {
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
        return descrizioneEstesa;
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
    
    public String getEsercenteComune() {
        return esercenteComune;
    }    
}
