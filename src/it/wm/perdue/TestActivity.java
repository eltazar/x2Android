
package it.wm.perdue;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import it.wm.CachedAsyncImageView;
import it.wm.HTTPAccess;

import java.util.ArrayList;
import java.util.HashMap;

public class TestActivity extends Activity {
    private static final String DEBUG_TAG = "TestActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        HTTPAccess httpAccess = new HTTPAccess();
        String urlString = "http://www.cartaperdue.it/partner/commenti.php?id=119&from=0&to=10";
        Log.i(DEBUG_TAG, "Connecting to: " + urlString);
        httpAccess.setResponseListener(new HTTPAccess.ResponseListener() {
            public void onHTTPResponseReceived(String tag, String response) {
                if (tag.equals("testGet")) {
                    ((TextView) TestActivity.this
                            .findViewById(R.id.textView1)).setText(response + "["
                            + response.length() + "]");
                    parseJSON(response);
                } else if (tag.equals("testPost")) {
                    TextView t = (TextView) TestActivity.this.findViewById(R.id.textView1);
                    t.setText(t.getText() + "\n\n" + response + "\n\n");
                }
            }

            public void onHTTPerror(String tag) {
                ((TextView) TestActivity.this
                        .findViewById(R.id.textView1))
                        .setText("\n\nArrangiati, Errore di Rete.(" + tag + ")\n\n");
            }
        });
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.GET, null, "testGet");
        urlString = "http://www.cartaperdue.it/partner/v2.0/News.php";
        HashMap<String, String> postMap = new HashMap<String, String>();
        postMap.put("from", "0");
        httpAccess.startHTTPConnection(urlString, HTTPAccess.Method.POST, postMap, "testPost");
        urlString = "http://ubuntuforums.org/images/rebrand/ubuntulogo-o-small.png";
        ((CachedAsyncImageView) findViewById(R.id.cachedAsyncImageView1))
                .loadImageFromURL(urlString);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    synchronized (this) {
                        this.wait(10000);
                    }
                } catch (InterruptedException e) {
                    Log.v("WAITER", "interrotto");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                String urlString = "http://ubuntuforums.org/images/rebrand/ubuntulogo-o-small.png";
                ((CachedAsyncImageView) TestActivity.this.findViewById(R.id.cachedAsyncImageView1))
                        .loadImageFromURL(urlString);
            }
        }.execute(new Void[1]);
    }

    private void parseJSON(String jsonStr) {
        Gson gson = new Gson();
        JSONFormat dataModel = gson.fromJson(jsonStr, JSONFormat.class);

        android.widget.TextView tv = ((android.widget.TextView) findViewById(R.id.textView1));
        StringBuilder b = new StringBuilder(tv.getText());

        for (int i = 0; i < dataModel.getEsercente().size(); i++) {
            CommentDataModel data = dataModel.getEsercente().get(i);
            b.append("\n****************\n");
            b.append(data.comment_author);
            b.append("\n===\n");
            b.append(data.comment_content);
            b.append("\n===\n");
            b.append(data.comment_date);
            b.append("\n===\n");
            b.append("" + data.comment_ID);
        }
        tv.setText(b.toString());
    }

    @SuppressWarnings("unused")
    private class JSONFormat {
        private ArrayList<CommentDataModel> Esercente;

        public void setEsercente(ArrayList<CommentDataModel> list) {
            Esercente = list;
        }

        public ArrayList<CommentDataModel> getEsercente() {
            return Esercente;
        }

    }

    @SuppressWarnings("unused")
    private class CommentDataModel {
        private String comment_author;
        private String comment_content;
        private String comment_date;
        private int comment_ID;

        public void setComment_author(String s) {
            comment_author = s;
        }

        public String getComment_author() {
            return comment_author;
        }

        public void setComment_content(String s) {
            comment_content = s;
        }

        public String getComment_content() {
            return comment_content;
        }

        public void setComment_date(String s) {
            comment_date = s;
        }

        public String getComment_date() {
            return comment_date;
        }

        public void setComment_ID(int id) {
            comment_ID = id;
        }

        public int getComment_ID() {
            return comment_ID;
        }
    }

}
