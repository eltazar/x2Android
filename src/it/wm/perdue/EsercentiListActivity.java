
package it.wm.perdue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.android.adaptor.JSONListAdapter;
import it.wm.perdue.businessLogic.Esercente;

import java.util.HashMap;

public class EsercentiListActivity extends SherlockListActivity implements
        HTTPAccess.ResponseListener, OnScrollListener, OnQueryTextListener {
    
    private static final String      DEBUG_TAG   = "EsercentiListFragment";
    private EsercenteJSONListAdapter adapter     = null;
    private String                   urlString   = null;
    private HTTPAccess               httpAccess  = null;
    private Parcelable               listState   = null;
    private int                      downloading = 0;
    private boolean                  noMoreData  = false;
    private View                     footerView  = null;
    private String                   category    = "";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.esercenti_list);
        
        adapter = new EsercenteJSONListAdapter(
                this,
                R.layout.esercente_row,
                Esercente[].class);
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        
        ListView lv = getListView();
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.endless_list_footer, null);
        lv.addFooterView(footerView);
        
        lv.setOnScrollListener(this);
        // setListShown(false);
        
        int nRows = 10;
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable("listState");
            nRows = savedInstanceState.getInt("nRows");
            if (nRows == 0)
                nRows = 10;
        }
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            category = extras.getString("category");
        }
        
        urlString = "http://www.cartaperdue.it/partner/v2.0/EsercentiNonRistorazione.php";
        Log.d(DEBUG_TAG, "nrows " + nRows);
        for (int i = 0; i < nRows / 10; i++) {
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("from", "" + i * 10);
            postMap.put("request", "fetch");
            postMap.put("categ", category.toLowerCase());
            postMap.put("prov", "Qui");
            postMap.put("giorno", "Venerdi");
            postMap.put("lat", "37.332331");
            postMap.put("long", "-122.031219");
            postMap.put("ordina", "distanza");
            postMap.put("filtro", "");
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
            downloading++;
            Log.d(DEBUG_TAG, "ONCREATE Donwloading " + downloading);
        }
        
        setListAdapter(adapter);
        
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        bar.setTitle(category);
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
    
    /* *** BEGIN: OptionsMenu Methods **************** */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getSupportMenuInflater().inflate(R.menu.esercenti_menu, menu);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.abSearch)
                .getActionView();
        mSearchView.setOnQueryTextListener(this);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra(Intent.EXTRA_TEXT, MainActivity.DOVE_USARLA_TAB_TAG);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /* *** END: OptionsMenu Methods **************** */
    
    /* *** BEGIN: OnQueryTextListener Methods **************** */
    @Override
    public boolean onQueryTextSubmit(String query) {
        
        // TODO: dismettere la tastiera quando si preme "cerca" sulla tastiera
        
        // Hide keyboard
        // InputMethodManager imm = (InputMethodManager) this.getSystemService(
        // SherlockListActivity.INPUT_METHOD_SERVICE);
        // SearchView mSearchView = (SearchView) findViewById(R.id.abSearch);
        // imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
        // mSearchView.setFocusable(false);
        // mSearchView.setFocusableInTouchMode(false);
        return true;
    }
    
    @Override
    public boolean onQueryTextChange(String newText) {
        
        newText = newText.replace(" ", "-");
        // Log.d("*********", "stringa replicata = " + newText);
        
        // Log.d(DEBUG_TAG, "TEXT CHANGE query ");
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("from", "0");
        postMap.put("request", "search");
        postMap.put("categ", category.toLowerCase());
        postMap.put("lat", "37.332331");
        postMap.put("long", "-122.031219");
        postMap.put("ordina", "distanza");
        postMap.put("chiave", newText);
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
        
        return true;
    }
    
    /* *** END: OnQueryTextListener Methods **************** */
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        /*
         * Toast.makeText( getActivity(),
         * getListView().getItemAtPosition(position).toString(),
         * Toast.LENGTH_SHORT).show();
         */
        /*
         * Bundle extras = new Bundle(); extras.putSerializable("e",
         * (Serializable) l.getItemAtPosition(position)); Intent intent = new
         * Intent(getActivity(), NotiziaActivity.class);
         * intent.putExtras(extras); startActivity(intent);
         */
    }
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        
        // Log.d(DEBUG_TAG, "response " + response);
        
        // se il risultato  dovuto ad una query di ricerca cancella la lista
        try {
            if (response.substring(0, 20).equals("{\"Esercente:Search\":")) {
                adapter.clear();
            }
        } catch (StringIndexOutOfBoundsException ex) {
            Log.d("EXCEPTION", ex.getLocalizedMessage());
        }
        
        int n = adapter.addFromJSON(response);
        if (n == 0) {
            noMoreData = true;
            getListView().removeFooterView(footerView);
        }
        // setListShown(true);
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
        
        // Log.d(DEBUG_TAG, "load more = " + loadMore + " downloading = " +
        // downloading+ " noMoreData = " + noMoreData);
        
        if (loadMore && downloading == 0 && !noMoreData) {
            Log.d(DEBUG_TAG, "Donwload from: " + adapter.getCount());
            HashMap<String, String> postMap = new HashMap<String, String>();
            postMap.put("from", "" + adapter.getCount());
            postMap.put("request", "fetch");
            postMap.put("categ", category.toLowerCase());
            postMap.put("prov", "Qui");
            postMap.put("giorno", "Venerdi");
            postMap.put("lat", "37.332331");
            postMap.put("long", "-122.031219");
            postMap.put("ordina", "distanza");
            postMap.put("filtro", "");
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
            downloading++;
            Log.d(DEBUG_TAG, "Donwloading " + downloading);
        }
    }
    
    /* *** END: AbsListView.OnScrollListener ****************** */
    
    private static class EsercenteJSONListAdapter extends JSONListAdapter<Esercente> {
        
        public EsercenteJSONListAdapter(Context context, int resource,
                Class<Esercente[]> clazz) {
            super(context, resource, clazz);
            // TODO Auto-generated constructor stub
            // Log.d(DEBUG_TAG, "ESERCENTE JSON ADAPT CREATO ");
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            
            // Log.d(DEBUG_TAG, "GET VIEW ADAPTER ESERCENTI");
            
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) super.getContext().getSystemService(
                        
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.esercente_row, null);
            }
            
            Esercente str = getItem(position);
            // Log.d("DEBUG_TAG", "esercente.insegna = " + str.getInsegna() +
            // " esercente.indirizzo = " + str.getIndirizzo());
            if (str != null) {
                TextView title = (TextView) v.findViewById(R.id.eseTitle);
                // Log.d("DEBUG_TAG", "title textView = " + title);
                TextView address = (TextView) v.findViewById(R.id.address);
                
                CachedAsyncImageView caImageView = (CachedAsyncImageView) v
                        .findViewById(R.id.eseImage);
                
                String urlImageString = "http://www.cartaperdue.it/partner/v2.0/ImmagineEsercente.php?id="
                        + str.getId();
                
                if (caImageView != null) {
                    Log.d("DEBUG_TAG", "esercente id  = " + str.getId());
                    caImageView.loadImageFromURL(urlImageString);
                }
                
                if (title != null) {
                    title.setText(str.getInsegna());
                }
                if (address != null) {
                    // SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    address.setText(str.getIndirizzo());
                }
            }
            
            return v;
        }
    }
}
