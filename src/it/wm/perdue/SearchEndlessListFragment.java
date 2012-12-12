/**
 * 
 */
package it.wm.perdue;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import java.util.ArrayList;

/**
 * @author Gabriele "Whisky" Visconti
 *
 */
public abstract class SearchEndlessListFragment extends EndlessListFragment {
    private static final String     DEBUG_TAG         = "SearchEndlessListFragment";
    protected ListAdapter           searchAdapter     = null;
    private   int                   searchDownloading = 0;
    private   boolean               searchNoMoreData  = false;
    private   ArrayList<String>     searchDataToSave  = null;
    private   Boolean               inSearch          = false;
    private   String                query             = null;
    
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Boolean previousState = false;
        
        if (savedInstanceState != null) {
            searchDataToSave = savedInstanceState.getStringArrayList(Tags.SEARCH_DATA_TO_SAVE);
            searchNoMoreData = savedInstanceState.getBoolean(Tags.SEARCH_NO_MORE_DATA);
            previousState    = savedInstanceState.getBoolean(Tags.IN_SEARCH);
        }
        
        if (searchDataToSave == null) searchDataToSave = new ArrayList<String>();
        
        for (String data : searchDataToSave) {
            restoreData(data);
        }
        
        changeState(previousState);
    }
    
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(Tags.SEARCH_DATA_TO_SAVE, searchDataToSave);
        outState.putBoolean(Tags.SEARCH_NO_MORE_DATA, searchNoMoreData);
        outState.putBoolean(Tags.IN_SEARCH, inSearch);
    }
    
    /* *** BEGIN: AbsListView.OnScrollListener **************** */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
    
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (!inSearch) {
            super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        } else {
            boolean loadMore =
                    firstVisibleItem + visibleItemCount >= totalItemCount - visibleItemCount;
            if (loadMore && (searchDownloading == 0) && !searchNoMoreData) {
                    downloadSearchRows(adapter.getCount());
            }
        }
    }
    /* *** END: AbsListView.OnScrollListener ****************** */
    
    protected void notifySearchDataEnded() {
        searchNoMoreData = true;
        getListView().removeFooterView(footerView);
    }
    
    protected void notifySearchDownloadStarted() {
        searchDownloading++;
        Log.d(DEBUG_TAG, "Donwloading +: " + searchDownloading);
    }
    
    protected void notifySearchDownloadEnded() {
       searchDownloading--;
        Log.d(DEBUG_TAG, "Donwloading -: " + searchDownloading);
    }
    
    protected void saveSearchData(String data) {
        searchDataToSave.add(data);
    }
    
    protected void purgeSearchDataToSave() {
    	if (searchDataToSave != null)
    		// sul config change il metodo viene richiamato 
    		// PRIMA che il membro venga inizializzato
    		searchDataToSave.clear();
    }
    
    
    protected void setQuery(String query) {
        this.query = query; 
    }
    
    protected void changeState(Boolean inSearch) {
        if (this.inSearch == inSearch) return;
        
        this.inSearch = inSearch;
        
        if (inSearch) {
            setListAdapter(searchAdapter);
            if (searchNoMoreData) {
                setListShown(true);
                footerView.setVisibility(View.INVISIBLE);
            } else {
                footerView.setVisibility(View.VISIBLE);
                if (searchDataToSave.size() == 0) {
                    downloadSearchRows(0);
                    setListShown(false); 
                } else {
                    setListShown(true);
                }
            }
        } else {
            setListAdapter(adapter);
            super.setupFooterAndList();
        } 
        
        onStateChange(inSearch);
    }
    
    public Boolean isInSearchMode() {
    	return inSearch;
    }
    
    protected abstract void downloadSearchRows(int from);
    protected abstract void restoreSearchData(String data);
    protected abstract void onStateChange(Boolean inSearch);
    
    private static class Tags {
        public static final String SEARCH_DATA_TO_SAVE = "searchDataToSave";
        public static final String SEARCH_NO_MORE_DATA = "searchNoMoreData";
        public static final String IN_SEARCH           = "inSearch";
    }
   
}
