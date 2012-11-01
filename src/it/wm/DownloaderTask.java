/**
 * 
 */

package it.wm;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An AsyncTask which opens a network connection using the HTTP protocol and
 * exchanges data in a separate thread.
 * 
 * @author Gabriele "Whisky" Visconti
 */
class DownloaderTask extends AsyncTask<DownloadRequest, Void, byte[]> {

    /** tag meant to be used in ${Link android.util.Log} */
    private static final String DEBUG_TAG = "DownloaderTask";
    /**
     * The <code>ResponseListener</code> object which will be notified about
     * connection error and will receive the requsted HTTP page
     */
    private DownloadListener    listener  = null;

    private DownloadRequest      params    = null;

    /** Object used to open the socket and handle the HTTP socket. */
    private HttpURLConnection   conn      = null;

    /** Sets the listener */
    public void setListener(DownloadListener l) {
        this.listener = l;
    }

    @Override
    protected byte[] doInBackground(DownloadRequest... paramsArray) {
        // Se method è null viene assunto GET.
        // Se postMap è null viene assunto un campo dati POST vuoto.
        // Se url è null, l'AsyncTask termina richiamando la callback di
        // errore.
        Log.d(DEBUG_TAG, "***doInBackground");
        params = paramsArray[0];
        URL url;
        try {
            url = new URL(params.urlString);
        } catch (MalformedURLException e) {
            Log.e(DEBUG_TAG, "Unable to malformed URL:" + e.getMessage());
            e.printStackTrace();
            return null;
        }

        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setDoInput(true);

            if (params.httpMethod == DownloadRequest.POST) {
                // Setting POST data:
                String postString = params.getPostString();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setFixedLengthStreamingMode(postString.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(postString);
                out.close();
            } else if (params.httpMethod == DownloadRequest.GET) {
                conn.setRequestMethod("GET");
            } else {
                throw new RuntimeException("Invalid Connection Method");
            }

            conn.connect();
            // int responseCode = conn.getResponseCode();
            // Log.d(DEBUG_TAG, "Content Type: " + conn.getContentType());
            // Log.d(DEBUG_TAG, "The response is: " + responseCode);

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
        Log.d(DEBUG_TAG, "***onPostExecute");
        if (listener == null) {
            return;
        }
        if (result != null) {
            Log.d(DEBUG_TAG, "***onPostExecute: success");
            listener.onDownloadCompleted(params, result);
        } else {
            Log.d(DEBUG_TAG, "***onPostExecute: fail");
            listener.onDownloadError(params);
        }
    }

    /**
     * Receives the requested HTTP page, through the
     * <code>HttpURLConnection</code> object
     */
    private byte[] fetchResponse() throws IOException {
        Log.d(DEBUG_TAG, "***fetchResponse");
        InputStream inputStream = conn.getInputStream();
        // Log.d(DEBUG_TAG, "Headers in the HTTP response: \n" +
        // conn.getHeaderFields().toString());

        // Premature optimization is the root of all evil. Donald Knuth
        // maledicimi.
        /*
         * List<Byte> bytesList = new LinkedList<Byte>(); int n = 0; // int
         * length = 0; byte[] chunk = new byte[1500]; // 1500 bytes è MTU tipica
         * su internet. while ((n = inputStream.read(chunk)) > 0) { for (int i =
         * 0; i < n; i++) { bytesList.add(chunk[i]); } // length += n; //
         * Log.d(DEBUG_TAG, "Read " + n + " bytes.\n"); } // Log.d(DEBUG_TAG,
         * "Total: " + length + " bytes.\n"); byte[] responseBody = new
         * byte[bytesList.size()]; int i = 0; for (Byte b : bytesList) {
         * responseBody[i++] = b; } return responseBody;
         */
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[512];

        while ((nRead = inputStream.read(data, 0, data.length)) > 0) {
            try {
                synchronized (this) {
                    this.wait(200);
                }
            } catch (InterruptedException e) {
                Log.d("WAITER", "interrotto");
            }
            outBuffer.write(data, 0, nRead);
        }

        outBuffer.flush();
        Log.d(DEBUG_TAG, "***fetchResponse: [" + outBuffer.toString() + "]");
        return outBuffer.toByteArray();
    }

    /**
     * This interface must be implemented by the object used as a Listener for
     * the HTTP request
     * 
     * @author Gabriele "Whisky" Visconti
     */
    public interface DownloadListener {
        /**
         * This method will be called if the HTTP page
         * 
         * @param responseBody the contents of the requested HTTP page
         */
        public void onDownloadCompleted(DownloadRequest request, byte[] responseBody);

        /**
         * This method will be called if an error happened while trying to
         * download the requested HTTP page
         */
        public void onDownloadError(DownloadRequest request);
    }
}
