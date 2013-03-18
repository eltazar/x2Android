
package it.wm.perdue.forms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;

import java.util.HashMap;

public class RichiediCartaFragment extends UserFormFragment implements
        HTTPAccess.ResponseListener, OnClickListener, OnEditorActionListener, OnFocusChangeListener {
    
    protected static final String TAG_CARD     ="card";

    private int                 cardPosition = -1;
    private String              card = null;
    private Spinner             cardSpinner     = null;
        
    // onAttach
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
    }
    
    // onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    // onActivityCreated
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState !=null){
            cardPosition = savedInstanceState.getInt(TAG_CARD);
        }
    }
    
    @Override
    public void onResume(){
        super.onResume();
        cardSpinner.setSelection(cardPosition);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(outState != null)
            outState.putInt(TAG_CARD,cardSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }
    // onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.richiedi_carta, container, false);
        setupView(view);
        return view;
    }
    
    protected void setupView(View view){
        super.setupView(view);
        cardSpinner = (Spinner) view.findViewById(R.id.spinnerCarta);
        // Create an ArrayAdapter using the string array and a default spinner
        // layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.carte_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        cardSpinner.setAdapter(adapter);
    }
    /*
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        
        //Log.d("XXX", "RisPosta -> " + response);
        progressDialog.cancel();
        CharSequence text = "Richiesta inviata!";
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        //Log.d("XXX", "ERRORE INVIO ->" + tag);
        progressDialog.cancel();
        CharSequence text = "C'è stato un problema, riprova!";
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
    */
    
    protected boolean validateFields() {
   
        boolean isValid = super.validateFields();
        
        card = cardSpinner.getSelectedItem().toString();
        
        if (card.length() == 0 || card.equals("Scegli carta PerDue")) {
           // Log.d("XXX", "card -> " + card + " invalido");
            ((TextView) cardSpinner.getChildAt(0)).setTextColor(Color.RED);
            isValid = false;
        }
        
        return isValid;
        
    }
    
    @Override
    public boolean onEditorAction(TextView v, int arg1, KeyEvent arg2) {
        // Log.d("XXX", "EDIT TEXT EDIT");
        
        return false;
    }
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        
        if (hasFocus) {
            //Log.d("XXX", "has focus");
            EditText field = (EditText) v;
            field.setHintTextColor(Color.GRAY);
            field.setTextColor(Color.BLACK);
        }
    }
    
    protected void sendRequestToServer() {
        
        String urlString = "https://cartaperdue.it/partner/v2.0/RichiediCarta.php";
        
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("cardType", getCardString());
        postMap.put("name", name);
        postMap.put("surname", surname);
        postMap.put("phone", Utils.replacePlusInPhone(tel));
        postMap.put("email", email);
        
//         httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST,
//         postMap, TAG_NORMAL);
        Log.d("XXX", "FIGLIO: i dati da inviare al server sono: " + postMap);
        
        progressDialog = ProgressDialog.show(getActivity(), "", "Invio in corso...");
        
    }
    
    private String getCardString() {
        if (card.subSequence(6, card.length()).equals("Semestrale 24€")) {
            card = "Semestrale";
        }
        else if (card.subSequence(6, card.length()).equals("Annuale 39€")) {
            card = "Annuale";
        }
        else if (card.subSequence(6, card.length()).equals("Biennale 59€")) {
            card = "Biennale";
        }
        return card;
    }
}
