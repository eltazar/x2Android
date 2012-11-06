
package it.wm.perdue;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import it.wm.HTTPAccess;
import it.wm.perdue.businessLogic.Notizia;

import org.json.JSONException;
import org.json.JSONObject;

public class NotiziaActivity extends Activity implements HTTPAccess.ResponseListener {
    
    private static final String DEBUG_TAG  = "NotiziaActivity";
    private HTTPAccess          httpAccess = null;
    private String              urlString;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notizia);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        
        Notizia notizia = (Notizia) getIntent().getSerializableExtra("notizia");
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(notizia.getTitolo());
        
        urlString = "http://www.cartaperdue.it/partner/Notizia.php?id=" + notizia.getId();
        
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, null);
        
        Log.d(DEBUG_TAG, "id " + notizia.getId());
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        Log.d(DEBUG_TAG, "RICEVUTA RISPOSTA");
        JSONObject mainObject = null;
        try {
            
            mainObject = new JSONObject(response);
            
            Log.d(DEBUG_TAG,
                    "***** object:"
                            + mainObject.getJSONArray("Esercente").getJSONObject(0)
                                    .getString("post_content"));
            WebView webView = (WebView) findViewById(R.id.newsWebView);
            webView.loadData(mainObject.getJSONArray("Esercente").getJSONObject(0)
                    .getString("post_content"), "text/html; charset=UTF-8", null);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.getLocalizedMessage();
        }
        // JSONObject esercente = mainObject.getJSONObject("Esercente");
        // String post_content = esercente.getString("post_content");
        
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        
    }
    
}
