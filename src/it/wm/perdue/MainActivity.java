
package it.wm.perdue;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity implements TabListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        bar.addTab(bar.newTab().setText("Dove usarla").setTabListener(this));
        bar.addTab(bar.newTab().setText("tempTab").setTabListener(this));
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_USE_LOGO);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        bar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_activity_menu_layout, menu);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d65151")));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

}
