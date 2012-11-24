
package it.wm.perdue.doveusarla;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.perdue.JSONListAdapter;
import it.wm.perdue.R;
import it.wm.perdue.businessLogic.EsercenteRistorazione;

import java.util.HashMap;

public class EsercentiRistoListFragment extends EsercentiListFragment {
    
    private static final String DEBUG_TAG = "EsercentiRistoListFragment";
    
    // Gestione dello stato della lista:
    // protected EsercenteRistoJSONListAdapter adapter = null;
    // protected EsercenteRistoJSONListAdapter searchAdapter = null;
    
    public static EsercentiRistoListFragment newInstance(String sort, String categ) {
        EsercentiRistoListFragment fragment = new EsercentiRistoListFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_CATEGORY, categ.toLowerCase());
        args.putString(ARGS_SORTING, sort.toLowerCase());
        fragment.setArguments(args);
        Log.d("***************", "NEW INSTANCE --> SORTING = " + fragment.sorting + " category = "
                + fragment.category);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlString = "http://www.cartaperdue.it/partner/v2.0/EsercentiRistorazione.php";
        Log.d(DEBUG_TAG, "url string = " + urlString);
    }
    
    @Override
    public void onCreateAdapters() {
        adapter = new EsercenteRistoJSONListAdapter(
                getActivity(),
                R.layout.esercente_risto_row,
                EsercenteRistorazione[].class,
                sorting);
        searchAdapter = new EsercenteRistoJSONListAdapter(
                getActivity(),
                R.layout.esercente_risto_row,
                EsercenteRistorazione[].class,
                sorting);
    }
    
    public void didChangeFilter(String f) {
        
        this.filter = f;
        Log.d("BLA", " cambiato filtro dentro risto = " + this.filter);
        
        postMap = new HashMap<String, String>();
        postMap.put("request", "fetch");
        postMap.put("categ", category.toLowerCase());
        postMap.put("prov", "Qui");
        postMap.put("giorno", "Venerdi");
        postMap.put("lat", "41.801007");
        postMap.put("long", "12.454273");
        postMap.put("ordina", sorting);
        postMap.put("from", "" + 0);
        postMap.put("filtro", this.filter);
        adapter.clear();
        setListAdapter(adapter);
        // Log.d("BLA", "4) postMap is: " + postMap);
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST,
                postMap, TAG_NORMAL);
    }
    
    private static class EsercenteRistoJSONListAdapter extends
            JSONListAdapter<EsercenteRistorazione> {
        private String sorting = null;
        
        public EsercenteRistoJSONListAdapter(Context context, int resource,
                Class<EsercenteRistorazione[]> clazz, String sorting) {
            super(context, resource, clazz);
            Log.d("--------------", " RISTO ADAPTER ISTANZIATO");
            this.sorting = sorting;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            
            Log.d("--------------", " GET VIEW DI RISTO LIST");
            
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) super.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.esercente_risto_row, null);
            }
            
            EsercenteRistorazione esercente = getItem(position);
            if (esercente != null) {
                TextView title = (TextView) v.findViewById(R.id.eseTitle);
                TextView address = (TextView) v.findViewById(R.id.address);
                TextView distance = (TextView) v.findViewById(R.id.distance);
                TextView foodKind = (TextView) v.findViewById(R.id.kindFood);
                TextView price = (TextView) v.findViewById(R.id.price);
                CachedAsyncImageView caImageView = (CachedAsyncImageView) v
                        .findViewById(R.id.eseImage);
                
                String urlImageString = "http://www.cartaperdue.it/partner/v2.0/ImmagineEsercente.php?id="
                        + esercente.getID();
                
                if (caImageView != null) {
                    Log.d("DEBUG_TAG", "esercente id  = " + esercente.getID());
                    // caImageView.loadImageFromURL(urlImageString);
                }
                
                if (title != null) {
                    Log.d(DEBUG_TAG, "Sorting �: " + sorting);
                    title.setText(esercente.getInsegna());
                }
                if (address != null) {
                    address.setText(esercente.getIndirizzo());
                }
                if (distance != null) {
                    distance.setText(String.format("a %.3f km ", esercente.getDistanza()));
                }
                if (foodKind != null) {
                    foodKind.setText(String.format("Cucina: %s ", esercente.getSottoTipologia()));
                }
                if (price != null) {
                    if (esercente.getFasciaPrezzo() == null)
                        price.setText("-- �");
                    else
                        price.setText(String.format("%s �: ", esercente.getFasciaPrezzo()));
                }
            }
            return v;
        }
    }
}
