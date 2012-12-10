/**
 * 
 */
package it.wm.perdue;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

import it.wm.HTTPAccess;

/**
 * @author Gabriele "Whisky" Visconti
 *
 */
public abstract class EndlessListFragment extends SherlockListFragment
        implements AbsListView.OnScrollListener {
    private static final String DEBUG_TAG   = "EndlessListFragment";
    protected LayoutInflater    inflater    = null;
    protected ListAdapter       adapter     = null;
    protected String            urlString   = null; 
    protected HTTPAccess        httpAccess  = null;
    private Parcelable          listState   = null;
    private int                 downloading = 0;
    private boolean             noMoreData  = false;
    private View                footerView  = null;
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();
        inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.endless_list_footer, null);
        lv.addFooterView(footerView);
        lv.setOnScrollListener(this);
        setListShown(false);
        
        int nRows = 10;
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable("listState");
            nRows = savedInstanceState.getInt("nRows");
            if (nRows == 0)
                nRows = 10;
        }
        
        downloadRows(0, nRows);
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
    public void onDestroy() {
        super.onDestroy();
        httpAccess.setResponseListener(null);
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
            /*Log.d(DEBUG_TAG, "Donwload from: " + adapter.getCount());
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("from", "" + adapter.getCount());
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
            downloading++;
            Log.d(DEBUG_TAG, "Donwloading " + downloading);*/
            downloadRows(adapter.getCount(), -1);
        }
    }
    
    /* *** END: AbsListView.OnScrollListener ****************** */
    
    protected void notifyDataEnded() {
        noMoreData = true;
        getListView().removeFooterView(footerView);
    }
    
    protected void notifyDownloadStarted() {
        downloading++;
        Log.d(DEBUG_TAG, "Donwloading " + downloading);
    }
    
    protected void notifyDownloadEnded() {
        downloading--;
        Log.d(DEBUG_TAG, "Donwloading " + downloading);
    }
    
    protected abstract void downloadRows(int from, int to);
}
