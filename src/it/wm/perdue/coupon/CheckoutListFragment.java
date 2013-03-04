package it.wm.perdue.coupon;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.R;

public class CheckoutListFragment extends SherlockListFragment implements
    HTTPAccess.ResponseListener {

    public static final Integer[] rows     = {
        R.layout.coupon_title_row,R.layout.checkout_row,R.layout.user_data_row,R.layout.user_data_row,R.layout.button_row
                                         };
    public CheckoutListFragment newInstance(String eseId) {
        CheckoutListFragment fragment = new CheckoutListFragment();        
        return fragment;
    }
    
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState){
//        super.onActivityCreated(savedInstanceState);
//        View view = inflater.inflate(android.R.layout.list_content, null);
//        ListView ls = (ListView) view.findViewById(android.R.id.list);
//        ls.setDividerHeight(5);
//    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListAdapter listAdapter = new BuyListAdapter(getActivity(), R.layout.coupon_title_row, rows);
        setListAdapter(listAdapter);
    }
    
    /*
     * HttpAccessListener
     * */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onHTTPerror(String tag) {
        // TODO Auto-generated method stub
        
    }
    /*
     * HttpAccessListener END
     * */
    
    private static class BuyListAdapter extends ArrayAdapter<Integer> {
        private Context   context    = null;
        private Integer[] rows     = null;
        private static TextView total = null;
        private static TextView price = null;

        public BuyListAdapter(Context context, int resourceId, Integer[] rows) {
            super(context, resourceId, rows);
            this.context = context;
            this.rows = rows;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            
            int viewType = getItemViewType(position);
            
            if (v == null) {
                v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(viewType, null);               
            }

            switch(position){
                case 0:
                    //setto il titolo
                    TextView title = (TextView)v.findViewById(R.id.coupon_title_row);
                    if(title != null){
                        
                    }
                    break;
                case 1:
                    //setto i dati di checkout
                    EditText amount = (EditText)v.findViewById(R.id.checkoutAmount);
                    price = (TextView)v.findViewById(R.id.checkoutPrice);
                    total = (TextView)v.findViewById(R.id.checkoutTotal);
                    if(price != null){
                        price.setText("20");
                    }
                    if(amount != null){
                        amount.addTextChangedListener(new TextWatcher() {
                            
                            public void afterTextChanged(Editable s) {
                            }
                          
                            public void beforeTextChanged(CharSequence s, int start, 
                              int count, int after) {
                            }
                          
                            public void onTextChanged(CharSequence s, int start, 
                              int before, int count) {
                                total.setText(calculateTotal(s)+"€");
                            }
                            
                            private int calculateTotal(CharSequence s){
                                int output = 0;
                                
                                try{
                                    int input = Integer.parseInt(s.toString());
                                    int pr = Integer.parseInt(price.getText().toString());
                                    output = input*pr;
                                }
                                catch(NumberFormatException e){
                                    Log.d("checkout","number format exception");
                                    output = 0;
                                }
                                return output;
                            }
                           });
                    }
                    break;
                case 2:
                    //setto la mail con la quale ci si è loggati
                    setUserData(v,"card");
                    break;
                case 3:
                    //setto numero carta di credito
                    setUserData(v,"mail");
                    break;
                case 4:
                   //setto compito del button
                    break;
                default: break;
            }
            
            return v;
        }
        
        @Override
        public int getItemViewType(int position) {
                //ritorno id della view
                return rows[position];
        }

        @Override
        public int getViewTypeCount() {
                return 5;
        }
        
        private void setUserData(View v,String type){
            TextView dataType = (TextView)v.findViewById(R.id.dataType);
            TextView data = (TextView)v.findViewById(R.id.data);
            
            if(type.equals("card")){
                if(dataType != null){
                    dataType.setText("Utente");
                }
                if(data != null){
                    data.setText("email di logging");
                }
            }
            else{
                if(dataType != null){
                    dataType.setText("Carta di credito");
                }
                if(data != null){
                    data.setText("numero della carta");
                }
            }
        }
/*
        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //TextView total = (TextView)v.findViewById(R.id.checkoutTotal);
            Log.d("check","testo imemsso = "+s);
        }*/
    }
}
