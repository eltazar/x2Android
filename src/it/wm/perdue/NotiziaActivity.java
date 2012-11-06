
package it.wm.perdue;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import it.wm.HTTPAccess;
import it.wm.perdue.businessLogic.Notizia;

public class NotiziaActivity extends SherlockActivity implements HTTPAccess.ResponseListener {
    
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
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME);
        
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
