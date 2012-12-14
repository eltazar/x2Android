
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;

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
    
    private ProgressDialog                              progressDialog;
    
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
        View detailImg = inflater
                .inflate(R.layout.ese_dettaglio_header_image, null);
        
        String urlImageString = "http://www.cartaperdue.it/partner/v2.0/ImmagineEsercente.php?id="
                + eseId;
        ((CachedAsyncImageView) detailImg.findViewById(R.id.dettaglioImg))
                .loadImageFromURL(urlImageString);
        lv.addHeaderView(detailImg, null, false);
        
        setListAdapter(adapter);
        
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
                null, TAG_NORMAL);
        
        progressDialog = ProgressDialog.show(getActivity(), "", "Caricamento in corso...");
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
        
        Log.d("xxx", "RISPOSTA = " + response);
        
        if (tag.equals(TAG_NORMAL)) {
            // Se riceviamo un risultato non di ricerca, lo aggiungiamo sempre e
            // comunque:
            adapter.addFromJSON(response);
        }
        progressDialog.dismiss();
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO: aggiungere tasto TAP TO REFRESH
        
        Log.d(DEBUG_TAG, "Errore nel download");
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
            Log.d("AAA", " CELLA MAIL-->" + ((TextView) v.findViewById(R.id.contactResource))
                    .getText().toString());
            
            try {
                Utils.writeEmail(((TextView) v.findViewById(R.id.contactResource))
                        .getText().toString(), getActivity());
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "Spiacenti, non ci sono client di posta installati.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (cellKind != null && cellKind.equals("tel")) {
            
            Log.d("AAA", " CELLA TEL-->" + ((TextView) v.findViewById(R.id.contactResource))
                    .getText().toString());
            
            try {
                Utils.callNumber(((TextView) v.findViewById(R.id.contactResource))
                        .getText().toString(), getActivity());
            } catch (ActivityNotFoundException e) {
                Log.e("helloandroid dialing example", "Call failed", e);
                Toast.makeText(getActivity(),
                        "Spiacenti, non è possibile chiamare il numero selezionato.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (cellKind != null && cellKind.equals("web")) {
            Log.d("AAA", " CELLA web -> " + ((TextView) v.findViewById(R.id.contactResource))
                    .getText().toString());
            try {
                Utils.openBrowser(((TextView) v.findViewById(R.id.contactResource))
                        .getText().toString(), getActivity());
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), "Spiacenti, non è stato possibile aprire la pagina.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    protected static class DettaglioEsercenteAdapter<T extends Esercente> extends
            DettaglioJSONAdapter<T> {
        
        public DettaglioEsercenteAdapter(Context context, int resource, Class<T> clazz) {
            super(context, resource, clazz);
            Log.d("UUU", "DETTAGLIO ESERCENTE ADAPTER --> count = "+sections.size());
        }
        
        public View getView(int position, View v, ViewGroup parent) {
            
            //View v = convertView;
            int resource = getItemViewType(position);
            Log.d("uuu","resource = "+resource);
            
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
             v = inflater.inflate(resource, null);
               
            if (esercente != null) {
                
                TextView infoTextView = null;            

                
                if (sections.get(position).equals("info")) {
                    infoTextView = (TextView) v.findViewById(R.id.infoRow);
                    
                    String giorniString = null;
                    
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
                else if (sections.get(position).equals("map")) {
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
}
