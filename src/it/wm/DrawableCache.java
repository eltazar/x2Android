/**
 * 
 */

package it.wm;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import it.wm.DownloaderTask.DownloadListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class DrawableCache implements DownloadListener {
    private static final String                 DEBUG_TAG     = "DrawableCache";
    private static final int                    MAX_SIZE      = 15;
    private static DrawableCache                INSTANCE      = null;
    private Context                             appContext    = null;
    static final int                            CACHE_HIT     = 1;
    static final int                            CACHE_PENDING = 2;
    static final int                            CACHE_MISS    = 3;
    private HashMap<DrawableRequest, CacheLine> cache         = null;
    private List<DrawableRequest>               cacheIdx      = null;
    
    private DrawableCache() {
        cache    = new HashMap<DrawableRequest, CacheLine>();
        cacheIdx = new LinkedList<DrawableRequest>();
    }
    
    public static DrawableCache getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DrawableCache();
            INSTANCE.appContext = context.getApplicationContext();
        }
        return INSTANCE;
    }
    
    private int getCacheLineStatus(DownloadRequest key) {
        //Log.d(DEBUG_TAG, "**getCacheLineStatus");
        CacheLine line = cache.get(key);
        if (line == null) {
            //Log.d(DEBUG_TAG, "**getCacheLineStatus: MISS");
            return CACHE_MISS;
        } // line != null
          // TODO: IMPORTANTE - se data non è null listeners dovrebbe esserlo
          // SEMPRE, invece evidentemente non succede... perché???
        if (line.drawable == null && line.listeners != null) {
            //Log.d(DEBUG_TAG, "**getCacheLineStatus: PENDING");
            return CACHE_PENDING;
        } else {
            //Log.d(DEBUG_TAG, "**getCacheLineStatus: HIT");
            return CACHE_HIT;
        }
        
    }
    
    public Drawable getCacheLine(DownloadRequest request, int reqWidth, int reqHeight, 
            DrawableCacheListener l) {
        //Log.d(DEBUG_TAG, "**getCacheLine");
        DrawableRequest params = new DrawableRequest(request, reqWidth, reqHeight);
        switch (getCacheLineStatus(params)) {
            case CACHE_HIT:
                return cache.get(params).drawable;
                
            case CACHE_PENDING:
                CacheLine line = cache.get(params);
                if (!line.listeners.contains(l)) {
                    //Log.d(DEBUG_TAG, "**getCacheLine: aggiungo listener");
                    line.listeners.add(l);
                }
                return null;
                
            case CACHE_MISS:
                CacheLine newLine = new CacheLine();
                if (!newLine.listeners.contains(l)) {
                    //Log.d(DEBUG_TAG, "**getCacheLine: aggiungo listener");
                    newLine.listeners.add(l);
                }
                cache.put(params, newLine);
                cacheIdx.add(params);
                if (cacheIdx.size() > MAX_SIZE) {
                    cache.remove(cacheIdx.remove(0));
                }
                Log.d(DEBUG_TAG, "Abbiamo " + cache.size() + " elementi.");
                DownloaderTask task = new DownloaderTask();
                task.setListener(this);
                task.execute(params);
                return null;
                
            default:
                return null;
        }
    }
    
    public void removeListener(DownloadRequest request, DrawableCacheListener l) {
        CacheLine line = cache.get(request);
        if (line != null && line.listeners != null) {
            line.listeners.remove(l);
        }
    }
    
    private Drawable convertData(byte[] data, int reqWidth, int reqHeight) {
        BitmapDrawable d = null;
        if ((new String(data)).equals("Use a placeholder")) {
            // TODO: settare un placeholder.
        } else {
            
            BitmapFactory.Options opt = new BitmapFactory.Options();
            
            opt.inJustDecodeBounds = true;
            opt.inPurgeable = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opt);
            //Log.d(DEBUG_TAG, "Image size: (" + opt.outWidth + ", " + opt.outHeight + ") -> (" 
            //+ reqWidth + ", " + reqHeight + ")");
            opt.inSampleSize = calculateInSampleSize(opt, reqWidth, reqHeight);
            
            opt.inJustDecodeBounds = false;
            try{
                d = new BitmapDrawable(appContext.getResources(), 
                        BitmapFactory.decodeByteArray(data, 0, data.length, opt));                
            }
            catch(OutOfMemoryError e){
                Log.d(DEBUG_TAG,"errore memoria esaurita"+e.getLocalizedMessage());
            }
        }
        return d;
    }
    
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        final int height = options.outHeight;
        final int width  = options.outWidth;
        int inSampleSize = 1;
        
        if ( (height > reqHeight || width > reqWidth)
              && (reqWidth > 0 && reqHeight > 0)      ) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }
    
    /* *** BEGIN: DownloaderTask.ResponseListener **************** */
    @Override
    public void onDownloadCompleted(DownloadRequest request, byte[] responseBody) {
        
        try{
        //Log.d(DEBUG_TAG, "**onHTTPResponseReceived");
        CacheLine line = cache.get(request);
        DrawableRequest dRequest = (DrawableRequest) request;
        line.drawable = convertData(responseBody, dRequest.reqWidth, dRequest.reqHeight);
        for (DrawableCacheListener l : line.listeners) {
            l.onCacheLineLoaded(request, line.drawable);
        }
        line.listeners.clear();
        line.listeners = null;
        }
        catch(NullPointerException e){
            Log.d(DEBUG_TAG,"null pointer in onDownloadCompleted");
        }
    }
    
    @Override
    public void onDownloadError(DownloadRequest request) {
        //Log.d(DEBUG_TAG, "**onHTTPerror");
        CacheLine line = cache.get(request);
        for (DrawableCacheListener l : line.listeners) {
            l.onCacheLineError(request);
        }
        line.listeners.clear();
        line.listeners = null;
        cache.remove(request);
    }
    
    /* *** END: DownloaderTask.ResponseListener **************** */
    
    private class DrawableRequest extends DownloadRequest {
        private int reqHeight;
        private int reqWidth;
        
        public DrawableRequest(String urlString, int httpMethod, HashMap<String, String> postMap,
                int reqWidth, int reqHeight) {
            super(urlString, httpMethod, postMap);
            this.reqWidth = reqWidth;
            this.reqHeight = reqHeight;
        }
        
        public DrawableRequest(DownloadRequest request, int reqWidth, int reqHeight) {
            super(request.urlString, request.httpMethod, request.postMap);
            this.reqWidth  = reqWidth;
            this.reqHeight = reqHeight;
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof DrawableRequest)) {
                return false;
            }
            if (o == this) {
                return true;
            }
            
            DrawableRequest p = (DrawableRequest) o;
            getPostString();
            
            if ((urlString == p.urlString || (urlString != null && urlString.equals(p.urlString)))
                    && (postString == p.postString || (postString != null && postString
                            .equals(p.postString)))
                    && (httpMethod == p.httpMethod)
                    && (reqWidth   == p.reqWidth)
                    && (reqHeight  == p.reqHeight)) {
                return true;
            } else {
                return false;
            }
        }
        
        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 37 * result + reqWidth;
            result = 37 * result + reqHeight;
            return result;
        }
    }
    
    private class CacheLine {
        public Drawable                    drawable = null;
        public List<DrawableCacheListener> listeners;
        
        public CacheLine() {
            listeners = new ArrayList<DrawableCacheListener>();
        }
        
    }
    
    public interface DrawableCacheListener {
        public void onCacheLineLoaded(DownloadRequest request, Drawable data);
        
        public void onCacheLineError(DownloadRequest request);
    }
    
}
