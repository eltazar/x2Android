
package it.wm.perdue;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.android.adaptor.CustomListAdapter;
import it.wm.android.adaptor.NewsListAdapter;

public class NewsFragment extends SherlockListFragment {
    private static final String[] categories = {
            "Ristorantiaaaaa aaaaaaa aaaaaaaaaaa aaaaa aaaaaa aaaaaa", "Pubs e Bar",
            "Cinema", "Teatri",
            "Musei", "Librerie",
            "Benessere", "Parchi",
            "Viaggi", "Altro"
                                             };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomListAdapter myListAdapter = new NewsListAdapter(getActivity(), categories);
        setListAdapter(myListAdapter);
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
