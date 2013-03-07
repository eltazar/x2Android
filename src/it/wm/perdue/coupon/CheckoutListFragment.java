package it.wm.perdue.coupon;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.perdue.LoggingHandler;
import it.wm.perdue.R;
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
        
    /*DATA MODEL KEYS
     * logindData -> dati di login dell'utente
     * couponInfo -> temporaneo, info di base del coupon
     * creditCard -> la carta di credito
     * amount -> il numero di coupon da acquistare
     * totalPrice -> prezzo totale del carrello
     **/
    
    public void createDataModel(int userId, String... couponInfo){
        //dataModel.put("userId", userId);
        //idCoupon, titolo, prezzo, credit cards, loginData
        dataModel.put("couponInfo", (new ArrayList<String>(Arrays.asList(couponInfo))));  
        dataModel.put("loginData", LoggingHandler.getSavedLoginData());
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
        //ricostruisco dataModel
        if(savedInstanceState != null){
            dataModel.put("couponInfo", savedInstanceState.getStringArrayList("coupon"));
            dataModel.put("creditCard",savedInstanceState.getParcelable("card"));
            dataModel.put("loginData", LoggingHandler.getSavedLoginData());
            dataModel.put("amount", savedInstanceState.getString("amount"));
            dataModel.put("totalPrice", savedInstanceState.getString("totalPrice"));
        }
        else{
            //creo model con dati in input del fragment
            createDataModel(1234,"0000","vacanza in montagna","120.54");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onSaveInstanceState(Bundle outState) {        
        //salvo info coupon
        outState.putStringArrayList("coupon", (ArrayList<String>) dataModel.get("couponInfo"));
        //salvo carta di credito
        outState.putParcelable("card", (Parcelable) dataModel.get("creditCard"));
        //login lo recupero dalle preferenza
        //salvo amount e totalPrice
        outState.putString("amount", (String) dataModel.get("amount"));
        outState.putString("totalPrice",  (String) dataModel.get("totalPrice"));        
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataModel =  new HashMap<String,Object>();
        ListAdapter listAdapter = new BuyListAdapter(getActivity(), R.layout.coupon_title_row, rowKinds, dataModel);
        setListAdapter(listAdapter);
        urlString = "https://cartaperdue.it/partner/acquistoCoupon.php";
        postMap = new TreeMap<String,String>();
    }
    
    @Override
    public void onDestroy(){
        super.onDestroy();
        //distruggo carta di credito ogni volta che esco, e il data model in generale
        dataModel.put("creditCard",null);
        dataModel.put("loginData", null); //superfluo? ---> in teoria vengono riscritti alla creazione del fragment
        dataModel.put("couponInfo", null);//superfluo?
        dataModel.put("amount", null);
        dataModel.put("totalPrice", null);
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
        else{
            Intent i = new Intent(getActivity(), CouponsBaseActivity.class);
            startActivity(i);
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
    
    protected static void launchBuyAlert(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Conferma");
        builder.setMessage("Confermare l'acquisto?");
        builder.setPositiveButton("Conferma", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("checkout","acquisto confermato");
            }
        });
        builder.setNegativeButton("Annulla", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("checkout","acquisto annullato");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    private static class BuyListAdapter extends ArrayAdapter<Integer> {
        private Context   context    = null;
        private Integer[] rows     = null;
        private static TextView total = null;
        private static TextView price = null;
        private Map<String,Object> dataModel = null;
        private int amountItems = 0;
        
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
                    setButtonProperties(v);
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

        private void setButtonProperties(View v){
            Button buyButton = (Button) v.findViewById(R.id.buyButton);
            buyButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) { 
                    Log.d("checkout","E' stato premuto il bottone buy");
                       
                        String result = "ok";
                        CreditCard creditCard = (CreditCard) dataModel.get("creditCard");
                        if(amountItems <= 0){
                            result = "Devi inserire almeno un coupon";                
                        }                       
                        if(creditCard == null || !creditCard.isComplete()){
                            result = "Inserisci i dati della carta di credito";
                        }
                        
                        if(result.equals("ok")){
                            launchBuyAlert(context);
                        }
                        else{
                            AlertDialog.Builder builder = new Builder(context);
                            builder.setTitle("Attenzione");
                            builder.setMessage(result);
                            builder.setNegativeButton("Ok", new OnClickListener() {
                                
                                @Override
                                public void onClick(DialogInterface dialog, int which) {                                            
                                }
                            });
                            builder.create().show();    
                        }
                }
            });
        }
        
        private void setUserData(View v,String type){
            TextView dataType = (TextView)v.findViewById(R.id.dataType);
            TextView data = (TextView)v.findViewById(R.id.data);
            
            if(type.equals("card")){
                if(dataType != null){
                    dataType.setText("Utente");
                }
                if(data != null){
                    //recupera i dati dal model relativi al login, se presenti
                    data.setText(((LoginData) dataModel.get("loginData")).getEmail());
                }
            }
            else{
                if(dataType != null){
                    dataType.setText("Carta di credito");
                }
                if(data != null){
                    // recupera i dati dal model relativi alla carta di credito, se presenti
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
            
            //setto se presente il valore precedente del model
            if(price != null){
                ArrayList<String> couponInfo = (ArrayList<String>) dataModel.get("couponInfo");
                Log.d("check","array = "+couponInfo);
                price.setText(couponInfo.get(2)+"€");
            }
            if(total != null && dataModel.get("totalPrice")!= null){
                total.setText(dataModel.get("totalPrice")+"€");
            }
            /**/
            
            if(amount != null){
                
                if(dataModel.get("amount")!=null){
                    //setto se presente il valore precedente del model
                    amount.setText(dataModel.get("amount")+"");
                }
                
                amount.addTextChangedListener(new TextWatcher() {
                    
                    public void afterTextChanged(Editable s) {
                    }
                    
                    public void beforeTextChanged(CharSequence s, int start, 
                            int count, int after) {
                    }
                    
                    public void onTextChanged(CharSequence s, int start, 
                            int before, int count) {
                        try{
                            amountItems = Integer.parseInt(s.toString());
                        }
                        catch(NumberFormatException e){
                            amountItems = 0;
                        }
                        //aggiorno dataModel con amount convertito in stringa
                        dataModel.put("amount", amountItems+"");
                        total.setText(String.format("%.2f", calculateTotal())+"€");
                    }
                    
                    private double calculateTotal(){
                        double output = 0.0;
                        
                        try{
                            double pr = Double.parseDouble(price.getText().toString().replace("€", ""));
                            output = amountItems*pr;
                        }
                        catch(NumberFormatException e){
                            Log.d("checkout","number format exception");
                            output = 0.0;
                        }
                        //aggiorno dataModel con prezzo totale convertito in stringa
                        dataModel.put("totalPrice", output+"");
                        return output;
                    }
                });
            }
        }
    }
}
