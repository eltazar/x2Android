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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Gabriele "Whisky" Visconti
 */
public class DrawableCache implements DownloadListener {
    private static final String                 DEBUG_TAG     = "DrawableCache";
    private static DrawableCache                INSTANCE      = null;
    private Context                             appContext    = null;
    static final int                            CACHE_HIT     = 1;
    static final int                            CACHE_PENDING = 2;
    static final int                            CACHE_MISS    = 3;
    private HashMap<DownloadRequest, CacheLine> cache         = null;
    
    private DrawableCache() {
        cache = new HashMap<DownloadRequest, CacheLine>();
    }
    
    public static DrawableCache getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DrawableCache();
            INSTANCE.appContext = context.getApplicationContext();
        }
        return INSTANCE;
    }
    
    private int getCacheLineStatus(DownloadRequest key) {
        Log.d(DEBUG_TAG, "**getCacheLineStatus");
        CacheLine line = cache.get(key);
        if (line == null) {
            Log.d(DEBUG_TAG, "**getCacheLineStatus: MISS");
            return CACHE_MISS;
        } // line != null
          // TODO: IMPORTANTE - se data non è null listeners dovrebbe esserlo
          // SEMPRE, invece evidentemente non succede... perché???
        if (line.drawable == null && line.listeners != null) {
            Log.d(DEBUG_TAG, "**getCacheLineStatus: PENDING");
            return CACHE_PENDING;
        } else {
            Log.d(DEBUG_TAG, "**getCacheLineStatus: HIT");
            return CACHE_HIT;
        }
        
    }
    
    public Drawable getCacheLine(DownloadRequest params, DrawableCacheListener l) {
        Log.d(DEBUG_TAG, "**getCacheLine");
        switch (getCacheLineStatus(params)) {
            case CACHE_HIT:
                return cache.get(params).drawable;
                
            case CACHE_PENDING:
                CacheLine line = cache.get(params);
                if (!line.listeners.contains(l)) {
                    Log.d(DEBUG_TAG, "**getCacheLine: aggiungo listener");
                    line.listeners.add(l);
                }
                return null;
                
            case CACHE_MISS:
                CacheLine newLine = new CacheLine();
                if (!newLine.listeners.contains(l)) {
                    Log.d(DEBUG_TAG, "**getCacheLine: aggiungo listener");
                    newLine.listeners.add(l);
                }
                cache.put(params, newLine);
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
    
    private Drawable convertData(byte[] data) {
        BitmapDrawable d = null;
        if ((new String(data)).equals("Use a placeholder")) {
            // TODO: settare un placeholder.
        } else {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opt);
            Log.d(DEBUG_TAG, "Image size: (" + opt.outWidth + ", " + opt.outHeight + ")");
            // opt.inSampleSize = calculateInSampleSize(opt, displaySize.x,
            // displaySize.y);
            
            d = new BitmapDrawable(
                    appContext.getResources(),
                    new ByteArrayInputStream(data));
        }
        return d;
    }
    
    /* *** BEGIN: DownloaderTask.ResponseListener **************** */
    @Override
    public void onDownloadCompleted(DownloadRequest request, byte[] responseBody) {
        Log.d(DEBUG_TAG, "**onHTTPResponseReceived");
        CacheLine line = cache.get(request);
        line.drawable = convertData(responseBody);
        for (DrawableCacheListener l : line.listeners) {
            l.onCacheLineLoaded(request, line.drawable);
        }
        line.listeners.clear();
        line.listeners = null;
        
    }
    
    @Override
    public void onDownloadError(DownloadRequest request) {
        Log.d(DEBUG_TAG, "**onHTTPerror");
        CacheLine line = cache.get(request);
        for (DrawableCacheListener l : line.listeners) {
            l.onCacheLineError(request);
        }
        line.listeners.clear();
        line.listeners = null;
        cache.remove(request);
    }
    
    /* *** END: DownloaderTask.ResponseListener **************** */
    
    private class CacheLine {
        public Drawable                 drawable = null;
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
