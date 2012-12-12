
package it.wm.perdue.doveusarla;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import it.wm.CachedAsyncImageView;
import it.wm.perdue.JSONListAdapter;
import it.wm.perdue.R;
import it.wm.perdue.businessLogic.EsercenteRistorazione;
import it.wm.perdue.dettaglioEsercenti.DettaglioEsercenteBaseActivity;

public class EsercentiRistoListFragment extends EsercentiListFragment { 
    private static final String DEBUG_TAG = "EsercentiRistoListFragment";
        
    public static EsercentiRistoListFragment newInstance(String sort, String categ) {
        EsercentiRistoListFragment fragment = new EsercentiRistoListFragment();
        Bundle args = new Bundle();
        args.putString(Tags.ARGS_CATEGORY, categ.toLowerCase());
        args.putString(Tags.ARGS_SORTING,  sort.toLowerCase());
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
        Log.d(DEBUG_TAG, "cambiato filtro dentro risto = " + this.filter);
        onChangeWhereWhenFilter();
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        EsercenteRistorazione ese = (EsercenteRistorazione) l.getAdapter().getItem(position);
        
        Intent intent = new Intent(getActivity(), DettaglioEsercenteBaseActivity.class);
        intent.putExtra(DettaglioEsercenteBaseActivity.Tags.ID,       "" + ese.getID());
        intent.putExtra(DettaglioEsercenteBaseActivity.Tags.TITLE,    ese.getInsegna());
        intent.putExtra(DettaglioEsercenteBaseActivity.Tags.IS_RISTO, true);
        startActivity(intent);
    }
    
    private static class EsercenteRistoJSONListAdapter extends
            JSONListAdapter<EsercenteRistorazione> {
        private static final String DEBUG_TAG = "EsercenteRistoJSONListAdapter";
        public EsercenteRistoJSONListAdapter(Context context, int resource,
                Class<EsercenteRistorazione[]> clazz, String sorting) {
            super(context, resource, clazz);
            Log.d(DEBUG_TAG, "RISTO ADAPTER ISTANZIATO");
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(DEBUG_TAG, "GET VIEW DI RISTO LIST");
            
            View v = convertView;
            if (v == null) {
                v = inflater.inflate(R.layout.esercente_risto_row, null);
            }
            
            EsercenteRistorazione esercente = getItem(position);
            if (esercente != null) {
                TextView title    = (TextView) v.findViewById(R.id.eseTitle);
                TextView address  = (TextView) v.findViewById(R.id.address);
                TextView distance = (TextView) v.findViewById(R.id.distance);
                TextView foodKind = (TextView) v.findViewById(R.id.kindFood);
                TextView price    = (TextView) v.findViewById(R.id.price);
                CachedAsyncImageView caImageView = (CachedAsyncImageView) v
                        .findViewById(R.id.eseImage);
                
                String urlImageString = "http://www.cartaperdue.it/partner/v2.0/ImmagineEsercente.php?id="
                        + esercente.getID();
                
                if (caImageView != null) {
                    Log.d(DEBUG_TAG, "esercente id  = " + esercente.getID());
                    caImageView.loadImageFromURL(urlImageString);
                }
                
                if (title != null) {
                    title.setText(esercente.getInsegna());
                }
                if (address != null) {
                    address.setText(esercente.getIndirizzo());
                }
                if (distance != null) {
                    distance.setText(String.format("a %.3fkm ", esercente.getDistanza()));
                }
                if (foodKind != null) {
                    foodKind.setText(String.format("Cucina: %s ", esercente.getSottoTipologia()));
                }
                if (price != null) {
                    if (esercente.getFasciaPrezzo() == null)
                        price.setText("Prezzo non disponibile");
                    else
                        price.setText(String.format("%s€", esercente.getFasciaPrezzo()));
                }
            }
            return v;
        }
    }
}
