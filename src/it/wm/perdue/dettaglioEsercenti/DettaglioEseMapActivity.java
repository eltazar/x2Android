package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapView;

import it.wm.SimpleGeoPoint;
import it.wm.perdue.MainActivity;
import it.wm.perdue.R;
import it.wm.perdue.businessLogic.Esercente;
import it.wm.perdue.doveusarla.EsercentiItemizedOverlay;

public class DettaglioEseMapActivity extends SherlockMapActivity implements LocationListener {
        
        private MapView mapView;
        private LocationManager          locationManager = null;

        private EsercentiItemizedOverlay itemizedOverlay = null;

        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            
            super.onCreate(savedInstanceState);
            
            Esercente esercente= (Esercente)getIntent().getSerializableExtra("esercente");            
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mapView = new MapView(this, getResources().getString(R.string.map_api_key));
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
            mapView.setClickable(true);
            mapView.getController().setZoom(18);// (12);
            mapView.setBuiltInZoomControls(true);
            mapView.setSatellite(false);

            if(esercente != null){
                itemizedOverlay = new EsercentiItemizedOverlay(
                        this,
                        getResources().getDrawable(android.R.drawable.presence_online),
                        mapView);
                itemizedOverlay.setShowClose(false);
                itemizedOverlay.setShowDisclosure(true);
                itemizedOverlay.setBalloonBottomOffset(10);
                mapView.getOverlays().add(itemizedOverlay);
                
                itemizedOverlay.addItem(esercente);
                mapView.getController().setCenter(new SimpleGeoPoint(esercente.getLatitude(),esercente.getLongitude()).toGeoPoint());
            }
          
            ActionBar bar = getSupportActionBar();
            bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE);
            
            bar.setTitle("Mappa");
            
            setContentView(mapView);
        }
        
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getSupportMenuInflater().inflate(R.menu.esercenti_map_menu, menu);
            menu.getItem(0).setVisible(false);
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

        @Override
        protected boolean isRouteDisplayed() {
            return false;
        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            
        }
}