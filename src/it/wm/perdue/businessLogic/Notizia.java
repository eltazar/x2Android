
package it.wm.perdue.businessLogic;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class Notizia {
    @SerializedName("ID")
    private int    id     = -1;
    @SerializedName("post_title")
    private String titolo = null;
    @SerializedName("post_date")
    private Date   data   = null;
    @SerializedName("post_content")
    private String testo  = null;
    
    public int getId() {
        return id;
    }
    
    public String getTitolo() {
        return titolo;
    }
    
    public Date getData() {
        return data;
    }
    
    public String getLocalizedDataString() {
        // TODO: implementare!
        return "";
    }
    
    public String getTesto() {
        return testo;
    }
}
