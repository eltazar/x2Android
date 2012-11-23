
package it.wm.perdue.doveusarla;

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
import it.wm.perdue.JSONListAdapter;
import it.wm.perdue.R;
import it.wm.perdue.businessLogic.Esercente;

import java.util.HashMap;

public class EsercentiListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener, OnScrollListener {
    private static final String                    DEBUG_TAG        = "EsercentiListFragment";
    private static final int                       STATE_NORMAL     = 1;
    private static final int                       STATE_SEARCH     = 2;
    private static final String                    TAG_NORMAL       = "normal";
    private static final String                    TAG_SEARCH       = "search";
    private static final String                    TAG_SEARCH_MORE  = "searchmore";
    private int                                    state            = 0;
    protected String                               category         = "";
    protected String                               sorting          = "";
    // Gestione dei download:
    private HTTPAccess                             httpAccess       = null;
    protected String                               urlString        = null;
    protected HashMap<String, String>              postMap          = null;
    // private int downloading = 0;
    private boolean                                noMoreData       = false;
    private boolean                                searchNoMoreData = false;
    private View                                   footerView       = null;
    // Gestione dello stato della lista:
    protected JSONListAdapter<? extends Esercente> adapter          = null;
    protected JSONListAdapter<? extends Esercente> searchAdapter    = null;
    private Parcelable                             listState        = null;
    
    // problema: perchè al primo avvio di una categoria stampa due volte il log
    // SORTING ? :|
    // perchè
    
    public static EsercentiListFragment newInstance(String sort, String categ) {
        EsercentiListFragment fragment = new EsercentiListFragment(sort.toLowerCase(),
                categ.toLowerCase());
        Log.d(DEBUG_TAG, "NEW INSTANCE --> SORTING = " + fragment.sorting + " category = "
                + fragment.category);
        return fragment;
    }
    
    public EsercentiListFragment(String sorting, String category) {
        this.state = STATE_NORMAL;
        this.sorting = sorting;
        this.category = category;
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        urlString = "http://www.cartaperdue.it/partner/v2.0/EsercentiNonRistorazione.php";
        postMap = new HashMap<String, String>();
        postMap.put("request", "fetch");
        postMap.put("categ", category.toLowerCase());
        postMap.put("prov", "Qui");
        postMap.put("giorno", "Venerdi");
        postMap.put("lat", "41.801007");
        postMap.put("long", "12.454273");
        postMap.put("ordina", sorting);
        Log.d(DEBUG_TAG, "Ctor: ordina =" + "[" + postMap.get("ordina") + "]");
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new EsercenteJSONListAdapter(
                getActivity(),
                R.layout.esercente_row,
                Esercente[].class,
                sorting);
        searchAdapter = new EsercenteJSONListAdapter(
                getActivity(),
                R.layout.esercente_row,
                Esercente[].class,
                sorting);
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
        
        Log.d(DEBUG_TAG, "nrows " + nRows);
        for (int i = 0; i < nRows / 10; i++) {
            postMap.put("from", "" + i * 10);
            postMap.put("request", "fetch");
            if (TAG_NORMAL == null)
                throw new RuntimeException();
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, TAG_NORMAL);
            // downloading++;
            // Log.d(DEBUG_TAG,
            // "onActivityCreated: Donwloading " + downloading + "[" +
            // postMap.get("ordina")
            // + "]");
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
        Log.d(DEBUG_TAG, "on destroy view");
        // listState = getListView().onSaveInstanceState();
    }
    
    protected void setDataForQuery(String data) {
        Log.d(DEBUG_TAG, "setDataForQuery = " + data);
        if (data.equals("")) {
            state = STATE_NORMAL;
            setListAdapter(adapter);
            footerView.setVisibility(noMoreData ? View.INVISIBLE : View.VISIBLE);
        } else {
            state = STATE_SEARCH;
            searchAdapter.clear();
            setListAdapter(searchAdapter);
            searchNoMoreData = false;
            footerView.setVisibility(searchNoMoreData ? View.INVISIBLE : View.VISIBLE);
            
            data = data.replace(" ", "-");
            postMap.put("from", "0");
            postMap.put("request", "search");
            postMap.put("chiave", data);
            if (TAG_SEARCH == null)
                throw new RuntimeException();
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, TAG_SEARCH);
        }
    }
    
    protected void setCategory(String category) {
        this.category = category;
    }
    
    /*
     * protected void clearSearchingResults() { adapter.clear(); HashMap<String,
     * String> postMap = new HashMap<String, String>(); postMap.put("from",
     * "0"); postMap.put("request", "fetch"); postMap.put("categ",
     * category.toLowerCase()); postMap.put("prov", "Qui");
     * postMap.put("giorno", "Venerdi"); postMap.put("lat", "41.801007");
     * postMap.put("long", "12.454273"); postMap.put("ordina", sorting);
     * postMap.put("filtro", ""); httpAccess.startHTTPConnection(urlString,
     * HTTPAccess.Method.POST, postMap, null); downloading++; }
     */
    
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
        // downloading--;
        // Log.d(DEBUG_TAG, "Donwloading " + downloading);
        int n;
        if (tag.equals(TAG_NORMAL)) {
            // Se riceviamo un risultato non di ricerca, lo aggiungiamo sempre e
            // comunque:
            n = adapter.addFromJSON(response);
            if (n == 0) {
                noMoreData = true;
                footerView.setVisibility(View.INVISIBLE);
            }
        } else {
            /*
             * Se invece riceviamo un risultato di ricerca, lo aggiungiamo solo
             * se siamo in modalità di ricerca, altrimenti è tempo perso: al
             * prossimo rientro in search mode l'adapter verrà svuotato.
             */
            if (state != STATE_SEARCH) {
                return;
            }
            n = searchAdapter.addFromJSON(response);
            if (n == 0) {
                noMoreData = true;
                footerView.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    @Override
    public void onHTTPerror(String tag) {
        Log.d(DEBUG_TAG, "Errore nel download");
        // downloading--;
        // Log.d(DEBUG_TAG, "Donwloading " + downloading);
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
        String tag = null;
        boolean noMoreData = false;
        switch (state) {
            case STATE_NORMAL:
                tag = TAG_NORMAL;
                noMoreData = this.noMoreData;
                break;
            
            case STATE_SEARCH:
                tag = TAG_SEARCH_MORE;
                noMoreData = this.searchNoMoreData;
                break;
        }
        if (tag == null)
            throw new RuntimeException();
        
        if (loadMore /* && downloading == 0 */&& !noMoreData) {
            Log.d(DEBUG_TAG, "Donwload from: " + adapter.getCount());
            postMap.put("from", "" + adapter.getCount());
            postMap.put("request", "fetch");
            postMap.put("filtro", "");
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, tag);
            // downloading++;
            // Log.d(DEBUG_TAG, "onScroll: Donwloading " + downloading + "[" +
            // postMap.get("ordina")
            // + "]");
        }
    }
    
    /* *** END: AbsListView.OnScrollListener ****************** */
    
    private static class EsercenteJSONListAdapter extends JSONListAdapter<Esercente> {
        private String sorting = null;
        
        public EsercenteJSONListAdapter(Context context, int resource,
                Class<Esercente[]> clazz, String sorting) {
            super(context, resource, clazz);
            this.sorting = sorting;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            
            Log.d("+++++++++++++", " GET VIEW DI ESE LIST");
            
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) super.getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.esercente_row, null);
            }
            
            Esercente esercente = getItem(position);
            if (esercente != null) {
                TextView title = (TextView) v.findViewById(R.id.eseTitle);
                TextView address = (TextView) v.findViewById(R.id.address);
                TextView distance = (TextView) v.findViewById(R.id.distance);
                CachedAsyncImageView caImageView = (CachedAsyncImageView) v
                        .findViewById(R.id.eseImage);
                
                String urlImageString = "http://www.cartaperdue.it/partner/v2.0/ImmagineEsercente.php?id="
                        + esercente.getID();
                
                if (caImageView != null) {
                    Log.d("DEBUG_TAG", "esercente id  = " + esercente.getID());
                    // caImageView.loadImageFromURL(urlImageString);
                }
                
                if (title != null) {
                    Log.d(DEBUG_TAG, "Sorting è: " + sorting);
                    title.setText(esercente.getInsegna());
                }
                if (address != null) {
                    address.setText(esercente.getIndirizzo());
                }
                if (distance != null) {
                    distance.setText(String.format("%.3f km", esercente.getDistanza()));
                }
            }
            return v;
        }
    }
}
