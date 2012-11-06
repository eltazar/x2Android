/**
 * 
 */

package it.wm.perdue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Gabriele "Whisky" Visconti
 */
public final class Utils {
    public static String stripEsercente(CharSequence c) {
        StringBuilder builder = new StringBuilder(c);
        if (builder.substring(0, 13).equals("{\"Esercente\":")) {
            builder.delete(0, 13);
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }
    
    public static String stripFinalFalse(CharSequence c) {
        StringBuilder builder = new StringBuilder(c);
        int start = builder.length() - ",false]".length();
        int end = builder.length();
        if (builder.substring(start, end).equals(",false]")) {
            builder.replace(start, end, "]");
        }
        return builder.toString();
    }
    
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd kk:mm:ss");
        return gsonBuilder.create();
    }
}
