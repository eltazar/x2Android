package it.wm;

import com.google.android.maps.GeoPoint;

import it.wm.HTTPAccess.ResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeoDecoder implements ResponseListener {
    private  HTTPAccess httpAccess = null;
    private GeoDecoder.Listener listener = null;
    
    public interface Listener{
       public void onGeoPointReceived(SimpleGeoPoint sgp);
       public void onGeoPointNotReceived();

    }
    
    public GeoDecoder(){
         httpAccess = new HTTPAccess();
         httpAccess.setResponseListener(this);
        // Log.d("map","geodec allocato");
    }
    
    public void setListener(Listener listener){
        this.listener = listener;
    }
    
    public void getLocationInfo(String address) {
        //Log.d("map","geodec start query");

        String urlString = "http://maps.google.com/maps/api/geocode/json?address=" +address+"&ka&sensor=false";
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, null);
    }
    
    public static GeoPoint getGeoPoint(JSONObject jsonObject) {
        
        Double lon = new Double(0);
        Double lat = new Double(0);
        
        try {
            
            lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");
            
            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
    }

    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        try {
            //Log.d("map","respone = "+response);
            JSONObject jo = new JSONObject(response);
            SimpleGeoPoint sgp = new SimpleGeoPoint(getGeoPoint(jo));
            if(listener != null)
                listener.onGeoPointReceived(sgp);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        
    }
    
}