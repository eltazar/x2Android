
package it.wm.perdue;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;

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
    
    @TargetApi(11)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        cities = getResources().getStringArray(R.array.cities);
        days = getResources().getStringArray(R.array.days);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dove_quando_dialog, null);
        builder.setView(view);
        
        ArrayAdapter<String> adapter = null;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            ListView listWhere = (ListView) view.findViewById(R.id.where);
            adapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_single_choice,
                    android.R.id.text1,
                    cities);
            listWhere.setAdapter(adapter);
            listWhere.setOnItemClickListener(this);
            
            ListView listQuando = (ListView) view.findViewById(R.id.when);
            adapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_single_choice,
                    android.R.id.text1,
                    days);
            listQuando.setAdapter(adapter);
            listQuando.setOnItemClickListener(this);
        } else {
            final NumberPicker pickerWhere = (NumberPicker) view.findViewById(R.id.where);
            pickerWhere.setMinValue(0);
            pickerWhere.setMaxValue(cities.length - 1);
            pickerWhere.setDisplayedValues(cities);
            pickerWhere.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    DoveQuandoDialog.this.onValueChange(pickerWhere, newVal);
                }
            });
            pickerWhere.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            final NumberPicker pickerWhen = (NumberPicker) view.findViewById(R.id.when);
            pickerWhen.setMinValue(0);
            pickerWhen.setMaxValue(days.length - 1);
            pickerWhen.setDisplayedValues(days);
            pickerWhen.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    DoveQuandoDialog.this.onValueChange(pickerWhen, newVal);
                }
            });
            pickerWhen.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        }
        
        builder.setTitle("Dove e quando")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // salvo le impostazioni selezionate se la selezione non
                        // è vuota
                        Log.d("uu", " ### WHERE ->  : " + where + " WHEN -> " + when);
                        if (!where.equals(""))
                            Utils.setPreferenceString(getActivity(), WHERE, where);
                        else
                            Utils.setPreferenceString(getActivity(), WHERE, "Qui");
                        if (!when.equals(""))
                            Utils.setPreferenceString(getActivity(), WHEN, when);
                        else
                            Utils.setPreferenceString(getActivity(), WHEN, "Oggi");
                        mListener.onSaveDoveQuandoDialog();
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onValueChange(parent, position);
    }
    
    private void onValueChange(View view, int position) {
        // salvo in variabili locali le scelte effettuate
        switch (view.getId()) {
            case R.id.where:
                where = cities[position];
                Log.d("XX", "LISTA WHERE -> oggetto : " + where);
                break;
            case R.id.when:
                Log.d("XX", "LISTA WHEN -> oggetto : " + days[position]);
                when = days[position];
                break;
            default:
                // inserire Qui -> oggi
                break;
        }
    }
}
