package it.wm.perdue.coupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.CreditCard;
import it.wm.perdue.businessLogic.LoginData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CheckoutListFragment extends SherlockListFragment implements
    HTTPAccess.ResponseListener{

    public static final Integer[] rowKinds     = {
        R.layout.coupon_title_row,R.layout.checkout_row,R.layout.user_data_row,R.layout.button_row
                                         };
    
    private Map<String,Object> dataModel =  null;
        
    public void createDataModel(int userId, String... couponInfo){
        dataModel.put("userId", userId);
        //idCoupon, titolo, prezzo, credit cards, loginData
        dataModel.put("couponInfo", (new ArrayList<String>(Arrays.asList(couponInfo))));  
        dataModel.put("loginData", Utils.getSavedLoginData());
        //Log.d("check","data model = "+dataModel);
    }
    
    public void insertIntoDataModel(Object o){
        //per aggiungere qlc
    }
    
    public CheckoutListFragment newInstance(String eseId) {
        CheckoutListFragment fragment = new CheckoutListFragment();        
        return fragment;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ListView listView = getListView();
        listView.setDividerHeight(2);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataModel =  new HashMap<String,Object>();
        createDataModel(1234,"0000","vacanza in montagna","120.54");
        ListAdapter listAdapter = new BuyListAdapter(getActivity(), R.layout.coupon_title_row, rowKinds, dataModel);
        setListAdapter(listAdapter);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == 0 && resultCode == SherlockActivity.RESULT_OK && data != null){
          Log.d("check","CheckoutList: ricevuto carta salvata -> "+((CreditCard)data.getExtras().get("creditCard")).toString());
          //ho ricevuto la carta di credito creata, la salvo nel model
          dataModel.put("creditCard", data.getExtras().get("creditCard"));
          //aggiorno la listView
          ((BaseAdapter) getListView().getAdapter()).notifyDataSetChanged();
      }
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(position == 3){
            //riga carta di credito
            Intent intent = new Intent(getSherlockActivity(),CreditCardActivity.class);
            //TODO: se già esiste mandare la carta di credito precedente
            startActivityForResult( intent, 0);
        }
    }
    
    /*
     * HttpAccessListener
     * */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        
    }

    @Override
    public void onHTTPerror(String tag) {
        
    }
    /*
     * HttpAccessListener END
     * */
    
    private static class BuyListAdapter extends ArrayAdapter<Integer> {
        private Context   context    = null;
        private Integer[] rows     = null;
        private static TextView total = null;
        private static TextView price = null;
        private Map<String,Object> dataModel = null;

        public BuyListAdapter(Context context, int resourceId, Integer[] rows, Map<String,Object> dataModel) {
            super(context, resourceId, rows);
            this.context = context;
            this.rows = rows;
            this.dataModel = dataModel;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
       
            Log.d("check","get view");
            View v = convertView;
            
            int viewType = 0;         
            
            switch(position){
                case 0:
                    viewType = rows[0];
                    break;
                case 1:
                    viewType = rows[1];
                    break;
                case 2:
                    viewType = rows[2]; 
                    break;
                case 3:
                    viewType = rows[2]; 
                    break;
                case 4:
                    viewType = rows[3]; 
                    break;
                default:
                    break;
            }
            
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
                    setCheckoutView(v);
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
        public int getCount() {
            //numero di tipi di riga + 1 dato che 2 righe sono dello stesso tipo
            return rows.length+1;
        }

        private void setUserData(View v,String type){
            TextView dataType = (TextView)v.findViewById(R.id.dataType);
            TextView data = (TextView)v.findViewById(R.id.data);
            
            if(type.equals("card")){
                if(dataType != null){
                    dataType.setText("Utente");
                }
                if(data != null){
                    data.setText(((LoginData) dataModel.get("loginData")).getEmail());
                }
            }
            else{
                if(dataType != null){
                    dataType.setText("Carta di credito");
                }
                if(data != null){
                    //qui recuperare oggetto carta di credito e stampare numero
                    CreditCard c = (CreditCard) dataModel.get("creditCard");
                    if(c != null && c.getNumber() != null)
                        data.setText(c.getNumber());
                    else data.setText("Inserisci carta di credito");
                }
            }
        }
        
        @SuppressWarnings("unchecked")
        private void setCheckoutView(View v){
            EditText amount = (EditText)v.findViewById(R.id.checkoutAmount);
            price = (TextView)v.findViewById(R.id.checkoutPrice);
            total = (TextView)v.findViewById(R.id.checkoutTotal);
            
            if(price != null){
                ArrayList<String> couponInfo = (ArrayList<String>) dataModel.get("couponInfo");
                Log.d("check","array = "+couponInfo);
                price.setText(couponInfo.get(2)+"€");
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
                    
                    private double calculateTotal(CharSequence s){
                        double output = 0.0;
                        
                        try{
                            double input = Double.parseDouble(s.toString());
                            double pr = Double.parseDouble(price.getText().toString().replace("€", ""));
                            output = input*pr;
                        }
                        catch(NumberFormatException e){
                            Log.d("checkout","number format exception");
                            output = 0.0;
                        }
                        return output;
                    }
                });
            }
        }
    }
}
