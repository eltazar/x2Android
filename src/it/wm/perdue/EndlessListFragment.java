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

import java.util.ArrayList;

/**
 * @author Gabriele "Whisky" Visconti
 *
 */
public abstract class EndlessListFragment extends SherlockListFragment
        implements AbsListView.OnScrollListener {
    private static final String DEBUG_TAG   = "EndlessListFragment";
    protected LayoutInflater    inflater    = null;
    protected ListAdapter       adapter     = null;
    private Parcelable          listState   = null;
    private int                 downloading = 0;
    private boolean             noMoreData  = false;
    private View                footerView  = null;
    private ArrayList<String>   dataToSave  = null;
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();
        inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.endless_list_footer, null);
        lv.addFooterView(footerView);
        lv.setOnScrollListener(this);
        setListAdapter(adapter);
        
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable("listState");
            dataToSave = savedInstanceState.getStringArrayList("dataToSave");
            Log.d(DEBUG_TAG, "dataToSave è: " + dataToSave);
        }
        
        if (dataToSave == null) dataToSave = new ArrayList<String>();
        
        for (String data : dataToSave) {
            restoreData(data);
        }
        
        if (dataToSave.size() == 0) {
            downloadRows(0);
            setListShown(false);
        } else {
            setListShown(true);
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
        outState.putStringArrayList("dataToSave", dataToSave);
        
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
            downloadRows(adapter.getCount());
        }
    }
    
    /* *** END: AbsListView.OnScrollListener ****************** */
    
    protected void notifyDataEnded() {
        noMoreData = true;
        getListView().removeFooterView(footerView);
    }
    
    protected void notifyDownloadStarted() {
        downloading++;
        Log.d(DEBUG_TAG, "Donwloading +: " + downloading);
    }
    
    protected void notifyDownloadEnded() {
        downloading--;
        Log.d(DEBUG_TAG, "Donwloading -: " + downloading);
    }
    
    protected abstract void downloadRows(int from);
    protected abstract void restoreData(String data);
    
    protected void saveData(String data) {
        dataToSave.add(data);
    }
}
