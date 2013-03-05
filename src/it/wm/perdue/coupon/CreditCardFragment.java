package it.wm.perdue.coupon;

import android.app.Activity;
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

import it.wm.perdue.R;
import it.wm.perdue.businessLogic.CreditCard;

public class CreditCardFragment extends SherlockFragment implements OnClickListener, OnEditorActionListener, OnFocusChangeListener {
    
    private static final String TAG_LOGIN      = "login";
    private static final String TAG_RETRIEVE      = "retrieve";
    
    private static final String TAG_MAIL       = "mail";
    private static final String TAG_PSW       = "psw";

    // views
    private EditText            numberEditText    = null;
    private EditText            cvvEditText    = null;
    private EditText            monthEditText    = null;
    private EditText            yearEditText    = null;
    private EditText            ownerEditText    = null;
    
    private CreditCard          creditCard = null;
    
    //private OnLoginFormListener formListener = null;
    //
    public static CreditCardFragment newInstance() {
        CreditCardFragment fragment = new CreditCardFragment();
    return fragment;
    }
    
    //public interface OnLoginFormListener{
    //public void onRegButtonClicked();
    //}
    
    // onAttach
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //try {
        //    formListener = (OnLoginFormListener) activity;
        //} catch (ClassCastException e) {
        //    // The activity doesn't implement the interface, throw exception
        //    throw new ClassCastException(activity.toString()
        //            + " must implement OnLoginFormListener");
        //}
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
        //if(savedInstanceState !=null){
        //    Log.d("login","entrato -> "+savedInstanceState.getString(TAG_MAIL));
        //    email = savedInstanceState.getString(TAG_MAIL);
        //    psw = savedInstanceState.getString(TAG_PSW);
        //}
    }
    
    @Override
    public void onResume(){
        super.onResume();
        //mailEditText.setText(email);
        //pswEditText.setText(psw);       
    }
    
    // onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.credit_card_form, container, false);
        
        Button doneBtn = (Button) view.findViewById(R.id.saveCardBtn);
        doneBtn.setOnClickListener(this);
    
        numberEditText = (EditText) view.findViewById(R.id.creditNumber);
        numberEditText.setOnFocusChangeListener(this);

        cvvEditText = (EditText) view.findViewById(R.id.cvv);
        cvvEditText.setOnFocusChangeListener(this);

        monthEditText = (EditText) view.findViewById(R.id.month);
        monthEditText.setOnFocusChangeListener(this);

        yearEditText = (EditText) view.findViewById(R.id.year);
        yearEditText.setOnFocusChangeListener(this);

        ownerEditText = (EditText) view.findViewById(R.id.owner);
        ownerEditText.setOnFocusChangeListener(this);

        creditCard = new CreditCard();
        return view;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        
        //outState.putString(TAG_MAIL,mailEditText.getText().toString());
        //outState.putString(TAG_PSW,pswEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }    
    
    /*OnClickListener
     * */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
        switch (v.getId()) {
            case R.id.saveCardBtn:
                //Log.d("XXX", "SEND BTN");
                saveIntoModel();
                if (validateFields() == false) {
                    // mostro avviso errore
                    showErrorMessage();
                }
                else {
                    //ritorno oggetto carta di credito al padre
                }
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
        
        return false;
    }
    
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            EditText field = (EditText) v;      
            Log.d("XXX", "has focus");
            field.setHintTextColor(Color.GRAY);
            field.setTextColor(Color.BLACK);
        }
    }
    /*OnEditorActionListener, OnFocusChangeListener END
     * */
    
    /*Metodi privati
     * */
    
    //salva info nella carta di credito
    private void saveIntoModel(){
        //salvo nel modello
        creditCard.setNumber(numberEditText.getText().toString().replace(" ", ""));
        creditCard.setCvv(cvvEditText.getText().toString().replace(" ", ""));
        creditCard.setOwner(ownerEditText.getText().toString().replace(" ", ""));

        if(yearEditText.getText().toString().equals("")){
            creditCard.setYear(-1);
        }
        else{
            creditCard.setYear(Integer.parseInt(yearEditText.getText().toString()));

        }
        if(monthEditText.getText().toString().equals("")){
            creditCard.setMonth(-1);
        }
        else{
            creditCard.setMonth(Integer.parseInt(monthEditText.getText().toString()));
        }
    }
    
    //controlla i dati della carta di credito
    private boolean validateFields() {
          
        boolean isValid = true;
        
        //controllo se il modello è valido

        if (creditCard.getNumber().length() == 0){ 
            setErrorText(numberEditText);
            isValid = false;
        }
        if(creditCard.getCvv().length() == 0){
            setErrorText(cvvEditText);
            isValid = false;
        }
        if(creditCard.getOwner().length() == 0){
            setErrorText(ownerEditText);
            isValid = false;
        }
        if(creditCard.getMonth() < 1 || creditCard.getMonth() > 12){
            setErrorText(monthEditText);
            isValid = false;
        }
        if(creditCard.getYear() < 2013 || creditCard.getYear() > 2050){
            setErrorText(yearEditText);
            isValid = false;
        }
        
        //TODO: controllare numero di cifre della carta di credito e del cvv
        return isValid;
    }
    
    private void setErrorText(EditText e){
        e.setTextColor(Color.RED);
        e.setHintTextColor(Color.RED);
    }
    
    private void showErrorMessage(){
        CharSequence text = "Per favore completa i campi richiesti correttamente";
        Toast toast = Toast.makeText(getSherlockActivity(), text, Toast.LENGTH_SHORT);
        toast.show();
    }
}