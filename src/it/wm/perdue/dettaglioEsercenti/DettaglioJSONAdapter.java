
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.wm.CachedAsyncImageView;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;

import java.util.ArrayList;

public class DettaglioJSONAdapter<T extends Esercente> extends
        ArrayAdapter<T> {
    Class<T>          clazz     = null;
    ArrayList<String> sections  = null;
    Context           context   = null;
    T                 esercente = null;
    protected LayoutInflater inflater = null;
    
    public DettaglioJSONAdapter(Context context, int resource, Class<T> clazz) {
        super(context, resource);
        this.clazz = clazz;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sections = new ArrayList<String>();
    }
    
    public View getView(int position, View v, ViewGroup parent) {
       
        //TODO: ragionare bene sul fatto del riciclo delle celle
        /*Dove sono arrivato con i ragionamenti:
         * - abbiamo diversi tipi di righe, e non sempre alcune sono presente della tabella, dipende da quali dati ha l'esercente
         * - quando viene riichiamato getView controllo se v==null e creo una nuova view, facendo così viene popolata bene qls tipo
         * sia la riga
         * - se v != null, viene riciclata ---> crasha perchè ad esempio se getView deve disegnare la riga "email" ma la view riciclata era
         * del tipo ad esempio "info" la funzione cerca in tale view gli id dei textview relativi ai contatti, ed ovviamente sono null.
         * - quindi la view riclciata può esser di tipo differente da quella che dobbiamo disegnare!!!
         * 
         * vale la pena lasciare così? alla fine sono poche righe, e quante volte un utente può scrollare la lista?
         * 
         * **/
        
        //View v = convertView;
        int resource = getItemViewType(position);
        Log.d("uuu","resource = "+resource);
        
        
        v = inflater.inflate(resource, null);
      
        if (esercente != null) {
            TextView infoTextView = null;            

            if (sections.get(position).equals("map")) {
                CachedAsyncImageView mapImage = null;
                
                infoTextView = (TextView) v.findViewById(R.id.mapInfo);
                infoTextView.setText(Html.fromHtml(
                        (esercente.getCitta() != null ? "<b>Città</b>" + "<br />" +
                                esercente.getCitta() + "<br />" : "")
                                +
                                (esercente.getZona() != null ? "<b> Zona </b>" + "<br />"
                                        + esercente.getZona()
                                        + "<br />" : "")
                                        +
                                        (esercente.getIndirizzo() != null ? "<b> Indirizzo</b>"
                                                + "<br />" + esercente.getIndirizzo() : "")));
                
                mapImage = (CachedAsyncImageView) v.findViewById(R.id.mapImage);
                String urlString =
                        "http://maps.googleapis.com/maps/api/staticmap?" +
                                "zoom=14&size=512x240&markers=size:big|color:red|" +
                                esercente.getLatitude() +
                                "," +
                                esercente.getLongitude() +
                                "&sensor=false";
                mapImage.setTag(urlString);
                mapImage.loadImageFromURL(urlString);
                
            }
            else if (sections.get(position).equals("altre")) {
                TextView actionTextView = (TextView) v.findViewById(R.id.action);
                actionTextView.setText("Altre informazioni");
            }
            else{
                
                TextView contactTextView = (TextView) v.findViewById(R.id.contactResource);
                TextView kindContactTextView = (TextView) v.findViewById(R.id.contactKind);
                TextView cellKind = (TextView) v.findViewById(R.id.cellKind);
                ImageView contactImage = (ImageView) v.findViewById(R.id.contactImage);
                
                if (sections.get(position).equals("tel")) {
                    contactTextView.setText(esercente.getTelefono());
                    kindContactTextView.setText("Telefono");
                    cellKind.setText("tel");
                    contactImage.setImageResource(R.drawable.ic_phone);
                }
                else if (sections.get(position).equals("mail")) {
                    contactTextView.setText(esercente.getEmail());
                    kindContactTextView.setText("E-mail");
                    cellKind.setText("mail");
                    contactImage.setImageResource(R.drawable.ic_mail);
                }
                else if (sections.get(position).equals("url")) {
                    contactTextView.setText(esercente.getUrl());
                    kindContactTextView.setText("Sito web");
                    cellKind.setText("web");
                    contactImage.setImageResource(R.drawable.ic_web);
                }
            }
        }
        return v;
        
        
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
    
    public int getItemViewType(int position){
        
        int resource = 0;
        if (sections.get(position).equals("info") || sections.get(position).equals("infoRisto")) {
            resource = R.layout.dettaglio_info_row;
        }
        else if (sections.get(position).equals("map")) {
            resource = R.layout.map_row;
        }
        else if (sections.get(position).equals("tel")
                || sections.get(position).equals("mail") ||
                sections.get(position).equals("url")) {
            resource = R.layout.contact_row;
        }
        else if (sections.get(position).equals("altre")) {
            resource = R.layout.action_row;
        }
        
        return resource;
    }
    
}
