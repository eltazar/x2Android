
package it.wm.perdue.businessLogic;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class Commento implements HasID {
    @SerializedName("comment_ID")
    private int    id     = -1;
    @SerializedName("comment_author")
    private String autore = null;
    @SerializedName("comment_content")
    private String testo  = null;
    @SerializedName("comment_date")
    private Date   data   = null;
    
    public int getID() {
        return id;
    }
    
    public String getAutore() {
        return autore;
    }
    
    public String getTesto() {
        return testo;
    }
    
    public Date getData() {
        return data;
    }
    
    public String getLocalizedDataString() {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.ITALIAN);
        return dateFormat.format(data) + " " + timeFormat.format(data);
    }
}
