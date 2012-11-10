
package it.wm.perdue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;
import it.wm.android.adaptor.JSONListAdapter;
import it.wm.perdue.businessLogic.Notizia;

import java.io.Serializable;
import java.util.HashMap;

public class NewsFragment extends SherlockListFragment implements HTTPAccess.ResponseListener,
        OnScrollListener {
    private static final String    DEBUG_TAG   = "NewsFragment";
    private NotiziaJSONListAdapter adapter     = null;
    private String                 urlString   = null;
    private HTTPAccess             httpAccess  = null;
    private Parcelable             listState   = null;
    private int                    downloading = 0;
    private boolean                noMoreData  = false;
    private View                   footerView  = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NotiziaJSONListAdapter(
                getActivity(),
                R.layout.news_row,
                Notizia[].class);
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.endless_list_footer, null);
        lv.addFooterView(footerView);
        setListAdapter(adapter);
        lv.setOnScrollListener(this);
        setListShown(false);
        
        int nRows = 10;
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
            downloading++;
            Log.d(DEBUG_TAG, "Donwloading " + downloading);
        }
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
        /*
         * Toast.makeText( getActivity(),
         * getListView().getItemAtPosition(position).toString(),
         * Toast.LENGTH_SHORT).show();
         */
        Bundle extras = new Bundle();
        extras.putSerializable("notizia", (Serializable) l.getItemAtPosition(position));
        Intent intent = new Intent(getActivity(), NotiziaActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        int n = adapter.addFromJSON(response);
        if (n == 0) {
            noMoreData = true;
            getListView().removeFooterView(footerView);
        }
        setListShown(true);
        downloading--;
        Log.d(DEBUG_TAG, "Donwloading " + downloading);
    }
    
    @Override
    public void onHTTPerror(String tag) {
        Log.d(DEBUG_TAG, "Errore nel download");
        downloading--;
        Log.d(DEBUG_TAG, "Donwloading " + downloading);
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
        if (loadMore && downloading == 0 && !noMoreData) {
            Log.d(DEBUG_TAG, "Donwload from: " + adapter.getCount());
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("from", "" + adapter.getCount());
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
            downloading++;
            Log.d(DEBUG_TAG, "Donwloading " + downloading);
        }
    }
    
    /* *** END: AbsListView.OnScrollListener ****************** */
    
    private static class NotiziaJSONListAdapter extends JSONListAdapter<Notizia> {
        
        public NotiziaJSONListAdapter(Context context, int resource,
                Class<Notizia[]> clazz) {
            super(context, resource, clazz);
        }
        
        @SuppressWarnings("deprecation")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) super.getContext().getSystemService(
                        
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.news_row, null);
            }
            
            Notizia str = getItem(position);
            if (str != null) {
                TextView title = (TextView) v.findViewById(R.id.newsTitle);
                TextView date = (TextView) v.findViewById(R.id.newsDate);
                if (title != null) {
                    title.setText(str.getTitolo());
                }
                if (date != null) {
                    // SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    date.setText(str.getLocalizedDataString(false));
                }
            }
            
            return v;
        }
    }
    
}
