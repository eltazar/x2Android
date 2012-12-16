/**
 *  Copyright Stuff
 */

package it.wm;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>HTTPAccess</code> provides an intuitive and simplified API to access
 * network resources through the HTTP protocol. TODO: Add a short example of use
 * 
 * @author Gabriele "Whisky" Visconti
 */
public class HTTPAccess implements DownloaderTask.DownloadListener {
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
    public Boolean startHTTPConnection(String urlString, Method method,
            Map<String, String> postMap, String tag) {
        int httpMethod;
        if (method == Method.POST) {
            httpMethod = DownloadRequest.POST;
        } else {
            httpMethod = DownloadRequest.GET;
        }
        
        DownloadRequest params = new DownloadRequest(urlString, httpMethod, postMap);
        Log.d(DEBUG_TAG, "postMap is: " + postMap);
        tag = (tag == null ? urlString : tag);
        if (tagMap.put(params, tag) == null) {
            // Eseguiamo la richiesta solo se non è già in corso
            DownloaderTask task = new DownloaderTask();
            task.setListener(this);
            task.execute(params);
            Log.d(DEBUG_TAG, "Starting download");
            return true;
        }
        Log.d(DEBUG_TAG, "Download ALREADY started");
        return false;
    }
    
    /* *** BEGIN: DownloaderTask.ResponseListener **************** */
    @Override
    public void onDownloadCompleted(DownloadRequest request, byte[] responseBody) {
        Log.d(DEBUG_TAG, "onDownloadCompleted");
        String tag = tagMap.remove(request);
        if (listener != null) {
            listener.onHTTPResponseReceived(tag, new String(responseBody));
        }
    }
    
    @Override
    public void onDownloadError(DownloadRequest request) {
        String tag = tagMap.remove(request);
        if (listener != null) {
            listener.onHTTPerror(tag);
        }
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
