package it.wm.perdue;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import it.wm.HTTPAccess;
//import it.wm.JSONDecoder;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends Activity {
	private static final String DEBUG_TAG = "TestActivity";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        HTTPAccess httpAccess = HTTPAccess.getInstance();
        String urlString = "http://www.cartaperdue.it/partner/commenti.php?id=119&from=0&to=10";
        
        httpAccess.startHTTPConnection(
        		urlString, 
        		HTTPAccess.Method.GET, 
        		null, 
        		new HTTPAccess.ResponseListener() {
        	public void onHTTPResponseReceived(String response) {
        		((android.widget.TextView)TestActivity.this
        				.findViewById(R.id.textView1)).setText(response);
        		parseJSON(response);
        	}
        	public void onHTTPerror() {
        		((android.widget.TextView)TestActivity.this
        				.findViewById(R.id.textView1))
        				.setText("Arrangiati, Errore di Rete.");
        	}
        });
        

	}
	
	private void parseJSON(String jsonStr) {
		JSONObject obj = null;
		try {
			obj = new JSONObject(jsonStr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray arr = null;
		try {
			arr = obj.getJSONArray("Esercente");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<CommentDataModel> dataModel = new ArrayList<CommentDataModel>(arr.length());
		for (int i=0; i<arr.length(); i++) {
			Gson gson = new Gson();
			CommentDataModel data = null;
			try {
				data = gson.fromJson(arr.getJSONObject(i).toString(), CommentDataModel.class);
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataModel.add(data);
		}
		
		android.widget.TextView tv = ((android.widget.TextView)findViewById(R.id.textView1));
		StringBuilder b = new StringBuilder(tv.getText());
		
		for (int i=0; i<dataModel.size(); i++) {
			CommentDataModel data = dataModel.get(i);
			b.append("\n****************\n");
			b.append(data.comment_author);
			b.append("\n===\n");
			b.append(data.comment_content);
			b.append("\n===\n");
			b.append(data.comment_date);
			b.append("\n===\n");
			b.append(""+data.comment_ID);	
		}
		tv.setText(b.toString());
	}
	
	
	private class CommentDataModel {
		private String comment_author;
		private String comment_content;
		private String comment_date;
		private int comment_ID;
		
		public void   setComment_author(String s) {comment_author = s;}
		public String getComment_author() {return comment_author;}
		
		public void   setComment_content(String s) {comment_content = s;}
		public String getComment_content() {return comment_content;}
		
		public void   setComment_date(String s) {comment_date = s;}
		public String getComment_date() {return comment_date;}
		
		public void setComment_ID(int id) {comment_ID = id;}
		public int  getComment_ID() {return comment_ID;}	
	}

}
