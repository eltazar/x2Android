
package it.wm.perdue.dettaglioEsercenti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.webkit.WebView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import it.wm.HTTPAccess;
import it.wm.perdue.MainActivity;
import it.wm.perdue.R;

import org.apache.http.util.EncodingUtils;

public class AltreInfoActivity extends SherlockActivity implements HTTPAccess.ResponseListener {
    
    private static final String DEBUG_TAG  = "AltreInfoActivity";
    private HTTPAccess          httpAccess = null;
    private WebView             webView    = null;
    private String              urlString  = null;
    private String              eseId      = null;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle args = getIntent().getExtras();
        eseId = args.getString("eseId");
        
        setContentView(R.layout.altre_info);
        webView = (WebView) findViewById(R.id.altreInfoWebView);
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        bar.setTitle(args.getString("title"));
        
        urlString = "http://www.cartaperdue.it/partner/v2.0/UlterioriInformazioni.php";
        
        // httpAccess = new HTTPAccess();
        // httpAccess.setResponseListener(this);
        // httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET,
        // null, null);
        
        Log.d("XXX", "ESERECENTE ID = " + eseId);
        String postData = "idesercente=" + eseId;
        webView.postUrl(urlString, EncodingUtils.getBytes(postData, "BASE64"));
        
        // mHTTPService.doRequest(mmiEvent.getContentUrl(),
        // HTTPRequest.HTTP_METHOD_POST, "gui="+mmiEvent.getData());
        //
        // The last parameter what i want to send as requestParameter
        //
        // There is a callback registered that my service pushs the received
        // content back to. To display the content I use a handler where within
        // I
        // do:
        //
        // mWebView.loadDataWithBaseURL(BASE_URL,(String)msg.obj, "text/html",
        // "utf-8",FAIL_URL);
        
        //
        // NSString *infoUrlString =
        // @"http://www.cartaperdue.it/partner/v2.0/UlterioriInformazioni.php";
        // NSURL *url = [NSURL URLWithString:infoUrlString];
        //
        // NSString *postString = [NSString stringWithFormat:
        // @"idesercente=%d", self.idEsercente];
        // NSData *postData = [postString dataUsingEncoding:NSUTF8StringEncoding
        // allowLossyConversion:YES];
        // NSString *postLength = [NSString stringWithFormat:@"%d",[postData
        // length]];
        //
        // NSMutableURLRequest *request = [NSMutableURLRequest
        // requestWithURL:url];
        // [request addValue:postLength forHTTPHeaderField:@"Content-Length"];
        // [request setHTTPMethod:@"POST"];
        // [request setHTTPBody:postData];
        // [self.sitoWebView loadRequest:request];
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Inflating the current activity's menu with res/menu/items.xml */
        getSupportMenuInflater().inflate(R.menu.dettaglio_ese_menu, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra(Intent.EXTRA_TEXT, MainActivity.NEWS_TAB_TAG);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // Log.d(DEBUG_TAG, "RICEVUTA RISPOSTA: " + response);
    }
    
    @Override
    public void onHTTPerror(String tag) {
    }
    
}
