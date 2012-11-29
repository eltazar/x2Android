/**
 * 
 */

package it.wm.perdue;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import it.wm.perdue.businessLogic.Esercente;

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
    
    public static String stripSingleEsercente(String c) {
        StringBuilder builder = new StringBuilder(c.trim());
        if (builder.length() >= 14 && builder.substring(0, 14).equals("{\"Esercente\":[")) {
            builder.delete(0, 14);
            builder.deleteCharAt(builder.length() - 1);
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
    
    public static GeoPoint geoPoint(double latitude, double longitude) {
        return new GeoPoint(
                Math.round((float) (latitude * 1E6)),
                Math.round((float) (longitude * 1E6)));
    }
    
    public static Esercente getEsercenteFromJSON(String jsonString) {
        
        Log.d("XXX", "UTILS PRIMA = " + jsonString);
        jsonString = Utils.stripSingleEsercente(jsonString);
        Log.d("XXX", "UTILS DOPO = " + jsonString);
        
        Gson gson = Utils.getGson();
        Esercente esercente = null;
        try {
            esercente = gson.fromJson(jsonString, Esercente.class);
        } catch (JsonSyntaxException e) {
            // In teoria se siamo qui, significa che Ã¨ arrivato un array vuoto,
            Log.d("XXX", "Utils: errore parsing json");
            e.printStackTrace();
            esercente = gson.fromJson("[]", Esercente.class);
        }
        
        return esercente;
    }
    
    public static Bitmap getDropShadow3(Bitmap bitmap) {
        
        if (bitmap == null)
            return null;
        int think = 6;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        
        int newW = w - (think);
        int newH = h - (think);
        
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);
        Bitmap sbmp = Bitmap.createScaledBitmap(bitmap, newW, newH, false);
        
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas c = new Canvas(bmp);
        
        // Right
        Shader rshader = new LinearGradient(newW, 0, w, 0, Color.GRAY, Color.LTGRAY,
                Shader.TileMode.CLAMP);
        paint.setShader(rshader);
        c.drawRect(newW, think, w, newH, paint);
        
        // Bottom
        Shader bshader = new LinearGradient(0, newH, 0, h, Color.GRAY, Color.LTGRAY,
                Shader.TileMode.CLAMP);
        paint.setShader(bshader);
        c.drawRect(think, newH, newW, h, paint);
        
        // Corner
        Shader cchader = new LinearGradient(0, newH, 0, h, Color.LTGRAY, Color.LTGRAY,
                Shader.TileMode.CLAMP);
        paint.setShader(cchader);
        c.drawRect(newW, newH, w, h, paint);
        
        c.drawBitmap(sbmp, 0, 0, null);
        
        return bmp;
    }
    
}
