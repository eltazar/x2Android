
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import it.wm.perdue.R;
import it.wm.perdue.businessLogic.EsercenteRistorazione;

public class DettaglioEseRistoListFragment extends DettaglioEseListFragment {
    
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
    
    @Override
    protected void onCreateAdapters() {
        
        adapter = new DettaglioEsercenteRistoAdapter(
                getActivity(),
                R.layout.esercente_row, EsercenteRistorazione.class);
        Log.d("XXX", "ON CREATE ADAPTER RISTO = " + adapter);
    }
    
    private static class DettaglioEsercenteRistoAdapter extends
            DettaglioEsercenteAdapter<EsercenteRistorazione> {
        
        public DettaglioEsercenteRistoAdapter(Context context, int resource,
                Class<EsercenteRistorazione> clazz) {
            super(context, resource, clazz);
            Log.d("XXX", "DETTAGLIO RISTO ADAPTER");
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            Log.d("XXX", "Getview figlia");
            
            View v = super.getView(position, convertView, parent);
            // int resource = 0;
            // TextView textView = null;
            // TextView contactTextView = null;
            // TextView kindContactTextView = null;
            // ImageView mapImage = null;
            // Log.d("xxx", "getView figlia n sections = " + sections.size() +
            // " position = "
            // + position);
            // for (int i = 0; i < sections.size(); i++)
            // Log.d("XXX", "nome sezione  = " + sections.get(position));
            // if (sections.get(position).equals("infoRisto")) {
            // resource = R.layout.dettaglio_info_row;
            // }
            // v = ((LayoutInflater) super.getContext().getSystemService(
            // Context.LAYOUT_INFLATER_SERVICE))
            // .inflate(resource, null);
            //
            // if (esercente != null) {
            //
            // if (sections.get(position).equals("infoRisto")) {
            // textView = (TextView) v.findViewById(R.id.infoRow);
            //
            // String giorniString = null;
            //
            // try {
            // giorniString = (esercente.getGiorniString() != null ?
            // "<b> Giorni validitˆ </b>" + "<br />"
            // + esercente.getGiorniString() + "<br />" : "");
            // } catch (NullPointerException e) {
            // Log.d("XXX", "eccezione in getView: " + e.getLocalizedMessage());
            // }
            //
            // textView.setText(Html.fromHtml((
            // esercente.getGiornoChiusura() != null ?
            // "<b> Giorno di chiusura</b>"
            // + "<br />" +
            // esercente.getGiornoChiusura() + "<br />" : "")
            // +
            // (giorniString != null ? giorniString : "")
            // + (esercente.getNoteVarie() != null ? "<b> Condizioni</b>" +
            // "<br />"
            // + esercente.getNoteVarie() : "")));
            //
            // }
            // }
            
            return v;
            
        }
        
        @Override
        protected void checkFields() {
            
            super.checkFields();
            Log.d("XXX", "*********** checkfield figlia");
            if (esercente.getAmbiente() != null
                    || esercente.getSottoTipologia() !=
                    null ||
                    esercente.getFasciaPrezzo() != null
                    || esercente.getSpecialita() != null) {
                sections.add(2, "infoRisto");
            }
            
            for (int i = 0; i < sections.size(); i++)
                Log.d("XXX", "nome sezione  = " + sections.get(i));
            
        }
        
    }
    
}
