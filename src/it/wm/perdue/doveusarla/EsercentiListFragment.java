
package it.wm.perdue.doveusarla;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.perdue.JSONListAdapter;
import it.wm.perdue.R;
import it.wm.perdue.SearchEndlessListFragment;
import it.wm.perdue.Utils;
import it.wm.perdue.businessLogic.Esercente;
import it.wm.perdue.dettaglioEsercenti.DettaglioEsercenteBaseActivity;

import java.util.Map.Entry;
import java.util.TreeMap;



public class EsercentiListFragment extends SearchEndlessListFragment
		implements HTTPAccess.ResponseListener  {
    private static String				    DEBUG_TAG        = "EsercentiListFragment";
    private   Boolean						activityCreated  = false;
    private   Boolean						areWeRestoring   = false;
    protected String                        category         = "";
    protected String                        sorting          = "";
    protected String                        filter           = "";	
    private   String                        searchKey        = "";
    protected static double                 latitude         = 0.0;
    protected static double                 longitude        = 0.0;
    // Gestione dei download:
    protected static final int              PHP_ARRAY_LENGTH = 20;  
    protected HTTPAccess                    httpAccess       = null;
    protected String                        urlString        = null;
    protected TreeMap<String, String>       postMap          = null;
    protected TreeMap<String, String>       searchPostMap	 = null;
    
    

        
    public static EsercentiListFragment newInstance(String sort, String categ) {
        EsercentiListFragment fragment = new EsercentiListFragment();
        Bundle args = new Bundle();
        args.putString(Tags.ARGS_CATEGORY, categ.toLowerCase());
        args.putString(Tags.ARGS_SORTING, sort.toLowerCase());
        fragment.setArguments(args);
        Log.d(DEBUG_TAG, "NEW INSTANCE --> SORTING = " + fragment.sorting + " category = "
                + fragment.category);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	onCreateAdapters();
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
        	areWeRestoring = true;
        	searchKey = savedInstanceState.getString(Tags.SEARCH_KEY);
        	filter    = savedInstanceState.getString(Tags.FILTER);
        	latitude  = savedInstanceState.getDouble(Tags.LATITUDE);
        	longitude = savedInstanceState.getDouble(Tags.LONGITUDE);
        }
        
        Bundle args = getArguments();
        category = args.getString(Tags.ARGS_CATEGORY);
        sorting  = args.getString(Tags.ARGS_SORTING);
        DEBUG_TAG = sorting + DEBUG_TAG;
        
        urlString = "http://www.cartaperdue.it/partner/v2.0/EsercentiNonRistorazione.php";
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        postMap = new TreeMap<String, String>();
        
              
        postMap.put("request", 	"fetch");
        postMap.put("categ", 	category.toLowerCase());
        postMap.put("prov", 	Utils.getPreferenceString("where", "Qui"));
        postMap.put("giorno", 	Utils.getWeekDay());
        postMap.put("lat",      "" + latitude);
        postMap.put("long",     "" + longitude);
        postMap.put("filtro", 	filter);
        postMap.put("ordina", 	sorting);
        searchPostMap = new TreeMap<String, String>();
        searchPostMap.put("request", "search");
        searchPostMap.put("categ", 	 category.toLowerCase());
        searchPostMap.put("ordina",  sorting);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	activityCreated = true;
    }
        
    @Override    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "ON CREATED VIEW --> SORTING = " + sorting + " category = " + category);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putString(Tags.SEARCH_KEY, searchKey);
    	outState.putString(Tags.FILTER, filter);
    	outState.putDouble(Tags.LATITUDE, latitude);
    	outState.putDouble(Tags.LONGITUDE, longitude);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        httpAccess.setResponseListener(null);
    }
    
    
    protected void onCreateAdapters() {
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
    
    @SuppressWarnings("rawtypes")
    protected void setDataForQuery(String data) {
    	String newSearchKey = data.replace(" ", "-");
        Log.d(DEBUG_TAG, "setDataForQuery = " + data);
        if (data.equals("")) {
            setNormalMode();
        } else {
        	if (activityCreated && !newSearchKey.equals(searchKey)) {
        		searchKey = newSearchKey;
        		if (!areWeRestoring) {
        			((JSONListAdapter)searchAdapter).clear();
        			setSearchMode();
        			downloadSearchRows(0);
        			areWeRestoring = false;
        		}
        	}
        }
    }
    
    protected void setCategory(String category) {
        this.category = category;
    }
    
    
    
    /* *** BEGIN: HTTPAccess.ResponseListener ****************** */
    @SuppressWarnings("rawtypes")
    @Override
    public void onHTTPResponseReceived(String tag, String response) {
        int n;
        if (tag.equals(Tags.TAG_NORMAL + mapToTag(postMap))) {
            // Se riceviamo un risultato non di ricerca, lo aggiungiamo sempre e
            // comunque:
            Log.d("eee"," JSON = "+response);
            n = ((JSONListAdapter)adapter).addFromJSON(response);
            if (n < PHP_ARRAY_LENGTH) {
                notifyDataEnded();
            }
            setListShown(true);
            saveData(response);
            notifyDownloadEnded();
        } else {
            /*
             * Se invece riceviamo un risultato di ricerca, lo aggiungiamo solo
             * se siamo in modalità di ricerca, altrimenti è tempo perso: al
             * prossimo rientro in search mode l'adapter verrà svuotato.
             * Inoltre aggiungiamo i risultati solo se sono della ricerca
             * corrente scartando quelli di ricerche vecchie. TODO: le
             * connessioni delle ricerche vecchie andrebbero proprio fermate
             */
            if (!isInSearchMode() || !tag.equals(Tags.TAG_SEARCH + mapToTag(searchPostMap))) {
            	// Ricerca vecchia. Non facciamo niente.
            	Log.d(DEBUG_TAG, "Scarto i risultati della ricerca: " + tag
            			+ " inSearchMode: " + isInSearchMode() 
            			+ "searchKey: "     + searchKey );
                return;
            }
            n = ((JSONListAdapter)searchAdapter).addFromJSON(response);
            if (n < PHP_ARRAY_LENGTH) {
                notifySearchDataEnded();
            }
            setListShown(true);
            saveSearchData(response);
            notifySearchDownloadEnded();
        }
    }
    
    @Override
    public void onHTTPerror(String tag) {
        Log.d(DEBUG_TAG, "Errore nel download");
        if (tag.equals(Tags.TAG_SEARCH + searchKey))
        	notifySearchDownloadEnded();
        else if (tag.equals(Tags.TAG_NORMAL))
        	notifyDownloadEnded();
        else {
        	// Ricerca vecchia fallita. Non facciamo niente.
        }
    }
    /* *** END: HTTPAccess.ResponseListener ******************* */
    
    
    public void onListItemClick(ListView l, View v, int position, long id) {
        Esercente ese = (Esercente) l.getAdapter().getItem(position);
        Log.d("XXX", "cliccato item = " + ese.getID());
        
        Intent intent = new Intent(getActivity(), DettaglioEsercenteBaseActivity.class);
        intent.putExtra(DettaglioEsercenteBaseActivity.Tags.ID,       "" + ese.getID());
        intent.putExtra(DettaglioEsercenteBaseActivity.Tags.TITLE,    ese.getInsegna());
        intent.putExtra(DettaglioEsercenteBaseActivity.Tags.IS_RISTO, false);
        startActivity(intent);
    }
    
    @SuppressWarnings("rawtypes")
    public void onChangeWhereWhenFilter() {
//        if (postMap == null) {
//            //Toast.makeText(Utils.context, "Zompo", Toast.LENGTH_SHORT).show();
//            return;
//        }
        //Toast.makeText(Utils.context, "Non Zompo", Toast.LENGTH_SHORT).show();
        postMap.put("prov",   Utils.getPreferenceString("where", "Qui"));
        postMap.put("giorno", Utils.getWeekDay());
        postMap.put("filtro", this.filter);
        ((JSONListAdapter)adapter).clear();
        resetData();
        downloadRows(0);
	}
    

    @Override
	protected void downloadRows(int from) {
    	postMap.put("from", "" + from);
        Boolean downloadStarted = httpAccess.startHTTPConnection(
                urlString,
                HTTPAccess.Method.POST,
                postMap,
                Tags.TAG_NORMAL + mapToTag(postMap));
        if (downloadStarted) notifyDownloadStarted();
	}
    
    @Override
	protected void downloadSearchRows(int from) {
        searchPostMap.put("from", "" + from);
        searchPostMap.put("chiave", searchKey);
        Boolean downloadStarted = httpAccess.startHTTPConnection(
                urlString,
                HTTPAccess.Method.POST,
                searchPostMap,
                Tags.TAG_SEARCH + mapToTag(searchPostMap));
        if (downloadStarted) notifySearchDownloadStarted();
	}
    
    private static String mapToTag(TreeMap<String, String> map) {  
        StringBuilder tag = new StringBuilder();
        Entry<String, String> e = map.firstEntry();
        tag.append("{");
        while (e != null) {
            String key = e.getKey();
            if (!key.equals("from")) { 
                tag.append(key);
                tag.append("=");
                tag.append(e.getValue());
                tag.append("&");
            }
            e = map.higherEntry(key);
        }
        tag.append("}");
        return tag.toString();
    }
    

	@SuppressWarnings("rawtypes")
    @Override
	protected void restoreSearchData(String data) {
		((JSONListAdapter)searchAdapter).addFromJSON(data);		
	}
	
	@SuppressWarnings("rawtypes")
    @Override
	protected void restoreData(String data) {
		((JSONListAdapter)adapter).addFromJSON(data);
	}

	@Override
	protected void onStateChange(Boolean inSearch) {
		// TODO Auto-generated method stub
	}

	public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
	
    
    private static class EsercenteJSONListAdapter extends JSONListAdapter<Esercente> {
    	//private static final String DEBUG_TAG = "EsercenteJSONListAdapter";
        
        public EsercenteJSONListAdapter(Context context, int resource,
                Class<Esercente[]> clazz, String sorting) {
            super(context, resource, clazz);
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = inflater.inflate(R.layout.esercente_row, null);
            }
            
            Esercente esercente = getItem(position);
            if (esercente != null) {
                TextView title 		= (TextView) v.findViewById(R.id.eseTitle);
                TextView address 	= (TextView) v.findViewById(R.id.address);
                TextView distance 	= (TextView) v.findViewById(R.id.distance);
                CachedAsyncImageView caImageView = 
                		(CachedAsyncImageView) v.findViewById(R.id.eseImage);
                
                String urlImageString = "http://www.cartaperdue.it/partner/v2.0/ImmagineEsercente.php?id="
                        + esercente.getID();
                
                if (caImageView != null) {
                    //Log.d(DEBUG_TAG, "esercente id  = " + esercente.getID());
                    caImageView.loadScaledImageFromURL(urlImageString);
                }
                
                if (title != null) {
                    //Log.d(DEBUG_TAG, "Sorting è: " + sorting);
                    title.setText(esercente.getInsegna());
                }
                if (address != null) {
                    address.setText(esercente.getIndirizzo());
                }
                if (distance != null) {
                    if(latitude != 0 && longitude != 0)
                        distance.setText(String.format("a %.1f km", esercente.getDistanza()));
                    else distance.setText(String.format("a -- km", esercente.getDistanza()));
                }
            }
            return v;
        }
    }
    
    public static class Tags {
        protected static final String TAG_NORMAL       = "normal";
        private   static final String TAG_SEARCH       = "search";
        private   static final String SEARCH_KEY 	   = "searchKey";
        private   static final String FILTER		   = "filter";
        private   static final String LATITUDE         = "latitude";
        private   static final String LONGITUDE        = "longitude";
        public    static final String ARGS_CATEGORY    = "category";
        public    static final String ARGS_SORTING     = "sorting";
    }
}
