/**
 * 
 */

package it.wm.perdue.doveusarla;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.wm.HTTPAccess;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;

import java.util.ArrayList;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class EsercentiItemizedOverlay extends ItemizedOverlay<OverlayItem> implements
        HTTPAccess.ResponseListener {
    private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
    private ArrayList<Integer>     ids      = new ArrayList<Integer>();
    
    public EsercentiItemizedOverlay(Drawable marker) {
        super(boundCenterBottom(marker));
    }
    
    @Override
    protected OverlayItem createItem(int index) {
        Log.d("MapView", "createItem");
        return overlays.get(index);
    }
    
    @Override
    public int size() {
        return overlays.size();
    }
    
    public void addItem(Esercente esercente) {
        GeoPoint geoPoint = Utils.geoPoint(esercente.getLatitude(), esercente.getLongitude());
        OverlayItem item = new OverlayItem(
                geoPoint,
                esercente.getInsegna(),
                esercente.getIndirizzo());
        overlays.add(item);
        populate();
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String json) {
        json = Utils.stripEsercente(json);
        json = Utils.stripFinalFalse(json);
        
        Gson gson = Utils.getGson();
        Esercente[] objects = null;
        try {
            objects = gson.fromJson(json, Esercente[].class);
        } catch (JsonSyntaxException e) {
            // In teoria se siamo qui, significa che è arrivato un array vuoto,
            Log.d("JSONListAdapter", "Ho rilevato un array vuoto");
            e.printStackTrace();
            objects = new Esercente[0];
        }
        for (Esercente e : objects) {
            int id = e.getID();
            if (!ids.contains(id)) {
                ids.add(id);
                this.addItem(e);
            }
        }
        // return objects.length;
    }
    
    @Override
    public void onHTTPerror(String tag) {
        
    }
}
