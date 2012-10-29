/**
 * 
 */

package it.wm;

import android.os.AsyncTask;
import android.util.Log;

import it.wm.DownloaderTask.Params;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * An AsyncTask which opens a network connection using the HTTP protocol and
 * exchanges data in a separate thread.
 * 
 * @author Gabriele "Whisky" Visconti
 */
class DownloaderTask extends AsyncTask<Params, Void, byte[]> {

    /** tag meant to be used in ${Link android.util.Log} */
    private static final String DEBUG_TAG  = "DownloaderTask";
    /**
     * The <code>ResponseListener</code> object which will be notified about
     * connection error and will receive the requsted HTTP page
     */
    private ResponseListener    listener   = null;

    /** String containing the POST data, reconstructed from <code>postMap</code> */
    private String              postString = null;
    /** Object used to open the socket and handle the HTTP socket. */
    private HttpURLConnection   conn       = null;

    /** Sets the listener */
    public void setListener(ResponseListener l) {
        this.listener = l;
    }

    @Override
    protected byte[] doInBackground(Params... params) {
        // Se method è null viene assunto GET.
        // Se postMap è null viene assunto un campo dati POST vuoto.
        // Se url è null, l'AsyncTask termina richiamando la callback di
        // errore.
        Log.d(DEBUG_TAG, "Starting background work");

        URL url = params[0].url;
        String method = params[0].method;
        buildPostString(params[0].postMap);

        if (url == null) {
            Log.e(DEBUG_TAG, "Unable to connect to a null URL");
            return null;
        }

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setDoInput(true);

            if (method.toUpperCase().equals("POST")) {
                // Setting POST data:
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setFixedLengthStreamingMode(postString.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(postString);
                out.close();
            } else if (method.toUpperCase().equals("GET")) {
                conn.setRequestMethod("GET");
            } else {
                throw new RuntimeException("Invalid Connection Method");
            }

            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d(DEBUG_TAG, "Content Type: " + conn.getContentType());
            Log.d(DEBUG_TAG, "The response is: " + responseCode);

            return fetchResponse();
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "Unable to connect to " + url.toString() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            conn.disconnect();
        }
    }

    public void onPostExecute(byte[] result) {
        if (listener == null) {
            return;
        }
        if (result != null) {
            listener.onHTTPResponseReceived(this, result);
        } else {
            listener.onHTTPerror(this);
        }
    }

    /** Builds the postData */
    private void buildPostString(HashMap<String, String> postMap) {
        if (postMap == null) {
            postString = "";
            return;
        }
        StringBuilder postStringBuilder = new StringBuilder();
        for (Iterator<String> i = postMap.keySet().iterator(); i.hasNext();) {
            String key = i.next();
            try {
                postStringBuilder.append(URLEncoder.encode(key, "UTF-8"));
                postStringBuilder.append("=");
                postStringBuilder.append(URLEncoder.encode(postMap.get(key), "UTF-8"));
                postStringBuilder.append("&");
            } catch (UnsupportedEncodingException e) {
                // Ma dai.... con utf-8? Mi ci gioco le palle che in
                // questo blocco non ci entreramo mai!!
                postString = "";
                return;
            }
        }
        if (postStringBuilder.length() > 0) {
            postStringBuilder.deleteCharAt(postStringBuilder.length() - 1);
        }
        postString = postStringBuilder.toString();
    }

    /**
     * Receives the requested HTTP page, through the
     * <code>HttpURLConnection</code> object
     */
    private byte[] fetchResponse() throws IOException {
        InputStream inputStream = conn.getInputStream();
        Log.d(DEBUG_TAG, "Headers in the HTTP response: \n" + conn.getHeaderFields().toString());

        // Premature optimization is the root of all evil. Donald Knuth
        // maledicimi.
        List<Byte> bytesList = new LinkedList<Byte>();

        int n = 0;
        int length = 0;
        byte[] chunk = new byte[1500]; // 1500 bytes è MTU tipica su internet.
        while ((n = inputStream.read(chunk)) > 0) {
            for (int i = 0; i < n; i++) {
                bytesList.add(chunk[i]);
            }
            length += n;
            Log.d(DEBUG_TAG, "Read " + n + " bytes.\n");
        }
        Log.d(DEBUG_TAG, "Total: " + length + " bytes.\n");

        byte[] responseBody = new byte[bytesList.size()];

        int i = 0;
        for (Byte b : bytesList) {
            responseBody[i++] = b;
        }

        return responseBody;
    }

    /**
     * Class meant to be used only to pass data from the HTTPAccess Singleton to
     * the ${link DownloaderTask} it creates whenerver a new connection is
     * opened.
     * <p>
     * Data is not passed directly to the <code>Params.. params</code> argument
     * of DownloaderTask to enforce type-checking and safety.
     * 
     * @author Gabriele "Whisky" Visconti
     */
    public static class Params {
        /** The {$link URL} of the requested HTTP page. */
        public URL                     url;
        /** The HTTP connection method. */
        public String                  method;
        /**
         * A HashMap of POST parameters.
         * 
         * @see startHTTPConnection
         */
        public HashMap<String, String> postMap;

        /** Creates an object with the given data. */
        public Params(URL url, String method, HashMap<String, String> postMap) {
            this.url = url;
            this.method = method;
            this.postMap = postMap;
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
        public void onHTTPResponseReceived(DownloaderTask task, byte[] response);

        /**
         * This method will be called if an error happened while trying to
         * download the requested HTTP page
         */
        public void onHTTPerror(DownloaderTask task);
    }
}
