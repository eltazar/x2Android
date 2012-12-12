
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

/*
 * Questa classe funge da contenitore per un fragment. Ha il compito di visualizzare i vari elementi dell'interfaccia
 * come la search bar ecc...
 * 
 * */
public class CommentiBaseActivity extends SherlockFragmentActivity {
    
    private static final String DEBUG_TAG  = "DettaglioBaseActivity";
    private String              eseId      = "";
    private String              eseInsegna = "";
    private boolean             isRisto    = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // setContentView(R.layout.dettaglio_ese_base_activity);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eseId = extras.getString(Tags.ID);
            eseInsegna = extras.getString("ESE_TITLE");
        }
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        
        bar.setTitle("Commenti");
        
        if (savedInstanceState != null)
            return;
        
        // creo il fragment da mostrare e gli passo degli argomenti
        
        Bundle args = new Bundle();
        args.putString(CommentiListFragment.Tags.ID, eseId);
        
        extras = new Bundle();
        
        Fragment f = new CommentiListFragment();
        f.setArguments(args);
        
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
            case R.id.commenti:
                Log.d("AAA", "cliccato pulsante commenti");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public static class Tags {
        public static final String ID = "id";
    }
}
