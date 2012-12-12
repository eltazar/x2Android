
package it.wm.perdue.doveusarla;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import it.wm.perdue.DoveQuandoDialog;
import it.wm.perdue.DoveQuandoDialog.ChangeDoveQuandoDialogListener;
import it.wm.perdue.FilterSpinnerAdapter;
import it.wm.perdue.MainActivity;
import it.wm.perdue.R;
import it.wm.perdue.Utils;

import java.util.ArrayList;
import java.util.List;

/*
 * Questa classe funge da contenitore per un fragment. Ha il compito di visualizzare i vari elementi dell'interfaccia
 * come la search bar ecc...
 * 
 * */
public class EsercentiBaseActivity extends SherlockFragmentActivity implements OnQueryTextListener,
        OnNavigationListener, ChangeDoveQuandoDialogListener {
    
    private static final String   DEBUG_TAG        = "EsercentiBaseActivity";
    private String                category         = "";
    private EsercentiPagerAdapter pagerAdapter;
    private String[]              mealHourFilter   = new String[] {
            "Tutti",
            "Pranzo",
            "Cena"
                                                   };
    private Integer[]             icons            = {
            R.drawable.ic_action_filter,
            R.drawable.ic_action_sun, R.drawable.ic_action_moon
                                                   };
    
    private Menu                  menu             = null;
    private static final String   WHERE            = "where";
    private static final String   WHEN             = "when";
    
    private String                providerId       = LocationManager.GPS_PROVIDER;
    private LocationListener      locationListener = new LocationListener() {
                                                       @Override
                                                       public void onStatusChanged(
                                                               String provider, int status,
                                                               Bundle extras) {
                                                           if (status == LocationProvider.AVAILABLE) {
                                                               
                                                           } else {
                                                               
                                                           }
                                                       }
                                                       
                                                       @Override
                                                       public void onProviderEnabled(
                                                               String provider) {
                                                           
                                                       }
                                                       
                                                       @Override
                                                       public void onProviderDisabled(
                                                               String provider) {
                                                       }
                                                       
                                                       @Override
                                                       public void onLocationChanged(
                                                               Location location) {
                                                           updateLocationData(location);
                                                       }
                                                   };
    
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
        
        // dropDownList per il filtro pranzo-cena
        if (category.equals("Ristoranti") || category.equals("Pubs e Bar")) {
            
            /** Create an array adapter to populate dropdownlist */
            FilterSpinnerAdapter adapter = new FilterSpinnerAdapter(getBaseContext(),
                    android.R.layout.simple_spinner_dropdown_item, mealHourFilter, icons);
            
            /** Enabling dropdown list navigation for the action bar */
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            
            /**
             * Setting dropdown items and item navigation listener for the
             * actionbar
             */
            bar.setListNavigationCallbacks(adapter, this);
        }
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager) getSystemService(
                LOCATION_SERVICE
                );
        LocationProvider provider = locationManager.getProvider(providerId);
        if (provider == null) {
        } else {
            boolean gpsEnabled = locationManager.isProviderEnabled(providerId);
            if (gpsEnabled) {
            } else {
            }
            Location location = locationManager.getLastKnownLocation(
                    LocationManager.GPS_PROVIDER
                    );
            if (location != null) {
                Log.d("AA", "ON RESUME");
                
                // appena creo/riesumo l'activity vedo se ho dati gps pronti e
                // aggiorno i fragment
                // così la query sarà con quei valori gps
                for (int i = 0; i < pagerAdapter.getCount(); i++) {
                    EsercentiListFragment f = (EsercentiListFragment)
                            pagerAdapter.getItem(i);
                    f.setLatitude(location.getLatitude());
                    f.setLongitude(location.getLongitude());
                }
            }
            locationManager.requestLocationUpdates(providerId, 5, 5000, locationListener);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        LocationManager locationManager = (LocationManager) getSystemService(
                LOCATION_SERVICE
                );
        locationManager.removeUpdates(locationListener);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationListener = null;
    }
    
    /* *** BEGIN: Aggiornamento coordinate gps **************** */
    
    private void updateLocationData(Location location) {
        
        // ricevuti nuovi dati gps aggiorno le coordinate del fragment e lancio
        // la query
        
        Log.d("AA", "UPDATE LOCATION DATA");
        
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d("VVV", " base activity LAT = " + latitude + " LONG =" + longitude);
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            EsercentiListFragment f = (EsercentiListFragment)
                    pagerAdapter.getItem(i);
            
            f.setLatitude(latitude);
            f.setLongitude(longitude);
            f.onChangeWhereWhenFilter();
        }
    }
    
    /* *** END: Aggiornamento coordinate gps **************** */
    
    /* *** BEGIN: OptionsMenu Methods **************** */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.esercenti_menu, menu);
        this.menu = menu;
        // mSearchView.setOnCloseListener(this);
        
        menu.findItem(R.id.ww).setTitle(Utils.getPreferenceString(getApplicationContext(),
                WHERE, "Qui vicino") + "-" + Utils.getPreferenceString(getApplicationContext(),
                WHEN, "Oggi"));
        
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
            case R.id.ww:
                DoveQuandoDialog dialog = new DoveQuandoDialog();
                dialog.show(getSupportFragmentManager(), "whereWhen");
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
    
    /* *** START:: OnNavigationListener **************** */
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            EsercentiRistoListFragment f = (EsercentiRistoListFragment)
                    pagerAdapter.getItem(i);
            f.onChangeFilter(mealHourFilter[itemPosition]);
        }
        
        return false;
    }
    
    /* *** END:: OnNavigationListener **************** */
    
    /* *** START:: ChangeDoveQuandoDialogListener **************** */
    
    @Override
    public void onSaveDoveQuandoDialog() {
        // TODO Auto-generated method stub
        
        MenuItem wwMenuItem = menu.findItem(R.id.ww);
        // wwMenuItem.setTitle(wwMap.get("label"));
        
        String dove = Utils.getPreferenceString(getApplicationContext(), WHERE, "Qui vicino");
        if (dove.equals("Qui"))
            dove = "Qui vicino";
        
        wwMenuItem.setTitle(dove
                + "-"
                + Utils.getPreferenceString(getApplicationContext(), WHEN, "Oggi"));
        
        // rilancio query
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            EsercentiListFragment f = (EsercentiListFragment)
                    pagerAdapter.getItem(i);
            f.onChangeWhereWhenFilter();
        }
    }
    
    /* *** END:: ChangeDoveQuandoDialogListener **************** */
    
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
            
            // il problema ora è che bisogna gestire con il fragment manager i
            // vari
            // fragment?
            // inoltre bisognerebbe ritornare ad "esercentiBaseActivity" quale
            // fragment è visualizzato,
            // perché quando si fa ad esempio la ricerca o si cambia filtro,
            // bisogna
            // inviare al fragment i dati per le nuove query
            
            // Mario, vanno i dati per le nuove query vanno inviati a TUTTI i
            // fragment, perché quando fai lo swipe le views devono apparire già
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
            // Log.d(DEBUG_TAG, "getCount:" + mCount);
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
