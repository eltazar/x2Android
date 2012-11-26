
package it.wm.perdue;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DoveQuandoDialog extends SherlockDialogFragment implements OnItemClickListener {
    
    public interface ChangeDoveQuandoDialogListener {
        void onSaveDoveQuandoDialog();
    }
    
    private String[]                       cities = null;
    private String[]                       days   = null;
    
    private ChangeDoveQuandoDialogListener mListener;
    private String                         where  = "";
    private String                         when   = "";
    private static final String            WHERE  = "where";
    private static final String            WHEN   = "when";
    
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
        cities = getResources().getStringArray(R.array.cities);
        days = getResources().getStringArray(R.array.days);
        
        getDialog().setTitle("Dove e quando");
        setStyle(STYLE_NORMAL, android.R.style.Theme_Dialog);
        
        View view = inflater.inflate(R.layout.dove_quando_dialog, container, false);
        
        ListView listDove = (ListView) view.findViewById(R.id.where);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice, android.R.id.text1,
                cities
                );
        listDove.setAdapter(adapter);
        listDove.setOnItemClickListener(this);
        
        ListView listQuando = (ListView) view.findViewById(R.id.when);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, days
                );
        listQuando.setAdapter(adapter2);
        listQuando.setOnItemClickListener(this);
        
        Button saveBtn = (Button) view.findViewById(R.id.saveWW);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // salvo le impostazioni selezionate se la selezione non  vuota
                Log.d("XX", " ### WHERE ->  : " + where + " WHEN -> " + when);
                if (!where.equals(""))
                    Utils.setPreferenceString(getActivity(), WHERE, where);
                if (!when.equals(""))
                    Utils.setPreferenceString(getActivity(), WHEN, when);
                mListener.onSaveDoveQuandoDialog();
                dismiss();
            }
        });
        
        return view;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            mListener = (ChangeDoveQuandoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
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
        
        // salvo in variabili locali le scelte effettuate
        switch (arg0.getId()) {
            case R.id.where:
                where = cities[arg2];
                Log.d("XX", "LISTA WHERE -> oggetto : " + where);
                break;
            case R.id.when:
                Log.d("XX", "LISTA WHEN -> oggetto : " + days[arg2]);
                when = days[arg2];
                break;
            default:
                // inserire Qui -> oggi
                break;
        }
    }
}
