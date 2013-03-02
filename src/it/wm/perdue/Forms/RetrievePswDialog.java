package it.wm.perdue.Forms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

import it.wm.perdue.R;
import it.wm.perdue.Utils;

public class RetrievePswDialog extends SherlockDialogFragment {
    
    private EditText retrieveEditText = null;
    
    public interface RetrievePswListener {
        public void onDialogPositiveClick(SherlockDialogFragment dialog,String retrieveString);
        public void onDialogNegativeClick(SherlockDialogFragment dialog);
    }
    
    RetrievePswListener mListener;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (RetrievePswListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RetrievePswDialogListener");
        }
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        // Get the layout inflater
        LayoutInflater inflater = getSherlockActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.retrieve_psw_dialog, null);
        builder.setView(v)
        // Add action buttons
               .setPositiveButton("Avanti", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       if(validateRetrieveFields()){
                           mListener.onDialogPositiveClick(RetrievePswDialog.this, retrieveEditText.getText().toString());
                       }
                       else{
                           CharSequence text = "Per favore completa i campi richiesti correttamente";
                           Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                           toast.show();
                       }
                   }
               })
               .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Send the negative button event back to the host activity
                       mListener.onDialogNegativeClick(RetrievePswDialog.this);
                   }
               });     
        builder.setTitle("Recupera password");
        
        retrieveEditText = (EditText)v.findViewById(R.id.retrieveField);
        
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            v.setBackgroundColor(Color.WHITE);
            //per inserire come titolo una custom view, dato che quella di default è nera
            //http://stackoverflow.com/questions/12811977/set-the-title-background-color-of-alert-dialog-without-making-a-custome-dialog
        }
        
        return builder.create();
    }
    
    //controlla l'input del dialog
    private boolean validateRetrieveFields() {
        boolean isValid = true;
        String email = retrieveEditText.getText().toString();
        if (email.length() == 0 || !Utils.checkEmail(email)) {
            retrieveEditText.setHintTextColor(Color.RED);
            retrieveEditText.setTextColor(Color.RED);
            isValid = false;
        }
        
        return isValid;
    }
    
    
}