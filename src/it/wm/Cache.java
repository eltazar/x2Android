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
/* abstract */class Cache implements DownloaderTask.DownloadListener {
    static final String                         DEBUG_TAG     = "AbstractCache";
    static final int                            CACHE_HIT     = 1;
    static final int                            CACHE_PENDING = 2;
    static final int                            CACHE_MISS    = 3;
    private static Cache                __instance    = null;
    private HashMap<DownloadRequest, CacheLine> cache         = null;

    // public abstract void onCacheLineLoaded();

    // public abstract void onCacheLineError();

    private Cache() {
        if (cache == null) {
            cache = new HashMap<DownloadRequest, CacheLine>();
        }
    }

    public static Cache getInstance() {
        if (__instance == null) {
            __instance = new Cache();
        }
        return __instance;
    }

    public int getCacheLineStatus(DownloadRequest key) {
        Log.d(DEBUG_TAG, "**getCacheLineStatus");
        CacheLine line = cache.get(key);
        if (line == null) {
            Log.d(DEBUG_TAG, "**getCacheLineStatus: MISS");
            return CACHE_MISS;
        } // line != null
        if (line.data == null) {
            Log.d(DEBUG_TAG, "**getCacheLineStatus: PENDING");
            return CACHE_PENDING;
        } else {
            Log.d(DEBUG_TAG, "**getCacheLineStatus: HIT");
            return CACHE_HIT;
        }

    }

    public byte[] getCacheLine(DownloadRequest params, CacheListener l) {
        Log.d(DEBUG_TAG, "**getCacheLine");
        switch (getCacheLineStatus(params)) {
            case CACHE_HIT:
                return cache.get(params).data;

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

    public void removeListener(DownloadRequest request, CacheListener l) {
        CacheLine line = cache.get(request);
        if (line != null && line.listeners != null) {
            line.listeners.remove(l);
        }
    }

    /* *** BEGIN: DownloaderTask.ResponseListener **************** */
    @Override
    public void onDownloadCompleted(DownloadRequest request, byte[] responseBody) {
        Log.d(DEBUG_TAG, "**onHTTPResponseReceived");
        CacheLine line = cache.get(request);
        line.data = responseBody;
        for (CacheListener l : line.listeners) {
            l.onCacheLineLoaded(request, line.data);
        }
        line.listeners.clear();
        line.listeners = null;
    }

    @Override
    public void onDownloadError(DownloadRequest request) {
        Log.d(DEBUG_TAG, "**onHTTPerror");
        CacheLine line = cache.get(request);
        for (CacheListener l : line.listeners) {
            l.onCacheLineError(request);
        }
        line.listeners.clear();
        line.listeners = null;
        cache.remove(request);
    }

    /* *** END: DownloaderTask.ResponseListener **************** */

    private class CacheLine {
        public byte[]              data = null;
        public List<CacheListener> listeners;

        public CacheLine() {
            listeners = new ArrayList<CacheListener>();
        }

    }

    public interface CacheListener {
        public void onCacheLineLoaded(DownloadRequest request, byte[] data);

        public void onCacheLineError(DownloadRequest request);
    }
}
