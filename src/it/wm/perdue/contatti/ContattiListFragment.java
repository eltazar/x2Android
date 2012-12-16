
package it.wm.perdue.contatti;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.CachedAsyncImageView;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;
import it.wm.perdue.dettaglioEsercenti.DettaglioEseMapActivity;

public class ContattiListFragment extends SherlockListFragment {
    
    // ListView listView = (ListView) findViewById(R.id.contactList);
    private String[] values = new String[] {
            "map", "800737383", "redazione@cartaperdue.it", "www.cartaperdue.it",
            "perdue.roma"
                            };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ContactArrayAdapter adapter = new ContactArrayAdapter(getActivity(),
                R.layout.contact_row, values);
        
        // Assign adapter to ListView
        setListAdapter(adapter);
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        
        switch (position) {
            case 1:
                try {
                    Utils.callNumber(values[position], getActivity());
                } catch (ActivityNotFoundException e) {
                    Log.e("helloandroid dialing example", "Call failed", e);
                    Toast.makeText(getActivity(),
                            "Spiacenti, non è possibile chiamare il numero selezionato.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                try {
                    Utils.writeEmail(values[position], getActivity());
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(),
                            "Spiacenti, non ci sono client di posta installati.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                try {
                    Utils.openBrowser(values[position], getActivity());
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(),
                            "Spiacenti, non è stato possibile aprire la pagina.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                try {
                    Utils.openBrowser("www.facebook.it/" + values[position], getActivity());
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(),
                            "Spiacenti, non è stato possibile aprire la pagina.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    
    private class ContactArrayAdapter extends ArrayAdapter<String> implements OnClickListener {
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
            
            View rowView = inflater.inflate(getItemViewType(position), parent, false);
            TextView resource = null;
            TextView kind = null;
            ImageView contactImage = null;
            CachedAsyncImageView mapImage = null;
            
            if (position == 0) {
                mapImage = (CachedAsyncImageView) rowView
                        .findViewById(R.id.mapImage);
                mapImage.setOnClickListener(this);
            }
            else {
                resource = (TextView) rowView.findViewById(R.id.contactResource);
                kind = (TextView) rowView.findViewById(R.id.contactKind);
                contactImage = (ImageView)
                        rowView.findViewById(R.id.contactImage);
            }
            
            switch (position) {
                case 0:
                    String urlString =
                            "http://maps.googleapis.com/maps/api/staticmap?"
                                    +
                                    "zoom=16&size=512x240&markers=size:big|color:red|41.91755149999999,12.49941110&sensor=false";
                    mapImage.loadImageFromURL(urlString);
                    
                    TextView textView = (TextView) rowView.findViewById(R.id.mapInfo);
                    textView.setText(Html.fromHtml("<b>Sede</b>" + "<br />" + "Via Po 116, 00198, Roma <br />"));
                    break;
                
                case 1:
                    resource.setText(values[position]);
                    kind.setText("Telefono");
                    contactImage.setImageResource(R.drawable.ic_phone);
                    break;
                case 2:
                    resource.setText(values[position]);
                    kind.setText("E-mail");
                    contactImage.setImageResource(R.drawable.ic_mail);
                    break;
                case 3:
                    resource.setText(values[position]);
                    kind.setText("Sito web");
                    contactImage.setImageResource(R.drawable.ic_web);
                    break;
                case 4:
                    resource.setText(values[position]);
                    kind.setText("Facebook");
                    contactImage.setImageResource(R.drawable.ic_facebook);
                    break;
                default:
                    break;
            }
            
            return rowView;
        }
        
        // Returns the number of types of Views that will be created ...
        
        public int getItemViewType(int position) {
            if (position == 0)
                return R.layout.map_row;
            else
                return R.layout.contact_row;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch(v.getId()){
                case R.id.mapImage:
                    Esercente esercente = new Esercente("PerDue",41.91755149999999,12.49941110,"Via Po 116, Roma");
                    Intent intent = new Intent(v.getContext(), DettaglioEseMapActivity.class);                  
                    intent.putExtra("esercente",esercente);
                    v.getContext().startActivity(intent);
            }
        }
        
    }
}
