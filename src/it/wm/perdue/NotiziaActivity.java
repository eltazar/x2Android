
package it.wm.perdue;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import it.wm.HTTPAccess;
import it.wm.perdue.businessLogic.Notizia;

public class NotiziaActivity extends Activity implements HTTPAccess.ResponseListener {
    
    private static final String DEBUG_TAG  = "NotiziaActivity";
    private HTTPAccess          httpAccess = null;
    private String              urlString  = null;
    private Notizia             notizia    = null;
    private WebView             webView    = null;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notizia = (Notizia) getIntent().getSerializableExtra("notizia");
        
        setContentView(R.layout.notizia);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(notizia.getTitolo());
        webView = (WebView) findViewById(R.id.newsWebView);
        
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            httpAccess = new HTTPAccess();
            httpAccess.setResponseListener(this);
            urlString = "http://www.cartaperdue.it/partner/Notizia.php?id=" + notizia.getId();
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, null);
            Log.d(DEBUG_TAG, "id " + notizia.getId());
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        Log.d(DEBUG_TAG, "RICEVUTA RISPOSTA: " + response);
        response = Utils.stripEsercente(response);
        notizia = Utils.getGson().fromJson(response, Notizia[].class)[0];
        
        Log.d(DEBUG_TAG, "***** object: " + notizia.getTesto());
        webView.loadData(notizia.getTesto(), "text/html; charset=UTF-8", null);
        
    }
    
    @Override
    public void onHTTPerror(String tag) {
    }
    
}
