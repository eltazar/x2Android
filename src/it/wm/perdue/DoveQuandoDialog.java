
package it.wm.perdue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DoveQuandoDialog extends SherlockDialogFragment implements OnItemClickListener {
    
    private String[] values = new String[] {
            "a", "b", "c",
            "d", "e", "r", "g", "b", "c",
            "d", "e", "r", "g"
                            };
    private String[] bho    = new String[] {
            "1", "2", "3",
            "4", "5", "6", "7"
                            };
    
    // @Override
    // public Dialog onCreateDialog(Bundle savedInstanceState) {
    //
    // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    //
    // //
    // LayoutInflater inflater = getActivity().getLayoutInflater();
    // View view = inflater.inflate(R.layout.dove_quando_dialog, null);
    //
    // builder.setView(view);
    //
    // // //getResources().getStringArray(R.array.citta)
    // ListView listDove = (ListView) view.findViewById(R.id.where);
    // ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
    // android.R.layout.simple_list_item_single_choice, android.R.id.text1,
    // values
    // );
    // listDove.setAdapter(adapter);
    // listDove.setOnItemClickListener(this);
    //
    // ListView listQuando = (ListView) view.findViewById(R.id.when);
    // ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
    // android.R.layout.simple_list_item_single_choice, android.R.id.text1, bho
    // );
    // listQuando.setAdapter(adapter2);
    // listQuando.setOnItemClickListener(this);
    //
    // // Use the Builder class for convenient dialog construction
    // builder.setMessage("Dove e quando")
    // .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    // public void onClick(DialogInterface dialog, int id) {
    // // FIRE ZE MISSILES!
    // }
    // })
    // .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
    // public void onClick(DialogInterface dialog, int id) {
    // // User cancelled the dialog
    // }
    // });
    //
    // // Create the AlertDialog object and return it
    // return builder.create();
    // }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dove_quando_dialog, container, false);
        getDialog().setTitle("Dove e quando");
        // //getResources().getStringArray(R.array.citta)
        ListView listDove = (ListView) view.findViewById(R.id.where);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                values
                );
        listDove.setAdapter(adapter);
        listDove.setOnItemClickListener(this);
        
        ListView listQuando = (ListView) view.findViewById(R.id.when);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, bho
                );
        listQuando.setAdapter(adapter2);
        listQuando.setOnItemClickListener(this);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Dialog);
        return view;
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
