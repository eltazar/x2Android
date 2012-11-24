
package it.wm.perdue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FilterSpinnerAdapter extends ArrayAdapter<String> {
    
    String[]  label   = null;
    Integer[] image   = null;
    Context   context = null;
    
    public FilterSpinnerAdapter(Context context, int textViewResourceId,
            String[] objects, Integer[] image) {
        super(context, textViewResourceId, objects);
        label = objects;
        this.image = image;
        this.context = context;
    }
    
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }
    
    public View getCustomView(int position, View convertView,
            ViewGroup parent) {
        // TODO Auto-generated method stub
        // return super.getView(position, convertView, parent);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.filter_spinner_row, parent, false);
        TextView textView = (TextView) row.findViewById(R.id.filterText);
        textView.setText(label[position]);
        
        ImageView icon = (ImageView) row.findViewById(R.id.filterIcon);
        icon.setImageResource(image[position]);
        return row;
    }
}
