/**
 * 
 */

package it.wm.perdue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * @author Gabriele "Whisky" Visconti
 */
public final class Utils {
    
    private final static String APP_PREFERENCES       = "AppPref";
    public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
                                                              .compile(
                                                              "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
                                                                      +
                                                                      "\\@"
                                                                      +
                                                                      "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"
                                                                      +
                                                                      "("
                                                                      +
                                                                      "\\."
                                                                      +
                                                                      "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"
                                                                      +
                                                                      ")+"
                                                              );
    private static Context context = null; 
    
    public static boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
    
    private static String stripEsercente(CharSequence c) {
        StringBuilder builder = new StringBuilder(c.toString().trim());
        
        if(builder.length() >= 10 && builder.substring(0, 10).equals("{\"login\":[") ){
            builder.delete(0, 10);
            builder.deleteCharAt(builder.length() - 1);
        }
        if (builder.length() >= 14 && builder.substring(0, 14).equals("{\"Esercente\":[")) {
            builder.delete(0, 13);
            builder.deleteCharAt(builder.length() - 1);
        }
        if (builder.length() >= 13 && builder.substring(0, 13).equals("{\"Esercente\":")) {
            builder.delete(0, 13);
            builder.deleteCharAt(builder.length() - 1);
        }
        if (builder.length() >= 23 && builder.substring(0, 23).equals("{\"Esercente:FirstRows\":")) {
            builder.delete(0, 23);
            builder.deleteCharAt(builder.length() - 1);
        }
        if (builder.length() >= 22 && builder.substring(0, 22).equals("{\"Esercente:MoreRows\":")) {
            builder.delete(0, 22);
            builder.deleteCharAt(builder.length() - 1);
        }
        if (builder.length() >= 20 && builder.substring(0, 20).equals("{\"Esercente:Search\":")) {
            builder.delete(0, 20);
            builder.deleteCharAt(builder.length() - 1);
        }
        
        return builder.toString();
    }
    
    private static String stripFinalFalse(CharSequence c, boolean isReplace) {
        StringBuilder builder = new StringBuilder(c);
        int start = builder.length() - ",false]".length();
        int end = builder.length();
        if(isReplace){
            if (start >= 0 && builder.substring(start, end).equals(",false]")) {
                builder.replace(start, end, "]");
            }
        }
        else{
            if (start >= 0 && builder.substring(start, end).equals(",false]")) {
                builder.replace(start, end, "");
            }
        }
        return builder.toString();
    }
    
    private static String formatBooleans(CharSequence c) {
        StringBuilder builder = new StringBuilder(c);
        int index;
        while ((index = builder.indexOf("\"0\"")) != -1) {
            builder.replace(index, index + 3, "false");
        }
        while ((index = builder.indexOf("\"1\"")) != -1) {
            builder.replace(index, index + 3, "true");
        }
        return builder.toString();
    }
    
    public static String formatJSONwithoutBool(CharSequence c) {
        String json;
        json = stripEsercente(c);
        json = stripFinalFalse(json,true);
        return json;
    }
    
    public static String formatJSON(CharSequence c) {
        String json;
        json = stripEsercente(c);
        json = stripFinalFalse(json,true);
        json = formatBooleans(json);
        return json;
    }
    
    public static String formatJSONlogin(CharSequence c) {
        String json;
        json = stripEsercente(c);
        json = stripFinalFalse(json,false);
        return json;
    }
    
    public static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd kk:mm:ss")
                .create();
    }
    
    public static void setContext(Context c) {
        context = c;
    }
    
    public static String getPreferenceString(String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(
                APP_PREFERENCES,
                0);
        return settings.getString(key, defaultValue);
    }
    
    public static void setPreferenceString(String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(
                APP_PREFERENCES,
                0);
        
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
    public static String getWeekDay() {
        
        String day = getPreferenceString("when", "Qui");
        
        if (day.equals("Lunedì")) {
            return "Lunedi";
        } else if (day.equals("Martedì")) {
            return "Martedi";
        } else if (day.equals("Mercoledì")) {
            return "Mercoledi";
        } else if (day.equals("Giovedì")) {
            return "Giovedi";
        } else if (day.equals("Venerdì")) {
            return "Venerdi";
        } else if (day.equals("Sabato")) {
            return "Sabato";
        } else if (day.equals("Domenica")) {
            return "Domenica";
        } else if (day.equals("Oggi")) {
            return today();
        } else {
            return today();
        }
    }
    
    public static String today() {
        Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
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
                return null;
        }
    }
    
    public static GeoPoint geoPoint(double latitude, double longitude) {
        return new GeoPoint(
                Math.round((float) (latitude * 1E6)),
                Math.round((float) (longitude * 1E6)));
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
    
    public static String replacePlusInPhone(String phone) {
        
        StringBuilder newPhone = new StringBuilder("");
        boolean isPlus = false;
        
        if (phone.subSequence(0, 1).equals("+")) {
            newPhone.append("%2B");
            isPlus = true;
        }
        char[] chars = phone.toCharArray();
        
        // loop through chars array and print out values separated with a space
        for (int i = 0; i < phone.length(); i++) {
            if (isPlus && i == 0)
                continue;
            newPhone.append(chars[i]);
        }
        
        // Log.d("XXX", "STRINGA SOSTITUITA -> " + newPhone);
        return newPhone.toString();
        
    }
    
    public static void callNumber(String number, Context context) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        context.startActivity(callIntent);
    }
    
    public static void openBrowser(String urlString, Context context) {
        
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://"))
            urlString = "http://" + urlString;
        
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        context.startActivity(browserIntent);
    }
    
    public static void writeEmail(String address, Context context) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        i.putExtra(Intent.EXTRA_SUBJECT, "Informazioni");
        i.putExtra(Intent.EXTRA_TEXT, "");
        context.startActivity(Intent.createChooser(i, "Invia email"));
    }
    
    public static boolean isNumeric(String str)
    {          
      String s = str.replace(" ", "");
      //match a number with optional '-' and decimal.
      return s.matches("-?\\d+(\\.\\d+)?"); 
    }    
    
    public static String formatPrice(double price) {
        // Oook, lo so, si vede che di base resto sempre un programmatore C...
        if (price / 1.00 == (int)price) {
            return (int)price+"";
        } 
        else {
            return String.format("%.2f", price);
        }
    }
    
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
