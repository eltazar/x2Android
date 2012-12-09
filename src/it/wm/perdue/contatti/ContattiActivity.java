
package it.wm.perdue.contatti;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListActivity;

import it.wm.perdue.R;

public class ContattiActivity extends SherlockListActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // We'll define a custom screen layout here (the one shown above), but
        // typically, you could just use the standard ListActivity layout.
        setContentView(R.layout.contatti_list_activity);
        
        // ListView listView = (ListView) findViewById(R.id.contactList);
        String[] values = new String[] {
                "800737383", "redazione@cartaperdue.it", "www.cartaperdue.it",
                "perdue.roma"
        };
        
        // First paramenter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ContactArrayAdapter adapter = new ContactArrayAdapter(this,
                R.layout.contact_row, values);
        
        // Assign adapter to ListView
        setListAdapter(adapter);
        
        WebView webView = (WebView) findViewById(R.id.perdueWebView);
        webView.loadUrl("http://www.cartaperdue.it/partner/PD.html");
        webView.getSettings().setTextZoom(22);
        
    }
    
    private class ContactArrayAdapter extends ArrayAdapter<String> {
        private final Context  context;
        private final String[] values;
        
        public ContactArrayAdapter(Context context, int contactRow, String[] values) {
            super(context, contactRow, values);
            this.context = context;
            this.values = values;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.contact_row, parent, false);
            
            TextView resource = (TextView) rowView.findViewById(R.id.contactResource);
            TextView kind = (TextView) rowView.findViewById(R.id.contactKind);
            ImageView imageView = (ImageView)
                    rowView.findViewById(R.id.contactImage);
            /*
             * "800737383", "redazione@cartaperdue.it", "www.cartaperdue.it",
             * "https://www.facebook.com/perdue.roma"
             */
            switch (position) {
                case 0:
                    resource.setText(values[0]);
                    kind.setText("Telefono");
                    break;
                case 1:
                    resource.setText(values[1]);
                    kind.setText("E-mail");
                    break;
                case 2:
                    resource.setText(values[2]);
                    kind.setText("Sito web");
                    break;
                case 3:
                    resource.setText(values[3]);
                    kind.setText("Facebook");
                    break;
                default:
                    break;
            }
            
            return rowView;
        }
    }
}
