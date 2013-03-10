package it.wm.perdue.coupon;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.wm.perdue.R;
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
    
    @Override
    public int getCount() {
        //Log.d("XXX", "COUNT = " + sections.size());
        return sections.size();
    }
    
    public void addFromJSON(String jsonString) {
        jsonString = Utils.formatJSONwithoutBool(jsonString);
        jsonString = jsonString.substring(1, jsonString.length() - 1);
        //Log.d("TIMER",jsonString);
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
        sections.add("title");
        sections.add("details");
        sections.add("ese");
        sections.add("others"); 
        sections.add("tel");
        sections.add("mail");
        sections.add("faq");        
    }
    
    @Override
    public void clear() {
        super.clear();
        sections.clear();
    }
    
    public int getItemViewType(int position){
        
        int resource = 0;
        
        if(position == 0){
            resource = R.layout.coupon_title_row;
        }
        else if(position == 1){
            resource = R.layout.coupon_detail_row;
        }
        else if(position == 2){
            resource = R.layout.coupon_ese_row;
        }
        else if(position == 3){
            resource = R.layout.coupon_options_row;
        }
        else if(position == 4 || position == 5 || position == 6){
            resource = R.layout.contact_row;
        }
        return resource;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        
    } 
    
    public T getObject(){
        return coupon;
    }
}