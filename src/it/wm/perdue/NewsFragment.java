
package it.wm.perdue;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.android.adaptor.JSONListAdapter;
import it.wm.perdue.businessLogic.Notizia;

import java.util.HashMap;

public class NewsFragment extends SherlockListFragment implements HTTPAccess.ResponseListener,
        OnScrollListener {
    private static final String      DEBUG_TAG  = "NewsFragment";
    private JSONListAdapter<Notizia> adapter    = null;
    private String                   urlString  = null;
    private HTTPAccess               httpAccess = null;
    private Parcelable               listState  = null;
    private int                      nRows      = 10;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new JSONListAdapter<Notizia>(
                getActivity(),
                R.layout.news_row,
                R.id.newsTitle,
                Notizia[].class);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable("listState");
            nRows = savedInstanceState.getInt("nRows");
            if (nRows == 0)
                nRows = 10;
        }
        
        urlString = "http://www.cartaperdue.it/partner/v2.0/News.php";
        for (int i = 0; i < nRows / 10; i++) {
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("from", "" + i * 10);
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
        }
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lv.addFooterView(inflater.inflate(R.layout.endless_list_footer, null));
        setListAdapter(adapter);
        lv.setOnScrollListener(this);
        setListShown(false);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (listState != null) {
            getListView().onRestoreInstanceState(listState);
            listState = null;
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listState = getListView().onSaveInstanceState();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter.getCount() > 0) {
            outState.putInt("nRows", adapter.getCount());
        }
        if (listState != null) {
            outState.putParcelable("listState", listState);
        } else {
            outState.putParcelable("listState", getListView().onSaveInstanceState());
        }
        
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        Toast.makeText(
                getActivity(),
                getListView().getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
    }
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        adapter.addFromJSON(response);
        setListShown(true);
    }
    
    @Override
    public void onHTTPerror(String tag) {
        Log.d(DEBUG_TAG, "Errore nel download");
    }
    
    /* *** END: HTTPAccess.ResponseListener ******************* */
    
    /* *** BEGIN: AbsListView.OnScrollListener **************** */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
    
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        // Fonte: http://stackoverflow.com/questions/1080811/
        boolean loadMore =
                firstVisibleItem + visibleItemCount >= totalItemCount - visibleItemCount;
        if (loadMore) {
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("from", "" + adapter.getCount());
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
        }
    }
    /* *** END: AbsListView.OnScrollListener ****************** */
    
}
