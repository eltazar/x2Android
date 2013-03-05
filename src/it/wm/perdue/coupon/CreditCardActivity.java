package it.wm.perdue.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import it.wm.perdue.businessLogic.CreditCard;
import it.wm.perdue.coupon.CreditCardFragment.OnCreditCardFormListener;

public class CreditCardActivity extends SherlockFragmentActivity implements OnCreditCardFormListener{
    
    private static final String DEBUG_TAG  = "CreditcardActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        bar.setTitle("Carta di credito");    
        
        //se prima volta istanzio il fragment, 
        //altrimenti era già stato istanziato e bisogna quindi ripristinare lo stato
        if(savedInstanceState == null){
            Fragment f = CreditCardFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.replace(android.R.id.content,f, "creditCardFragment");
            fragmentTransaction.commit();
        }
    }
    
    @Override
    public void onResume(){
        super.onResume();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    
    @Override
    public void onCreditCardDoneButtonClicked(CreditCard c) {
        //mando carta di credito indietro
        Log.d("check","creo intent extra per mandare indietro carta");
        Intent i = getIntent();
        i.putExtra("creditCard", c);
        setResult(RESULT_OK, i);
        finish();
    }
    @Override
    public void onCreditCardCancelButtonClicked() {
        //comunico che la creazione è stata annullata
    }
}
