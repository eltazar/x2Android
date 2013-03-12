package it.wm.perdue.coupon;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import it.wm.CachedAsyncImageView;
import it.wm.perdue.R;

public class FullImageActivity extends SherlockFragmentActivity{
    
    private CachedAsyncImageView image = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        bar.setTitle("Immagine");    
        LayoutInflater inflater = (LayoutInflater)   getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        View view = inflater.inflate(R.layout.image_layout, null);

        Bundle extras = getIntent().getExtras();
        String urlImage = "";
        Log.d("coupon","immagine = "+extras.getString("urlImage"));
        if (extras != null) {
            urlImage = extras.getString("urlImage");
        }
        
        image= (CachedAsyncImageView) view.findViewById(R.id.fullImage);
        image.loadImageFromURL(urlImage);
    }
    
    
    @Override
    public void onResume(){
        super.onResume();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
