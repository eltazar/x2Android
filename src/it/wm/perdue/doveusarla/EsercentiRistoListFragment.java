
package it.wm.perdue.doveusarla;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.perdue.JSONListAdapter;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.EsercenteRistorazione;
import it.wm.perdue.dettaglioEsercenti.DettaglioEsercenteBaseActivity;

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
    
    public void onChangeFilter(String f) {
        
        this.filter = f;
        Log.d("BLA", " cambiato filtro dentro risto = " + this.filter);
        
        postMap = new HashMap<String, String>();
        postMap.put("request", "fetch");
        postMap.put("categ", category.toLowerCase().replace(" ", ""));
        postMap.put("prov", Utils.getPreferenceString(getActivity(), "where", "Qui"));
        postMap.put("giorno", Utils.getWeekDay(getActivity()));
        postMap.put("lat", "" + latitude);
        postMap.put("long", "" + longitude);
        postMap.put("ordina", sorting);
        postMap.put("from", "" + 0);
        postMap.put("filtro", this.filter);
        adapter.clear();
        setListAdapter(adapter);
        // Log.d("BLA", "4) postMap is: " + postMap);
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST,
                postMap, TAG_NORMAL);
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        
        if (v.getId() == FOOTER_VIEW_ID) {
            return;
        }
        
        EsercenteRistorazione ese = (EsercenteRistorazione) l.getAdapter().getItem(position);
        
        // Log.d("XXX", "cliccato item = " + ese.getID());
        Intent intent = new Intent(getActivity(), DettaglioEsercenteBaseActivity.class);
        intent.putExtra(ESE_ID, "" + ese.getID());
        intent.putExtra("ESE_TITLE", ese.getInsegna());
        intent.putExtra("isRisto", true);
        startActivity(intent);
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
                    caImageView.loadImageFromURL(urlImageString);
                }
                
                if (title != null) {
                    Log.d(DEBUG_TAG, "Sorting Ã¨: " + sorting);
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
                        price.setText("-- Û");
                    else
                        price.setText(String.format("%s Û: ", esercente.getFasciaPrezzo()));
                }
            }
            return v;
        }
    }
}
