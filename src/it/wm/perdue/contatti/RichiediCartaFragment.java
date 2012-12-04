
package it.wm.perdue.contatti;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.R;

public class RichiediCartaFragment extends SherlockFragment implements
        HTTPAccess.ResponseListener {
    
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
    }
    
    // onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.richiedi, container, false);
        
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerCarta);
        // Create an ArrayAdapter using the string array and a default spinner
        // layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.carte_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        
        return view;
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        
    }
    
}
