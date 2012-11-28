
package it.wm.perdue.dettaglioEsercenti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import it.wm.perdue.MainActivity;
import it.wm.perdue.R;

public class DettaglioEsercenteBaseActivity extends SherlockFragmentActivity {
    // http://www.cartaperdue.it/partner/v2.0/DettaglioEsercente.php?id=%d
    
    private static final String ESE_ID     = "eseId";
    private String              eseId      = "";
    private String              eseInsegna = "";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // setContentView(R.layout.dettaglio_ese_base_activity);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eseId = extras.getString(ESE_ID);
            eseInsegna = extras.getString("ESE_TITLE");
        }
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        
        bar.setTitle(eseInsegna);
        
        Fragment f = DettaglioEseListFragment.newInstance(eseId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.add(android.R.id.content, f);
        fragmentTransaction.commit();
        
    }
    
    /* *** BEGIN: OptionsMenu Methods **************** */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.dettaglio_ese_menu, menu);
        // mSearchView.setOnCloseListener(this);
        
        return true;
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
