
package it.wm.perdue.contatti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.webkit.WebView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import it.wm.perdue.MainActivity;
import it.wm.perdue.R;

public class InfoPerDueBaseActivity extends SherlockFragmentActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // We'll define a custom screen layout here (the one shown above), but
        // typically, you could just use the standard ListActivity layout.
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        
        String request = null;
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            request = extras.getString("request");
        }
        Log.d("YYY", "REQUEST = " + request);
        
        if (savedInstanceState != null)
            return;
        
        if (request.equals("info")) {
            setContentView(R.layout.contatti_list_activity);
            
            WebView webView = (WebView) findViewById(R.id.perdueWebView);
            webView.loadUrl("http://www.cartaperdue.it/partner/PD2.html");
            bar.setTitle("Chi siamo");
        }
        else {
            Fragment f = new ContattiListFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content, f);
            fragmentTransaction.commit();
            bar.setTitle("Contatti");
        }
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra(Intent.EXTRA_TEXT, MainActivity.DOVE_USARLA_TAB_TAG);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
