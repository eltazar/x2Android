/**
 * 
 */

package it.wm.perdue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;

/**
 * @author Gabriele "Whisky" Visconti
 */
public final class Utils {
    
    private final static String APP_PREFERENCES = "AppPref";
    
    public static String stripEsercente(String c) {
        StringBuilder builder = new StringBuilder(c.trim());
        
        if (builder.length() >= 13 && builder.substring(0, 13).equals("{\"Esercente\":")) {
            builder.delete(0, 13);
            builder.deleteCharAt(builder.length() - 1);
        }
        
        if (builder.length() >= 23 && builder.substring(0, 23).equals("{\"Esercente:FirstRows\":")) {
            Log.d("UTILS", "SUBSTRING firstRows = " + builder.substring(0, 23));
            builder.delete(0, 23);
            builder.deleteCharAt(builder.length() - 1);
        }
        if (builder.length() >= 22 && builder.substring(0, 22).equals("{\"Esercente:MoreRows\":")) {
            Log.d("UTILS", "SUBSTRING moreRows = " + builder.substring(0, 22));
            builder.delete(0, 22);
            builder.deleteCharAt(builder.length() - 1);
        }
        if (builder.length() >= 20 && builder.substring(0, 20).equals("{\"Esercente:Search\":")) {
            Log.d("UTILS", "SUBSTRING search = " + builder.substring(0, 20));
            builder.delete(0, 20);
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
    
    public static String getPreferenceString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCES,
                0);
        return settings.getString(key, defaultValue);
    }
    
    public static void setPreferenceString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(
                APP_PREFERENCES,
                0);
        
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
    public static String getWeekDay(Context context) {
        
        String dayFromSharedPreferences = getPreferenceString(context, "when", "Qui");
        
        if (dayFromSharedPreferences.equals("Luned“")) {
            return "Lunedi";
        }
        if (dayFromSharedPreferences.equals("Marted“")) {
            return "Martedi";
        }
        if (dayFromSharedPreferences.equals("Mercoled“")) {
            return "Mercoledi";
        }
        if (dayFromSharedPreferences.equals("Gioved“")) {
            return "Giovedi";
        }
        if (dayFromSharedPreferences.equals("Venerd“")) {
            return "Venerdi";
        }
        if (dayFromSharedPreferences.equals("Sabato")) {
            return "Sabato";
        }
        if (dayFromSharedPreferences.equals("Domenica")) {
            return "Domenica";
        }
        if (dayFromSharedPreferences.equals("Oggi")) {
            
            Calendar calendar = Calendar.getInstance();
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            
            switch (weekDay) {
                case 1:
                    return "Domenica";
                case 2:
                    return "Lunedi";
                    
                case 3:
                    return "Martedi";
                    
                case 4:
                    return "Mercoledi";
                    
                case 5:
                    return "Giovedi";
                    
                case 6:
                    return "Venerdi";
                    
                case 7:
                    return "Sabato";
                    
                default:
                    break;
            }
        }
        return "";
    }
    
}
