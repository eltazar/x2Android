
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
import android.widget.Toast;

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
    
    // private TextView lastTouchedRowTextView = null;
    
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
            eseId = extras.getString(Tags.ID);
        }
        
        ListView lv = getListView();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = inflater.inflate(R.layout.endless_list_footer, null);
        lv.addFooterView(footerView);
        setListAdapter(adapter);
        lv.setOnScrollListener(this);
        setListShown(false);
        lv.setDividerHeight(2);
        
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
        httpAccess.setResponseListener(null);
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
        
        // TODO: quando si scrolla la textView viene riciclata e quindi altre
        // righe appaiono expanded anche se nn si � fatto click sopra
        
        TextView row = (TextView) v.findViewById(R.id.commentoDemo);
        // lastTouchedRowTextView = row;
        //Log.d("CCC", " TEXT VIEW pointer = " + row);
        
        row.setMaxLines(30);
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
        //Log.d(DEBUG_TAG, "Donwloading " + downloading);
        
        if (adapter.isEmpty()) {
            CharSequence text = "Spiacenti, nessun commento disponibile";
            Toast toast = Toast.makeText(getActivity(), text,
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    
    @Override
    public void onHTTPerror(String tag) {
       // Log.d(DEBUG_TAG, "Errore nel download");
        downloading--;
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
        if (loadMore && downloading == 0 && !noMoreData) {
          //  Log.d(DEBUG_TAG, "Donwload from: " + adapter.getCount());
            String urlString = String.format(urlStringFormat, eseId, adapter.getCount());
            httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, null);
            downloading++;
           // Log.d(DEBUG_TAG, "Donwloading " + downloading);
        }
        
        // try {
        // lastTouchedRowTextView.setMaxLines(2);
        // } catch (NullPointerException e) {
        // Log.d("CCC",
        // "puntatore nullo in onScrollView di CommentiListFragment");
        // }
        
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
            CommentViewHolder commentViewHolder = null;
            
            if (v == null) {
                v = ((LayoutInflater) super.getContext().getSystemService(
                        
                        Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.commento_row, null);
                commentViewHolder = new CommentViewHolder();
                commentViewHolder.author = (TextView) v.findViewById(R.id.autoreData);
                commentViewHolder.demoComment = (TextView) v.findViewById(R.id.commentoDemo);
                v.setTag(commentViewHolder);
            }
            else{
                commentViewHolder = (CommentViewHolder)v.getTag();
            }
            
            Commento comm = getItem(position);
            
            if (comm != null) {
                
                commentViewHolder.author.setText("Inviato da "
                        + (comm.getAutore().length() == 0 ? "Anonimo" : comm.getAutore()) + " il "
                        + comm.getData());
                commentViewHolder.demoComment.setText(comm.getTesto());
                
                if (comm.getAutore().length() == 0) {
                    commentViewHolder.author.setText("Inviato da Anonimo il " + comm.getData());
                }
                else {
                    commentViewHolder.author.setText("Inviato da " + comm.getAutore().replace("&amp;", "&") + " il "
                            + comm.getData());
                }
                commentViewHolder.demoComment.setText(comm.getTesto());
                
                commentViewHolder.demoComment.setMaxLines(2);
            }
            
            return v;
        }
    }
    
    static class CommentViewHolder{
        TextView author;
        TextView demoComment;
    }
    
    public static class Tags {
        public static final String ID = "id";
    }
    
}
