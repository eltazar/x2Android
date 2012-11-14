
package it.wm.perdue.test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;
import it.wm.perdue.R;
import it.wm.perdue.R.id;
import it.wm.perdue.R.layout;
import it.wm.perdue.businessLogic.Commento;

import java.util.ArrayList;
import java.util.HashMap;

public class TestActivity extends Activity {
    private static final String DEBUG_TAG  = "TestActivity";
    private HTTPAccess          httpAccess = null;
    private String              getText    = null;
    private String              postText   = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        
        httpAccess = new HTTPAccess();
        httpAccess.setResponseListener(new HTTPAccess.ResponseListener() {
            public void onHTTPResponseReceived(String tag, String response) {
                if (tag.equals("testGet")) {
                    getText = response;
                    parseJSON(getText);
                } else if (tag.equals("testPost")) {
                    postText = response;
                }
                setText();
            }
            
            public void onHTTPerror(String tag) {
                ((Button) findViewById(R.id.startBtn)).setText("[!]");
            }
        });
        
        ((Button) findViewById(R.id.startBtn)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = "http://www.cartaperdue.it/partner/commenti.php?id=119&from=0&to=10";
                httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, "testGet");
                
                urlString = "http://www.cartaperdue.it/partner/v2.0/News.php";
                HashMap<String, String> postMap = new HashMap<String, String>();
                postMap.put("from", "0");
                httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap,
                        "testPost");
                
                // urlString =
                // "http://multimedia.coldiretti.it/Manifestazione_Latte_Milano_marzo_07/Immagine%20046.jpg";
                urlString = "http://ubuntuforums.org/images/rebrand/ubuntulogo-o-small.png";
                ((CachedAsyncImageView) findViewById(R.id.headerImage))
                        .loadImageFromURL(urlString);
            }
        });
        
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    synchronized (this) {
                        this.wait(11000);
                    }
                } catch (InterruptedException e) {
                    Log.d("WAITER", "interrotto");
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(Void result) {
                String urlString =
                        "http://ubuntuforums.org/images/rebrand/ubuntulogo-o-small.png";
                ((CachedAsyncImageView) TestActivity.this.findViewById(R.id.headerImage))
                        .loadImageFromURL(urlString);
            }
        }/* .execute(new Void[1]) */;
        
    }
    
    @Override
    protected void finalize() throws Throwable {
        Log.d(DEBUG_TAG, "Help! I'm getting deallocated! :(");
        super.finalize();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("getText", getText);
        outState.putString("postText", postText);
        outState.putString("btnLabel", (String) ((Button) findViewById(R.id.startBtn)).getText());
        outState.putInt("scroll1", ((ScrollView) findViewById(R.id.scrollView1)).getScrollY());
        outState.putInt("scroll2", ((ScrollView) findViewById(R.id.scrollView2)).getScrollY());
        // TODO: cercare di capire se questo va qua
        httpAccess.setResponseListener(null);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getText = savedInstanceState.getString("getText");
        postText = savedInstanceState.getString("postText");
        ((Button) findViewById(R.id.startBtn)).setText(savedInstanceState.getString(
                "btnLabel"));
        // savedInstanceState.getInt("scroll1",
        // ((ScrollView)findViewById(R.id.scrollView1)).getScrollY());
        // savedInstanceState.getInt("scroll2",
        // ((ScrollView)findViewById(R.id.scrollView2)).getScrollY());
        setText();
    }
    
    private void setText() {
        StringBuilder builder = new StringBuilder();
        if (getText != null) {
            builder.append(getText);
        }
        if (getText != null && postText != null) {
            builder.append("**********************************************************");
        }
        if (postText != null) {
            builder.append(postText);
        }
        ((TextView) findViewById(R.id.textView1)).setText(builder.toString(),
                TextView.BufferType.NORMAL);
    }
    
    private void parseJSON(String jsonStr) {
        new AsyncTask<String, Void, JSONFormat>() {
            
            @Override
            protected JSONFormat doInBackground(String... params) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("yyyy-MM-dd kk:mm:ss");
                Gson gson = gsonBuilder.create();
                JSONFormat dataModel = gson.fromJson(params[0], JSONFormat.class);
                return dataModel;
            }
            
            protected void onPostExecute(JSONFormat result) {
                android.widget.TextView tv = ((android.widget.TextView) findViewById(R.id.textView2));
                StringBuilder b = new StringBuilder();
                
                for (int i = 0; i < result.getEsercente().size(); i++) {
                    Commento data = result.getEsercente().get(i);
                    b.append("\n****************\n");
                    b.append(data.getAutore());
                    b.append("\n===\n");
                    b.append(data.getTesto());
                    b.append("\n===\n");
                    b.append(data.getData());
                    b.append("\n===\n");
                    b.append("" + data.getId());
                }
                tv.setText(b.toString(), TextView.BufferType.NORMAL);
            }
            
        }.execute(jsonStr);
    }
    
    @SuppressWarnings("unused")
    private class JSONFormat {
        private ArrayList<Commento> Esercente;
        
        public void setEsercente(ArrayList<Commento> list) {
            Esercente = list;
        }
        
        public ArrayList<Commento> getEsercente() {
            return Esercente;
        }
        
    }
    
}
