
package it.wm.perdue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import it.wm.HTTPAccess;
import it.wm.perdue.businessLogic.Notizia;

import java.io.Serializable;
import java.util.HashMap;

public class NewsListFragment extends EndlessListFragment 
            implements HTTPAccess.ResponseListener {
    private static final String     DEBUG_TAG  = "NewsFragment";
    private String                  urlString  = null;
    private HashMap<String, String> postMap    = null;
    private HTTPAccess              httpAccess = null;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        adapter = new NewsJSONListAdapter(
                getActivity(),
                R.layout.news_row,
                Notizia[].class);
        super.onCreate(savedInstanceState); 
        // Super. onCreate utilizza l'adapter in caso di config change, 
        // quindi va richiamato dopo averlo inizializzato
        urlString = "http://www.cartaperdue.it/partner/v2.0/News.php";
        postMap = new HashMap<String, String>();
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
        extras.putSerializable("notizia", (Serializable)(l.getItemAtPosition(position)));
        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }
    
    
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        
        try{
            int n = ((NewsJSONListAdapter)adapter).addFromJSON(response);
        
        if (n == 0) {
            notifyDataEnded();
        }
        setListShown(true);
        notifyDownloadEnded();
        saveData(response);
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
        postMap.put("from", "" + from);
        Boolean downloadStarted = httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
        if (downloadStarted) notifyDownloadStarted();
    }
    
    @Override
    protected void restoreData(String data) {
        ((NewsJSONListAdapter)adapter).addFromJSON(data);
    }
    
    
   
    private static class NewsJSONListAdapter extends JSONListAdapter<Notizia> {
        
        public NewsJSONListAdapter(Context context, int resource,  Class<Notizia[]> clazz) {
            super(context, resource, clazz);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = inflater.inflate(R.layout.news_row, null);
            }
            
            Notizia str = getItem(position);
            if (str != null) {
                TextView title = (TextView) v.findViewById(R.id.newsTitle);
                TextView date = (TextView) v.findViewById(R.id.newsDate);
                if (title != null) {
                    title.setText(str.getTitolo());
                }
                if (date != null) {
                    date.setText(str.getLocalizedDataString(false));
                }
            }
            
            return v;
        }
    }
}
