
package it.wm.perdue.doveusarla;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.perdue.R;

public class DoveUsarlaFragment extends SherlockListFragment {
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
    
    public DoveUsarlaFragment() {
        super();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListAdapter myListAdapter = new CategoryListAdapter(getActivity(), categories, images);
        setListAdapter(myListAdapter);
        
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), EsercentiBaseActivity.class);
        intent.putExtra("category",
                l.getItemAtPosition(position).toString());
        startActivity(intent);
    }
    
    private static class CategoryListAdapter extends ArrayAdapter<String> {
        private Context   context    = null;
        private Integer[] images     = null;
        private String[]  categories = null;
        
        public CategoryListAdapter(Context context, String[] categories, Integer[] images) {
            super(context, R.layout.category_row, R.id.title, categories);
            this.context = context;
            this.images = images;
            this.categories = categories;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.category_row, null);
            }
            
            String cat = categories[position];
            if (cat != null) {
                TextView title = (TextView) v.findViewById(R.id.title);
                if (title != null) {
                    title.setText(cat);
                }
            }
            Integer image = images[position];
            ImageView imageRow = (ImageView) v.findViewById(R.id.icon);
            imageRow.setImageResource(image);
            return v;
        }
    }
}
