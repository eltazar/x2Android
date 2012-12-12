
package it.wm.perdue.dettaglioEsercenti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import it.wm.perdue.MainActivity;
import it.wm.perdue.R;

public class DettaglioEsercenteBaseActivity extends SherlockFragmentActivity {
    // http://www.cartaperdue.it/partner/v2.0/DettaglioEsercente.php?id=%d
    
    private static final String DEBUG_TAG  = "DettaglioBaseActivity";
    private String              id      = "";
    private String              insegna = "";
    private boolean             isRisto = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // setContentView(R.layout.dettaglio_ese_base_activity);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id      = extras.getString (Tags.ID);
            insegna = extras.getString (Tags.TITLE);
            isRisto = extras.getBoolean(Tags.IS_RISTO);
            
        }
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        
        bar.setTitle(insegna);
        
        Fragment f = null;
        
        if (savedInstanceState != null)
            return;
        
        if (isRisto) {
            f = DettaglioEseRistoListFragment.newInstance(id);
        }
        else {
            f = DettaglioEseListFragment.newInstance(id);
        }
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
        
        if (isRisto) {
            getSupportMenuInflater().inflate(R.menu.dettaglio_ese_risto_menu, menu);
        }
        else {
            getSupportMenuInflater().inflate(R.menu.dettaglio_ese_menu, menu);
        }
        
        return true;
    }
    
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
    }
    
    public static class Tags {
        public static final String ID       = "id";
        public static final String TITLE    = "title";
        public static final String IS_RISTO = "isRisto";
    }
}
