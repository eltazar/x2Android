package it.wm.perdue.coupon;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.perdue.LoggingHandler;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Coupon;
import it.wm.perdue.forms.BaseFormActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DetailCouponListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener, OnClickListener {
    private static final String                         DEBUG_TAG  = "CouponListFragment";
    protected static final String                       TAG_NORMAL = "normal";
    
    // Gestione dei download:
    protected HTTPAccess                                httpAccess = null;
    protected String                                    urlString  = null;
    private ProgressDialog                              progressDialog;
    
    // Gestione dello stato della lista:
    protected DetailCouponAdapter<Coupon> adapter    = null;
    private Parcelable                                  listState  = null;
    
    //dati esercente
    protected String                                    eseId      = null;
    private Button                                      buyButton = null;
    private TextView                                    offerTextView = null;
    private TextView                                    expiryTimerTextView = null;
    private OnCouponActionListener                      listener = null;
    private CountDownTimer                              countDownTimer = null;
    private long                                        millisTot = 0;
    private boolean                                     isCouponOfTheDay = false;
    
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
        
//      urlString = "http://www.cartaperdue.it/partner/v2.0/DettaglioEsercenteCompleto.php?id="
//      + eseId;
        
        if(isCouponOfTheDay)
        getSherlockActivity().getActionBar().setTitle("Coupon del giorno");
        else getSherlockActivity().getActionBar().setTitle("Coupon");

        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        urlString = "http://www.cartaperdue.it/partner/android/coupon2.php?prov=" + "Roma";
//        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
//                null, TAG_NORMAL);
        //showProgressDialog();
        onCreateAdapters();     
        setListAdapter(adapter);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("couponList","onActivityCreated");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("couponList","onCreateView");

      View view = inflater.inflate(R.layout.coupon, container, false);
      buyButton = (Button) view.findViewById(R.id.buyButton);
      buyButton.setOnClickListener(this);
      offerTextView = (TextView) view.findViewById(R.id.summaryTextView);
      offerTextView.setText("Caricamento...");
      expiryTimerTextView = (TextView)view.findViewById(R.id.expiryString);
      
      return view;
    }
    
    @Override
    public void onDestroy(){
        super.onDestroy();
        httpAccess.setResponseListener(null);
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
    }
    
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("couponList","onDestroyView");
        // setListAdapter(null);
    }
    
    public void onResume(){
        super.onResume();
        Log.d("couponList","onResume");        
        
        //ripeto query
        if(/*isCouponOfTheDay ||*/ adapter.getObject() == null){
            Log.d("couponList","onResume adapter model è null, rifaccio query");
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
                    null, TAG_NORMAL);
            showProgressDialog();
        }
        
        millisTot = 0;
        //su ios facevo così, qui non so se serve... 
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        
        if(adapter.getObject() != null){
            setHeaderViews();
        }
        
        //forzo a ricaricare i dati del model per far ripartire il timer
        //adapter.notifyDataSetChanged();
    }
    
    public void onStop(){
        super.onStop();
        Log.d("couponList","onStop");
        if(countDownTimer != null)
            countDownTimer.cancel();
    }
    
    protected void onCreateAdapters() {
        adapter = new DetailCouponAdapter<Coupon>(
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
        //Log.d("couponList", "RISPOSTA = " + response);
        buyButton.setEnabled(true);
        
        if (tag.equals(TAG_NORMAL)) {
            adapter.addFromJSON(response);
            jsonString = response;
            setHeaderViews();
        }
        progressDialog.dismiss();
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO: aggiungere tasto TAP TO REFRESH
        progressDialog.dismiss();
        CharSequence text = "C'è stato un problema, riprova!";
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    /*Metodi privati
     * */
    //setta l'header fisso del coupon
    private void setHeaderViews(){
        Coupon c = adapter.getObject();
        if(c != null){
            buyButton.setEnabled(true);
        }
        offerTextView.setText(Html.fromHtml("Solo <b>"+c.getValoreAcquisto()+"€</b>, sconto <b>"+c.getScontoPer()+"</b>" ));
        setTimer();
    }
    
    //imposta il timer preso in input una data di scadenza
    private void setTimer(){
        
        Date expiryDate = adapter.getObject().getFineValidita();
        //creo il timer appena la view torna visibile
          Date now = new Date();
          DateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
          
          try{
              //Log.d("uffa","data scadenza coupon1 = "+coupon.getFineValidita().toString());
              //Date scad = formatter.parse("Sat Mar 16 23:59:00 CET 2013");
              //Log.d("uffa","data scadenza coupon2 = "+scad.toString());
              //String scadT = formatter.format(coupon.getFineValidita().toString());
              Log.d("uffa","data di scad ---> "+expiryDate.toString());
              Log.d("uffa", "data now --->"+formatter.format(now));
              millisTot = expiryDate.getTime() - now.getTime();
              Log.d("Timer","millisecondti minutes = "+millisTot);
          }
          catch(IllegalArgumentException e){
              e.printStackTrace();
          }
          countDownTimer = new CountDownTimer(millisTot, 1000) {
              int count = 0;
              public void onTick(long millisUntilFinished) {
                  //Log.d("COUNT","COUNT = "+(count++));
                  //Log.d("Timer","millisUntilFinished = "+millisUntilFinished);
                  countDown(millisUntilFinished);
              }

              public void onFinish() {
                  expiryTimerTextView.setText(Html.fromHtml("<b>Scade tra:</b> offerta scaduta - 2"));
                  buyButton.setEnabled(false);
              }
           }.start();
      }
      
    //effettua il countdown a partire da un tot di tempo
    private void countDown(long millisUntilFinished){
        long days;
        long minutes;
        long seconds;
        long hours;
        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
        
        if (secondsLeft > 0) {
            secondsLeft = secondsLeft - 1;
            days= secondsLeft / (3600*24);
            hours = (secondsLeft - (days * 24 * 3600)) / 3600;
            minutes = (secondsLeft - ((hours * 3600) + (days * 24 * 3600))) / 60;
            seconds = secondsLeft % 60;
            
            expiryTimerTextView.setText(Html.fromHtml("<b>Scade tra:</b> "+days+"g "+hours+"h "+minutes+"m "+ seconds+"s"));
        } 
        else {
            secondsLeft = 0;
            expiryTimerTextView.setText(Html.fromHtml("<b>Scade tra:</b> offerta scaduta - 1"));
            buyButton.setEnabled(false);
            //annullo il timer quando scade il countDown
            countDownTimer.cancel();
        }
    }
    
    private void showProgressDialog(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Caricamento in corso...");
        //progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true); 
        progressDialog.show();
    }
    
    /*Metodi privati END
     * */
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
    
    
    protected class DetailCouponAdapter<T extends Coupon> extends
            CouponJSONAdapter<T> {
        
        public DetailCouponAdapter(Context context, int resource, Class<T> clazz) {
            super(context, resource, clazz);
          //  Log.d("UUU", "DETTAGLIO ESERCENTE ADAPTER --> count = "+sections.size());
        }
        
        public View getView(int position, View v, ViewGroup parent) {
            
            
            int layout = 0;
            
            switch(position){
                case 0:
                    //titolo
                    layout = R.layout.coupon_title_row;
                    break;
                case 1:
                    //dettaglio
                    layout = R.layout.coupon_detail_row;
                    break;
                case 2:
                    //altro
                    layout = R.layout.coupon_options_row;
                    break;
            }
            
            if (v == null) {
                v = inflater.inflate(layout, null);
            }
            if (coupon != null) {
                if(position == 0){
                    TextView title = (TextView) v.findViewById(R.id.coupon_title_row); 
                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    title.setText(coupon.getTitoloBreve());
                }
                else if(position == 1){
                    Log.d("couponList","coupon diverso null");
                    TextView price = (TextView) v.findViewById(R.id.couponPrice);            
                    TextView discount = (TextView) v.findViewById(R.id.couponDiscount);  
                    TextView differencePrice = (TextView) v.findViewById(R.id.couponDifference);   
                    //TextView timer = (TextView) v.findViewById(R.id.couponExipiry);  
                    TextView normalPrice = (TextView) v.findViewById(R.id.couponNormalPrice);  
                    CachedAsyncImageView image = (CachedAsyncImageView) v.findViewById(R.id.couponImage);
                    
                    String urlImg = "http://www.cartaperdue.it/coupon/img_offerte/";
                    image.loadImageFromURL(urlImg+coupon.getUrlImmagine());
                    discount.setText(Html.fromHtml("<b> Sconto </b>" + "<br />"
                            + coupon.getScontoPer()));
                    differencePrice.setText(Html.fromHtml("<b> Risparmio </b>" + "<br />"
                            + Utils.formatPrice(coupon.getSconto())+"€"));
                    normalPrice.setText(Html.fromHtml("<b> Prezzo pieno </b>" + "<br />"
                            + Utils.formatPrice(coupon.getValoreFacciale())+"€"));
                    price.setText(Html.fromHtml("<b> Solo </b>" + "<br />"
                            + Utils.formatPrice(coupon.getValoreAcquisto())+"€"));      
                    //setTimer(timer);
                }
            }
            return v;
        }

        /*
        private void setTimer(TextView t){
          //creo il timer appena la view torna visibile
            Date now = new Date();
            DateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
            final TextView timer = t;
            
            try{
                //Log.d("uffa","data scadenza coupon1 = "+coupon.getFineValidita().toString());
                //Date scad = formatter.parse("Sat Mar 16 23:59:00 CET 2013");
                //Log.d("uffa","data scadenza coupon2 = "+scad.toString());
                //String scadT = formatter.format(coupon.getFineValidita().toString());
                Log.d("uffa","data di scad ---> "+coupon.getFineValidita().toString());
                Log.d("uffa", "data now --->"+formatter.format(now));
                millisTot = coupon.getFineValidita().getTime() - now.getTime();
                Log.d("Timer","millisecondti minutes = "+millisTot);
            }
            catch(IllegalArgumentException e){
                e.printStackTrace();
            }
            countDownTimer = new CountDownTimer(millisTot, 1000) {
                int count = 0;
                public void onTick(long millisUntilFinished) {
                    //Log.d("COUNT","COUNT = "+(count++));
                    //Log.d("Timer","millisUntilFinished = "+millisUntilFinished);
                    countDown(millisUntilFinished, timer);
                }

                public void onFinish() {
                    timer.setText(Html.fromHtml("<b>Scade tra:</b> offerta scaduta - 2"));
                }
             }.start();
        }*/
        /*
        private void countDown(long millisUntilFinished, TextView timer){
            long days;
            long minutes;
            long seconds;
            long hours;
            long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
            
            if (secondsLeft > 0) {
                secondsLeft = secondsLeft - 1;
                days= secondsLeft / (3600*24);
                hours = (secondsLeft - (days * 24 * 3600)) / 3600;
                minutes = (secondsLeft - ((hours * 3600) + (days * 24 * 3600))) / 60;
                seconds = secondsLeft % 60;
                
                timer.setText(Html.fromHtml("<b>Scade tra:</b> "+days+"g "+hours+"h "+minutes+"m "+ seconds+"s"));
            } 
            else {
                secondsLeft = 0;
                timer.setText(Html.fromHtml("<b>Scade tra:</b> offerta scaduta - 1"));
                //annullo il timer quando scade il countDown
                countDownTimer.cancel();
            }
        }*/
    }
    
}
