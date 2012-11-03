
package it.wm.android.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CustomListAdapter extends BaseAdapter {
    
    protected LayoutInflater _inflater;
    protected String[]       _items;
    protected Context        _context;
    
    public CustomListAdapter(Context context, String[] categories) {
        _inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _items = categories;
        _context = context;
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
        // TODO Auto-generated method stub
        return null;
    }
    
}
