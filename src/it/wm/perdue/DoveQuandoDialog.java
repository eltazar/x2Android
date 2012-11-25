
package it.wm.perdue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DoveQuandoDialog extends DialogFragment implements OnItemClickListener {
    
    private FragmentActivity activity = null;
    private String[]         values   = new String[] {
            "a", "b", "c",
            "d", "e", "r", "g"
                                      };
    private String[]         bho      = new String[] {
            "1", "2", "3",
            "4", "5", "6", "7"
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
        
        // //getResources().getStringArray(R.array.citta)
        ListView listDove = (ListView) view.findViewById(R.id.where);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                values
                );
        listDove.setAdapter(adapter);
        listDove.setOnItemClickListener(this);
        
        ListView listQuando = (ListView) view.findViewById(R.id.when);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, bho
                );
        listQuando.setAdapter(adapter2);
        listQuando.setOnItemClickListener(this);
        
        // Use the Builder class for convenient dialog construction
        builder.setMessage("Dove e quando")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
    
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        /*
         * arg2 is the position in your list. It looks like you are doing some
         * extra (unnecessary) processing. arg1 is your row view, and since the
         * android.R.layout.simple_list_item_single_choice layout contains only
         * a CheckedTextView, you can use that directly without having to look
         * for it.
         */
        switch (arg0.getId()) {
            case R.id.where:
                Log.d("XX", "LISTA WHERE -> oggetto : " + values[arg2]);
                break;
            case R.id.when:
                Log.d("XX", "LISTA WHEN -> oggetto : " + bho[arg2]);
                break;
            default:
                // inserire Qui -> oggi
                break;
        }
        
    }
}
