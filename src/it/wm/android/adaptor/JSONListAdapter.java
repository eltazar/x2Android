
package it.wm.android.adaptor;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class JSONListAdapter<T> extends ArrayAdapter<T> {
    // Un costruttore esplicito a casaccio giusto per far star zitto il
    // compilatore
    public JSONListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }
    
    public void addFromJSON(String jsonString) {
        new AsyncTask<String, Void, List<T>>() {
            
            @Override
            protected List<T> doInBackground(String... params) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                // onRegisterDeserializers();
                /*
                 * gsonBuilder.registerTypeAdapter(java.util.Date.class, new
                 * Commento.DateDeserializer());
                 */
                Gson gson = gsonBuilder.create();
                Type typeToken = new TypeToken<List<T>>() {
                }.getType();
                List<T> objects = gson.fromJson(params[0], typeToken);
                return objects;
            }
            
            protected void onPostExecute(List<T> result) {
                JSONListAdapter.this.addAll(result);
            }
            
        }.execute(jsonString);
    }
    
}
