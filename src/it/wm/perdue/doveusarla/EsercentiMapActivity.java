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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

import it.wm.HTTPAccess;
import it.wm.ObservableMapView;
import it.wm.SimpleGeoPoint;
import it.wm.perdue.R;

import java.util.HashMap;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class EsercentiMapActivity extends SherlockMapActivity implements 
        LocationListener, 
        HTTPAccess.ResponseListener,
        ObservableMapView.MapViewListener,
        View.OnTouchListener {
    private static final String      DEBUG_TAG       = "EsercentiMapActivity";
    private String                   category        = null;
    private ObservableMapView        mapView         = null;
    private MapController            mapController   = null;
    private Boolean                  gestureStarted  = false;
    private LocationManager          locationManager = null;
    private EsercentiItemizedOverlay itemizedOverlay = null;
    private DownloadHandler          dh              = null;
    private String                   currentDLTag    = null;
    
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        
        Bundle extras = getIntent().getExtras();
        // Valori predefiniti per il debug:
        category = "teatri";
        Boolean isRisto = false;
        if (extras != null) {
            category = extras.getString ("category").replace(" ", "");
            isRisto  = extras.getBoolean("isRisto" );
        }
        
        
        dh = new DownloadHandler(this, category, isRisto);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
        Location lastLoc = locationManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER);
        
        mapView = new ObservableMapView(this, 
                getResources().getString(R.string.map_api_key));
        mapView.setMapViewListener(this);
        mapView.setOnTouchListener(this);
        mapView.setClickable(true);
        
        mapController = mapView.getController();
        if (lastLoc != null) {
            SimpleGeoPoint p = new SimpleGeoPoint(lastLoc.getLatitude(), lastLoc.getLongitude());
            mapController.animateTo(p.toGeoPoint());
            mapController.setZoom(12);
            currentDLTag = dh.startDowloading(p, getRange());
        } else {
            SimpleGeoPoint italia = new SimpleGeoPoint(41.891544, 12.497532);
            mapController.animateTo(italia.toGeoPoint());
            mapController.setZoom(7);
            currentDLTag = dh.startDowloading(italia, getRange());
        }
        
        itemizedOverlay = new EsercentiItemizedOverlay(
                this,
                getResources().getDrawable(android.R.drawable.presence_online),
                mapView);
        itemizedOverlay.setShowClose(false);
        itemizedOverlay.setShowDisclosure(true);
        itemizedOverlay.setBalloonBottomOffset(10);
        mapView.getOverlays().add(itemizedOverlay);
        
        setContentView(mapView);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
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
    /* ***** END: OptionsMenu Methods **************** */
    
    
    
    /* *** BEGIN: LocationListener Methods ********************* */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(DEBUG_TAG, "onLocationChanged: " + location.getLatitude() + ", " + location.getLongitude());
        locationManager.removeUpdates(this);
        final SimpleGeoPoint sGeoPoint = new SimpleGeoPoint(
                location.getLatitude(),
                location.getLongitude());
        mapController.animateTo(
                sGeoPoint.toGeoPoint(),
                new Runnable() {
                    @Override
                    public void run() {
                        currentDLTag = dh.startDowloading(sGeoPoint, getRange());
                    }
                });
        mapController.setZoom(12);
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
    /* *** END: LocationListener Methods *********************** */
    
    
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        /* Aggiungiamo a priori gli esercenti appena ricevuti alla mappa. DopodichŽ 
         * continuiamo a fetchare gli altri elementi della stessa query se e solo se
         * la query combiacia col download tag corrente. */
        int receivedElements = itemizedOverlay.addFromJSON(response);
        mapView.postInvalidate();
        if (tag.contains(currentDLTag) && receivedElements > 0) {
            dh.downloadMore();
        }
    }
    
    @Override
    public void onHTTPerror(String tag) {
        
    }
    /* *** END: HTTPAccess.ResponseListener ******************** */
    
    
    
    /* *** BEGIN: View.OnTouchListener ************************* */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v != mapView) return false;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            Log.d(DEBUG_TAG, "DITO GIU");
            gestureStarted = true;
        } else if (action == MotionEvent.ACTION_UP) {
            Log.d(DEBUG_TAG, "DITO SU");
            gestureStarted = true;
        }
        return false;
    }
    /* *** END: View.OnTouchListener *************************** */
    
    
    
    /* * BEGIN: ObservableMavView.MapViewListener ************** */
    @Override
    public void onPan(GeoPoint oldTopLeft, GeoPoint oldCenter, GeoPoint oldBottomRight,
            GeoPoint newTopLeft, GeoPoint newCenter, GeoPoint newBottomRight) {
        
    }

    @Override
    public void onZoom(
            GeoPoint oldTopLeft, GeoPoint oldCenter, GeoPoint oldBottomRight,
            GeoPoint newTopLeft, GeoPoint newCenter, GeoPoint newBottomRight, 
            int oldZoomLevel, int newZoomLevel) {
        if (gestureStarted) return;
        currentDLTag = dh.startDowloading(new SimpleGeoPoint(newCenter), getRange());
    }

    @Override
    public void onClick(GeoPoint clickedPoint) { 
        /* Dopo un pan parte sempre un click: facciamo partire la query qui
         * altrimenti a ogni microspostamento ne parte una */
        Log.d(DEBUG_TAG, "Click");
        currentDLTag = dh.startDowloading(new SimpleGeoPoint(clickedPoint), getRange());
    }
    /* *** END: ObservableMavView.MapViewListener ************** */

    
    
    
    private double getRange() {
        SimpleGeoPoint topLeft;
        SimpleGeoPoint bottomRight;
        SimpleGeoPoint center = new SimpleGeoPoint(mapView.getMapCenter());
        double latSpan = mapView.getLatitudeSpan() / 1E6;
        double longSpan = mapView.getLongitudeSpan() / 1E6;
        topLeft = new SimpleGeoPoint(
                center.getLatitude() - latSpan / 2,
                center.getLongitude() - longSpan / 2);
        bottomRight = new SimpleGeoPoint(
                center.getLatitude() + latSpan / 2,
                center.getLongitude() + longSpan / 2);
        Log.d(DEBUG_TAG, "Il range Ã¨: " + topLeft.calculateDistance(bottomRight) / 2);
        double distance = topLeft.calculateDistance(bottomRight) / 2;
        return (distance >= 0 && distance < 1) ? 1 : distance;
    }
    
    private static class DownloadHandler {
        private String                  urlString;
        private HashMap<String, String> postMap;
        private HTTPAccess              httpAccess;
        private int                     from;
        private SimpleGeoPoint          queryPoint;
        private double                  range;
        
        public DownloadHandler(HTTPAccess.ResponseListener listener,
                String category, Boolean isRisto) {
            httpAccess = new HTTPAccess();
            httpAccess.setResponseListener(listener);
            postMap = new HashMap<String, String>();
            postMap.put("request", "fetch");
            postMap.put("categ", category);
            postMap.put("ordina", "distanza");
            
            urlString = isRisto ? 
                    "http://www.cartaperdue.it/partner/v2.0/EsercentiRistorazione.php":
                    "http://www.cartaperdue.it/partner/v2.0/EsercentiNonRistorazione.php";
        }
        
        public String startDowloading(SimpleGeoPoint point, double range) {
            Log.d(DEBUG_TAG, "Starting Download from: " + point + "" + range);
            this.queryPoint = point;
            this.range = range;
            this.from = 0;
            postMap.put("lat",    "" + queryPoint.getLatitude());
            postMap.put("long",   "" + queryPoint.getLongitude());
            postMap.put("raggio", "" + range);
            postMap.put("from",   "" + from);
            Log.d(DEBUG_TAG, "startDownloading, postMap: " + postMap);
            return startHTTPConnection();
        }
        
        public void downloadMore() {
            postMap.put("from", "" + (from += 20));
            startHTTPConnection();
        }
        
        private void stopDownloading() {
            // TODO: implementare in HTTPAccess
        }
        
        public String getTag() {
            return queryPoint.toString() + range;
        }
        
        private String startHTTPConnection() {
            String tag = getTag();
            httpAccess.startHTTPConnection(
                    urlString,
                    HTTPAccess.Method.POST,
                    postMap,
                    getTag() + from);
            return tag;
        }
    }
}
