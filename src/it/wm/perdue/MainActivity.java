
package it.wm.perdue;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import it.wm.perdue.contatti.InfoPerDueBaseActivity;
import it.wm.perdue.coupon.ProvaCouponTimer;
import it.wm.perdue.doveusarla.DoveUsarlaFragment;
import it.wm.perdue.forms.BaseFormActivity;
import it.wm.perdue.forms.RichiediCartaFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SherlockFragmentActivity implements TabListener {
    public static final String  COUPON_TAB_TAG = "coupon";
    public static final String  DOVE_USARLA_TAB_TAG = "doveusarla";
    public static final String  NEWS_TAB_TAG        = "news";
    public static final String  RICHIEDI_TAB_TAG    = "richiedi";
    private static final String DEBUG_TAG           = "MainActivity";
    List<TabDescriptor>         tabList             = null;
    ViewPager                   pager               = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Utils.setContext(this.getApplicationContext());
        
        tabList = new ArrayList<TabDescriptor>();
        tabList.add(new TabDescriptor(COUPON_TAB_TAG, ProvaCouponTimer.class, "Coupon"));
        tabList.add(new TabDescriptor(DOVE_USARLA_TAB_TAG, DoveUsarlaFragment.class, "Dove Usarla"));
        tabList.add(new TabDescriptor(NEWS_TAB_TAG, NewsListFragment.class, "News"));
        tabList.add(new TabDescriptor(RICHIEDI_TAB_TAG, RichiediCartaFragment.class, "Richiedi"));
        
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        }
        
        setContentView(R.layout.main_activity);
        setupTabs();
        pager = (ViewPager) findViewById(R.id.mainActivityFragmentContainer);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(pagerAdapter);
        pager.setPageMargin(30);
        
        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("selectedtab"));
        }
        
        String tag = (String) getIntent().getSerializableExtra(Intent.EXTRA_TEXT);
        if (tag != null) {
            int i = 0;
            for (TabDescriptor t : tabList) {
                if (t.tag.equals(tag))
                    bar.setSelectedNavigationItem(i);
                i++;
            }
        }
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedtab", getSupportActionBar().getSelectedNavigationIndex());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.infoPerDue:
//                intent = new Intent(this, InfoPerDueBaseActivity.class);
//                intent.putExtra("request", "info");
//                startActivity(intent);
                intent = new Intent(this, BaseFormActivity.class);
                //intent.putExtra("request", "info");
                startActivity(intent);
                return true;
            case R.id.contactInfo:
                intent = new Intent(this, InfoPerDueBaseActivity.class);
                intent.putExtra("request", "contacts");
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setupTabs() {
        ActionBar bar = getSupportActionBar();
        for (TabDescriptor t : tabList) {
            bar.addTab(bar.newTab().setText(t.title).setTag(t.tag).setTabListener(this));
        }
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }
    
    /* *** BEGIN: ActionBar.TabListener ********************* */
    
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (pager != null) {
            pager.setCurrentItem(tab.getPosition(), true);
        }
    }
    
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
    
    /* *** END: ActionBar.TabListener ********************* */
    
    private class PagerAdapter extends FragmentPagerAdapter implements
            ViewPager.OnPageChangeListener {
        
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }
        
        @Override
        public Fragment getItem(int position) {
            TabDescriptor tabDesc = tabList.get(position);
            Fragment frgmnt = getSupportFragmentManager().findFragmentByTag(tabDesc.tag);
            if (frgmnt == null) {
                frgmnt = Fragment.instantiate(MainActivity.this, tabDesc.clazz.getName());
            }
            return frgmnt;
        }
        
        @Override
        public int getCount() {
            return tabList.size();
        }
        
        /* *** BEGIN: ViewPager.OnPageChangeListener **************** */
        @Override
        public void onPageScrollStateChanged(int state) {
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        
        @Override
        public void onPageSelected(int position) {
            MainActivity.this.getSupportActionBar().setSelectedNavigationItem(position);
        }
        /* *** END: ViewPager.OnPageChangeListener **************** */
        
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
