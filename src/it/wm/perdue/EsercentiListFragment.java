
package it.wm.perdue;

import android.content.Context;
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

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.android.adaptor.JSONListAdapter;
import it.wm.perdue.businessLogic.Esercente;

import java.util.HashMap;

public class EsercentiListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener, OnScrollListener {
    
    private static final String      DEBUG_TAG   = "EsercentiListFragment";
    private EsercenteJSONListAdapter adapter     = null;
    private String                   urlString   = null;
    private HTTPAccess               httpAccess  = null;
    private Parcelable               listState   = null;
    private int                      downloading = 0;
    private boolean                  noMoreData  = false;
    private View                     footerView  = null;
    private String                   category    = "";
    private String                   sorting     = "";
    
    // problema: perchè al primo avvio di una categoria stampa due volte il log
    // SORTING ? :|
    // perchè
    
    public static EsercentiListFragment newInstance(String sort, String categ) {
        EsercentiListFragment fragment = new EsercentiListFragment();
        fragment.sorting = sort.toLowerCase();
        fragment.category = categ.toLowerCase();
        Log.d(DEBUG_TAG, "NEW INSTANCE --> SORTING = " + fragment.sorting + " category = "
                + fragment.category);
        return fragment;
    }
    
    public EsercentiListFragment() {
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        adapter = new EsercenteJSONListAdapter(
                getActivity(),
                R.layout.esercente_row,
                Esercente[].class, sorting);
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
        
        int nRows = 10;
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable("listState");
            nRows = savedInstanceState.getInt("nRows");
            if (nRows == 0)
                nRows = 10;
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
            postMap.put("lat", "41.801007");
            postMap.put("long", "12.454273");
            postMap.put("ordina", sorting);
            postMap.put("filtro", "");
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST,
                    postMap, null);
            downloading++;
            Log.d(DEBUG_TAG, "ONCREATE Donwloading " + downloading);
        }
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "ON CREATED VIEW --> SORTING = " + sorting + " category = " + category);
        return super.onCreateView(inflater, container, savedInstanceState);
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
    
    protected void setDataForQuery(String data) {
        Log.d("FRAGMENT", " DATI RICEVUTI = " + data);
        
        data = data.replace(" ", "-");
        
        // Log.d(DEBUG_TAG, "TEXT CHANGE query ");
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("from", "0");
        postMap.put("request", "search");
        postMap.put("categ", category.toLowerCase());
        postMap.put("lat", "41.801007");
        postMap.put("long", "12.454273");
        postMap.put("ordina", sorting);
        postMap.put("chiave", data);
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
    }
    
    protected void setCategory(String category) {
        this.category = category;
    }
    
    protected void clearSearchingResults() {
        adapter.clear();
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("from", "0");
        postMap.put("request", "fetch");
        postMap.put("categ", category.toLowerCase());
        postMap.put("prov", "Qui");
        postMap.put("giorno", "Venerdi");
        postMap.put("lat", "41.801007");
        postMap.put("long", "12.454273");
        postMap.put("ordina", sorting);
        postMap.put("filtro", "");
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST,
                postMap, null);
        downloading++;
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
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        
        // Log.d(DEBUG_TAG, "response " + response);
        
        // se il risultato è dovuto ad una query di ricerca cancella la lista
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
            postMap.put("lat", "41.801007");
            postMap.put("long", "12.454273");
            postMap.put("ordina", sorting);
            postMap.put("filtro", "");
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, null);
            downloading++;
            Log.d(DEBUG_TAG, "Donwloading " + downloading);
        }
    }
    
    /* *** END: AbsListView.OnScrollListener ****************** */
    
    private static class EsercenteJSONListAdapter extends JSONListAdapter<Esercente> {
        private String sorting = null;
        
        public EsercenteJSONListAdapter(Context context, int resource,
                Class<Esercente[]> clazz, String sorting) {
            super(context, resource, clazz);
            // TODO Auto-generated constructor stub
            // Log.d(DEBUG_TAG, "ESERCENTE JSON ADAPT CREATO ");
            this.sorting = sorting;
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
                    // caImageView.loadImageFromURL(urlImageString);
                }
                
                if (title != null) {
                    Log.d(DEBUG_TAG, "Sorting è: " + sorting);
                    if (sorting.equals("distanza")) {
                        title.setText("[" + str.getDistanza() + "] " + str.getInsegna());
                    } else {
                        title.setText(str.getInsegna());
                    }
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
