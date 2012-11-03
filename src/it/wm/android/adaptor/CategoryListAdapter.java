
package it.wm.android.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.wm.perdue.R;

public class CategoryListAdapter extends BaseAdapter {
    
    private LayoutInflater _inflater;
    private String[]       _items;
    private Context        _context;
    private Integer[]      _images;
    
    public CategoryListAdapter(Context context, String[] categories, Integer[] images) {
        _inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _items = categories;
        _context = context;
        _images = images;
    }
    
    @Override
    public int getCount() {
        return _items.length;
    }
    
    @Override
    public Object getItem(int position) {
        return _items[position];
    }
    
    @Override
    public long getItemId(int position) {
        return position + 1;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.category_row, null);
        }
        
        String str = _items[position];
        if (str != null) {
            TextView title = (TextView) v.findViewById(R.id.title);
            if (title != null) {
                title.setText(str);
            }
        }
        Integer image = _images[position];
        ImageView imageRow = (ImageView) v.findViewById(R.id.icon);
        imageRow.setImageResource(image);
        
        return v;
    }
    
}
