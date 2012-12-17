/**
 * 
 */
package it.wm.perdue;

import android.os.Bundle;
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
    private   Boolean				previousInSearch  = false;
    private   String                query             = null;
    
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (savedInstanceState != null) {
            searchDataToSave = savedInstanceState.getStringArrayList(Tags.SEARCH_DATA_TO_SAVE);
            searchNoMoreData = savedInstanceState.getBoolean(Tags.SEARCH_NO_MORE_DATA);
            previousInSearch = savedInstanceState.getBoolean(Tags.IN_SEARCH);
        }
        
        if (searchDataToSave == null) searchDataToSave = new ArrayList<String>();
        
        if (previousInSearch) for (String data : searchDataToSave) {
            restoreSearchData(data);
        }
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	if (previousInSearch)
    		setSearchMode();
    	else 
    		setNormalMode();
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
                //Log.d(DEBUG_TAG, "onScroll Downloading from: " + searchAdapter.getCount());
                downloadSearchRows(searchAdapter.getCount());
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
        //Log.d(DEBUG_TAG, "Donwloading +: " + searchDownloading);
    }
    
    protected void notifySearchDownloadEnded() {
       searchDownloading--;
        //Log.d(DEBUG_TAG, "Donwloading -: " + searchDownloading);
    }
    
    protected void saveSearchData(String data) {
        searchDataToSave.add(data);
    }
    
    protected void resetSearchData() {
    	searchDataToSave.clear();
    	searchDownloading = 0;
    	searchNoMoreData = false;
    }
    
    
    protected void setQuery(String query) {
        this.query = query; 
    }
    
    protected void setNormalMode() {
    	if (!inSearch) return;
    	inSearch = false;
    	setListAdapter(adapter);
        super.setupFooterAndList();
        onStateChange(inSearch);
    }
    
    protected void setSearchMode() {
    	resetSearchData();
        if (!inSearch) {
        	inSearch = true;
            setListAdapter(searchAdapter);
            footerView.setVisibility(View.VISIBLE);
            setListShown(false);   
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
