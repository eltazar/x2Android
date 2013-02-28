package it.wm.perdue.coupon;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.LoginData;

import java.util.HashMap;

public class RegistrazioneFormFragment extends SherlockFragment implements
        HTTPAccess.ResponseListener, OnClickListener, OnEditorActionListener, OnFocusChangeListener {
    
    private static final String TAG_LOGIN      = "login";
    private static final String TAG_RETRIEVE      = "retrieve";
    private static final String TAG_SIGNUP      = "signup";
    
    // Gestione dei download:
    private HTTPAccess          httpAccess      = null;
    private String              loginUrlString = null;
    // fields
    private String              email           = "";
    private String              psw             = "";
    
    // views
    private EditText            mailEditText    = null;
    private EditText            pswEditText    = null;

    private ProgressDialog      progressDialog;
    
    
    public static RegistrazioneFormFragment newInstance() {
        RegistrazioneFormFragment fragment = new RegistrazioneFormFragment();
        return fragment;
    }
    
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
        
        loginUrlString = "https://cartaperdue.it/partner/login.php";
    }
    
    // onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_reg, container, false);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        
        Button sendBtn = (Button) view.findViewById(R.id.loginBtn);
        sendBtn.setOnClickListener(this);
        
        mailEditText = (EditText) view.findViewById(R.id.email);
        pswEditText = (EditText) view.findViewById(R.id.psw);
        
        mailEditText.setOnFocusChangeListener(this);
        pswEditText.setOnFocusChangeListener(this);
        
        return view;
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        
        Log.d("login", "RisPosta -> " + response);
        CharSequence text = "";
        
        //se utente non esiste
        if(response.length() == 17 && response.endsWith("{\"login\":[false]}")){
            Log.d("login","mail o psw login errati");
            text = "Username o password errati, riprova";
            Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            //altrimenti parso il json e controllo
            Gson gson = Utils.getGson();
            String strippedString = Utils.formatJSONlogin(response);
            //Log.d("login", "stripped -> " + strippedString);
            LoginData loginData = gson.fromJson(strippedString, LoginData.class);
            Log.d("login","RICEVUTO OGGETTO = "+loginData.getIdCustomer());
            if(loginData.getIdCustomer() != -1){
                //loggato
                Log.d("login","loggato");
            }
        }
        
        progressDialog.cancel();   
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
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
        switch (v.getId()) {
            case R.id.loginBtn:
                //Log.d("XXX", "SEND BTN");
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
               // Log.d("XXX", "COGNOME EDIT TEXT");
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
        
        psw = pswEditText.getText().toString().replace(" ", "");
        email = mailEditText.getText().toString().replace(" ", "");
        
        boolean isValid = true;
        
        if (psw.length() == 0) {
            // setto rosso il field
            //Log.d("XXX", "surname-> " + surname + " invalido");
            // surnameEditText.setHint("Cognome");
            pswEditText.setText("");
            pswEditText.setHintTextColor(Color.RED);
            // surnameEditText.setTextColor(Color.RED);
            isValid = false;
        }
        
        if (email.length() == 0 || !Utils.checkEmail(email)) {
            // setto rosso il field
           // Log.d("XXX", "mail -> " + email + " invalido");
            // mailEditText.setHint("E-mail");
            // mailEditText.setText("");
            mailEditText.setHintTextColor(Color.RED);
            mailEditText.setTextColor(Color.RED);
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
            //Log.d("XXX", "has focus");
            EditText field = (EditText) v;
            field.setHintTextColor(Color.GRAY);
            field.setTextColor(Color.BLACK);
        }
    }
    
    private void sendRequestToServer() {
        
        String urlString = "https://cartaperdue.it/partner/v2.0/RichiediCarta.php";
        
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("usr", email);
        postMap.put("psw", psw);
        
         httpAccess.startHTTPConnection(loginUrlString, HTTPAccess.Method.POST,
         postMap, TAG_LOGIN);
        Log.d("XXX", "i dati da inviare al server sono: " + postMap);
        
        progressDialog = ProgressDialog.show(getActivity(), "", "Invio in corso...");
        
    }
    
    /*
     if(array.count == 2){
            NSLog(@"UTENTE ESISTE");
            idUtente = [[[array objectAtIndex:0] objectForKey:@"idcustomer"] intValue];
            NSLog(@" ID CUSTOMER LOGIN = %d",idUtente);
            
            //salvo i dati per il login
            NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
            
            [prefs removeObjectForKey:@"_idUtente"];
            [prefs setObject:[NSNumber numberWithInt:idUtente] forKey:@"_idUtente"];
            [prefs removeObjectForKey:@"_nomeUtente"];
            [prefs setObject:[[array objectAtIndex:0] objectForKey:@"nome_contatto"] forKey:@"_nomeUtente"];
            [prefs removeObjectForKey:@"_cognome"];
            [prefs setObject:[[array objectAtIndex:0] objectForKey:@"cognome_contatto"] forKey:@"_cognome"];
            [prefs removeObjectForKey:@"_email"];
            [prefs setObject:self.user forKey:@"_email"];
            [prefs synchronize];
            
            if(delegate && [delegate respondsToSelector:@selector(didLogin:)])
                [delegate didLogin:idUtente];
            
        }
        else{
            NSLog(@"PSW SBAGLIATA ");
            UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"Spiacenti" message:@"L'utente o la password non esistono. Inserisci i dati login corretti e riprova" delegate:self cancelButtonTitle:@"Chiudi" otherButtonTitles:nil, nil];
            [alert show];
            [alert release];
        }
    }
     * 
     * 
     * 
     * 
     * */
    
    
}