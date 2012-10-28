/**
 *  Copyright Stuff
 */

package it.wm;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * <code>HTTPAccess</code> provides an intuitive and simplified API to access
 * network resources through the HTTP protocol. TODO: Add a short example of use
 * 
 * @author Gabriele "Whisky" Visconti
 */
public class HTTPAccess implements DownloaderTask.ResponseListener {
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
    private static final String DEBUG_TAG = "HTTPAccess";
    private ResponseListener listener = null;
    // private HashMap<DownloaderTask, ResponseListener> connectionMap = null;
    private HashMap<DownloaderTask, String> tagMap = null;

    public HTTPAccess() {
        // connectionMap = new HashMap<DownloaderTask, ResponseListener>();
        tagMap = new HashMap<DownloaderTask, String>();
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
    public void startHTTPConnection(URL url, Method method, HashMap<String, String> parameters,
            String tag) {
        DownloaderTask task;
        synchronized (this) { // TODO: ma serve davvero?
            task = new DownloaderTask();
            task.setListener(this);
            // connectionMap.put(task, listener);
            tagMap.put(task, tag);
        }
        task.execute(new DownloaderTask.Params(url, method.toString(), parameters));

    }

    public void startHTTPConnection(String urlString, Method method,
            HashMap<String, String> parameters, String tag) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(DEBUG_TAG, "Malformed URL. Not opening http connection.");
            e.printStackTrace();
            return;
        }
        startHTTPConnection(url, method, parameters, tag);
    }

    @Override
    public void onHTTPResponseReceived(DownloaderTask task, byte[] response) {
        String tag;
        synchronized (this) {
            // listener = connectionMap.remove(task);
            tag = tagMap.remove(task);
        }
        if (listener == null) {
            return;
        }
        listener.onHTTPResponseReceived(tag, new String(response));
    }

    @Override
    public void onHTTPerror(DownloaderTask task) {
        String tag;
        synchronized (this) {
            // listener = connectionMap.remove(task);
            tag = tagMap.remove(task);
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
