package it.wm.perdue.coupon;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.businessLogic.Coupon;

public class CouponListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener {
    private static final String                         DEBUG_TAG  = "CouponListFragment";
    protected static final String                       TAG_NORMAL = "normal";
    
    // Gestione dei download:
    protected HTTPAccess                                httpAccess = null;
    protected String                                    urlString  = null;
    private ProgressDialog                              progressDialog;
    
    // Gestione dello stato della lista:
    protected CouponJSONAdapter<Coupon> adapter    = null;
    private Parcelable                                  listState  = null;
    
    //dati esercente
    protected String                                    eseId      = null;

    
    
    public static CouponListFragment newInstance(String eseId) {
        CouponListFragment fragment = new CouponListFragment();
        Bundle args = new Bundle();
        //args.putString(ESE_ID, eseId);
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d("coupon","onCreate");
        
        Bundle args = getArguments();
        //eseId = args.getString(ESE_ID);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        urlString = "http://www.cartaperdue.it/partner/v2.0/DettaglioEsercenteCompleto.php?id="
                + eseId;
        
        onCreateAdapters();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("coupon","onActivityCreated");

        ListView lv = getListView();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        String urlImageString = "http://www.cartaperdue.it/partner/v2.0/ImmagineEsercente.php?id="
                + eseId;
        
        setListAdapter(adapter);
        
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
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("coupon","onCreateView");

        View view = inflater.inflate(R.layout.coupon, container, false);
        return view;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (adapter.getCount() > 0) {
//            outState.putInt("nRows", adapter.getCount());
//        }
//        if (listState != null) {
//            outState.putParcelable("listState", listState);
//        } else {
//            outState.putParcelable("listState", getListView().onSaveInstanceState());
//        }
        
    }
    
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("coupon","onDestroyView");

        // setListAdapter(null);
        httpAccess.setResponseListener(null);
    }
    
    public void onResume(){
        super.onResume();
  
        Log.d("coupon","onResume");
    }
    
    public void onStop(){
        super.onStop();
        Log.d("coupon","onStop");
    }
    
    
    protected void onCreateAdapters() {
        adapter = new CouponJSONAdapter<Coupon>(
                getActivity(),
                R.layout.esercente_row, Coupon.class);
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        
        //http://stackoverflow.com/questions/10024739/how-to-determine-when-fragment-becomes-visible-in-viewpager
        
        if (isVisibleToUser == true) { 
            Log.d("coupon","fragment è visibile");
        }
        else {
            Log.d("coupon","fragment non è visibile");

        }
        
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
        }
        progressDialog.dismiss();
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
        
    }
    
    protected void dismissWaitingProgressDialog(){
        //Log.d("uuu","dismissed");
        progressDialog.dismiss();
    }
    
    /*
    protected static class DettaglioEsercenteAdapter<T extends Esercente> extends
            CouponJSONAdapter<T> {
        
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
    */
}
