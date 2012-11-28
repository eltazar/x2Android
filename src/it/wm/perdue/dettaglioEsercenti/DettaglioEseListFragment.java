
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.JSONListAdapter;
import it.wm.perdue.R;
import it.wm.perdue.businessLogic.Esercente;

import java.util.HashMap;

public class DettaglioEseListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener {
    private static final String                    DEBUG_TAG  = "DettaglioEseListFragment";
    protected static final String                  TAG_NORMAL = "normal";
    protected static final String                  ESE_ID     = "eseId";
    
    // Gestione dei download:
    protected HTTPAccess                           httpAccess = null;
    protected String                               urlString  = null;
    protected HashMap<String, String>              postMap    = null;
    
    // Gestione dello stato della lista:
    protected JSONListAdapter<? extends Esercente> adapter    = null;
    private Parcelable                             listState  = null;
    
    private String                                 eseId      = null;
    
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
        
        setListAdapter(adapter);
        
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
                null, TAG_NORMAL);
    }
    
    protected void onCreateAdapters() {
        adapter = new DettaglioEsercenteJSONListAdapter(
                getActivity(),
                R.layout.esercente_row,
                Esercente[].class);
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
        
        int n;
        if (tag.equals(TAG_NORMAL)) {
            // Se riceviamo un risultato non di ricerca, lo aggiungiamo sempre e
            // comunque:
            n = adapter.addFromJSON(response);
            
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
    
    private static class DettaglioEsercenteJSONListAdapter extends JSONListAdapter<Esercente> {
        
        public DettaglioEsercenteJSONListAdapter(Context context, int resource,
                Class<Esercente[]> clazz) {
            super(context, resource, clazz);
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) super.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.esercente_row, null);
            }
            
            Esercente esercente = getItem(position);
            
            // prova html tag
            if (esercente != null) {
                TextView title = (TextView) v.findViewById(R.id.eseTitle);
                
                title.setText(Html.fromHtml("<b>" + esercente.getCitta() + "</b>" + "<br />" +
                        "<small>" + esercente.getIndirizzo() + "</small>" + "<br />" +
                        "<small>" + esercente.getInsegna() + "</small>"));
            }
            return v;
        }
    }
}
