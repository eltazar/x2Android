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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import it.wm.perdue.R;
import it.wm.perdue.businessLogic.CreditCard;

public class CreditCardFragment extends SherlockFragment implements OnClickListener, OnEditorActionListener, OnFocusChangeListener {
    
    private static final String TAG_CREDIT_CARD      = "login";

    // views
    private EditText            numberEditText    = null;
    private EditText            cvvEditText    = null;
    private EditText            monthEditText    = null;
    private EditText            yearEditText    = null;
    private EditText            ownerEditText    = null;
    private Spinner             spinnerInstitute = null;
    
    private CreditCard          creditCard = null;
    
    private OnCreditCardFormListener formListener = null;
    
    public static CreditCardFragment newInstance() {
        CreditCardFragment fragment = new CreditCardFragment();
    return fragment;
    }
    
    public interface OnCreditCardFormListener{
        public void onCreditCardDoneButtonClicked(CreditCard c);
        public void onCreditCardCancelButtonClicked();
    }
    
    // onAttach
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            formListener = (OnCreditCardFormListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnCreditCardFormListener");
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
        if(savedInstanceState != null){
            creditCard = (CreditCard)savedInstanceState.getParcelable(TAG_CREDIT_CARD);
            Log.d("check","leggo vecchio stato ->"+creditCard.toString());
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {        
        //salvo stato della carta di credito 
        saveIntoModel();
        outState.putParcelable(TAG_CREDIT_CARD, creditCard);
        Log.d("check","salvo vecchio stato ->"+creditCard.toString());
        super.onSaveInstanceState(outState);
    }   
    
    @Override
    public void onResume(){
        super.onResume();
        if(creditCard != null){
            Log.d("check","setto valori del vecchio stato->"+creditCard.toString());

            numberEditText.setText(creditCard.getNumber());
            cvvEditText.setText(creditCard.getCvv());
            ownerEditText.setText(creditCard.getOwner());
            monthEditText.setText((creditCard.getMonth()>0)?(""+creditCard.getMonth()):"");
            yearEditText.setText((creditCard.getYear()>0)?(""+creditCard.getYear()):"");
            spinnerInstitute.setSelection(creditCard.getInstitute());
        }
    }
    
    // onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.credit_card_form, container, false);
        
        Button doneBtn = (Button) view.findViewById(R.id.saveCardBtn);
        doneBtn.setOnClickListener(this);
    
        numberEditText = (EditText) view.findViewById(R.id.creditNumber);
        numberEditText.setOnFocusChangeListener(this);
        numberEditText.setOnEditorActionListener(this);

        cvvEditText = (EditText) view.findViewById(R.id.cvv);
        cvvEditText.setOnFocusChangeListener(this);
        cvvEditText.setOnEditorActionListener(this);

        monthEditText = (EditText) view.findViewById(R.id.month);
        monthEditText.setOnFocusChangeListener(this);
        monthEditText.setOnEditorActionListener(this);

        yearEditText = (EditText) view.findViewById(R.id.year);
        yearEditText.setOnFocusChangeListener(this);
        yearEditText.setOnEditorActionListener(this);

        ownerEditText = (EditText) view.findViewById(R.id.owner);
        ownerEditText.setOnFocusChangeListener(this);
        ownerEditText.setOnEditorActionListener(this);

        spinnerInstitute = (Spinner) view.findViewById(R.id.spinnerInstitute);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.creditCards, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInstitute.setAdapter(adapter);
        spinnerInstitute.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                creditCard.setInstitute(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                creditCard.setInstitute(0);            }
        });
        
        creditCard = new CreditCard();
        return view;
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
                    formListener.onCreditCardDoneButtonClicked(creditCard);
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
    public boolean onEditorAction(TextView v, int actionId, KeyEvent arg2) {
        String input = v.getText().toString();
        Log.d("check","CHIAMATO ONEDITORACTION DONE = "+input);

        if(actionId == EditorInfo.IME_ACTION_DONE)
        {
            input= v.getText().toString();
            //Log.d("check","CHIAMATO ONEDITORACTION DONE = "+input);
            //MyActivity.calculate(input);
        }        
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
        creditCard.setInstitute(spinnerInstitute.getSelectedItemPosition());
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
        
        if (creditCard.getInstitute() == 0) {
           // Log.d("XXX", "card -> " + card + " invalido");
            ((TextView) spinnerInstitute.getChildAt(0)).setTextColor(Color.RED);
            isValid = false;
        }
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