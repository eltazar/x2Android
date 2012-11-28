/**
 * 
 */

package it.wm.perdue.doveusarla;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapView;

import it.wm.HTTPAccess;
import it.wm.SimpleGeoPoint;
import it.wm.perdue.R;

import java.util.HashMap;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class EsercentiMapActivity extends SherlockMapActivity implements LocationListener,
        HTTPAccess.ResponseListener {
    private String                   category        = null;
    private MapView                  mapView         = null;
    private LocationManager          locationManager = null;
    private EsercentiItemizedOverlay itemizedOverlay = null;
    private DownloadHandler          dh              = null;
    
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapView = new MapView(this, getResources().getString(R.string.map_api_key));
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
        mapView.setClickable(true);
        mapView.getController().setZoom(12);
        itemizedOverlay = new EsercentiItemizedOverlay(
                this,
                getResources().getDrawable(android.R.drawable.presence_online),
                mapView);
        itemizedOverlay.setShowClose(false);
        itemizedOverlay.setShowDisclosure(true);
        itemizedOverlay.setBalloonBottomOffset(10);
        mapView.getOverlays().add(itemizedOverlay);
        
        setContentView(mapView);
        dh = new DownloadHandler();
        dh.httpAccess = new HTTPAccess();
        dh.httpAccess.setResponseListener(this);
        dh.urlString = "http://www.cartaperdue.it/partner/v2.0/EsercentiNonRistorazione.php";
        dh.postMap = new HashMap<String, String>();
        dh.postMap.put("request", "fetch");
        dh.postMap.put("categ", "teatri");
        dh.postMap.put("lat", "41.801007");
        dh.postMap.put("long", "12.454273");
        dh.postMap.put("ordina", "distanza");
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
        locationManager.removeUpdates(this);
        SimpleGeoPoint sGeoPoint = new SimpleGeoPoint(
                location.getLatitude(),
                location.getLongitude());
        mapView.getController().animateTo(sGeoPoint.toGeoPoint());
        dh.postMap.put("lat", "" + location.getLatitude());
        dh.postMap.put("long", "" + location.getLongitude());
        dh.postMap.put("from", "0");
        dh.startDowloading(sGeoPoint);
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
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        if (itemizedOverlay.addFromJSON(response) > 0)
            dh.downloadMore();
    }
    
    @Override
    public void onHTTPerror(String tag) {
        
    }
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    
    private static class DownloadHandler {
        public String                  urlString;
        public HashMap<String, String> postMap;
        public HTTPAccess              httpAccess;
        public int                     from;
        private SimpleGeoPoint         queryPoint;
        
        public void startDowloading(SimpleGeoPoint point) {
            queryPoint = point;
            from = 0;
            postMap.put("lat", "" + queryPoint.getLatitude());
            postMap.put("long", "" + queryPoint.getLongitude());
            postMap.put("from", "" + from);
            startHTTPConnection();
        }
        
        public void downloadMore() {
            postMap.put("from", "" + (from += 20));
            startHTTPConnection();
        }
        
        private void stopDownloading() {
            // TODO: implementare in HTTPAccess
        }
        
        public String getTag() {
            return queryPoint.toString() + from;
        }
        
        private void startHTTPConnection() {
            httpAccess.startHTTPConnection(
                    urlString,
                    HTTPAccess.Method.POST,
                    postMap,
                    getTag());
        }
    }
}
