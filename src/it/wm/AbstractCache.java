/**
 * 
 */

package it.wm;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Gabriele "Whisky" Visconti
 */
abstract class AbstractCache<T> implements DownloaderTask.DownloadListener {
    static final String                            DEBUG_TAG     = "AbstractCache";
    static final int                               CACHE_HIT     = 1;
    static final int                               CACHE_PENDING = 2;
    static final int                               CACHE_MISS    = 3;
    private HashMap<DownloadRequest, CacheLine<T>> cache         = null;
    
    protected AbstractCache() {
        if (cache == null) {
            cache = new HashMap<DownloadRequest, CacheLine<T>>();
        }
    }
    
    protected int getCacheLineStatus(DownloadRequest key) {
        Log.d(DEBUG_TAG, "**getCacheLineStatus");
        CacheLine<T> line = cache.get(key);
        if (line == null) {
            Log.d(DEBUG_TAG, "**getCacheLineStatus: MISS");
            return CACHE_MISS;
        } // line != null
          // TODO: IMPORTANTE - se data non è null listeners dovrebbe esserlo
          // SEMPRE, invece evidentemente non succede... perché???
        if (line.data == null && line.listeners != null) {
            Log.d(DEBUG_TAG, "**getCacheLineStatus: PENDING");
            return CACHE_PENDING;
        } else {
            Log.d(DEBUG_TAG, "**getCacheLineStatus: HIT");
            return CACHE_HIT;
        }
        
    }
    
    public T getCacheLine(DownloadRequest params, CacheListener<T> l) {
        Log.d(DEBUG_TAG, "**getCacheLine");
        switch (getCacheLineStatus(params)) {
            case CACHE_HIT:
                return cache.get(params).data;
                
            case CACHE_PENDING:
                CacheLine<T> line = cache.get(params);
                if (!line.listeners.contains(l)) {
                    Log.d(DEBUG_TAG, "**getCacheLine: aggiungo listener");
                    line.listeners.add(l);
                }
                return null;
                
            case CACHE_MISS:
                CacheLine<T> newLine = new CacheLine<T>();
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
    
    protected abstract T convertData(byte[] data);
    
    public void removeListener(DownloadRequest request, CacheListener<T> l) {
        CacheLine<T> line = cache.get(request);
        if (line != null && line.listeners != null) {
            line.listeners.remove(l);
        }
    }
    
    /* *** BEGIN: DownloaderTask.ResponseListener **************** */
    @Override
    public void onDownloadCompleted(DownloadRequest request, byte[] responseBody) {
        Log.d(DEBUG_TAG, "**onHTTPResponseReceived");
        CacheLine<T> line = cache.get(request);
        line.data = convertData(responseBody);
        for (CacheListener<T> l : line.listeners) {
            l.onCacheLineLoaded(request, line.data);
        }
        line.listeners.clear();
        line.listeners = null;
    }
    
    @Override
    public void onDownloadError(DownloadRequest request) {
        Log.d(DEBUG_TAG, "**onHTTPerror");
        CacheLine<T> line = cache.get(request);
        for (CacheListener<T> l : line.listeners) {
            l.onCacheLineError(request);
        }
        line.listeners.clear();
        line.listeners = null;
        cache.remove(request);
    }
    
    /* *** END: DownloaderTask.ResponseListener **************** */
    
    private class CacheLine<K> {
        public T                      data = null;
        public List<CacheListener<K>> listeners;
        
        public CacheLine() {
            listeners = new ArrayList<CacheListener<K>>();
        }
        
    }
    
    public interface CacheListener<J> {
        public void onCacheLineLoaded(DownloadRequest request, J data);
        
        public void onCacheLineError(DownloadRequest request);
    }
}
