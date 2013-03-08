package it.wm.perdue.coupon;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.LoggingHandler;
import it.wm.perdue.R;
import it.wm.perdue.businessLogic.Coupon;
import it.wm.perdue.forms.BaseFormActivity;

public class DetailCouponListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener, OnClickListener {
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
    private Button                                      buyButton = null;
    private OnCouponActionListener                      listener = null;
    
    
    public interface OnCouponActionListener{
        public void onDidCheckout(Coupon c);
    }
    
    public static DetailCouponListFragment newInstance(String eseId) {
        Log.d("couponList","newInstance");

        DetailCouponListFragment fragment = new DetailCouponListFragment();
        Bundle args = new Bundle();
        //args.putString(ESE_ID, eseId);
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnCouponActionListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnCouponActionListener");
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d("couponList","onCreate");
        
        Bundle args = getArguments();
        //eseId = args.getString(ESE_ID);
        
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("couponList","onActivityCreated");
        
        onCreateAdapters();
        setListAdapter(adapter);

        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
//        urlString = "http://www.cartaperdue.it/partner/v2.0/DettaglioEsercenteCompleto.php?id="
//                + eseId;
        
        String urlImageString = "http://www.cartaperdue.it/partner/v2.0/ImmagineEsercente.php?id="
                + eseId;
        
        urlString = "http://www.cartaperdue.it/partner/android/coupon2.php?prov=" + "Roma";
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
        Log.d("couponList","onCreateView");

        View view = inflater.inflate(R.layout.coupon, container, false);
      buyButton = (Button) view.findViewById(R.id.buyButton);
      buyButton.setOnClickListener(this);
      TextView tv = (TextView) view.findViewById(R.id.summaryTextView);
      tv.setText("CIAOOOOO");
      
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
        Log.d("couponList","onDestroyView");

        // setListAdapter(null);
        httpAccess.setResponseListener(null);
    }
    
    public void onResume(){
        super.onResume();
  
        Log.d("couponList","onResume");
    }
    
    public void onStop(){
        super.onStop();
        Log.d("couponList","onStop");
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
            Log.d("couponList","fragment è visibile");
        }
        else {
            Log.d("couponList","fragment non è visibile");

        }
        
    }
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // downloading--;
        
        Log.d("couponList", "RISPOSTA = " + response);
        buyButton.setEnabled(true);
        
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
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        
    }
    
    protected void dismissWaitingProgressDialog(){
        //Log.d("uuu","dismissed");
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        //buy button pressed
        if(LoggingHandler.isLogged()){
            //mostra Checkout
            Log.d("couponList","mostra checkout");
            Coupon coupon = adapter.getObject();    
            //Log.d("coupon","coupon in detail scaricato: "+coupon.getID()+" "+coupon.getDescrizioneBreve());
            listener.onDidCheckout(coupon);
        }
        else{
            //mostra login
            Log.d("couponList","mostra login");
            Intent i = new Intent(getSherlockActivity(),BaseFormActivity.class);
            startActivity(i);
        }
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
