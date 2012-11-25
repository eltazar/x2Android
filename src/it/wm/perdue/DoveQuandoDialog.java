
package it.wm.perdue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DoveQuandoDialog extends DialogFragment {
    
    private FragmentActivity activity = null;
    private String[]         values   = new String[] {
            "Android", "iPhone", "WindowsMobile",
            "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
            "Linux", "OS/2"
                                      };
    
    public DoveQuandoDialog(FragmentActivity activity) {
        this.activity = activity;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        //
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dove_quando_dialog, null);
        
        builder.setView(view);
        
        // First paramenter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        // //getResources().getStringArray(R.array.citta)
        ListView listDove = (ListView) view.findViewById(R.id.where);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, android.R.id.text1, values
                );
        listDove.setAdapter(adapter);
        
        ListView listQuando = (ListView) view.findViewById(R.id.when);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, android.R.id.text1, values
                );
        listQuando.setAdapter(adapter2);
        
        // Use the Builder class for convenient dialog construction
        builder.setMessage("Dove e quando")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
