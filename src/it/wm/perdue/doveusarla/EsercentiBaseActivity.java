
package it.wm.perdue.doveusarla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import it.wm.perdue.MainActivity;
import it.wm.perdue.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Questa classe funge da contenitore per un fragment. Ha il compito di visualizzare i vari elementi dell'interfaccia
 * come la search bar ecc...
 * 
 * */
public class EsercentiBaseActivity extends SherlockFragmentActivity implements OnQueryTextListener {
    private static final String   DEBUG_TAG = "EsercentiBaseActivity";
    private String                category  = "";
    private EsercentiPagerAdapter pagerAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.esercenti_base_activity);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            category = extras.getString("category");
        }
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        
        bar.setTitle(category);
        
        pagerAdapter = new EsercentiPagerAdapter(getSupportFragmentManager(), category);
        
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        
        PageIndicator pageIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);
        
    }
    
    /* *** BEGIN: OptionsMenu Methods **************** */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.esercenti_menu, menu);
        // mSearchView.setOnCloseListener(this);
        setupSearchView(menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setupSearchView(Menu menu) {
        SearchView mSearchView = (SearchView) menu.findItem(R.id.abSearch)
                .getActionView();
        mSearchView.setOnQueryTextListener(this);
        menu.findItem(R.id.abSearch).setOnActionExpandListener(new OnActionExpandListener() {
            
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
            
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(DEBUG_TAG, "onMenuItemActionCollapse");
                onQueryTextChange("");
                return true;
            }
        });
    }
    
    /* *** END: OptionsMenu Methods **************** */
    
    /* *** BEGIN: OnQueryTextListener Methods **************** */
    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO: dismettere la tastiera quando si preme "cerca" sulla tastiera
        
        // Hide keyboard
        // InputMethodManager imm = (InputMethodManager) this.getSystemService(
        // SherlockListActivity.INPUT_METHOD_SERVICE);
        // SearchView mSearchView = (SearchView) findViewById(R.id.abSearch);
        // imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        // mSearchView.setFocusable(false);
        // mSearchView.setFocusableInTouchMode(false);
        return true;
    }
    
    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(DEBUG_TAG, "Query text changed: " + newText);
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            EsercentiListFragment f = (EsercentiListFragment) pagerAdapter.getItem(i);
            f.setDataForQuery(newText);
        }
        return true;
    }
    
    /* *** END: OnQueryTextListener Methods **************** */
    
    private static class EsercentiPagerAdapter extends FragmentPagerAdapter implements
            IconPagerAdapter {
        protected ArrayList<String> CONTENT      = new ArrayList<String>() {
                                                     {
                                                         add("Distanza");
                                                         add("Nome");
                                                     }
                                                 };
        private int                 mCount       = CONTENT.size();
        private String              category     = null;
        private List<Fragment>      fragmentList = null;
        private boolean             isRisto      = false;
        
        public EsercentiPagerAdapter(FragmentManager fm, String category) {
            super(fm);
            this.category = category;
            Log.d(DEBUG_TAG, "category " + category);
            if (category.equals("Ristoranti") || category.equals("Pubs e Bar")) {
                CONTENT.add("Prezzo");
                mCount++;
                isRisto = true;
            }
            
            fragmentList = new ArrayList<Fragment>(mCount);
            for (int i = 0; i < mCount; i++) {
                fragmentList.add(i, null);
            }
        }
        
        @Override
        public Fragment getItem(int position) {
            
            // il problema ora � che bisogna gestire con il fragment manager i
            // vari
            // fragment?
            // inoltre bisognerebbe ritornare ad "esercentiBaseActivity" quale
            // fragment � visualizzato,
            // perch� quando si fa ad esempio la ricerca o si cambia filtro,
            // bisogna
            // inviare al fragment i dati per le nuove query
            
            // Mario, vanno i dati per le nuove query vanno inviati a TUTTI i
            // fragment, perch� quando fai lo swipe le views devono apparire gi�
            // aggiornate
            
            // TODO: dirty, bisognrebbe usare il Fragment Manager
            Fragment f = fragmentList.get(position);
            if (f == null) {
                if (isRisto) {
                    f = EsercentiRistoListFragment
                            .newInstance(CONTENT.get(position % CONTENT.size()), category);
                }
                else {
                    f = EsercentiListFragment
                            .newInstance(CONTENT.get(position % CONTENT.size()), category);
                }
                fragmentList.add(position, f);
            }
            return f;
        }
        
        @Override
        public int getCount() {
            Log.d(DEBUG_TAG, "getCount:" + mCount);
            return mCount;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT.get(position % CONTENT.size());
        }
        
        @Override
        public int getIconResId(int index) {
            return 0;
        }
    }
    
}
