
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
        //Log.d("XXX", "ON CREATE ADAPTER RISTO = " + adapter);
    }
    
    private static class DettaglioEsercenteRistoAdapter extends
            DettaglioEsercenteAdapter<EsercenteRistorazione> {
        
        public DettaglioEsercenteRistoAdapter(Context context, int resource,
                Class<EsercenteRistorazione> clazz) {
            super(context, resource, clazz);
            //Log.d("XXX", "DETTAGLIO RISTO ADAPTER");
        }
        
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            v = super.getView(position, v, parent);
            
            if (esercente != null) {
                TextView infoTextView = (TextView) v.findViewById(R.id.infoRow);
            
                if (sections.get(position).equals("info")) {
                    
                    
                    //costruisco stringa giorni validità
                    
                    String giorniValidita = "<b>Giorni di validità</b><br />";
                    if(esercente.getPranzoString() != null)
                        giorniValidita = giorniValidita.concat( esercente.getPranzoString() + "<br />");
                    if(esercente.getCenaString() != null)
                        giorniValidita = giorniValidita.concat( esercente.getCenaString() + "<br />");
                    
                    if(giorniValidita.equals("<b>Giorni di validità</b><br />"))
                        giorniValidita = "";
                    
                    infoTextView.setText(Html.fromHtml((
                            ("<b>"
                                    + esercente.getTipologia()
                                    + "</b> <br />"
                                    +
                                    esercente.getInsegna()
                                    + "<br />")
                                    +
                                    giorniValidita
                                    +
                                    (esercente.getGiornoChiusura() != null ? "<b> Giorno di chiusura</b><br />"
                                            +
                                            esercente.getGiornoChiusura() + "<br />"
                                            : "")
                                    + (esercente.getNoteVarie() != null ? "<b> Condizioni di validità</b>"
                                    
                                    +" <br />"+ esercente.getNoteVarie() : ""))));
                    
                }
                else if (sections.get(position).equals("infoRisto")) {
                    
                    infoTextView.setText(Html.fromHtml((
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
                                    "<b> Specialità </b>"
                                            + "<br />" +
                                            esercente.getSpecialita() + "<br />" : "")
                            + (esercente.getFasciaPrezzo() != null ? "<b> Prezzo medio</b>" +
                                    "<br />"
                                    + esercente.getFasciaPrezzo() + "€" : "")));
                }
               
            }
            
            return v;
            
        }
        
        @Override
        protected void checkFields() {
            
            super.checkFields();
            
            //Log.d("XXX", "*********** checkfield figlia");
            if (esercente.getAmbiente() != null
                    || esercente.getSottoTipologia() !=
                    null ||
                    esercente.getFasciaPrezzo() != null
                    || esercente.getSpecialita() != null) {
                sections.add(2, "infoRisto");
            }
            
            // for (int i = 0; i < sections.size(); i++)
            // Log.d("XXX", "nome sezione è = " + sections.get(i));
            
        }
        
    }
    
}
