
package it.wm.perdue.businessLogic;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

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
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.ITALIAN);
        return dateFormat.format(data) + " " + timeFormat.format(data);
    }
    
    public String getTesto() {
        return testo;
    }
}
