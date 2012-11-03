
package it.wm.perdue;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity implements TabListener {
    private static final String DEBUG_TAG = "MainActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        ActionBar bar = getSupportActionBar();
        
        bar.addTab(bar.newTab().setText("Dove usarla").setTabListener(this));
        bar.addTab(bar.newTab().setText("News").setTabListener(this));
        
        bar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D65151")));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        Log.d(DEBUG_TAG, "onTabSelected:");
        // CharSequence tabText = tab.getText();
        // Sia ben chiaro che quanto segue è una porcheria sperimentale :D
        // Beccare il fragment sul testo del tab è una porcata, è giusto per
        // vedere su funziona :D
        // Forse è una porcata pure allocare Fragment ogni volta che si clicca
        // un tab :D
        
        switch (tab.getPosition()) {
            case 0:
                Log.d(DEBUG_TAG, "onTabSelected: Dove Usarla");
                ft.replace(R.id.mainActivityFragmentContainer, new KindOfShopFragment());
                break;
            case 1:
                Log.d(DEBUG_TAG, "onTabSelected: Coupon");
                ft.replace(R.id.mainActivityFragmentContainer, new NewsFragment());
                break;
            default:
                break;
        }
        /*
         * if (tabText.equals("Dove usarla")) { Log.d(DEBUG_TAG,
         * "onTabSelected: Dove Usarla");
         * ft.replace(R.id.mainActivityFragmentContainer, new
         * KindOfShopFragment()); } else if (tabText.equals("Coupon")) {
         * Log.d(DEBUG_TAG, "onTabSelected: Coupon");
         * ft.replace(R.id.mainActivityFragmentContainer, new CouponFragment());
         * } // Perché cavolo non vuole il commit? La documentazione dice //
         * esplicitamente che è obbligatorio.... che sia l'action bar stessa a
         * // chiamarlo? Investigare sulla doc dell'actionbar. // ft.commit();
         */
        
    }
    
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
        
    }
    
}
