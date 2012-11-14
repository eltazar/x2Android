
package it.wm.perdue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class JSONListAdapter<T> extends ArrayAdapter<T> {
    Class<T[]> clazz = null;
    
    public JSONListAdapter(Context context, int resource, Class<T[]> clazz) {
        super(context, resource);
        this.clazz = clazz;
    }
    
    public int addFromJSON(String jsonString) {
        jsonString = Utils.stripEsercente(jsonString);
        jsonString = Utils.stripFinalFalse(jsonString);
        
        Log.d("JSON ADAPTER", "JSONSTRING = " + Utils.stripEsercente(jsonString));
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
        
        Gson gson = Utils.getGson();
        T[] objects = null;
        try {
            objects = gson.fromJson(jsonString, clazz);
        } catch (JsonSyntaxException e) {
            // In teoria se siamo qui, significa che è arrivato un array vuoto,
            objects = gson.fromJson("[]", clazz);
        }
        this.supportAddAll(objects);
        return objects.length;
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
