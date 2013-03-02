
package it.wm.perdue.coupon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import it.wm.HTTPAccess;
import it.wm.perdue.contatti.UserFormFragment;
import it.wm.perdue.coupon.LoginFormFragment.OnLoginFormListener;
import it.wm.perdue.coupon.RetrievePswDialog.RetrievePswListener;

public class BaseFormActivity extends SherlockFragmentActivity implements
        HTTPAccess.ResponseListener, OnLoginFormListener, OnEditorActionListener, OnFocusChangeListener, RetrievePswListener {
    
    private static final String DEBUG_TAG  = "BaseFormActivity";
    private static final String LOGIN_FRAG_TAG = "loginFragment";
    private static final String SIGNUP_FRAG_TAG = "signupFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
                      
        }
    }
    
    @Override
    public void onResume(){
        super.onResume();
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        
        bar.setTitle("Login");
        
        Fragment f = null;
        
        f = LoginFormFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.add(android.R.id.content,f, LOGIN_FRAG_TAG);//.add(android.R.id.content, f);
        fragmentTransaction.commit();
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        
    }

    /*RetrievePswListener
     * */
    @Override
    public void onDialogPositiveClick(SherlockDialogFragment dialog, String retrieveString) {
        Log.d("retrieve","cliccato avanti su dialog");    
        LoginFormFragment loginFragment = (LoginFormFragment) getSupportFragmentManager().findFragmentByTag(LOGIN_FRAG_TAG);
        try{
            loginFragment.sendRetrieveRequest(retrieveString);
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogNegativeClick(SherlockDialogFragment dialog) {
        Log.d("retrieve","cliccato annulla su dialog");               
    }
    /*RetrievePswListener END
     * */

    /*OnLoginFormListener
     * */
    @Override
    public void onRegButtonClicked() {
        //lancia fragment di registrazione
        Fragment f = UserFormFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content,f, SIGNUP_FRAG_TAG);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Registrazione");
    }
    /*OnLoginFormListener END
     * */
}
