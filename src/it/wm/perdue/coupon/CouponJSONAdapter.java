package it.wm.perdue.coupon;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Coupon;

import java.util.ArrayList;
public class CouponJSONAdapter <T extends Coupon> extends
        ArrayAdapter<T> implements OnClickListener {
    Class<T>          clazz     = null;
    ArrayList<String> sections  = null;
    Context           context   = null;
    T                 coupon = null;
    protected LayoutInflater inflater = null;
    
    public CouponJSONAdapter(Context context, int resource, Class<T> clazz) {
        super(context, resource);
        this.clazz = clazz;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sections = new ArrayList<String>();
    }
    
    public View getView(int position, View v, ViewGroup parent) {
       
        return null;  
    }
    
    @Override
    public int getCount() {
        //Log.d("XXX", "COUNT = " + sections.size());
        return 0;
    }
    
    public void addFromJSON(String jsonString) {
        jsonString = Utils.formatJSONwithoutBool(jsonString);
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        Log.d("TIMER",jsonString);
        Gson gson = Utils.getGson();
        try {
            this.coupon = gson.fromJson(jsonString, clazz);
            super.add(this.coupon);
            checkFields();
            
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }
    
    protected void checkFields() {
        
        Log.d("TIMER","counpon = "+coupon.getID());
                
    }
    
    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        
    } 
}