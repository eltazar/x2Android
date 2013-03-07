package it.wm.perdue;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
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

public class DoveDialog extends SherlockDialogFragment implements OnItemClickListener {
    
    public interface ChangeDoveDialogListener {
        void onSaveDoveDialog();
    }
    
    private String[]                       cities = null;
    
    private ChangeDoveDialogListener mListener;
    private String                         where  = "";
    private static final String            COUPON_CITY  = "couponCity";
    
    @TargetApi(11)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        cities = getResources().getStringArray(R.array.cities_coupon);
        
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
            listWhere.setDividerHeight(0);
            listWhere.setBackgroundColor(Color.WHITE);
            ListView listWhen = (ListView) view.findViewById(R.id.when);
            listWhen.setVisibility(View.GONE);
        } else {
            final NumberPicker pickerWhere = (NumberPicker) view.findViewById(R.id.where);
            pickerWhere.setMinValue(0);
            pickerWhere.setMaxValue(cities.length - 1);
            pickerWhere.setDisplayedValues(cities);
            pickerWhere.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    DoveDialog.this.onValueChange(pickerWhere, newVal);
                }
            });
            pickerWhere.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            final NumberPicker pickerWhen = (NumberPicker) view.findViewById(R.id.when);
            pickerWhen.setVisibility(View.GONE);

        }
        
        builder.setTitle("Città")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // salvo le impostazioni selezionate se la selezione non
                        // è vuota
                        Log.d("uu", " ### WHERE ->  : " + where);
                        if (!where.equals(""))
                            Utils.setPreferenceString(COUPON_CITY, where);
                        else
                            Utils.setPreferenceString(COUPON_CITY, "Roma");
                        mListener.onSaveDoveDialog();
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
            mListener = (ChangeDoveDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ChangeDoveDialogListener");
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
                break;
            default:
                // inserire Qui -> oggi
                where = "Roma";
                break;
        }
    }
}
