
package it.wm.perdue.dettaglioEsercenti;

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

import it.wm.HTTPAccess;
import it.wm.perdue.JSONListAdapter;
import it.wm.perdue.R;
import it.wm.perdue.businessLogic.Commento;

public class CommentiListFragment extends SherlockListFragment implements
        HTTPAccess.ResponseListener,
        OnScrollListener {
    private static final String DEBUG_TAG       = "NewsFragment";
    private CommentiJSONAdapter adapter         = null;
    private String              urlStringFormat = null;
    private HTTPAccess          httpAccess      = null;
    private Parcelable          listState       = null;
    private int                 downloading     = 0;
    private boolean             noMoreData      = false;
    private View                footerView      = null;
    private String              eseId;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CommentiJSONAdapter(
                getActivity(),
                R.layout.commento_row,
                Commento[].class);
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(this);
        
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        Bundle extras = getArguments();
        if (extras != null) {
            eseId = extras.getString("eseId");
        }
        
        ListView lv = getListView();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.endless_list_footer, null);
        lv.addFooterView(footerView);
        setListAdapter(adapter);
        lv.setOnScrollListener(this);
        setListShown(false);
        
        urlStringFormat = "http://www.cartaperdue.it/partner/commenti.php?id=%s&from=%d&to=10";
        
        int nRows = 10;
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable("listState");
            nRows = savedInstanceState.getInt("nRows");
            if (nRows == 0)
                nRows = 10;
        }
        String urlString = String.format(urlStringFormat, eseId, 0);
        
        for (int i = 0; i < nRows / 10; i++) {
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, null);
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
        // Bundle extras = new Bundle();
        // extras.putSerializable("notizia", (Serializable)
        // l.getItemAtPosition(position));
        // Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        // intent.putExtras(extras);
        // startActivity(intent);
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
            String urlString = String.format(urlStringFormat, eseId, adapter.getCount());
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, null);
            downloading++;
            Log.d(DEBUG_TAG, "Donwloading " + downloading);
        }
    }
    
    /* *** END: AbsListView.OnScrollListener ****************** */
    
    private static class CommentiJSONAdapter extends JSONListAdapter<Commento> {
        
        public CommentiJSONAdapter(Context context, int resource,
                Class<Commento[]> clazz) {
            super(context, resource, clazz);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            View v = convertView;
            if (v == null) {
                v = ((LayoutInflater) super.getContext().getSystemService(
                        
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.commento_row, null);
            }
            
            Commento comm = getItem(position);
            if (comm != null) {
                
                TextView author = (TextView) v.findViewById(R.id.autoreData);
                TextView demoComment = (TextView) v.findViewById(R.id.commentoDemo);
                
                author.setText("Inviato da "
                        + (comm.getAutore().length() == 0 ? "Anonimo" : comm.getAutore()) + " il "
                        + (comm.getData()));
                demoComment.setText(comm.getTesto());
            }
            
            /*
             * titolo.text = [commento objectForKey:@"comment_content"];
             * NSString *dateString = [Utilita dateStringFromMySQLDate:[commento
             * objectForKey:@"comment_date"]]; if ([[commento
             * objectForKey:@"comment_author"] length] == 0 ) { data.text =
             * [NSString stringWithFormat:@"Inviato da Anonimo %@ il %@",
             * [commento objectForKey:@"comment_author"], dateString]; } else {
             * data.text = [NSString stringWithFormat:@"Inviato da %@ il %@",
             * [commento objectForKey:@"comment_author"], dateString]; data.text
             * = [data.text stringByReplacingOccurrencesOfString:@"&amp;"
             * withString:@"&"]; }
             */
            
            return v;
        }
    }
    
}
