
package it.wm.perdue.businessLogic;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class Commento {
    @SerializedName("comment_ID")
    private int    id     = -1;
    @SerializedName("comment_author")
    private String autore = null;
    @SerializedName("comment_content")
    private String testo  = null;
    @SerializedName("comment_date")
    private Date   data   = null;

    public int getId() {
        return id;
    }

    public String getAutore() {
        return autore;
    }

    public String getTesto() {
        return testo;
    }

    public String getData() {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALIAN);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.ITALIAN);
        return dateFormat.format(data) + " " + timeFormat.format(data);
    }

    public void setData(String data) {
        // new DateFormat();
        // data = new Date(data);
    }

    public class DateSerializer implements JsonSerializer<Date> {
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd kk:mm:ss");
            return new JsonPrimitive(dateFormat.format(src));
        }
    }

    public static class DateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String dateString = json.getAsJsonPrimitive().getAsString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            Date convertedDate;
            try {
                convertedDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                return new Date(0l);
            }
            return convertedDate;
        }
    }

}
