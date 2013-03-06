package it.wm.perdue.forms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import it.wm.perdue.LoggingHandler;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.LoginData;

import java.util.HashMap;

public class LoginFormFragment extends SherlockFragment implements
        HTTPAccess.ResponseListener, OnClickListener, OnEditorActionListener, OnFocusChangeListener {
    
    private static final String TAG_LOGIN      = "login";
    private static final String TAG_RETRIEVE      = "retrieve";
    
    private static final String TAG_MAIL       ="mail";
    private static final String TAG_PSW       ="psw";

    // Gestione dei download:
    private HTTPAccess          httpAccess      = null;
    // fields
    private String              email           = "";
    private String              psw             = "";
    
    // views
    private EditText            mailEditText    = null;
    private EditText            pswEditText    = null;

    private ProgressDialog      progressDialog;
    private OnLoginFormListener formListener = null;
    
    public static LoginFormFragment newInstance() {
        LoginFormFragment fragment = new LoginFormFragment();
        return fragment;
    }
    
    public interface OnLoginFormListener{
        public void onRegButtonClicked();
    }
    
    // onAttach
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            formListener = (OnLoginFormListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginFormListener");
        }
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
            Log.d("login","entrato -> "+savedInstanceState.getString(TAG_MAIL));
            email = savedInstanceState.getString(TAG_MAIL);
            psw = savedInstanceState.getString(TAG_PSW);
        }
    }
    
    @Override
    public void onResume(){
        super.onResume();
        mailEditText.setText(email);
        pswEditText.setText(psw);   
        
        //DEBUG:
        Log.d("login","isLoggato---> "+(LoggingHandler.isLogged()?"yes":"no"));
    }
    
    // onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_reg, container, false);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        
        Button sendBtn = (Button) view.findViewById(R.id.loginBtn);
        sendBtn.setOnClickListener(this);
        
        Button retrieveBtn = (Button) view.findViewById(R.id.retrievePswBtn);
        retrieveBtn.setOnClickListener(this);
        Button signUpBtn = (Button) view.findViewById(R.id.signInbtn);
        signUpBtn.setOnClickListener(this);
        
        mailEditText = (EditText) view.findViewById(R.id.email);
        pswEditText = (EditText) view.findViewById(R.id.psw);
        
        mailEditText.setOnFocusChangeListener(this);
        pswEditText.setOnFocusChangeListener(this);
        
        return view;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        
        outState.putString(TAG_MAIL,mailEditText.getText().toString());
        outState.putString(TAG_PSW,pswEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }    
    
    /*HTTPAccess.ResponseListener
     * */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        
        Log.d("login", "RisPosta -> " + response);
        CharSequence text = "";
        Toast toast= null;

        if(tag.equals(TAG_LOGIN)){
            //se utente non esiste
            if(response.length() == 17 && response.endsWith("{\"login\":[false]}")){
                Log.d("login","mail o psw login errati");
                text = "Username o password errati, riprova";
                toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
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
                    loginData.setEmail(email);
                    LoggingHandler.onDidLogin(loginData);
                }
            }
        }
        else if(tag.equals(TAG_RETRIEVE)){
            if(response.length()==6 && response.equals("psw_ok")){
                text = "Password inviata all'indirizzo e-mail indiacato";
            }
            else{
            text = "E-mail non valida o non registrata, riprova";
            }
            toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
            toast.show();
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
    /*HTTPAccess.ResponseListener END
     * */
    
    /*OnClickListener
     * */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
        switch (v.getId()) {
            case R.id.loginBtn:
                //Log.d("XXX", "SEND BTN");
                if (validateLoginFields() == false) {
                    // mostro avviso errore
                    showErrorMessage();
                }
                else {
                    sendRequestLogin();
                }
                break;
            case R.id.retrievePswBtn:
                showRetrieveDialog();
                break;
            case R.id.signInbtn:
                regBntClicked();
                break;
            default:
                break;
        }
        
    }
    /*OnClickListener END
     * */
    
     /*OnEditorActionListener, OnFocusChangeListener
      * */
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
    /*OnEditorActionListener, OnFocusChangeListener END
     * */
    
    
    /*Metodi per richieste di rete
     * */
    private void sendRequestLogin() {
        
        String urlString = "https://cartaperdue.it/partner/login.php";
        
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("usr", email);
        postMap.put("psw", psw);
        
         httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST,
         postMap, TAG_LOGIN);
        Log.d("XXX", "i dati da inviare al server sono: " + postMap);
        
        progressDialog = ProgressDialog.show(getActivity(), "", "Invio in corso...");
        
    }   
   
    public void sendRetrieveRequest(String retrieveString){
        Log.d("coupon","richiesta recupero psw inviata");
        String urlString = "http://www.cartaperdue.it/partner/recuperaPsw.php";
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("usr", retrieveString);
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST,
                postMap, TAG_RETRIEVE);
        
        progressDialog = ProgressDialog.show(getActivity(), "", "Invio in corso...");
    }
    /*Metodi per richieste di rete END
     * */
    
    /*Metodi privati
     * */
    //mostra il dialog per il recupero della psw
    private void showRetrieveDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new RetrievePswDialog();
        dialog.show(getSherlockActivity().getSupportFragmentManager(), "RetrievePswDialog");
    }
    
    private void showErrorMessage(){
        CharSequence text = "Per favore completa i campi richiesti correttamente";
        Toast toast = Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    //controlla i dati di login
    private boolean validateLoginFields() {
        
        psw = pswEditText.getText().toString().replace(" ", "");
        email = mailEditText.getText().toString().replace(" ", "");
        
        boolean isValid = true;
        
        if (psw.length() == 0) {
            pswEditText.setText("");
            pswEditText.setHintTextColor(Color.RED);
            isValid = false;
        }
        
        if (email.length() == 0 || !Utils.checkEmail(email)) {
            mailEditText.setHintTextColor(Color.RED);
            mailEditText.setTextColor(Color.RED);
            isValid = false;
        }
        
        return isValid;
    }
    
    private void regBntClicked(){
        formListener.onRegButtonClicked();
    }

}