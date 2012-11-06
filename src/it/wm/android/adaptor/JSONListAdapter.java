
package it.wm.android.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import it.wm.perdue.Utils;

public class JSONListAdapter<T> extends ArrayAdapter<T> {
    Class<T[]> clazz = null;
    
    public JSONListAdapter(Context context, int resource, int textViewResourceId, Class<T[]> clazz) {
        super(context, resource, textViewResourceId);
        this.clazz = clazz;
    }
    
    public int addFromJSON(String jsonString) {
        jsonString = Utils.stripEsercente(jsonString);
        jsonString = Utils.stripFinalFalse(jsonString);
        
        /*
         * new AsyncTask<String, Void, T[]>() {
         * @Override protected T[] doInBackground(String... params) {
         * GsonBuilder gsonBuilder = new GsonBuilder();
         * gsonBuilder.setDateFormat("yyyy-MM-dd kk:mm:ss");
         * onRegisterDeserializers(gsonBuilder); Gson gson =
         * gsonBuilder.create(); return gson.fromJson(params[0],
         * JSONListAdapter.this.clazz); } protected void onPostExecute(T[]
         * result) { JSONListAdapter.this.addAll(result); }
         * }.execute(builder.toString());
         */
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd kk:mm:ss");
        onRegisterDeserializers(gsonBuilder);
        Gson gson = gsonBuilder.create();
        T[] objects = null;
        try {
            objects = gson.fromJson(builder.toString(), clazz);
        } catch (JsonSyntaxException e) {
            // In teoria se siamo qui, significa che è arrivato un array vuoto,
            objects = gson.fromJson("[]", clazz);
        }
        this.supportAddAll(objects);
        return objects.length;
    }
    
    protected void onRegisterDeserializers(GsonBuilder gsonBuilder) {
    }
    
    @SuppressLint("NewApi")
    public void supportAddAll(T... items) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(items);
        } else {
            for (T item : items) {
                super.add(item);
            }
        }
    }
}
