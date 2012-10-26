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
        task.execute(url, method, parameters);
    }

    public void startHTTPConnection(String urlString, Method method,
            HashMap<String, String> parameters, ResponseListener listener) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(DEBUG_TAG, "Malformed URL. Not opening http connection.");
            e.printStackTrace();
        }
        startHTTPConnection(url, method, parameters, listener);
    }

    private class DownloaderTask extends AsyncTask<Object, Void, String> {
        private static final String DEBUG_TAG = "DownloaderTask";
        private boolean success = true;
        private ResponseListener listener = null;

        public void setListener(ResponseListener l) {
            this.listener = l;
        }

        @Override
        protected String doInBackground(Object... params) {
            Log.d(DEBUG_TAG, "Starting background work");

            URL url = null;
            HTTPAccess.Method method;
            String postString = "";
            if (params[0] instanceof URL &&
                    params[1] instanceof HTTPAccess.Method) {
                url = (URL) params[0];
                method = (HTTPAccess.Method) params[1];
            } else {
                Log.e(DEBUG_TAG, "First two parameters MUST be in order: " +
                        "URL, Request.Method. Not opening connection");
                success = false;
                return "";
            }
            if (method == HTTPAccess.Method.POST) {
                if (params[3] != null && params[3] instanceof HashMap<?, ?>) {
                    @SuppressWarnings("unchecked")
                    // Il check è fatto nell'if, ma l'analizzatore sintattico è
                    // tonto.
                    HashMap<String, String> postDict = (HashMap<String, String>) params[3];
                    Iterator<String> i = postDict.keySet().iterator();
                    StringBuilder postStringBuilder = new StringBuilder();
                    while (i.hasNext()) {
                        String key = i.next();
                        try {
                            postStringBuilder.append(URLEncoder.encode(key, "UTF-8"));
                            postStringBuilder.append("=");
                            postStringBuilder.append(URLEncoder.encode(postDict.get(key), "UTF-8"));
                            postStringBuilder.append("&");
                        } catch (UnsupportedEncodingException e) {
                            // Ma dai.... con utf-8? Mi ci gioco le palle che in
                            // questo
                            // blocco non ci entreramo mai!!
                        }
                    }
                    if (postStringBuilder.length() > 0) {
                        postStringBuilder.deleteCharAt(postStringBuilder.length());
                    }
                    postString = postStringBuilder.toString();
                }
            }

            StringBuilder response = null;
            InputStream inputStream = null;
            try {
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setDoInput(true);
                    if (method == HTTPAccess.Method.POST) {
                        // Setting POST data:
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setFixedLengthStreamingMode(postString.getBytes().length);
                        conn.setRequestProperty("Content-Type",
                                "application/x-www-form-urlencoded");
                        PrintWriter out = new PrintWriter(conn.getOutputStream());
                        out.print(postString);
                        out.close();
                    } else {
                        conn.setRequestMethod("GET");
                    }

                    // Starts the query
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    Log.d(DEBUG_TAG, "Content Type: " + conn.getContentType());
                    Log.d(DEBUG_TAG, "The response is: " + responseCode);

                    inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream, "utf-8"));

                    String line = null;
                    response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                        response.append("\n");
                    }

                } finally {
                    Log.d(DEBUG_TAG, "Closing inputStream");
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }

            } catch (IOException e) {
                Log.d(DEBUG_TAG, "An IOExcption has occurred: " + e.getMessage());
                success = false;
            }
            return response.toString();
        }

        public void onPostExecute(String result) {
            if (success) {
                listener.onHTTPResponseReceived(result);
            } else {
                listener.onHTTPerror();
            }
        }

    }

    public interface ResponseListener {
        public void onHTTPResponseReceived(String response);

        public void onHTTPerror();
    }

}
