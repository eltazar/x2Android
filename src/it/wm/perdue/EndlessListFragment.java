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
    protected LayoutInflater        inflater    = null;
    protected ListAdapter           adapter     = null;
    private   Parcelable            listState   = null;
    private   int                   downloading = 0;
    private   boolean               noMoreData  = false;
    protected View                  footerView  = null;
    private   ArrayList<String>     dataToSave  = null;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	footerView = inflater.inflate(R.layout.endless_list_footer, null);
    	
    	if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(Tags.LIST_STATE);
            dataToSave = savedInstanceState.getStringArrayList(Tags.DATA_TO_SAVE);
            noMoreData = savedInstanceState.getBoolean(Tags.NO_MORE_DATA);
            Log.d(DEBUG_TAG, "dataToSave è: " + dataToSave);
        }
        
        if (dataToSave == null) dataToSave = new ArrayList<String>();
        
        for (String data : dataToSave) {
            restoreData(data);
        }
    }
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();
        lv.addFooterView(footerView, null, false);
        lv.setOnScrollListener(this);
        setListAdapter(adapter);
        setupFooterAndList();
    }
    
    protected void setupFooterAndList() {
        if (noMoreData) {
            setListShown(true);
            footerView.setVisibility(View.INVISIBLE);
        } else {
            footerView.setVisibility(View.VISIBLE);
            if (dataToSave.size() == 0) {
                downloadRows(0);
                setListShown(false); 
            } else {
                setListShown(true);
            }
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
        outState.putStringArrayList(Tags.DATA_TO_SAVE, dataToSave);
        outState.putBoolean(Tags.NO_MORE_DATA, noMoreData);
        
        if (listState != null) {
            outState.putParcelable(Tags.LIST_STATE, listState);
        } else {
            outState.putParcelable(Tags.LIST_STATE, getListView().onSaveInstanceState());
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
        if (loadMore && (downloading == 0) && !noMoreData) {
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
    
    protected void saveData(String data) {
        dataToSave.add(data);
    }
    
    protected abstract void downloadRows(int from);
    protected abstract void restoreData(String data);
    
    private static class Tags {
        public static final String LIST_STATE   = "listState";
        public static final String DATA_TO_SAVE = "dataToSave";
        public static final String NO_MORE_DATA = "noMoreData";
    }
}
