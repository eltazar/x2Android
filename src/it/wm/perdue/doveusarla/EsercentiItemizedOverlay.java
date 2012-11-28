/**
 * 
 */

package it.wm.perdue.doveusarla;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;

import java.util.ArrayList;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class EsercentiItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
    private static final String    DEBUG_TAG = "EsercentiItemizedOverlay";
    private Context                context   = null;
    private ArrayList<OverlayItem> overlays  = new ArrayList<OverlayItem>();
    private ArrayList<Integer>     ids       = new ArrayList<Integer>();
    
    public EsercentiItemizedOverlay(Context context, Drawable marker, MapView mapView) {
        super(boundCenterBottom(marker), mapView);
        this.context = context;
        populate();
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
        OverlayItem item = new EsercenteOverlayItem(
                geoPoint,
                esercente.getInsegna(),
                esercente.getIndirizzo(),
                esercente.getID());
        overlays.add(item);
        populate();
    }
    
    @Override
    protected boolean onBalloonTap(int index, OverlayItem item) {
        Toast.makeText(context, "" + ((EsercenteOverlayItem) item).getID() +
                "/" + overlays.size() + "/" + ids.size(), Toast.LENGTH_LONG)
                .show();
        return true;
    }
    
    public void addFromJSON(String json) {
        json = Utils.stripEsercente(json);
        json = Utils.stripFinalFalse(json);
        
        Gson gson = Utils.getGson();
        Esercente[] objects = null;
        try {
            objects = gson.fromJson(json, Esercente[].class);
        } catch (JsonSyntaxException e) {
            // In teoria se siamo qui, significa che è arrivato un array vuoto,
            Log.d(DEBUG_TAG, "Ho rilevato un array vuoto");
            e.printStackTrace();
            objects = new Esercente[0];
        }
        for (Esercente e : objects) {
            int id = e.getID();
            Log.d(DEBUG_TAG, "(" + id + ") " + e.getInsegna());
            if (!ids.contains(id)) {
                ids.add(id);
                this.addItem(e);
            } else {
                Log.d(DEBUG_TAG, "Zompo");
            }
        }
        // return objects.length;
        Log.d(DEBUG_TAG, "Esercenti nel json: " + objects.length);
    }
    
    protected static class EsercenteOverlayItem extends OverlayItem {
        private int id = 0;
        
        public EsercenteOverlayItem(GeoPoint geoPoint, String insegna, String indirizzo, int id) {
            super(geoPoint, insegna, indirizzo);
            this.id = id;
        }
        
        public int getID() {
            return id;
        }
    }
}
