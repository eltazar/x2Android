/**
 * 
 */
package it.wm.perdue.coupon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Coupon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Gabriele "Whisky" Visconti
 *
 */
public class VetrinaCoupon extends SherlockFragment implements HTTPAccess.ResponseListener {
    private Coupon         coupon = null;
    
    // Gestione dei download:
    private HTTPAccess httpAccess = null;
    private String     urlString  = null;
    // Timer
    private TextView       timerLbl      = null;
    private CountDownTimer countDownTimer = null;
        
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vetrina_coupon, container, false);
        timerLbl = (TextView) view.findViewById(R.id.pd_timer);
        timerLbl.setText("CountDownTimer");
        Button tutteleofferteBtn = (Button) view.findViewById(R.id.pd_tutte_le_offerte_btn);
        tutteleofferteBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getSherlockActivity(), CouponsBaseActivity.class);
                startActivity(i);
            }
        });
        
        RelativeLayout cdelgiornoLayout = (RelativeLayout) view.findViewById(R.id.pd_cdelgiorno);
        cdelgiornoLayout.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                try{
                extras.putInt("couponId", coupon.getID());
                Intent intent = new Intent(getActivity(), DetailCouponBaseActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
                }
                catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        return view;
    }
    
    @Override
    public void onResume (){
        super.onResume();
        urlString = "http://www.cartaperdue.it/partner/android/coupon2.php?prov=" + "Roma";
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, null);
        
    }
    
    
    @Override
    public void onStop (){
        super.onStop();
        Log.d("TIMER","ON STOP");
        //annullo il timer quando la view non è più visibile
        if(countDownTimer != null)
            countDownTimer.cancel();
    }
    
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        Log.d("TIMER", " RISPOSTA = "+response);
        addFromJSON(response);
        showCoupon();
        setTimer();
    }
    
    
    @Override
    public void onHTTPerror(String tag) {
        //Log.d("XXX", "ERRORE INVIO ->" + tag);
     
    }
    /* *** END: HTTPAccess.ResponseListener ****************** */
    
    
    public void setTimer() {
        Log.d("TIMER", "on resume");
        long millisTot = 0;

        Date now = new Date();
        Date expiry = coupon.getFineValidita();
        DateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String scadT = formatter.format(expiry);
        Log.d("TIMER","data di scad ---> "+scadT);
        Log.d("TIMER", "data now --->"+formatter.format(now));
        millisTot = expiry.getTime() - now.getTime();
        Log.d("Timer","millisecondti minutes = " + millisTot);
        

        //su ios facevo così, qui non so se serve... 
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        
        //creo il timer appena la view torna visibile
        countDownTimer = new CountDownTimer(millisTot, 1000) {
            int count = 0;
            public void onTick(long millisUntilFinished) {
                //Log.d("COUNT","COUNT = "+(count++));
                updateTimer(millisUntilFinished);
            }

            public void onFinish() {
                timerLbl.setText("Tempo scaduto");
            }
         }.start();
    }
    
    
    private void showCoupon() {
        Activity c;
        TextView tv;
        c = getSherlockActivity();
        
        try{
            String html =  "<style media=\"screen\" type=\"text/css\">" +
                    "* {                     " +
                    "   font-size: 10pt;      " +
                    "   text-align: justify; " +
                    "   margin: 0;           " +
                    "   padding: 0;          " +
                    "}                       " +
                    "</style>                " + 
                    coupon.getDescrizioneBreve();
            
            tv = (TextView) c.findViewById(R.id.pd_titolo);
            tv.setText(coupon.getTitoloBreve());
            tv = (TextView) c.findViewById(R.id.pd_prezzo_tv);
            tv.setText("Solo "+coupon.getValoreAcquisto() + "€");
            tv = (TextView) c.findViewById(R.id.pd_sconto_tv);
            tv.setText(coupon.getScontoPer());
            tv = (TextView) c.findViewById(R.id.pd_prezzo_originale_tv);
            tv.setText(""+coupon.getValoreFacciale()+"€");
            
            WebView wv = (WebView) c.findViewById(R.id.pd_descrizione_wv);
            wv.loadData(html, "text/html", null);
            
            CachedAsyncImageView iv = (CachedAsyncImageView) c.findViewById(R.id.pd_imageView);
            iv.loadScaledImageFromURL("http://www.cartaperdue.it/coupon/img_offerte/" + coupon.getUrlImmagine());
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        ProgressBar pb = (ProgressBar) c.findViewById(R.id.pd_progressBar);
        pb.setVisibility(View.INVISIBLE);
        View v = (View) c.findViewById(R.id.cover);
        v.setVisibility(View.INVISIBLE);
    }

 
    private void updateTimer(long millisUntilFinished){
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
            
            timerLbl.setText(days+"g "+hours+"h "+minutes+"m "+ seconds+"s");
        } 
        else {
            secondsLeft = 0;
            timerLbl.setText("tempo scaduto");
            //annullo il timer quando scade il countDown
            countDownTimer.cancel();
        }
    }
    
    
    public void addFromJSON(String jsonString) {
        jsonString = Utils.formatJSON(jsonString);
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        //jsonString = jsonString.substring(1, jsonString.length() - 1);
        //jsonString = Utils.stripEsercente(jsonString);
        Log.d("Timer","stringa strippata ----->"+jsonString);
        Gson gson = Utils.getGson();
        try {
            this.coupon = gson.fromJson(jsonString, Coupon.class);
            Log.d("TIMER","oggetto coupon =  "+coupon.getDescrizione());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    
    
    //query fatta solo con la provincia è offerta del giorno
    //query fatte con id del coupon sono generiche
    
    /*
     * se è coupon del giorno, va rifatta la query ogni volta che si raggiunge la view relativo ad esso 
     * (ad esempio, da foreground->background->fore, oppure coupon del giorno-> dove usarla->coupon del giorno
     * se coupon normale non c'è bisogno di rifare la query come sopra
     * */
    
    /*
     * da "offerta_periodo_al" ottengo la data di validità del coupon, cioè fino al giorno x.
     * ottengo i secondi mancanti, e questo calcolo lo faccio in viewWillApper se ho il dataModel, o in cellForRow
     * 
     * did appear: invalidate, poi se NON è offerta del giorno E se il coupon è già arrivato E se la view è davanti gli occhi dell'utente
     *  ---> lancio timer
     * 
     * did disappear: invalidate timer
     * 
     * ricezione del json: se la view è davanti gli occhi dell'utente: invalidate, e lancio timer
     * 
     * quando secondleft = 0 invalidate
     * */
    
}

