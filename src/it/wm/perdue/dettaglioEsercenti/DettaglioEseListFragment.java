
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;
import it.wm.perdue.businessLogic.HasID;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DettaglioEseListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener {
    private static final String       DEBUG_TAG  = "DettaglioEseListFragment";
    protected static final String     TAG_NORMAL = "normal";
    protected static final String     ESE_ID     = "eseId";
    
    // Gestione dei download:
    protected HTTPAccess              httpAccess = null;
    protected String                  urlString  = null;
    protected HashMap<String, String> postMap    = null;
    
    // Gestione dello stato della lista:
    protected ArrayAdapter<Esercente> adapter    = null;
    private Parcelable                listState  = null;
    
    private String                    eseId      = null;
    
    public static DettaglioEseListFragment newInstance(String eseId) {
        DettaglioEseListFragment fragment = new DettaglioEseListFragment();
        Bundle args = new Bundle();
        args.putString(ESE_ID, eseId);
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle args = getArguments();
        eseId = args.getString(ESE_ID);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        urlString = "http://www.cartaperdue.it/partner/v2.0/DettaglioEsercenteCompleto.php?id="
                + eseId;
        
        onCreateAdapters();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        ListView lv = getListView();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
                null, TAG_NORMAL);
    }
    
    protected void onCreateAdapters() {
        // adapter = new DettaglioEsercenteJSONListAdapter(
        // getActivity(),
        // R.layout.esercente_row);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter.getCount() > 0) {
            outState.putInt("nRows", adapter.getCount());
        }
        if (listState != null) {
            outState.putParcelable("listState", listState);
        } else {
            outState.putParcelable("listState", getListView().onSaveInstanceState());
        }
        
    }
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // downloading--;
        
        Log.d("XXX", "RISPOSTA = " + response);
        Esercente esercente = null;
        
        if (tag.equals(TAG_NORMAL)) {
            // Se riceviamo un risultato non di ricerca, lo aggiungiamo sempre e
            // comunque:
            esercente = Utils.getEsercenteFromJSON(response);
            adapter = new DettaglioEsercenteAdapter<Esercente>(
                    getActivity(),
                    R.layout.esercente_row, esercente);
            setListAdapter(adapter);
        }
        
    }
    
    @Override
    public void onHTTPerror(String tag) {
        Log.d(DEBUG_TAG, "Errore nel download");
        // downloading--;
        // Log.d(DEBUG_TAG, "Donwloading " + downloading);
    }
    
    /* *** END: HTTPAccess.ResponseListener ******************* */
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        
    }
    
    private static class DettaglioEsercenteAdapter<T extends HasID> extends ArrayAdapter<Esercente> {
        
        private Esercente         esercente = null;
        private ArrayList<String> sections  = null;
        private Context           context   = null;
        
        public DettaglioEsercenteAdapter(Context context, int resource, Esercente esercente) {
            super(context, resource);
            // TODO: creare datamodel da esercente
            this.esercente = esercente;
            this.context = context;
            Log.d("XXX", "DETTAGLIO ESERCENTE ADAPTER --> " + this.esercente.getID());
            sections = new ArrayList<String>();
            checkFields();
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            
            View v = convertView;
            int resource = 0;
            TextView textView = null;
            TextView contactTextView = null;
            TextView kindContactTextView = null;
            ImageView mapImage = null;
            
            if (v == null) {
                
                if (sections.get(position).equals("info")) {
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
                    resource = R.layout.contact_row;
                }
                
                v = ((LayoutInflater) super.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(resource, null);
            }
            
            if (esercente != null) {
                
                // title.setText(Html.fromHtml("<b>" + esercente.getCitta() +
                // "</b>" + "<br />" +
                // "<small>" + esercente.getIndirizzo() + "</small>" + "<br />"
                // +
                // "<small>" + esercente.getInsegna() + "</small>"
                // + "<br />" +
                // "<small>" + esercente.getNoteVarie() + "</small>"));
                
                if (sections.get(position).equals("info")) {
                    textView = (TextView) v.findViewById(R.id.infoRow);
                    textView.setText(Html.fromHtml((
                            esercente.getGiornoChiusura() != null ? "<b> Giorno di chiusura</b>"
                                    + "<br />" +
                                    esercente.getGiornoChiusura() + "<br />" : "")
                            +
                            (esercente.getGiorniString() != null ?
                                    "<b> Giorni validitˆ </b>" + "<br />"
                                            + esercente.getGiorniString() + "<br />" : ""
                            )
                            + (esercente.getNoteVarie() != null ? "<b> Condizioni</b>" + "<br />"
                                    + esercente.getNoteVarie() : "")));
                    
                }
                else if (sections.get(position).equals("map")) {
                    textView = (TextView) v.findViewById(R.id.mapInfo);
                    textView.setText(Html.fromHtml(
                            (esercente.getCitta() != null ? "<b>Cittˆ</b>" + "<br />" +
                                    esercente.getCitta() + "<br />" : "")
                                    +
                                    (esercente.getZona() != null ? "<b> Zona </b>" + "<br />"
                                            + esercente.getZona()
                                            + "<br />" : "")
                                    +
                                    (esercente.getIndirizzo() != null ? "<b> Indirizzo</b>"
                                            + "<br />" + esercente.getIndirizzo() : "")));
                    
                    mapImage = (ImageView) v.findViewById(R.id.mapImage);
                    
                    String urlString =
                            "http://maps.googleapis.com/maps/api/staticmap?" +
                                    "zoom=16&size=600x240&markers=size:big|color:red|" +
                                    esercente.getLatitude() +
                                    "," +
                                    esercente.getLongitude() +
                                    "&sensor=false";
                    ProgressBar pB = (ProgressBar) v.findViewById(R.id.mapImageProgress);
                    mapImage.setTag(urlString);
                    
                    new DownloaderImageTask().execute(mapImage, pB);
                }
                else if (sections.get(position).equals("tel")) {
                    contactTextView = (TextView) v.findViewById(R.id.contactResource);
                    kindContactTextView = (TextView) v.findViewById(R.id.contactKind);
                    contactTextView.setText(esercente.getTelefono());
                    kindContactTextView.setText("Telefono");
                }
                else if (sections.get(position).equals("mail")) {
                    contactTextView = (TextView) v.findViewById(R.id.contactResource);
                    kindContactTextView = (TextView) v.findViewById(R.id.contactKind);
                    contactTextView.setText(esercente.getEmail());
                    kindContactTextView.setText("E-mail");
                }
                else if (sections.get(position).equals("url")) {
                    contactTextView = (TextView) v.findViewById(R.id.contactResource);
                    kindContactTextView = (TextView) v.findViewById(R.id.contactKind);
                    contactTextView.setText(esercente.getUrl());
                    kindContactTextView.setText("Sito web");
                }
                else if (sections.get(position).equals("altre")) {
                    contactTextView = (TextView) v.findViewById(R.id.contactResource);
                    kindContactTextView = (TextView) v.findViewById(R.id.contactKind);
                    contactTextView.setText(esercente.getTelefono());
                    kindContactTextView.setText("PROVA ALTRO");
                }
            }
            return v;
        }
        
        @Override
        public int getCount() {
            Log.d("XXX", "COUNT = " + sections.size());
            return sections.size();
        }
        
        private void checkFields() {
            
            if (esercente.getGiorni() != null || esercente.getGiornoChiusura() != null ||
                    esercente.getNoteVarie() != null) {
                sections.add("info");
            }
            if (esercente.getCitta() != null || esercente.getZona() != null
                    || esercente.getIndirizzo() != null) {
                sections.add("map");
            }
            if (esercente.isUlterioriInfo()) {
                sections.add("altre");
            }
            
            if (esercente.getUrl() != null) {
                sections.add("url");
            }
            if (esercente.getEmail() != null) {
                sections.add("mail");
            }
            
            if (esercente.getTelefono() != null) {
                sections.add("tel");
            }
            
        }
        
        public class DownloaderImageTask extends AsyncTask<Object, ProgressBar, Bitmap> {
            
            private ImageView   imageView = null;
            private ProgressBar pB        = null;
            
            protected Bitmap doInBackground(Object... imageViews) {
                
                this.imageView = (ImageView) imageViews[0];
                this.pB = (ProgressBar) imageViews[1];
                
                return downloadImage((String) imageView.getTag());
            }
            
            @Override
            protected void onPostExecute(Bitmap result) {
                result = Utils.getDropShadow3(result);
                imageView.setImageBitmap(result);
                pB.setVisibility(View.INVISIBLE);
            }
            
            private Bitmap downloadImage(String url) {
                URL myUrl = null;
                InputStream inputStream = null;
                try {
                    myUrl = new URL(url);
                    inputStream = (InputStream) myUrl.getContent();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            
        }
    }
}
