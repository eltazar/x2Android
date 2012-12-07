
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DettaglioEseListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener {
    private static final String                         DEBUG_TAG  = "DettaglioEseListFragment";
    protected static final String                       TAG_NORMAL = "normal";
    protected static final String                       ESE_ID     = "eseId";
    
    // Gestione dei download:
    protected HTTPAccess                                httpAccess = null;
    protected String                                    urlString  = null;
    
    // Gestione dello stato della lista:
    protected DettaglioJSONAdapter<? extends Esercente> adapter    = null;
    private Parcelable                                  listState  = null;
    
    protected String                                    eseId      = null;
    
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
        
        setListAdapter(adapter);
        
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
                null, TAG_NORMAL);
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
    
    public void onDestroyView() {
        super.onDestroyView();
        // setListAdapter(null);
        httpAccess.setResponseListener(null);
    }
    
    protected void onCreateAdapters() {
        adapter = new DettaglioEsercenteAdapter<Esercente>(
                getActivity(),
                R.layout.esercente_row, Esercente.class);
    }
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // downloading--;
        
        Log.d("XXX", "RISPOSTA = " + adapter);
        
        if (tag.equals(TAG_NORMAL)) {
            // Se riceviamo un risultato non di ricerca, lo aggiungiamo sempre e
            // comunque:
            adapter.addFromJSON(response);
        }
        
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO: aggiungere tasto TAP TO REFRESH
        
        Log.d(DEBUG_TAG, "Errore nel download");
        // downloading--;
        // Log.d(DEBUG_TAG, "Donwloading " + downloading);
    }
    
    /* *** END: HTTPAccess.ResponseListener ******************* */
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        
        Bundle extras = new Bundle();
        // extras.putSerializable("notizia", (Serializable)
        // l.getItemAtPosition(position));
        Intent intent = new Intent(getActivity(), AltreInfoActivity.class);
        extras.putString("eseId", eseId);
        extras.putString("title", getSherlockActivity().getSupportActionBar().getTitle()
                .toString());
        intent.putExtras(extras);
        startActivity(intent);
        
    }
    
    protected static class DettaglioEsercenteAdapter<T extends Esercente> extends
            DettaglioJSONAdapter<T> {
        
        public DettaglioEsercenteAdapter(Context context, int resource, Class<T> clazz) {
            super(context, resource, clazz);
            Log.d("XXX", "DETTAGLIO ESERCENTE ADAPTER --> ");
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            
            View v = convertView;
            int resource = 0;
            TextView textView = null;
            TextView contactTextView = null;
            TextView kindContactTextView = null;
            ImageView mapImage = null;
            
            Log.d("XXX", "get view madre position --> " + position);
            
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
                    resource = R.layout.action_row;
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
                    
                    String giorniString = null;
                    
                    try {
                        giorniString = (esercente.getGiorniString() != null ?
                                "<b> Giorni validitˆ </b>" + "<br />"
                                        + esercente.getGiorniString() + "<br />" : "");
                    } catch (NullPointerException e) {
                        Log.d(DEBUG_TAG, "eccezione in getView: " + e.getLocalizedMessage());
                    }
                    
                    textView.setText(Html.fromHtml((
                            esercente.getGiornoChiusura() != null ? "<b> Giorno di chiusura</b>"
                                    + "<br />" +
                                    esercente.getGiornoChiusura() + "<br />" : "")
                            +
                            (giorniString != null ? giorniString : "")
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
                                    "zoom=16&size=512x240&markers=size:big|color:red|" +
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
        protected void checkFields() {
            if (esercente.getGiorni() != null || esercente.getGiornoChiusura() != null ||
                    esercente.getNoteVarie() != null) {
                sections.add(0, "info");
            }
            super.checkFields();
        }
        
        protected class DownloaderImageTask extends AsyncTask<Object, ProgressBar, Bitmap> {
            
            private ImageView   imageView = null;
            private ProgressBar pB        = null;
            
            protected Bitmap doInBackground(Object... imageViews) {
                
                this.imageView = (ImageView) imageViews[0];
                this.pB = (ProgressBar) imageViews[1];
                
                return downloadImage((String) imageView.getTag());
            }
            
            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    result = Utils.getDropShadow3(result);
                    imageView.setImageBitmap(result);
                    pB.setVisibility(View.INVISIBLE);
                }
                else {
                    // TODO: mostrare pulsante TAP TO REFRESH
                }
            }
            
            private Bitmap downloadImage(String url) {
                URL myUrl = null;
                InputStream inputStream = null;
                Bitmap bitmap = null;
                try {
                    myUrl = new URL(url);
                    inputStream = (InputStream) myUrl.getContent();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    bitmap = null;
                }
                
                return bitmap;
            }
            
        }
    }
}
