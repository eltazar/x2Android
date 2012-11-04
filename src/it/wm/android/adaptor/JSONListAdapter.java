
package it.wm.android.adaptor;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONListAdapter<T> extends ArrayAdapter<T> {
    Class<T[]> clazz = null;
    
    public JSONListAdapter(Context context, int resource, int textViewResourceId, Class<T[]> clazz) {
        super(context, resource, textViewResourceId);
        this.clazz = clazz;
    }
    
    public void addFromJSON(String jsonString) {
        StringBuilder builder = new StringBuilder(jsonString);
        
        if (builder.substring(0, 13).equals("{\"Esercente\":")) {
            builder.delete(0, 13);
            builder.deleteCharAt(builder.length() - 1);
        }
        
        int start = builder.length() - ",false]".length();
        int end = builder.length();
        if (builder.substring(start, end).equals(",false]")) {
            builder.replace(start, end, "]");
        }
        
        new AsyncTask<String, Void, T[]>() {
            
            @Override
            protected T[] doInBackground(String... params) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("yyyy-MM-dd kk:mm:ss");
                onRegisterDeserializers(gsonBuilder);
                Gson gson = gsonBuilder.create();
                return gson.fromJson(params[0], JSONListAdapter.this.clazz);
            }
            
            protected void onPostExecute(T[] result) {
                JSONListAdapter.this.addAll(result);
            }
            
        }.execute(builder.toString());
    }
    
    protected void onRegisterDeserializers(GsonBuilder gsonBuilder) {
    }
}
