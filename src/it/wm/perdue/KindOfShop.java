
package it.wm.perdue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.android.adaptor.CategoryListAdapter;

public class KindOfShop extends SherlockListFragment {
    private static final String[] month  = {
            "Ristoranti",
            "Pubs e Bar",
            "Cinema",
            "Teatri",
            "Musei",
            "Librerie",
            "Benessere",
            "Parchi",
            "Viaggi",
            "Altro"
                                         };

    public static final Integer[] images = {
            R.drawable.ristoranti, R.drawable.pubsbar, R.drawable.cinema,
            R.drawable.teatri, R.drawable.musei, R.drawable.librerie,
            R.drawable.benessere, R.drawable.parchi, R.drawable.viaggi, R.drawable.altro
                                         };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListAdapter myListAdapter = new CategoryListAdapter(getActivity(), month, images);
        setListAdapter(myListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        System.out.println("ON CRATE VIEW");
        return inflater.inflate(R.layout.shop_categories, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        Toast.makeText(
                getActivity(),
                getListView().getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
    }
}
