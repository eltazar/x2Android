package it.wm.perdue.coupon;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.perdue.LoggingHandler;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.WebviewActivity;
import it.wm.perdue.businessLogic.Coupon;
import it.wm.perdue.dettaglioEsercenti.DettaglioEsercenteBaseActivity;
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
    protected DetailCouponAdapter<Coupon>               adapter    = null;
    private Parcelable                                  listState  = null;
    
    //dati esercente
    protected String                                    eseId      = null;
    private Button                                      buyButton = null;
    private ImageButton                                 refreshButton = null;
    private TextView                                    offerTextView = null;
    private TextView                                    expiryTimerTextView = null;
    private OnCouponActionListener                      listener = null;
    private CountDownTimer                              countDownTimer = null;
    private long                                        millisTot = 0;
    private boolean                                     isCouponOfTheDay = false;
    private String                                      jsonString = null;
    private int                                         idCoupon = -1;
    
    public interface OnCouponActionListener{
        public void onDidCheckout(Coupon c);
        public void onDidReceiveCoupon(Coupon c);
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
        
        //Log.d("couponList","onCreate");
        
        Bundle args = getArguments();
        if(args != null){
            idCoupon = args.getInt("couponId");
        }
        
        /*se 0 assumiamo query per coupon del giorno
         * se -1 problema
         * se > 0 è id per query con id
         * */
        //eseId = args.getString(ESE_ID);
        
        //Log.d("couponList","id coupon = "+idCoupon);
        if(idCoupon == 0){
            urlString = "http://www.cartaperdue.it/partner/android/coupon2.php?prov=" + "Roma";
            isCouponOfTheDay = true;
            getSherlockActivity().getActionBar().setTitle("Coupon del giorno");
        }
        else if (idCoupon > 0){
            urlString = "http://www.cartaperdue.it/partner/android/offerta2.php?id="+idCoupon;
            isCouponOfTheDay = false;
            getSherlockActivity().getSupportActionBar().setTitle("Coupon");
        }
        else{
            //mostra alert "coupon non disponibile"
        }            

        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        //urlString = "http://www.cartaperdue.it/partner/android/coupon2.php?prov=" + "Roma";
//        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
//                null, TAG_NORMAL);
        //showProgressDialog();
        onCreateAdapters();     
        setListAdapter(adapter);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d("couponList","onActivityCreated");
        
        if(savedInstanceState != null){
           // Log.d("couponList","recupero stato jsonstring");
            jsonString = savedInstanceState.getString("couponModel");
            if(jsonString != null){
                adapter.addFromJSON(jsonString);
                setHeaderViews();
            }
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.d("couponList","onCreateView");

      View view = inflater.inflate(R.layout.coupon, container, false);
      buyButton = (Button) view.findViewById(R.id.buyButton);
      buyButton.setOnClickListener(this);
      refreshButton = (ImageButton) view.findViewById(R.id.refreshBtn);
      refreshButton.setOnClickListener(this);
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
       // Log.d("couponList","salvo stato jsonstring");

        outState.putString("couponModel", jsonString);
    }
    
    public void onDestroyView() {
        super.onDestroyView();
       // Log.d("couponList","onDestroyView");
        // setListAdapter(null);
    }
    
    public void onResume(){
        super.onResume();
        //Log.d("couponList","onResume");        
        
        //ripeto query
        if(isCouponOfTheDay || adapter.getObject() == null){
           // Log.d("couponList","onResume adapter model è null, rifaccio query");
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
       // Log.d("couponList","onStop");
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
           // Log.d("couponList","fragment è visibile");
        }
        else {
           // Log.d("couponList","fragment non è visibile");

        }
        
    }
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {        
       // Log.d("couponList", "RISPOSTA = " + response);

        if (tag.equals(TAG_NORMAL)) {
            adapter.addFromJSON(response);
            jsonString = response;
            setHeaderViews();
            listener.onDidReceiveCoupon(adapter.getObject());
        }
        progressDialog.dismiss();
    }
    
    @Override
    public void onHTTPerror(String tag) {
        progressDialog.dismiss();
        setHeaderErrorViews();
        CharSequence text = "C'è stato un problema, riprova!";
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    /*Metodi privati
     * */
    //setta l'header fisso del coupon
    private void setHeaderViews(){
        Coupon c = adapter.getObject();
        if(c != null && c.getID() >= 0){
            refreshButton.setVisibility(View.INVISIBLE);
            refreshButton.setEnabled(false);
            buyButton.setVisibility(View.VISIBLE);
            buyButton.setEnabled(true);
            offerTextView.setText(Html.fromHtml("Solo <b>"+Utils.formatPrice(c.getValoreAcquisto())+"€</b>, sconto <b>"+c.getScontoPer()+"</b>" ));
            setTimer();
        }
        else{
            offerTextView.setText("Spiacenti, nessuna offerta");
            buyButton.setEnabled(false);
        }
    }
    
    private void setHeaderErrorViews(){
        offerTextView.setText("");
        buyButton.setEnabled(false);
        buyButton.setVisibility(View.INVISIBLE);
        refreshButton.setVisibility(View.VISIBLE);
        refreshButton.setEnabled(true);
    }
    
    private void refreshView(){
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
                null, TAG_NORMAL);
        showProgressDialog();
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
              //Log.d("uffa","data di scad ---> "+expiryDate.toString());
              //Log.d("uffa", "data now --->"+formatter.format(now));
              millisTot = expiryDate.getTime() - now.getTime();
            //  Log.d("Timer","millisecondti minutes = "+millisTot);
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
                  expiryTimerTextView.setText(Html.fromHtml("<b>Scade tra:</b> offerta scaduta"));
                  buyButton.setEnabled(false);
              }

            protected void dismissWaitingProgressDialog(){
                //Log.d("uuu","dismissed");
                progressDialog.dismiss();
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
            expiryTimerTextView.setText(Html.fromHtml("<b>Scade tra:</b> offerta scaduta"));
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
        
        if(position == 2){
            //cella esercente
            Coupon c = adapter.getObject();
            
            Intent intent = new Intent(getSherlockActivity(),DettaglioEsercenteBaseActivity.class);
            //DettaglioEsercenteBaseActivity eseActivity = new DettaglioEsercenteBaseActivity();
            //Bundle bundle = new Bundle();
            intent.putExtra(DettaglioEsercenteBaseActivity.Tags.ID, c.getIdEsercente()+"");
            intent.putExtra(DettaglioEsercenteBaseActivity.Tags.TITLE, c.getNomeEsercente());
            intent.putExtra(DettaglioEsercenteBaseActivity.Tags.COUPON_MODE, true);

            int tipologia = c.getIdTipologiaEsercente();
            //Log.d("TIPO","TIPOLOGIA = "+tipologia);
            
            if(tipologia == -1){
                //senza contratto
                intent.putExtra(DettaglioEsercenteBaseActivity.Tags.GENERICO_MODE, true);
            }
            else if ((tipologia ==  2) || (tipologia ==  5) || (tipologia ==  6) ||
                    (tipologia ==  9) || (tipologia == 59) || (tipologia == 60) ||
                    (tipologia == 61) || (tipologia == 27)) {
                //con contratto ristorazione
                intent.putExtra(DettaglioEsercenteBaseActivity.Tags.IS_RISTO, true);
                intent.putExtra(DettaglioEsercenteBaseActivity.Tags.GENERICO_MODE, false);
            }
            else { 
                //con qlc contratto
                intent.putExtra(DettaglioEsercenteBaseActivity.Tags.IS_RISTO, false);
                intent.putExtra(DettaglioEsercenteBaseActivity.Tags.GENERICO_MODE, false);
            }
            getSherlockActivity().startActivity(intent);
        }
        else if(position == 4){
            try {
                Utils.callNumber(((TextView) v.findViewById(R.id.contactResource))
                        .getText().toString().replace(" ", ""), getActivity());
            } catch (ActivityNotFoundException e) {
                //Log.e("helloandroid dialing example", "Call failed", e);
                Toast.makeText(getActivity(),
                        "Spiacenti, non è possibile chiamare il numero selezionato.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if(position == 5){
            try {
                Utils.writeEmail(((TextView) v.findViewById(R.id.contactResource))
                        .getText().toString(), getActivity());
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "Spiacenti, non ci sono client di posta installati.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if(position == 6){
            Intent i = new Intent(getSherlockActivity(), WebviewActivity.class);
            i.putExtra("contentTitle","F.A.Q.");
            i.putExtra("urlContent","http://www.cartaperdue.it/partner/android/faq.html");
            startActivity(i);
        }
        
    }
    
    @Override
    public void onClick(View v) {
        //buy button pressed
        switch(v.getId()){
            case R.id.buyButton:
                buyButtonPressed();
                break;
            case R.id.refreshBtn:
                //Log.d("couponList","detail btn pressed");
                refreshView();
                break;
        }
    }
    
    protected void buyButtonPressed(){
        if(LoggingHandler.isLogged()){
            //mostra Checkout
          //  Log.d("couponList","mostra checkout");
            Coupon coupon = adapter.getObject();    
            //Log.d("coupon","coupon in detail scaricato: "+coupon.getID()+" "+coupon.getDescrizioneBreve());
            listener.onDidCheckout(coupon);
        }
        else{
            //mostra login
         //   Log.d("couponList","mostra login");
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
            
            
            int layout = getItemViewType(position);
            
                v = inflater.inflate(layout, null);
            
            if (coupon != null && coupon.getID() >= 0 ) {
                if(position == 0){
                    TextView title = (TextView) v.findViewById(R.id.coupon_title_row); 
                    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    title.setText(coupon.getTitoloBreve());
                }
                else if(position == 1){
                   // Log.d("couponList","coupon diverso null");
                    TextView price = (TextView) v.findViewById(R.id.couponPrice);            
                    TextView discount = (TextView) v.findViewById(R.id.couponDiscount);  
                    TextView differencePrice = (TextView) v.findViewById(R.id.couponDifference);   
                    //TextView timer = (TextView) v.findViewById(R.id.couponExipiry);  
                    TextView normalPrice = (TextView) v.findViewById(R.id.couponNormalPrice);  
                    CachedAsyncImageView image = (CachedAsyncImageView) v.findViewById(R.id.couponImage);
                    image.setOnClickListener(this);
                    String urlImg = "http://www.cartaperdue.it/coupon/img_offerte/";
                    image.loadImageFromURL(urlImg+coupon.getUrlImmagine());
                    discount.setText(Html.fromHtml("<b> Sconto </b>" + "<br />"
                            + coupon.getScontoPer()));
                    differencePrice.setText(Html.fromHtml("<b> Risparmio </b>" + "<br />"
                            + Utils.formatPrice(coupon.getSconto())+"€"));
                    normalPrice.setText(Html.fromHtml("<b> Prezzo pieno </b>" + "<br />"
                            + Utils.formatPrice(coupon.getValoreFacciale())+"€"));
                    price.setText(Html.fromHtml("<b>Solo</b>" + "<br/>"
                            + Utils.formatPrice(coupon.getValoreAcquisto())+"€"));      
                }
                else if(position == 2){
                    //riga esercente
                    TextView eseName = (TextView) v.findViewById(R.id.eseName);
                    TextView eseAddress = (TextView) v.findViewById(R.id.eseAddress);

                    eseName.setText(coupon.getNomeEsercente());
                    eseAddress.setText(coupon.getIndirizzoEsercente());
                }
                else if(position == 3){
                    ImageButton detailBtn = (ImageButton) v.findViewById(R.id.detailBtn);
                    ImageButton rulesBtn = (ImageButton) v.findViewById(R.id.rulesBtn);
                    ImageButton infoBtn = (ImageButton) v.findViewById(R.id.infoBtn);
                    detailBtn.setOnClickListener(this);
                    rulesBtn.setOnClickListener(this);
                    infoBtn.setOnClickListener(this);

                    
                    if(coupon.getDescrizioneEstesa() == null || coupon.getDescrizioneEstesa().equals("")){
                        infoBtn.setEnabled(false);
                    }
                }
                else if(position == 4){
                    // telefono x2
                    setContactRows("Contatta PerDue","800 73 73 83",R.drawable.ic_phone, v);
                }
                else if(position == 5){
                    //mail x2
                    setContactRows("Scrivi a PerDue","redazione@cartaperdue.it",R.drawable.ic_mail, v);
                }
                else if(position == 6){
                    //faq x2
                    setContactRows("F.A.Q.","Domande frequenti",R.drawable.ic_faq, v);
                }
                
            }
            return v;
        }
        
        public boolean isEnabled(int position) {
            if(position == 0 || position == 1)
                return false;
            else return true;
         }
        
        private void setContactRows(String kind,String resource, int img, View v){
            TextView contactKind = (TextView) v.findViewById(R.id.contactKind);
            TextView contactResource = (TextView) v.findViewById(R.id.contactResource);
            ImageView contactImg = (ImageView) v.findViewById(R.id.contactImage);
            contactResource.setText(resource);
            contactKind.setText(kind);
            contactImg.setImageResource(img);
        }
        
        @Override
        public void onClick(View v) {
            //buy button pressed
            switch(v.getId()){
                case R.id.detailBtn:
                 //   Log.d("couponList","detail btn pressed");
                    loadWebPage(coupon.getDescrizioneBreve(),"Dettagli offerta");
                    break;
                case R.id.infoBtn:
                  //  Log.d("couponList","info btn pressed");
                    loadWebPage(coupon.getDescrizioneEstesa(),"Per saperne di più");
                    break;
                case R.id.rulesBtn:
                  //  Log.d("couponList","rules btn pressed");
                    loadWebPage(coupon.getCondizioni(),"Condizioni");
                    break;
                case R.id.couponImage:
                    String urlImage = "http://www.cartaperdue.it/coupon/img_offerte/"+coupon.getUrlImmagine();
                    Intent i = new Intent(getSherlockActivity(),FullImageActivity.class);
                    i.putExtra("urlImage",urlImage);
                    i.putExtra("eseName",coupon.getNomeEsercente());
                    startActivity(i);
                    break;
            }
        }
        
        private void loadWebPage(String contentData, String contentTitle){
            Intent i = new Intent(getSherlockActivity(), WebviewActivity.class);
            i.putExtra("content", contentData);
            i.putExtra("contentTitle",contentTitle);
            startActivity(i);
        }
    }
    
}
