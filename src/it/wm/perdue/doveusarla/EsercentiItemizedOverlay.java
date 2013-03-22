/**
 * 
 */

package it.wm.perdue.doveusarla;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

import it.wm.SimpleGeoPoint;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;
import it.wm.perdue.dettaglioEsercenti.DettaglioEsercenteBaseActivity;

import java.util.ArrayList;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class EsercentiItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
    private static final String    DEBUG_TAG = "EsercentiItemizedOverlay";
    private Context                context   = null;
    private ArrayList<OverlayItem> overlays  = new ArrayList<OverlayItem>();
    private ArrayList<Integer>     ids       = new ArrayList<Integer>();
    private Boolean                isRisto   = false;
    
    public EsercentiItemizedOverlay(Context context, Drawable marker, MapView mapView) {
        super(boundCenterBottom(marker), mapView);
        this.context = context;
        populate();
    }
    
    @Override
    protected OverlayItem createItem(int index) {
        //Log.d("MapView", "createItem");
        return overlays.get(index);
    }
    
    @Override
    public int size() {
        return overlays.size();
    }
    
    public void addItem(Esercente esercente) {
        GeoPoint geoPoint = new SimpleGeoPoint(esercente.getLatitude(), esercente.getLongitude())
                .toGeoPoint();
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
        Intent intent = new Intent(context, DettaglioEsercenteBaseActivity.class);
        EsercenteOverlayItem ese = (EsercenteOverlayItem) item;
        intent.putExtra(DettaglioEsercenteBaseActivity.Tags.ID,       "" + ese.getID());
        intent.putExtra(DettaglioEsercenteBaseActivity.Tags.TITLE,    ese.getInsegna());
        intent.putExtra(DettaglioEsercenteBaseActivity.Tags.IS_RISTO, isRisto);
        context.startActivity(intent);

        return true;
    }
    
    public int addFromJSON(String json) {
        json = Utils.formatJSON(json);
        
        Gson gson = Utils.getGson();
        Esercente[] objects = null;
        try {
            objects = gson.fromJson(json, Esercente[].class);
        } catch (JsonSyntaxException e) {
            // In teoria se siamo qui, significa che è arrivato un array vuoto,
            Log.d(DEBUG_TAG, "Ho rilevato un array vuoto");
            //e.printStackTrace();
            objects = new Esercente[0];
        }
        for (Esercente e : objects) {
            int id = e.getID();
            //Log.d(DEBUG_TAG, "(" + id + ") " + e.getInsegna());
            if (!ids.contains(id)) {
                ids.add(id);
                this.addItem(e);
            } else {
                //Log.d(DEBUG_TAG, "Zompo");
            }
        }
        Log.d(DEBUG_TAG, "Esercenti nel json: " + objects.length);
        return objects.length;
    }
    
    public void setRisto(Boolean isRisto) {
        this.isRisto = isRisto;
    }
    
    protected static class EsercenteOverlayItem extends OverlayItem {
        private int    id      = 0;
        private String insegna = null;
        
        public EsercenteOverlayItem(GeoPoint geoPoint, String insegna, String indirizzo, int id) {
            super(geoPoint, insegna, indirizzo);
            this.id = id;
            this.insegna = insegna;
        }
        
        public int getID() {
            return id;
        }
        
        public String getInsegna() {
            return this.insegna;
        }
    }
}
