
package it.wm.perdue.dettaglioEsercenti;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.wm.CachedAsyncImageView;
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
            
            View v = convertView;
            int resource = 0;
            TextView textView = null;
            TextView contactTextView = null;
            TextView kindContactTextView = null;
            TextView cellKind = null;
            
            Log.d("XXX", "get view madre position --> " + position);
            
            if (sections.get(position).equals("info")
                    || sections.get(position).equals("infoRisto")) {
                resource = R.layout.dettaglio_info_row;
            }
            else if (sections.get(position).equals("map")) {
                resource = R.layout.map_row;
            }
            else if (sections.get(position).equals("tel")
                    || sections.get(position).equals("mail") ||
                    sections.get(position).equals("url")) {
                resource = R.layout.contact_row;
            }
            else if (sections.get(position).equals("altre")) {
                resource = R.layout.action_row;
            }
            
            v = inflater.inflate(resource, null);
            
            if (esercente != null) {
                
                if (sections.get(position).equals("info")) {
                    textView = (TextView) v.findViewById(R.id.infoRow);
                    
                    textView.setText(Html.fromHtml((
                            ("<b>"
                                    + esercente.getTipologia()
                                    + "</b> <br />"
                                    +
                                    esercente.getInsegna()
                                    + "<br />")
                                    +
                                    (esercente.getPranzoString() != null ? "<b>Giorni di validit�</b><br />"
                                            +
                                            esercente.getPranzoString() + "<br />"
                                            : "")
                                    +
                                    (esercente.getCenaString() != null ? esercente.getCenaString()
                                            + "<br />" : "")
                                    +
                                    (esercente.getGiornoChiusura() != null ? "<b> Giorno di chiusura</b><br />"
                                            +
                                            esercente.getGiornoChiusura() + "<br />"
                                            : "")
                                    + (esercente.getNoteVarie() != null ? "<b> Condizioni</b>"
                                    + "<br />"
                                    + esercente.getNoteVarie() : ""))));
                    
                }
                else if (sections.get(position).equals("map")) {
                    CachedAsyncImageView mapImage = null;
                    
                    textView = (TextView) v.findViewById(R.id.mapInfo);
                    textView.setText(Html.fromHtml(
                            (esercente.getCitta() != null ? "<b>Citt�</b>" + "<br />" +
                                    esercente.getCitta() + "<br />" : "")
                                    +
                                    (esercente.getZona() != null ? "<b> Zona </b>" + "<br />"
                                            + esercente.getZona()
                                            + "<br />" : "")
                                    +
                                    (esercente.getIndirizzo() != null ? "<b> Indirizzo</b>"
                                            + "<br />" + esercente.getIndirizzo() : "")));
                    
                    mapImage = (CachedAsyncImageView) v.findViewById(R.id.mapImage);
                    
                    String urlString =
                            "http://maps.googleapis.com/maps/api/staticmap?" +
                                    "zoom=16&size=512x240&markers=size:big|color:red|" +
                                    esercente.getLatitude() +
                                    "," +
                                    esercente.getLongitude() +
                                    "&sensor=false";
                    mapImage.setTag(urlString);
                    mapImage.loadImageFromURL(urlString);
                    
                }
                else if (sections.get(position).equals("infoRisto")) {
                    
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
                                    "<b> Specialit� </b>"
                                            + "<br />" +
                                            esercente.getSpecialita() + "<br />" : "")
                            + (esercente.getFasciaPrezzo() != null ? "<b> Prezzo medio</b>" +
                                    "<br />"
                                    + esercente.getFasciaPrezzo() + "�" : "")));
                }
                else if (sections.get(position).equals("tel")) {
                    contactTextView = (TextView) v.findViewById(R.id.contactResource);
                    kindContactTextView = (TextView) v.findViewById(R.id.contactKind);
                    cellKind = (TextView) v.findViewById(R.id.cellKind);
                    contactTextView.setText(esercente.getTelefono());
                    kindContactTextView.setText("Telefono");
                    cellKind.setText("tel");
                }
                else if (sections.get(position).equals("mail")) {
                    contactTextView = (TextView) v.findViewById(R.id.contactResource);
                    kindContactTextView = (TextView) v.findViewById(R.id.contactKind);
                    cellKind = (TextView) v.findViewById(R.id.cellKind);
                    contactTextView.setText(esercente.getEmail());
                    kindContactTextView.setText("E-mail");
                    cellKind.setText("mail");
                }
                else if (sections.get(position).equals("url")) {
                    contactTextView = (TextView) v.findViewById(R.id.contactResource);
                    kindContactTextView = (TextView) v.findViewById(R.id.contactKind);
                    cellKind = (TextView) v.findViewById(R.id.cellKind);
                    contactTextView.setText(esercente.getUrl());
                    kindContactTextView.setText("Sito web");
                    cellKind.setText("web");
                }
                else if (sections.get(position).equals("altre")) {
                    Log.d("XXX", " riga altro");
                    TextView actionTextView = (TextView) v.findViewById(R.id.action);
                    actionTextView.setText("Altre informazioni");
                }
            }
            
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
            
            // for (int i = 0; i < sections.size(); i++)
            // Log.d("XXX", "nome sezione è = " + sections.get(i));
            
        }
        
    }
    
}
