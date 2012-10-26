/**
 * 
 */

package it.wm;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author fastfading
 */
public class HTTPAccess {

    public enum Method {
        GET,
        POST
    }

    private static final String DEBUG_TAG = "HTTPAccess";
    private static HTTPAccess __instance;

    private HTTPAccess() {
    }

    public static HTTPAccess getInstance() {
        if (__instance == null) {
            __instance = new HTTPAccess();
        }
        return __instance;
    }

    public void startHTTPConnection(URL url, Method method,
            HashMap<String, String> parameters, final ResponseListener listener) {
        DownloaderTask task = new DownloaderTask();
        task.setListener(listener);
        task.execute(new DownloaderTaskParams(url, method, parameters));
    }

    public void startHTTPConnection(String urlString, Method method,
            HashMap<String, String> parameters, ResponseListener listener) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(DEBUG_TAG, "Malformed URL. Not opening http connection.");
            e.printStackTrace();
            return;
        }
        startHTTPConnection(url, method, parameters, listener);
    }

    private class DownloaderTaskParams {
        public URL url;
        public HTTPAccess.Method method;
        public HashMap<String, String> postMap;

        public DownloaderTaskParams(URL url, Method method, HashMap<String, String> postMap) {
            this.url = url;
            this.method = method;
            this.postMap = postMap;
        }
    }

    private class DownloaderTask extends AsyncTask<DownloaderTaskParams, Void, String> {
        private static final String DEBUG_TAG = "DownloaderTask";
        private ResponseListener listener = null;

        private String postString = null;
        private HttpURLConnection conn = null;

        public void setListener(ResponseListener l) {
            this.listener = l;
        }

        @Override
        protected String doInBackground(DownloaderTaskParams... params) {
            // Se method è null viene assunto GET.
            // Se postMap è null viene assunto un campo dati POST vuoto.
            // Se url è null, l'AsyncTask termina richiamando la callback di
            // errore.
            Log.d(DEBUG_TAG, "Starting background work");

            URL url = params[0].url;
            Method method = params[0].method;
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

                if (method == HTTPAccess.Method.POST) {
                    // Setting POST data:
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setFixedLengthStreamingMode(postString.getBytes().length);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    PrintWriter out = new PrintWriter(conn.getOutputStream());
                    out.print(postString);
                    out.close();
                } else {
                    conn.setRequestMethod("GET");
                }

                conn.connect();
                int responseCode = conn.getResponseCode();
                Log.d(DEBUG_TAG, "Content Type: " + conn.getContentType());
                Log.d(DEBUG_TAG, "The response is: " + responseCode);

                return fetchResponse();
            } catch (IOException e) {
                Log.v(DEBUG_TAG, "Unable to connect to " + url.toString() + ": " + e.getMessage());
                e.printStackTrace();
                return null;
            } finally {
                conn.disconnect();
            }
        }

        public void onPostExecute(String result) {
            if (result != null) {
                listener.onHTTPResponseReceived(result);
            } else {
                listener.onHTTPerror();
            }
        }

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

        private String fetchResponse() throws IOException {
            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                Log.e(DEBUG_TAG, e.getMessage());
                e.printStackTrace();
                if (inputStream != null) {
                    inputStream.close();
                }
                return null;
            }

            String line = null;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }

            if (inputStream != null) {
                inputStream.close();
            }
            return response.toString();
        }
    }

    public interface ResponseListener {
        public void onHTTPResponseReceived(String response);

        public void onHTTPerror();
    }

}
