package it.wm.perdue.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

import it.wm.perdue.LoggingHandler;
import it.wm.perdue.LoggingHandler.OnLoggingHandlerListener;
import it.wm.perdue.MainActivity;
import it.wm.perdue.R;
import it.wm.perdue.businessLogic.Coupon;
import it.wm.perdue.coupon.DetailCouponListFragment.OnCouponActionListener;

public class DetailCouponBaseActivity extends SherlockFragmentActivity implements OnCouponActionListener, OnLoggingHandlerListener {
    
    private static final String        DEBUG_TAG  = "DetailCouponBaseActivity";
    private static final String        COUPON_FRAGMENT_TAG = "CouponFragmentTag";
    private int                        idCoupon    = -1;
    private static String              currentFragment = COUPON_FRAGMENT_TAG;
    private ShareActionProvider        mShareActionProvider = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        Log.d("coupon","onCreate");
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idCoupon = extras.getInt("couponId");
        }
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        
        //bar.setTitle("titolo coupon");
        LoggingHandler.setListener(this);

        if (savedInstanceState == null){            
            Fragment f = new DetailCouponListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("couponId", idCoupon);
            f.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.replace(android.R.id.content, f,COUPON_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        else{
            
        }
    }
    
    @Override
    public void onBackPressed(){
        // do something here and don't write super.onBackPressed()
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
        case KeyEvent.KEYCODE_BACK:
            if(currentFragment.equals(COUPON_FRAGMENT_TAG)){
                finish();
            }
            else{
                Log.d("coupon","back premuto");
                backToPreviousFragment();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /* *** BEGIN: OptionsMenu Methods **************** */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getSupportMenuInflater().inflate(R.menu.detail_coupon_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        MenuItem logoutItem = menu.findItem(R.id.menu_item_logout);

        //se il fragment è quello del coupon inserisco pulsanti
        if(currentFragment.equals(COUPON_FRAGMENT_TAG)){
            // Locate MenuItem with ShareActionProvider
            mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
            if(LoggingHandler.isLogged()){
                logoutItem.setVisible(true); 
            }
            else{
                logoutItem.setVisible(false); 
            }
            
        }
        else{
            //nascondo
            shareItem.setVisible(false); 
            logoutItem.setVisible(false); 
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra(Intent.EXTRA_TEXT, MainActivity.COUPON_TAB_TAG);
                NavUtils.navigateUpTo(this, intent);
                return true;
            case R.id.menu_item_logout:
                Log.d("coupon","pulsante menu logout premuto");
                LoggingHandler.doLogout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private Intent setShareIntent(Coupon c) {
        
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //Coupon c = ((DetailCouponListFragment) getSupportFragmentManager().findFragmentByTag(COUPON_FRAGMENT_TAG)).adapter.getObject();
        if(c != null){
            String contentUrl = "http://www.cartaperdue.it/coupon/dettaglio_affare.jsp?idofferta="+c.getID();
            /*String content = "<b>Descrizione:</b>"+c.getTitoloBreve()+
                    "<b>Prezzo coupon:</b>"+ c.getValoreAcquisto()+"€"+
                    "<b>Invece di:</b>"+c.getValoreFacciale()+"€"+
                    "</b>Risparmio: </b>"+c.getSconto()+"€"+
                    "<b>Link:</b>"+"<a href=\""+url+"\">Apri offerta</a>"; */
            intent.putExtra(Intent.EXTRA_SUBJECT, "Offerta coupon PerDue");
            intent.putExtra(Intent.EXTRA_TEXT, contentUrl);
        }
        
        if (mShareActionProvider != null && intent != null) {
            mShareActionProvider.setShareIntent(intent);
        }
        
        return intent;
    }
    
    @Override
    public void onDidCheckout(Coupon c) {
        Log.d("coupon","devo lanciare fragment per checkout");
        CheckoutListFragment f = new CheckoutListFragment((c.getID()+""),c.getTitoloBreve(),(c.getValoreAcquisto()+""));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, f,CheckoutListFragment.CHEKCOUT_LIST_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null); //per aggiungere il fragmente allo stack
        fragmentTransaction.commit();       
        currentFragment = CheckoutListFragment.CHEKCOUT_LIST_FRAGMENT_TAG;
        //per ricreare il menu
        invalidateOptionsMenu ();
    }

    @Override
    public void onDidLogin() {
        currentFragment = COUPON_FRAGMENT_TAG;
        invalidateOptionsMenu ();
//        DetailCouponListFragment f = (DetailCouponListFragment) getSupportFragmentManager().findFragmentByTag(COUPON_FRAGMENT_TAG);
//        onDidCheckout(f.adapter.getObject());
    } 
    
    @Override
    public void onDidLogout() {
        invalidateOptionsMenu ();
    }
    
    @Override
    public void onDidReceiveCoupon(Coupon c) {
        setShareIntent(c);        
    }
    
    private void backToPreviousFragment(){        
        //per tornare indietro di un fragment
        if(currentFragment.equals(CheckoutListFragment.CHEKCOUT_LIST_FRAGMENT_TAG)){
            getSupportFragmentManager().popBackStack();
            currentFragment = COUPON_FRAGMENT_TAG;
            invalidateOptionsMenu ();        
        } 
    }
}