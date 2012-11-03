
package it.wm.android.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.wm.perdue.R;

public class NewsListAdapter extends CustomListAdapter {
    
    public NewsListAdapter(Context context, String[] categories) {
        super(context, categories);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.news_row, null);
        }
        
        String str = _items[position];
        if (str != null) {
            TextView title = (TextView) v.findViewById(R.id.newsTitle);
            if (title != null) {
                title.setText(str);
            }
        }
        
        return v;
    }
    
}
