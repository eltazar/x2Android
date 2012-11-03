
package it.wm.perdue;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d("", "Restoring Instance State 1");
            listState = savedInstanceState.getParcelable("liststate");
            Log.d("", "** " + listState);
            Log.d("", "**** " + this);
        }
    }
    
    @Override
    public void onResume() {
        Log.d("", "Restoring Instance State 2?");
        Log.d("", "** " + listState);
        Log.d("", "**** " + this);
        super.onResume();
        if (listState != null) {
            Log.d("", "Restoring Instance State 2");
            getListView().onRestoreInstanceState(listState);
            listState = null;
        } else {
            Log.d("", "NOT Restoring Instance State 2");
        }
        
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(
                getActivity(),
                getListView().getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onDestroyView() {
        listState = getListView().onSaveInstanceState();
        super.onDestroyView();
    }
    
    public void onSaveInstanceState(Bundle outState) {
        Log.d("", "Saving Instance State ");
        Parcelable p = null;
        if (listState != null) {
            // la view è stata già distrutta
            p = listState;
        } else {
            p = getListView().onSaveInstanceState();
        }
        outState.putParcelable("liststate", p);
        Log.d("", "** " + p);
        Log.d("", "**** " + this);
        
    }
}
