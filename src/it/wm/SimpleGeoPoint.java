/**
 * 
 */

package it.wm;

import com.google.android.maps.GeoPoint;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class SimpleGeoPoint {
    private double latitude;
    private double longitude;
    
    public SimpleGeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public SimpleGeoPoint(GeoPoint point) {
        this.latitude = point.getLatitudeE6() / 1E6;
        this.longitude = point.getLongitudeE6() / 1E6;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public GeoPoint toGeoPoint() {
        return new GeoPoint(
                Math.round((float) (this.latitude * 1E6)),
                Math.round((float) (this.longitude * 1E6)));
    }
    
    public double calculateDistance(SimpleGeoPoint point) {
        double r = 6371;
        double latRad1 = Math.PI * this.latitude / 180;
        double longRad1 = Math.PI * this.longitude / 180;
        double latRad2 = Math.PI * point.latitude / 180;
        double longRad2 = Math.PI * point.longitude / 180;
        
        double phi = Math.abs(longRad1 - longRad2);
        
        double d = Math.acos(
                Math.sin(latRad1) * Math.sin(latRad2) +
                        Math.cos(latRad2) * Math.cos(latRad1) * Math.cos(phi)
                ) * r;
        return d;
    }
    
}
