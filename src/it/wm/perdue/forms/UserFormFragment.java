package it.wm.perdue.forms;


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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;

import java.util.HashMap;

    public class UserFormFragment extends SherlockFragment implements
            HTTPAccess.ResponseListener, OnClickListener, OnEditorActionListener, OnFocusChangeListener {
        
        protected static final String TAG_NORMAL    = "normal";
        protected static final String TAG_MAIL        ="mail";
        protected static final String TAG_NAME        ="name";
        protected static final String TAG_SURNAME     ="surname";
        protected static final String TAG_TEL         ="tel";
        
        // Gestione dei download:
        protected HTTPAccess          httpAccess      = null;
        
        // fields
        protected String              name            = null;
        protected String              surname         = null;
        protected String              email           = null;
        protected String              tel             = null;
        
        // views
        protected EditText            nameEditText    = null;
        protected EditText            surnameEditText = null;
        protected EditText            mailEditText    = null;
        protected EditText            telEditText     = null;
        
        protected ProgressDialog      progressDialog;
        
        public static UserFormFragment newInstance() {
            return new UserFormFragment();
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
            if(savedInstanceState !=null){
                email = savedInstanceState.getString(TAG_MAIL);
                name = savedInstanceState.getString(TAG_NAME);
                surname = savedInstanceState.getString(TAG_SURNAME);
                tel = savedInstanceState.getString(TAG_TEL);
            }
        }
        
        @Override
        public void onResume(){
            super.onResume();
            mailEditText.setText(email);
            nameEditText.setText(name);    
            surnameEditText.setText(surname);    
            telEditText.setText(tel);    
        }
        
        // onCreateView
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.user_form, container, false);
            
            httpAccess = new HTTPAccess();
            httpAccess.setResponseListener(this);
            
            setupView(view);
    
            return view;
        }
        
        @Override
        public void onSaveInstanceState(Bundle outState) {
            try{
                outState.putString(TAG_MAIL,mailEditText.getText().toString());
                outState.putString(TAG_NAME,nameEditText.getText().toString());
                outState.putString(TAG_SURNAME,surnameEditText.getText().toString());
                outState.putString(TAG_TEL,telEditText.getText().toString());
            }
            catch(NullPointerException e){
                e.printStackTrace();
            }
            
            super.onSaveInstanceState(outState);
        }   
        
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
        
        protected void setupView(View view){
            Button sendBtn = (Button) view.findViewById(R.id.sendBtn);
            sendBtn.setOnClickListener(this);
            Log.d("reg","setup view");
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
        }
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            
            switch (v.getId()) {
                case R.id.sendBtn:
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
        
        protected boolean validateFields() {
            
            name = nameEditText.getText().toString().replace(" ", "");
            surname = surnameEditText.getText().toString().replace(" ", "");
            tel = telEditText.getText().toString().replace(" ", "");
            email = mailEditText.getText().toString().replace(" ", "");
            
            boolean isValid = true;
            
            if (name.length() == 0) {
                //Log.d("XXX", "name -> " + name + " invalido");
                // nameEditText.setHint("Nome");
                nameEditText.setText("");
                nameEditText.setHintTextColor(Color.RED);
                // nameEditText.setTextColor(Color.RED);
                isValid = false;
            }
            
            if (surname.length() == 0) {
                // setto rosso il field
                //Log.d("XXX", "surname-> " + surname + " invalido");
                // surnameEditText.setHint("Cognome");
                surnameEditText.setText("");
                surnameEditText.setHintTextColor(Color.RED);
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
            
            if (tel.length() == 0 || !PhoneNumberUtils.isGlobalPhoneNumber(tel)) {
                // setto rosso il field
                //Log.d("XXX", "tel -> " + tel + " invalido");
                // telEditText.setHint("Telefono");
                // telEditText.setText("");
                telEditText.setHintTextColor(Color.RED);
                telEditText.setTextColor(Color.RED);
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
        
        protected void sendRequestToServer() {
            
            String urlString = "https://cartaperdue.it/coupon/registrazione_utente_app_exe.jsp";
            
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("nome", name);
            postMap.put("cognome", surname);
            postMap.put("telefono", Utils.replacePlusInPhone(tel));
            postMap.put("mail", email);
            
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST,
             postMap, TAG_NORMAL);
//            Log.d("XXX", "PADRE: i dati da inviare al server sono: " + postMap);
            
            progressDialog = ProgressDialog.show(getActivity(), "", "Invio in corso...");
            
        }
    }