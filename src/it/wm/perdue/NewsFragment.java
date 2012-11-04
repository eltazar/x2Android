
package it.wm.perdue;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.android.adaptor.JSONListAdapter;
import it.wm.perdue.businessLogic.Notizia;

import java.util.HashMap;

public class NewsFragment extends SherlockListFragment implements HTTPAccess.ResponseListener {
    private static final String      DEBUG_TAG  = "NewsFragment";
    private JSONListAdapter<Notizia> adapter    = null;
    private HTTPAccess               httpAccess = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new JSONListAdapter<Notizia>(
                getActivity(),
                R.layout.news_row,
                R.id.newsTitle,
                Notizia[].class);
        setListAdapter(adapter);
        
        httpAccess = new HTTPAccess();
        String urlString = "http://www.cartaperdue.it/partner/v2.0/News.php";
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("from", "0");
        httpAccess.setResponseListener(this);
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        Toast.makeText(
                getActivity(),
                getListView().getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        adapter.addFromJSON(response);
    }
    
    @Override
    public void onHTTPerror(String tag) {
        Log.d(DEBUG_TAG, "Errore nel download");
    }
}
