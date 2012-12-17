
package it.wm.perdue;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.wm.perdue.businessLogic.HasID;

import java.util.ArrayList;
import java.util.List;

public class JSONListAdapter<T extends HasID> extends ArrayAdapter<T> {
    //private static final String DEBUG_TAG = "JONListAdapter"; 
    Class<T[]>    clazz = null;
    List<Integer> ids   = null;
    protected LayoutInflater inflater = null; 

    
    public JSONListAdapter(Context context, int resource, Class<T[]> clazz) {
        super(context, resource);
        this.clazz = clazz;
        this.ids = new ArrayList<Integer>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public int addFromJSON(String jsonString) throws NullPointerException {
        jsonString = Utils.formatJSON(jsonString);
        
        //Log.d("JSONListAdapter", "JSONSTRING = " + jsonString);
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
            //Log.d("JSONListAdapter", "Ho rilevato un array vuoto");
            e.printStackTrace();
            objects = gson.fromJson("[]", clazz);
        }
        for (T object : objects) {
            int id = object.getID();
            if (!ids.contains(id)) {
                ids.add(id);
                super.add(object);
            }
        }
        return objects.length;
    }
    
    @Override
    public void clear() {
        super.clear();
        ids.clear();
    }
}
