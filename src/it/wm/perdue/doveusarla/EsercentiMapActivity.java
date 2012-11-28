/**
 * 
 */

package it.wm.perdue.doveusarla;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapView;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;

import java.util.HashMap;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class EsercentiMapActivity extends SherlockMapActivity implements LocationListener {
    private String                   category        = null;
    private MapView                  mapView         = null;
    private LocationManager          locationManager = null;
    private EsercentiItemizedOverlay itemizedOverlay = null;
    private HTTPAccess               httpAccess      = null;
    private String                   urlString       = null;
    private HashMap<String, String>  postMap         = null;
    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
        itemizedOverlay = new EsercentiItemizedOverlay(
                getResources().getDrawable(android.R.drawable.presence_online));
        mapView = new MapView(this, getResources().getString(R.string.map_api_key));
        mapView.setClickable(true);
        mapView.getController().setZoom(12);
        mapView.getOverlays().add(itemizedOverlay);
        
        setContentView(mapView);
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(itemizedOverlay);
        urlString = "http://www.cartaperdue.it/partner/v2.0/EsercentiNonRistorazione.php";
        postMap = new HashMap<String, String>();
        postMap.put("request", "fetch");
        postMap.put("categ", "teatri");
        postMap.put("lat", "41.801007");
        postMap.put("long", "12.454273");
        postMap.put("ordina", "distanza");
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* *** BEGIN: OptionsMenu Methods **************** */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.esercenti_map_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.lista: // / mmmh da qui dovrebbe tornare nello stesso
                             // stato :/
                Intent intent = NavUtils.getParentActivityIntent(this);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /* *** BEGIN: LocationListener Methods **************** */
    
    @Override
    public void onLocationChanged(Location location) {
        mapView.getController().animateTo(
                Utils.geoPoint(location.getLatitude(), location.getLongitude())
                );
        postMap.put("lat", "" + location.getLatitude());
        postMap.put("long", "" + location.getLongitude());
        postMap.put("from", "0");
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
    }
    
    @Override
    public void onProviderDisabled(String provider) {
    }
    
    @Override
    public void onProviderEnabled(String provider) {
    }
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    
    /* *** END: LocationListener Methods **************** */
}
