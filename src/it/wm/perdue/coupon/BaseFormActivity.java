
package it.wm.perdue.coupon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

import it.wm.HTTPAccess;
import it.wm.perdue.R;

public class BaseFormActivity extends SherlockFragmentActivity implements
        HTTPAccess.ResponseListener, OnClickListener, OnEditorActionListener, OnFocusChangeListener {
    
    private static final String DEBUG_TAG  = "BaseFormActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // setContentView(R.layout.dettaglio_ese_base_activity);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
                      
        }
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        
        bar.setTitle("Login");
        
        Fragment f = null;
        
        if (savedInstanceState != null)
            return;
        
        f = RegistrazioneFormFragment.newInstance();
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.add(android.R.id.content, f);
        fragmentTransaction.commit();
        
    }
    
    /* *** BEGIN: OptionsMenu Methods **************** */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // mSearchView.setOnCloseListener(this);
        
//        if (isRisto) {
//            getSupportMenuInflater().inflate(R.menu.dettaglio_ese_risto_menu, menu);
//        }
//        else {
//            getSupportMenuInflater().inflate(R.menu.dettaglio_ese_menu, menu);
//        }
        
        getSupportMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra(Intent.EXTRA_TEXT, MainActivity.DOVE_USARLA_TAB_TAG);
                NavUtils.navigateUpTo(this, intent);
                return true;
            case R.id.commenti:
                Log.d("BBB", "cliccato pulsante commenti, eseID = " + id);
                Bundle extras = new Bundle();
                // extras.putSerializable("notizia", (Serializable)
                // l.getItemAtPosition(position));
                intent = new Intent(this, CommentiBaseActivity.class);
                extras.putString(CommentiBaseActivity.Tags.ID, id);
                intent.putExtras(extras);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    
    public static class Tags {
        public static final String ID       = "id";
        public static final String TITLE    = "title";
        public static final String IS_RISTO = "isRisto";
    }

    @Override
    public void onFocusChange(View arg0, boolean arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        
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
