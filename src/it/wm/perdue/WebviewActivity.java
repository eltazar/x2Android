package it.wm.perdue;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import it.wm.HTTPAccess;

public class WebviewActivity extends SherlockActivity implements HTTPAccess.ResponseListener {
    
    protected HTTPAccess          httpAccess           = null;
    protected String              urlString            = null;
    protected WebView             webView              = null;
    private   String              content              = null;
    private String                urlContent           = null;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.webview_page);
        TextView titleTV = (TextView) findViewById(R.id.title);
        titleTV.setVisibility(View.GONE);
        webView = (WebView) findViewById(R.id.webView);
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        bar.setTitle("BLA");
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        
        Bundle extras = getIntent().getExtras(); 
        String title = "";
        if(extras !=null) {
            urlContent = extras.getString("urlContent");
            content = extras.getString("content");
            title = extras.getString("contentTitle");
            if(urlContent != null)
                loadPageUrl(urlContent);
            else loadPage(content);
        }
        bar.setTitle(title);
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        httpAccess.setResponseListener(null);
    }
    
    protected void loadPage(String contentString){
        webView.loadData(content, "text/html", "UTF-8");
    }
    
    protected void loadPageUrl(String urlContent){
        webView.loadUrl(urlContent);
    }


    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        
    }
    
}