package it.wm.perdue.coupon;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ProvaCouponTimer extends SherlockFragment implements
        HTTPAccess.ResponseListener {
    
    private static final String TAG_NORMAL      = "normal";
    
    // Gestione dei download:
    private HTTPAccess          httpAccess      = null;
    
  
    private TextView            timer = null;
    private long                 secondsLeft;
    private CountDownTimer countDownTimer = null;
    
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
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        timer = (TextView) view.findViewById(R.id.timer);
        
        timer.setText("ciaooo");
        
        return view;
    }
    
    @Override
    public void onResume (){
        super.onResume();
        Log.d("TIMER", "on resume");
        
        //2013-02-27 22:59:00
        //Date(2013, 02,27, 22, 59, 00);
        Date now = new Date();
        DateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        try {
            Date scad = formatter.parse("2013-02-27 22:59:00");
            String scadT = formatter.format(scad);
            Log.d("TIMER","data di scad ---> "+scadT);
            long minutes = scad.getTime() - now.getTime();
            secondsLeft = TimeUnit.MILLISECONDS.toSeconds(minutes);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = formatter.format(now);
        Log.d("TIMER","data di oggi "+s);
        //setDateTime(secondsLeft);
        
        //countDownTimer.cancel();
        countDownTimer = new CountDownTimer(secondsLeft, 1000) {

            public void onTick(long millisUntilFinished) {
                countDown();
            }

            public void onFinish() {
                timer.setText("done!");
            }
         }.start();
    }
    
    @Override
    public void onStop (){
        super.onStop();
        Log.d("TIMER","ON STOP");
        countDownTimer.cancel();
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        
    
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        //Log.d("XXX", "ERRORE INVIO ->" + tag);
     
    }
 
    private void countDown(){
        long days;
        //NSLog(@"RICHIAMATO COUNT DOWN");
        long minutes;
        long seconds;
        long hours;
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
            timer.setText("tempo scaduto");
            countDownTimer.cancel();
        }
        
    }
    
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
