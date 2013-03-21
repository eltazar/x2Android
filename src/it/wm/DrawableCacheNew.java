/**
 * 
 */

package it.wm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.util.Log;

import it.wm.DownloaderTask.DownloadListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class DrawableCacheNew implements DownloadListener {
    private static final String                  DEBUG_TAG     = "DrawableCache";
    private static DrawableCacheNew              INSTANCE      = null;
    private Context                              appContext    = null;
    static final int                             CACHE_HIT     = 1;
    static final int                             CACHE_PENDING = 2;
    static final int                             CACHE_MISS    = 3;
    private LruCache<DrawableRequest2, CacheLine2> cache         = null;
    private List<DrawableRequest2>                cacheIdx      = null;
    
    private DrawableCacheNew() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        cache    = new LruCache<DrawableRequest2, CacheLine2>(cacheSize) {
            @Override
            protected int sizeOf(DrawableRequest2 request, CacheLine2 cacheLine) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                BitmapDrawable bd = (BitmapDrawable)cacheLine.drawable;
                if (bd == null) {
                    // In caso di CACHE_MISS viene aggiunta la linea di cache, senza il drawable,
                    // in stato: CACHE_PENDING. In questo caso la dimensione � zero.
                    return 0;
                }
                Bitmap bm = bd.getBitmap();
                return bm.getByteCount()/ 1024;
            }
        };
        cacheIdx = new LinkedList<DrawableRequest2>();
    }
    
    public static DrawableCacheNew getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DrawableCacheNew();
            INSTANCE.appContext = context.getApplicationContext();
        }
        return INSTANCE;
    }
    
    private int getCacheLineStatus(DrawableRequest2 key) {
        //Log.d(DEBUG_TAG, "**getCacheLineStatus");
        CacheLine2 line = cache.get(key);
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
            DrawableCacheListener2 l) {
        //Log.d(DEBUG_TAG, "**getCacheLine");
        DrawableRequest2 params = new DrawableRequest2(request, reqWidth, reqHeight);
        switch (getCacheLineStatus(params)) {
            case CACHE_HIT:
                return cache.get(params).drawable;
                
            case CACHE_PENDING:
                CacheLine2 line = cache.get(params);
                if (!line.listeners.contains(l)) {
                    //Log.d(DEBUG_TAG, "**getCacheLine: aggiungo listener");
                    line.listeners.add(l);
                }
                return null;
                
            case CACHE_MISS:
                CacheLine2 newLine = new CacheLine2();
                if (!newLine.listeners.contains(l)) {
                    //Log.d(DEBUG_TAG, "**getCacheLine: aggiungo listener");
                    newLine.listeners.add(l);
                }
                cache.put(params, newLine);
                cacheIdx.add(params);
                /*if (cacheIdx.size() > MAX_SIZE) {
                    cache.remove(cacheIdx.remove(0));
                }*/
                Log.d(DEBUG_TAG, "Abbiamo " + cache.size() + " elementi.");
                DownloaderTask task = new DownloaderTask();
                task.setListener(this);
                task.execute(params);
                return null;
                
            default:
                return null;
        }
    }
    
    public void removeListener(DownloadRequest request, DrawableCacheListener2 l) {
        CacheLine2 line = cache.get((DrawableRequest2)request);
        if (line != null && line.listeners != null) {
            line.listeners.remove(l);
        }
    }
    
    private Drawable convertData(byte[] data, int reqWidth, int reqHeight) {
        BitmapDrawable d = null;
        if ((new String(data)).equals("Use a placeholder")) {
//            int imageResource = R.drawable.logo;
//            d = (BitmapDrawable) appContext.getResources().getDrawable(imageResource);
            
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
        DrawableRequest2 dRequest = (DrawableRequest2) request;
        CacheLine2 line = cache.get(dRequest);
        line.drawable = convertData(responseBody, dRequest.reqWidth, dRequest.reqHeight);
        for (DrawableCacheListener2 l : line.listeners) {
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
        CacheLine2 line = cache.get((DrawableRequest2)request);
        for (DrawableCacheListener2 l : line.listeners) {
            l.onCacheLineError(request);
        }
        line.listeners.clear();
        line.listeners = null;
        cache.remove((DrawableRequest2)request);
    }
    
    /* *** END: DownloaderTask.ResponseListener **************** */
    
    private class DrawableRequest2 extends DownloadRequest {
        private int reqHeight;
        private int reqWidth;
        
        public DrawableRequest2(String urlString, int httpMethod, HashMap<String, String> postMap,
                int reqWidth, int reqHeight) {
            super(urlString, httpMethod, postMap);
            this.reqWidth = reqWidth;
            this.reqHeight = reqHeight;
        }
        
        public DrawableRequest2(DownloadRequest request, int reqWidth, int reqHeight) {
            super(request.urlString, request.httpMethod, request.postMap);
            this.reqWidth  = reqWidth;
            this.reqHeight = reqHeight;
        }
        
        public boolean equals(Object o) {
            if (!(o instanceof DrawableRequest2)) {
                return false;
            }
            if (o == this) {
                return true;
            }
            
            DrawableRequest2 p = (DrawableRequest2) o;
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
    
    private class CacheLine2 {
        public Drawable                    drawable = null;
        public List<DrawableCacheListener2> listeners;
        
        public CacheLine2() {
            listeners = new ArrayList<DrawableCacheListener2>();
        }
        
    }
    
    public interface DrawableCacheListener2 {
        public void onCacheLineLoaded(DownloadRequest request, Drawable data);
        public void onCacheLineError(DownloadRequest request);
    }
    
}
