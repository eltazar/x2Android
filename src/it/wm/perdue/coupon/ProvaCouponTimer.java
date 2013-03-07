package it.wm.perdue.coupon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Coupon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ProvaCouponTimer extends SherlockFragment implements
        HTTPAccess.ResponseListener, OnClickListener {
    
    private static final String TAG_NORMAL      = "normal";
    
    // Gestione dei download:
    private HTTPAccess          httpAccess      = null;
    private String              urlString = null;
  
    private TextView            timer = null;
    private CountDownTimer countDownTimer = null;
    private Coupon coupon = null;
    
    private CouponJSONAdapter<? extends Coupon> adapter = null;
    // onAttach
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
    }
    
    // onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    // onActivityCreated
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    
    // onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer, container, false);
        Button button = (Button) view.findViewById(R.id.stub);
        button.setOnClickListener(this);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        timer = (TextView) view.findViewById(R.id.timer);
        
        timer.setText("CountDownTimer");
        adapter = new CouponJSONAdapter<Coupon>(
                getActivity(),
                R.layout.esercente_row, Coupon.class);
        
        return view;
    }
    
    @Override
    public void onResume (){
        super.onResume();
        Log.d("TIMER", "on resume");
        
        long millisTot = 0;
        
        //urlString = "http://www.cartaperdue.it/partner/v2.0/TipoEsercente.php";
        TreeMap<String, String>       postMap = new TreeMap<String, String>();
        postMap.put("idEsercente",  "1235");
        //urlString = "http://www.cartaperdue.it/partner/android/offerta2.php?id=" +714 /*IDCOUPON*/;
        urlString = "http://www.cartaperdue.it/partner/android/coupon2.php?prov=" + "Roma";
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, null);
        //httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
        Date now = new Date();
        DateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
       
        try {
            Date scad = formatter.parse("2013-02-28 23:59:00");
            String scadT = formatter.format(scad);
            Log.d("TIMER","data di scad ---> "+scadT);
            Log.d("TIMER", "data now --->"+formatter.format(now));
            millisTot = scad.getTime() - now.getTime();
            Log.d("Timer","millisecondti minutes = "+millisTot);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //su ios facevo così, qui non so se serve... 
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        
        //creo il timer appena la view torna visibile
        countDownTimer = new CountDownTimer(millisTot, 1000) {
            int count = 0;
            public void onTick(long millisUntilFinished) {
                //Log.d("COUNT","COUNT = "+(count++));
                //Log.d("Timer","millisUntilFinished = "+millisUntilFinished);
                countDown(millisUntilFinished);
            }

            public void onFinish() {
                timer.setText("Tempo scaduto2");
            }
         }.start();
    }
    
    @Override
    public void onStop (){
        super.onStop();
        Log.d("TIMER","ON STOP");
        //annullo il timer quando la view non è più visibile
        countDownTimer.cancel();
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        Log.d("TIMER", " RISPOSTA = "+response);
        adapter.addFromJSON(response);
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        //Log.d("XXX", "ERRORE INVIO ->" + tag);
     
    }
 
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
            
            timer.setText(days+"g "+hours+"h "+minutes+"m "+ seconds+"s");
        } 
        else {
            secondsLeft = 0;
            timer.setText("tempo scaduto1");
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

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getSherlockActivity(),DetailCouponBaseActivity.class);
        startActivity(i);
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
