/**
 *  Copyright Stuff
 */

package it.wm;

import it.wm.AbstractCache.CacheListener;

import java.util.HashMap;

/**
 * <code>HTTPAccess</code> provides an intuitive and simplified API to access
 * network resources through the HTTP protocol. TODO: Add a short example of use
 * 
 * @author Gabriele "Whisky" Visconti
 */
public class HTTPAccess implements CacheListener {
    /**
     * Represents the HTTP connection method.
     * 
     * @author Gabriele "Whisky" Visconti
     */
    public enum Method {
        /** GET request */
        GET,
        /** POST request */
        POST
    }

    /** tag meant to be used in ${Link android.util.Log} */
    private static final String              DEBUG_TAG = "HTTPAccess";
    private ResponseListener                 listener  = null;
    // private HashMap<DownloaderTask, ResponseListener> connectionMap = null;
    private HashMap<DownloadRequest, String> tagMap    = null;

    public HTTPAccess() {
        // connectionMap = new HashMap<DownloaderTask, ResponseListener>();
        tagMap = new HashMap<DownloadRequest, String>();
    }

    public ResponseListener getResponseListener() {
        return this.listener;
    }

    public void setResponseListener(ResponseListener listener) {
        this.listener = listener;
        if (this.listener == null) {
            for (DownloadRequest r : tagMap.keySet()) {
                AbstractCache.getInstance().removeListener(r, this);
            }
        }
    }

    /**
     * Opens an HTTP connection and fetches the requested page, with a {$link
     * ResponseListener} object which handles received data and errors.
     * 
     * @param url The {$link URL} to connect to. If <code>null</code> the HTTP
     *            connection won't be opened.
     * @param method The HTTP connection method.
     * @param parameters A HashMap of POST parameters: each (key, value) couple
     *            represents (postParameterName, postParameterValue). If null no
     *            POST data will be included in the HTTP request.
     * @param listener An object wich receives events related to a connection
     *            completed successfully or with error. If <code>null</code> the
     *            connection status won't be notified.
     */
    public void startHTTPConnection(String urlString, Method method,
            HashMap<String, String> postMap, String tag) {
        int httpMethod;
        if (method == Method.POST) {
            httpMethod = DownloadRequest.POST;
        } else {
            httpMethod = DownloadRequest.GET;
        }

        DownloadRequest params = new DownloadRequest(urlString, httpMethod, postMap);
        AbstractCache cache = AbstractCache.getInstance();
        byte[] data = cache.getCacheLine(params, this);
        if (data != null && listener != null) {
            listener.onHTTPResponseReceived(tag, new String(data));
        } else {
            tag = (tag == null ? urlString : tag);
            tagMap.put(params, tag);
        }
    }

    @Override
    public void onCacheLineLoaded(DownloadRequest request, byte[] data) {
        String tag;
        synchronized (this) {
            // listener = connectionMap.remove(task);
            tag = tagMap.remove(request);
        }
        if (listener == null) {
            return;
        }
        listener.onHTTPResponseReceived(tag, new String(data));
    }

    @Override
    public void onCacheLineError(DownloadRequest request) {
        String tag;
        synchronized (this) {
            // listener = connectionMap.remove(task);
            tag = tagMap.remove(request);
        }
        if (listener == null) {
            return;
        }
        listener.onHTTPerror(tag);
    }

    /**
     * This interface must be implemented by the object used as a Listener for
     * the HTTP request
     * 
     * @author Gabriele "Whisky" Visconti
     */
    public interface ResponseListener {
        /**
         * This method will be called if the HTTP page
         * 
         * @param response the contents of the requested HTTP page
         */
        public void onHTTPResponseReceived(String tag, String response);

        /**
         * This method will be called if an error happened while trying to
         * download the requested HTTP page
         */
        public void onHTTPerror(String tag);
    }
}
