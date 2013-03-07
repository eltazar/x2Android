package it.wm.perdue.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import it.wm.perdue.DoveDialog;
import it.wm.perdue.DoveDialog.ChangeDoveDialogListener;
import it.wm.perdue.MainActivity;
import it.wm.perdue.R;
import it.wm.perdue.Utils;

public class CouponsBaseActivity extends SherlockFragmentActivity implements ChangeDoveDialogListener {
    
    
    private static final String   DEBUG_TAG              = "CouponsBaseActivity";
    
    private Menu                  menu                   = null;
    private static final String   COUPON_CITY  = "couponCity";
    private static final String   COUPONS_FRAGMENT_TAG   = "couponsFragament";

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.d(DEBUG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
                
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        }
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        
        bar.setTitle("Altre Offerte");
       
        //recupero precedente stato se esiste, altrimenti creo il fragment di login
        if(savedInstanceState == null){       
            Fragment f = null;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            f = new CouponsListFragment();
            fragmentTransaction.replace(android.R.id.content,f, COUPONS_FRAGMENT_TAG);//.add(android.R.id.content, f);
            fragmentTransaction.commit();
        }
    }
      
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    
    /* *** BEGIN: OptionsMenu Methods **************** */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.coupon_list_menu, menu);
        this.menu = menu;
        //setto titolo pulsante
        String dove = Utils.getPreferenceString(COUPON_CITY, "Roma");
        menu.findItem(R.id.whereMenu).setTitle(dove);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra(Intent.EXTRA_TEXT, MainActivity.DOVE_USARLA_TAB_TAG);
                NavUtils.navigateUpTo(this, intent);
                return true;
            case R.id.whereMenu:
                DoveDialog dialog = new DoveDialog();
                dialog.show(getSupportFragmentManager(), "cia");
                return true;
                
        }
        return super.onOptionsItemSelected(item);
    }
    /* *** END: OptionsMenu Methods **************** */        
    
    
    /* *** START:: ChangeDoveQuandoDialogListener **************** */
    @Override
    public void onSaveDoveDialog() {        
        MenuItem wMenuItem = menu.findItem(R.id.whereMenu);
        
        String dove = Utils.getPreferenceString(COUPON_CITY, "Roma");       
        Log.d("dialog","città -> "+dove);
        wMenuItem.setTitle(dove);
        // rilancio query
        CouponsListFragment f = (CouponsListFragment)getSupportFragmentManager().findFragmentByTag(COUPONS_FRAGMENT_TAG);
        f.onChangeWhereFilter();
    }
    /* *** END:: ChangeDoveQuandoDialogListener **************** */
}