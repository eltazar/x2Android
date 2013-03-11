package it.wm.perdue.coupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.perdue.EndlessListFragment;
import it.wm.perdue.JSONListAdapter;
import it.wm.perdue.R;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Coupon;

public class CouponsListFragment extends EndlessListFragment 
implements HTTPAccess.ResponseListener {
    private static final String     DEBUG_TAG  = "CouponListFragment";
    private String                  urlString  = null;
    private HTTPAccess              httpAccess = null;
    protected static final int      PHP_ARRAY_LENGTH = 20;  
    private static final String   COUPON_CITY  = "couponCity";

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        adapter = new CouponsJSONListAdapter(
                getActivity(),
                R.layout.coupon_row,
                Coupon[].class);
        super.onCreate(savedInstanceState); 
        // Super. onCreate utilizza l'adapter in caso di config change, 
        // quindi va richiamato dopo averlo inizializzato
        urlString = "http://www.cartaperdue.it/partner/android/listaCoupon.php?prov="+Utils.getPreferenceString(COUPON_CITY, "Roma")+"&from=0";
        Log.d("coupon","url _---> "+urlString);
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        httpAccess.setResponseListener(null);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Bundle extras = new Bundle();
        extras.putInt("couponId",((Coupon)l.getAdapter().getItem(position)).getID() );
        Intent intent = new Intent(getActivity(), DetailCouponBaseActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }
    
    
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        
        try{
            int n = ((CouponsJSONListAdapter)adapter).addFromJSON(response);
            Log.d("coupon","n = "+n);
            if (n < PHP_ARRAY_LENGTH) {
                notifyDataEnded();
            }
            setListShown(true);
            saveData(response);
            notifyDownloadEnded();
        }
        catch(NullPointerException e){
            Log.d(DEBUG_TAG,"Eccezione in list fragment ---> "+e.getLocalizedMessage());
        }
    }
    
    @Override
    public void onHTTPerror(String tag) {
        Log.d(DEBUG_TAG, "Errore nel download");
        notifyDownloadEnded();
    }
    /* *** END: HTTPAccess.ResponseListener ******************* */
    
    
    
    @Override
    protected void downloadRows(int from) { 
        urlString = "http://www.cartaperdue.it/partner/android/listaCoupon.php?prov="+Utils.getPreferenceString(COUPON_CITY, "Roma")+"&from="+from;
        Boolean downloadStarted = httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, null);
        if (downloadStarted)
            notifyDownloadStarted();
    }
    
    @Override
    protected void restoreData(String data) {
        ((CouponsJSONListAdapter)adapter).addFromJSON(data);
    }
    
    public void onChangeWhereFilter(){
        ((JSONListAdapter)adapter).clear();
        resetData();
        downloadRows(0);
    }
    
    
    private static class CouponsJSONListAdapter extends JSONListAdapter<Coupon> {
        
        public CouponsJSONListAdapter(Context context, int resource,  Class<Coupon[]> clazz) {
            super(context, resource, clazz);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            CouponViewHolder viewHolder;
            View v = convertView;
            
            if (v == null) {
                v = inflater.inflate(R.layout.coupon_row, null);
                viewHolder = new CouponViewHolder();
                viewHolder.caImageView = (CachedAsyncImageView) v.findViewById(R.id.couponImage);
                viewHolder.title = (TextView) v.findViewById(R.id.couponShortTitle);
                viewHolder.price = (TextView) v.findViewById(R.id.couponShortPrice);
     
                v.setTag(viewHolder);
            }
            else {
                viewHolder = (CouponViewHolder) v.getTag();
            }
            
            Coupon c = getItem(position);
            if (c != null) {
                String urlImageString = "http://www.cartaperdue.it/coupon/img_offerte/"
                        + c.getUrlImmagine();

                if (viewHolder.caImageView != null) {
                    viewHolder.caImageView.loadScaledImageFromURL(urlImageString);
                }
                
                if (viewHolder.title != null) {
                    viewHolder.title.setText(c.getTitoloBreve());
                }
                if (viewHolder.price != null) {
                    viewHolder.price.setText("Solo "+Utils.formatPrice(c.getValoreAcquisto())+"€"+
                            " invece di "+Utils.formatPrice(c.getValoreFacciale())+"€");
                }
            }
            
            return v;
        }
        
        static class CouponViewHolder {
            TextView title;
            TextView price;
            CachedAsyncImageView caImageView;
        }
    }
}