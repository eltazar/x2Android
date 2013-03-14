
package it.wm.perdue.dettaglioEsercenti;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.CachedAsyncImageView;
import it.wm.CachedAsyncImageView.Listener;
import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;

public class DettaglioEseListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener, Listener {
    private static final String                         DEBUG_TAG  = "DettaglioEseListFragment";
    protected static final String                       TAG_NORMAL = "normal";
    protected static final String                       ESE_ID     = "eseId";

    // Gestione dei download:
    protected HTTPAccess                                httpAccess = null;
    protected String                                    urlString  = null;
    private ProgressDialog                              progressDialog;
    
    // Gestione dello stato della lista:
    protected DettaglioJSONAdapter<? extends Esercente> adapter    = null;
    private Parcelable                                  listState  = null;
    
    //dati esercente
    protected String                                    eseId      = null;
    protected static boolean                            isCoupon = false;
    protected static boolean                            isGenerico = false;
    protected CachedAsyncImageView                      cachedImg = null;
    private   String                                    jsonData  = null;

    public static DettaglioEseListFragment newInstance(String eseId, boolean mode, boolean generic) {
        DettaglioEseListFragment fragment = new DettaglioEseListFragment();
        Bundle args = new Bundle();
        args.putString(ESE_ID, eseId);
        fragment.setArguments(args);            
        isCoupon = mode;
        isGenerico = generic;
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle args = getArguments();
        eseId = args.getString(ESE_ID);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);

        if(isGenerico){
            Log.d("dettaglioEse","esercente generico query");
            urlString = "http://www.cartaperdue.it/partner/DettaglioEsercenteGenerico.php?id="
                    + eseId;
        }
        else if(isCoupon){
            Log.d("dettaglioEse","coupon mode query");
            urlString = "http://www.cartaperdue.it/partner/DettaglioEsercente.php?id="
                    + eseId;
        }
        else{
            Log.d("dettaglioEse","esercente senza contratto query");
            urlString = "http://www.cartaperdue.it/partner/v2.0/DettaglioEsercenteCompleto.php?id="
                    + eseId;
        }
        onCreateAdapters();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View detailImg = inflater
                .inflate(R.layout.ese_dettaglio_header_image, null);
        
        String urlImageString = "http://www.cartaperdue.it/partner/v2.0/ImmagineEsercente.php?id="
                + eseId;
        cachedImg = ((CachedAsyncImageView) detailImg.findViewById(R.id.dettaglioImg));
        cachedImg.setListener(this);
        cachedImg.loadImageFromURL(urlImageString);
        lv.addHeaderView(detailImg, null, false);
        
        setListAdapter(adapter);
        
        if(savedInstanceState != null){
            Log.d("couponList","recupero stato jsonstring");
            jsonData = savedInstanceState.getString("eseModel");
            adapter.addFromJSON(jsonData);
        }
        else{
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
                    null, TAG_NORMAL);
            
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Caricamento in corso...");
            //progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true); 
            //        progressDialog.setOnDismissListener(new DialogInterface.OnCancelListener(){
            //           @Override
            //           public void onCancel(DialogInterface dialog)
            //           {
            //               **** cleanup.  Not needed if not cancelable ****
            //           }});
            progressDialog.show();
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter.getCount() > 0) {
            outState.putInt("nRows", adapter.getCount());
            outState.putString("eseModel", jsonData);
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
        
        //Log.d("xxx", "RISPOSTA = " + response);
        
        if (tag.equals(TAG_NORMAL)) {
            // Se riceviamo un risultato non di ricerca, lo aggiungiamo sempre e
            // comunque:
            adapter.addFromJSON(response);
            saveData(response);
        }
        progressDialog.dismiss();
    }
    
    private void saveData(String jsonData){
        this.jsonData = jsonData;
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO: aggiungere tasto TAP TO REFRESH
        
        //Log.d(DEBUG_TAG, "Errore nel download");
        // downloading--;
        // Log.d(DEBUG_TAG, "Donwloading " + downloading);
        progressDialog.dismiss();
        CharSequence text = "C'è stato un problema, riprova!";
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    /* *** END: HTTPAccess.ResponseListener ******************* */
    
    // TODO: sistemare sto schifo che sto facendo per la fretta. soprattutto
    // sistemare come capire chi è la cella cliccata
    // dato che il nostro model non è mai lo stesso
    public void onListItemClick(ListView l, View v, int position, long id) {
        
        String cellKind = ((TextView) v.findViewById(R.id.cellKind)).getText().toString();
        
        if (cellKind != null && cellKind.equals("action")) {
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
        else if (cellKind != null && cellKind.equals("mail")) {
            //Log.d("AAA", " CELLA MAIL-->" + ((TextView) v.findViewById(R.id.contactResource))
                   // .getText().toString());
            
            try {
                Utils.writeEmail(((TextView) v.findViewById(R.id.contactResource))
                        .getText().toString(), getActivity());
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "Spiacenti, non ci sono client di posta installati.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (cellKind != null && cellKind.equals("tel")) {
            
//            Log.d("AAA", " CELLA TEL-->" + ((TextView) v.findViewById(R.id.contactResource))
//                    .getText().toString());
            
            try {
                Utils.callNumber(((TextView) v.findViewById(R.id.contactResource))
                        .getText().toString(), getActivity());
            } catch (ActivityNotFoundException e) {
                //Log.e("helloandroid dialing example", "Call failed", e);
                Toast.makeText(getActivity(),
                        "Spiacenti, non è possibile chiamare il numero selezionato.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (cellKind != null && cellKind.equals("web")) {
//            Log.d("AAA", " CELLA web -> " + ((TextView) v.findViewById(R.id.contactResource))
//                    .getText().toString());
            try {
                Utils.openBrowser(((TextView) v.findViewById(R.id.contactResource))
                        .getText().toString(), getActivity());
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), "Spiacenti, non è stato possibile aprire la pagina.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    protected void dismissWaitingProgressDialog(){
        //Log.d("uuu","dismissed");
        progressDialog.dismiss();
    }
    
    protected static class DettaglioEsercenteAdapter<T extends Esercente> extends
            DettaglioJSONAdapter<T> {
        
        public DettaglioEsercenteAdapter(Context context, int resource, Class<T> clazz) {
            super(context, resource, clazz);
          //  Log.d("UUU", "DETTAGLIO ESERCENTE ADAPTER --> count = "+sections.size());
        }
        
        public View getView(int position, View v, ViewGroup parent) {
            
            v = super.getView(position, v, parent);
            
            
            if (esercente != null) {
                
                TextView infoTextView = null;            
                if (sections.get(position).equals("info")) {
                    infoTextView = (TextView) v.findViewById(R.id.infoRow);
                    //infoTextView.setTextColor(Color.BLACK);

                    String giorniString = null;
                    
                    if(isCoupon){
                        infoTextView.setText(Html.fromHtml((
                                esercente.getGiornoChiusura() != null ? "<b> Giorno di chiusura</b>"
                                        + "<br />" +
                                        esercente.getGiornoChiusura(): "")));
                    }
                    else {                    
                        try {
                            giorniString = (esercente.getGiorniString() != null ?
                                    "<b> Giorni validità </b>" + "<br />"
                                    + esercente.getGiorniString() + "<br />" : "");
                        } catch (NullPointerException e) {
                            Log.d(DEBUG_TAG, "eccezione in getView: " + e.getLocalizedMessage());
                        }
                        
                        infoTextView.setText(Html.fromHtml((
                                esercente.getGiornoChiusura() != null ? "<b> Giorno di chiusura</b>"
                                        + "<br />" +
                                        esercente.getGiornoChiusura() + "<br />" : "")
                                        +
                                        (giorniString != null ? giorniString : "")
                                        + (esercente.getNoteVarie() != null ? "<b> Condizioni</b>" + "<br />"
                                                + esercente.getNoteVarie() : "")));
                    }
                }
            }
            return v;
        }
        
        @Override
        protected void checkFields() {
            
            super.checkFields();
            if (esercente.getGiorni() != null || esercente.getGiornoChiusura() != null ||
                    esercente.getNoteVarie() != null) {
                sections.add(0,"info");
            }
        }
        
        
        // @Override
        // public boolean isEnabled(int position) {
        //
        // if (sections.get(position).equals("info") ||
        // sections.get(position).equals("info") ||
        // sections.get(position).equals("map")
        // || sections.get(position).equals("infoRisto")) {
        // return false;
        // }
        // else
        // return true;
        // }
        //
        // @Override
        // public boolean areAllItemsEnabled() {
        // return true;
        // }
        //

    }

    @Override
    public void onImageLoadingCompleted(CachedAsyncImageView imageView) {
        Log.d("dettaglio","IMMAGINE CARICATA");     
        if(imageView.getImageView().getDrawable() == null){
            Log.d("dettaglio","immagine vuota");
            cachedImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onImageLoadingFailed(CachedAsyncImageView imageView) {
        Log.d("dettaglio","IMMAGINE NON CARICATA");                
    }
}
