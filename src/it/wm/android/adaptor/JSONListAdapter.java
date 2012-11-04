
package it.wm.android.adaptor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import it.wm.perdue.businessLogic.Notizia;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONListAdapter<T> extends ArrayAdapter<T> {
    // Un costruttore esplicito a casaccio giusto per far star zitto il
    // compilatore
    public JSONListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }
    
    private class JSONFormat extends ArrayList<T> {
    };
    
    public void addFromJSON(String jsonString) {
        StringBuilder builder = new StringBuilder(jsonString);
        builder.replace(jsonString.length() - ",false]".length(), jsonString.length(), "]");
        Log.d("", builder.toString());
        new AsyncTask<String, Void, List>() {
            
            @Override
            protected List doInBackground(String... params) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                // onRegisterDeserializers();
                /*
                 * gsonBuilder.registerTypeAdapter(java.util.Date.class, new
                 * Commento.DateDeserializer());
                 */
                Gson gson = gsonBuilder.create();
                Type typeToken = new TypeToken<ArrayList<Notizia>>() {
                }.getType();
                Object objects = null;
                // try {
                objects = gson.fromJson(params[0], typeToken);
                // } catch (ClassNotFoundException e) {
                // Log.d("JSON", "Seh, ciaooo!!!!");
                // }
                return (List) objects;
            }
            
            protected void onPostExecute(List result) {
                JSONListAdapter.this.addAll(result);
            }
            
        }.execute(builder.toString());
    }
}
