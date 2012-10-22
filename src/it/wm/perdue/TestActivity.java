package it.wm.perdue;

import it.wm.HTTPAccess;
//import it.wm.JSONDecoder;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends Activity {
	private static final String DEBUG_TAG = "TestActivity";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        HTTPAccess httpAccess = HTTPAccess.getInstance();
        String urlString = "http://www.cartaperdue.it/partner/commenti.php?id=119&from=0&to=10";
        
        httpAccess.startHTTPConnection(
        		urlString, 
        		HTTPAccess.Method.GET, 
        		null, 
        		new HTTPAccess.ResponseListener() {
        	public void onHTTPResponseReceived(String response) {
        		((android.widget.TextView)TestActivity.this
        				.findViewById(R.id.textView1)).setText(response);
        		//JSONDecoder.decode(response);
        	}
        	public void onHTTPerror() {
        		((android.widget.TextView)TestActivity.this
        				.findViewById(R.id.textView1))
        				.setText("Arrangiati, Errore di Rete.");
        	}
        });
        

	}

}
