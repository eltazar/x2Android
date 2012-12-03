
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
            
            Log.d("xxx", "getView figlia n sections = " + sections.size() +
                    " position = "
                    + position + " section name = " + sections.get(position));
            
            if (sections.get(position).equals("infoRisto")) {
                
                View v = convertView;
                int resource = 0;
                TextView textView = null;
                
                if (v == null) {
                    resource = R.layout.dettaglio_info_row;
                    
                    v = ((LayoutInflater) super.getContext().getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(resource, null);
                }
                textView = (TextView) v.findViewById(R.id.infoRow);
                
                textView.setText(Html.fromHtml((
                        esercente.getAmbiente() != null ?
                                "<b> Ambiente</b>"
                                        + "<br />" +
                                        esercente.getAmbiente() + "<br />" : "") +
                        (esercente.getSottoTipologia() != null ?
                                "<b> Cucina </b>"
                                        + "<br />" +
                                        esercente.getSottoTipologia() + "<br />" : "")
                        +
                        (esercente.getSpecialita() != null ?
                                "<b> Specialitˆ </b>"
                                        + "<br />" +
                                        esercente.getSpecialita() + "<br />" : "")
                        + (esercente.getFasciaPrezzo() != null ? "<b> Prezzo medio</b>" +
                                "<br />"
                                + esercente.getFasciaPrezzo() + "Û" : "")));
                
                return v;
            }
            return super.getView(position, convertView, parent);
            
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
            
            // for (int i = 0; i < sections.size(); i++)
            // Log.d("XXX", "nome sezione  = " + sections.get(i));
            
        }
        
    }
    
}
