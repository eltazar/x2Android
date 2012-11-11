
package it.wm.perdue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.android.adaptor.CategoryListAdapter;

public class KindOfShopFragment extends SherlockListFragment {
    Parcelable                    listState  = null;
    private static final String[] categories = {
            "Ristoranti", "Pubs e Bar",
            "Cinema", "Teatri",
            "Musei", "Librerie",
            "Benessere", "Parchi",
            "Viaggi", "Altro"
                                             };
    
    public static final Integer[] images     = {
            R.drawable.ristoranti, R.drawable.pubsbar,
            R.drawable.cinema, R.drawable.teatri,
            R.drawable.musei, R.drawable.librerie,
            R.drawable.benessere, R.drawable.parchi,
            R.drawable.viaggi, R.drawable.altro
                                             };
    
    public KindOfShopFragment() {
        super();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListAdapter myListAdapter = new CategoryListAdapter(getActivity(), categories, images);
        setListAdapter(myListAdapter);
        
    }
    
    // public void onPause() {
    // super.onPause();
    // this.overridePendingTransition(R.anim.animation_leave,
    // R.anim.animation_enter);
    // }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        /*
         * Toast.makeText( getActivity(),
         * getListView().getItemAtPosition(position).toString(),
         * Toast.LENGTH_SHORT).show();
         */
        
        Intent intent = new Intent(getActivity(), EsercentiBaseActivity.class);
        intent.putExtra("category",
                l.getItemAtPosition(position).toString());
        startActivity(intent);
    }
    
}
