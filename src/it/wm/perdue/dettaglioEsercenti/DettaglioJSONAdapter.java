
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;

import java.util.ArrayList;

public class DettaglioJSONAdapter<T extends Esercente> extends
        ArrayAdapter<T> {
    Class<T>          clazz     = null;
    ArrayList<String> sections  = null;
    Context           context   = null;
    T                 esercente = null;
    
    public DettaglioJSONAdapter(Context context, int resource, Class<T> clazz) {
        super(context, resource);
        this.clazz = clazz;
        this.context = context;
        sections = new ArrayList<String>();
    }
    
    @Override
    public int getCount() {
        Log.d("XXX", "COUNT = " + sections.size());
        return sections.size();
    }
    
    public void addFromJSON(String jsonString) {
        jsonString = Utils.formatJSON(jsonString);
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        
        Gson gson = Utils.getGson();
        try {
            this.esercente = gson.fromJson(jsonString, clazz);
            super.add(this.esercente);
            checkFields();
            
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }
    
    protected void checkFields() {
        
        if (esercente.getCitta() != null || esercente.getZona() != null
                || esercente.getIndirizzo() != null) {
            sections.add("map");
        }
        if (esercente.isUlterioriInfo()) {
            sections.add("altre");
        }
        if (esercente.getTelefono() != null) {
            sections.add("tel");
        }
        if (esercente.getEmail() != null) {
            sections.add("mail");
        }
        if (esercente.getUrl() != null) {
            sections.add("url");
        }
        
        Log.d("XXX", "fine checkfield madre");
        
    }
    
    @Override
    public void clear() {
        super.clear();
        sections.clear();
    }
    
}
