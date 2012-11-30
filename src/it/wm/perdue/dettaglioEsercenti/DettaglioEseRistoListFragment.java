
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import it.wm.perdue.R;
import it.wm.perdue.businessLogic.EsercenteRistorazione;

public class DettaglioEseRistoListFragment extends DettaglioEseListFragment {
    
    protected DettaglioEsercenteRistoAdapter adapter = null;
    
    public static DettaglioEseRistoListFragment newInstance(String eseId) {
        
        Log.d("XXX", "DETTAGLIO RISTO");
        
        DettaglioEseRistoListFragment fragment = new DettaglioEseRistoListFragment();
        Bundle args = new Bundle();
        args.putString(ESE_ID, eseId);
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        urlString = "http://www.cartaperdue.it/partner/v2.0/DettaglioRistorantePub.php?id="
                + eseId;
    }
    
    protected void onCreateAdapters(EsercenteRistorazione esercente) {
        adapter = new DettaglioEsercenteRistoAdapter(
                getActivity(),
                R.layout.esercente_row, esercente);
        setListAdapter(adapter);
    }
    
    private static class DettaglioEsercenteRistoAdapter extends
            DettaglioEsercenteAdapter<EsercenteRistorazione> {
        
        public DettaglioEsercenteRistoAdapter(Context context, int resource,
                EsercenteRistorazione esercente) {
            super(context, resource, esercente);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            Log.d("XXX", "CIAO");
            
            return null;
        }
        
        @Override
        protected void checkFields() {
            
            super.checkFields();
            
            Log.d("XXX", "*************************");
            if (((EsercenteRistorazione) esercente).getAmbiente() != null
                    || ((EsercenteRistorazione) esercente).getSottoTipologia() !=
                    null ||
                    ((EsercenteRistorazione) esercente).getFasciaPrezzo() != null
                    || ((EsercenteRistorazione) esercente).getSpecialita() != null) {
                sections.add(2, "infoRisto");
            }
            
        }
        
    }
    
}
