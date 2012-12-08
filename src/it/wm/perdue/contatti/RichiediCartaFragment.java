
package it.wm.perdue.contatti;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;

import java.util.HashMap;

public class RichiediCartaFragment extends SherlockFragment implements
        HTTPAccess.ResponseListener, OnClickListener, OnEditorActionListener, OnFocusChangeListener {
    
    private static final String TAG_NORMAL      = "normal";
    
    // Gestione dei download:
    private HTTPAccess          httpAccess      = null;
    
    // fields
    private String              card            = "";
    private String              name            = "";
    private String              surname         = "";
    private String              email           = "";
    private String              tel             = "";
    
    // views
    private EditText            nameEditText    = null;
    private EditText            surnameEditText = null;
    private EditText            mailEditText    = null;
    private EditText            telEditText     = null;
    private Spinner             cardSpinner     = null;
    
    private ProgressDialog      progressDialog;
    
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
    }
    
    // onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.richiedi_carta, container, false);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        
        cardSpinner = (Spinner) view.findViewById(R.id.spinnerCarta);
        // Create an ArrayAdapter using the string array and a default spinner
        // layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.carte_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        cardSpinner.setAdapter(adapter);
        
        Button sendBtn = (Button) view.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        
        nameEditText = (EditText) view.findViewById(R.id.name);
        surnameEditText = (EditText) view.findViewById(R.id.surname);
        mailEditText = (EditText) view.findViewById(R.id.email);
        telEditText = (EditText) view.findViewById(R.id.tel);
        
        nameEditText.setOnFocusChangeListener(this);
        surnameEditText.setOnFocusChangeListener(this);
        mailEditText.setOnFocusChangeListener(this);
        telEditText.setOnFocusChangeListener(this);
        telEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // rimuove focus e tastiera da textView
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                            .getWindowToken(), 0);
                    telEditText.clearFocus();
                }
                return false;
            }
        });
        
        return view;
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        
        Log.d("XXX", "RisPosta -> " + response);
        progressDialog.cancel();
        CharSequence text = "Richiesta inviata!";
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        Log.d("XXX", "ERRORE INVIO ->" + tag);
        progressDialog.cancel();
        CharSequence text = "C'� stato un problema, riprova!";
        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
        switch (v.getId()) {
            case R.id.sendBtn:
                Log.d("XXX", "SEND BTN");
                if (validateFields() == false) {
                    // mostro avviso errore
                    CharSequence text = "Per favore completa i campi richiesti correttamente";
                    Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    sendRequestToServer();
                }
                break;
            case R.id.surname:
                Log.d("XXX", "COGNOME EDIT TEXT");
                break;
            case R.id.email:
                break;
            case R.id.tel:
                break;
            default:
                break;
        }
        
    }
    
    private boolean validateFields() {
        
        name = nameEditText.getText().toString().replace(" ", "");
        surname = surnameEditText.getText().toString().replace(" ", "");
        tel = telEditText.getText().toString().replace(" ", "");
        email = mailEditText.getText().toString().replace(" ", "");
        card = cardSpinner.getSelectedItem().toString();
        
        boolean isValid = true;
        
        if (name.length() == 0) {
            Log.d("XXX", "name -> " + name + " invalido");
            // nameEditText.setHint("Nome");
            nameEditText.setText("");
            nameEditText.setHintTextColor(Color.RED);
            // nameEditText.setTextColor(Color.RED);
            isValid = false;
        }
        
        if (surname.length() == 0) {
            // setto rosso il field
            Log.d("XXX", "surname-> " + surname + " invalido");
            // surnameEditText.setHint("Cognome");
            surnameEditText.setText("");
            surnameEditText.setHintTextColor(Color.RED);
            // surnameEditText.setTextColor(Color.RED);
            isValid = false;
        }
        
        if (email.length() == 0 || !Utils.checkEmail(email)) {
            // setto rosso il field
            Log.d("XXX", "mail -> " + email + " invalido");
            // mailEditText.setHint("E-mail");
            // mailEditText.setText("");
            mailEditText.setHintTextColor(Color.RED);
            mailEditText.setTextColor(Color.RED);
            isValid = false;
        }
        
        if (tel.length() == 0 || !PhoneNumberUtils.isGlobalPhoneNumber(tel)) {
            // setto rosso il field
            Log.d("XXX", "tel -> " + tel + " invalido");
            // telEditText.setHint("Telefono");
            // telEditText.setText("");
            telEditText.setHintTextColor(Color.RED);
            telEditText.setTextColor(Color.RED);
            isValid = false;
        }
        
        if (card.length() == 0 || card.equals("Scegli carta PerDue")) {
            Log.d("XXX", "card -> " + card + " invalido");
            ((TextView) cardSpinner.getChildAt(0)).setTextColor(Color.RED);
            isValid = false;
        }
        
        return isValid;
        
    }
    
    @Override
    public boolean onEditorAction(TextView v, int arg1, KeyEvent arg2) {
        // TODO Auto-generated method stub
        // Log.d("XXX", "EDIT TEXT EDIT");
        
        return false;
    }
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        
        if (hasFocus) {
            Log.d("XXX", "has focus");
            EditText field = (EditText) v;
            field.setHintTextColor(Color.GRAY);
            field.setTextColor(Color.BLACK);
        }
    }
    
    private void sendRequestToServer() {
        
        String urlString = "https://cartaperdue.it/partner/v2.0/RichiediCarta.php";
        
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("cardType", getCardString());
        postMap.put("name", name);
        postMap.put("surname", surname);
        postMap.put("phone", Utils.replacePlusInPhone(tel));
        postMap.put("email", email);
        
        // httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST,
        // postMap, TAG_NORMAL);
        Log.d("XXX", "i dati da inviare al server sono: " + postMap);
        
        progressDialog = ProgressDialog.show(getActivity(), "", "Invio in corso...");
        
    }
    
    private String getCardString() {
        if (card.subSequence(6, card.length()).equals("Semestrale 20�")) {
            card = "Semestrale";
        }
        else if (card.subSequence(6, card.length()).equals("Annuale 36�")) {
            card = "Annuale";
        }
        else if (card.subSequence(6, card.length()).equals("Biennale 55�")) {
            card = "Biennale";
        }
        return card;
    }
}
