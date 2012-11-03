
package it.wm.perdue;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SherlockFragmentActivity implements TabListener {
    private static final String DEBUG_TAG = "MainActivity";
    List<TabDescriptor>         tabList   = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tabList = new ArrayList<TabDescriptor>();
        tabList.add(new TabDescriptor("doveusarla", KindOfShopFragment.class, "Dove Usarla"));
        tabList.add(new TabDescriptor("news", NewsFragment.class, "News"));
        
        setContentView(R.layout.main_activity);
        setupTabs();
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
        TabDescriptor tabDesc = tabList.get(tab.getPosition());
        Log.d(DEBUG_TAG, "onTabSelected: " + tabDesc.title);
        
        // Prima di istanziarlo bisogna chiedere al FragmentManager se c'è già,
        // perché su un config change viene reinstanziato automaticamente
        // (rotation). Se lo non lo si fa lo si istanzia due volte.
        // Ovviamente lo stesso discorso vale se il fragmente viene allocato
        // altrove (non nel tab listener): nel costruttore dell'activity, negli
        // attributi della classe, sulla tazza del cesso. Morale della favola:
        // PRIMA di istanziare un Fragment chiedere SEMPRE prima al fragment
        // manager.
        Fragment frgmnt = getSupportFragmentManager().findFragmentByTag(tabDesc.tag);
        
        if (frgmnt == null) {
            frgmnt = Fragment.instantiate(this, tabDesc.clazz.getName());
        }
        if (!frgmnt.isAdded()) {
            ft.add(R.id.mainActivityFragmentContainer, frgmnt, tabDesc.tag);
        }
        if (frgmnt.isDetached()) {
            ft.attach(frgmnt);
        }
    }
    
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        TabDescriptor tabDesc = tabList.get(tab.getPosition());
        Fragment frgmnt = getSupportFragmentManager().findFragmentByTag(tabDesc.tag);
        if (frgmnt != null) {
            ft.detach(frgmnt);
        }
    }
    
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
    
    private void setupTabs() {
        ActionBar bar = getSupportActionBar();
        for (TabDescriptor t : tabList) {
            bar.addTab(bar.newTab().setText(t.title).setTag(t.tag).setTabListener(this));
        }
        bar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D65151")));
    }
    
    private static class TabDescriptor {
        Class<? extends Fragment> clazz = null;
        String                    title = null;
        String                    tag   = null;
        
        public TabDescriptor(String tag, Class<? extends Fragment> clazz, String title) {
            this.tag = tag;
            this.clazz = clazz;
            this.title = title;
        }
    }
    
}
