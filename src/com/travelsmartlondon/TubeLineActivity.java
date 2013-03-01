package com.travelsmartlondon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class TubeLineActivity extends ListActivity {

	private ListView _mainListView;
	
	public static final String PICCADILLY_LINE = "Piccadilly";
	public static final String DISTRICT_LINE = "District";
	public static final String VICTORIA_LINE = "Victoria";
	public static final String CIRCLE_LINE = "Circle";
	public static final String HAMMERSMITH_CITY_LINE = "Hammersmith and City";
	public static final String BAKERLOO_LINE = "Bakerloo";
	public static final String WATERLOO_CITY_LINE = "Waterloo and City";
	public static final String CENTRAL_LINE = "Central";
	public static final String JUBILEE_LINE = "Jubilee";
	public static final String METROPOLITAN_LINE = "Metropolitan";
	public static final String NORTHERN_LINE = "Northern";
	public static final String DLR_LINE = "DLR";
	public static final String OVERGROUND_LINE = "Overground";
	
	public static final URI LINES_TSL_URI = URI.create("http://stud-tfl.cs.ucl.ac.uk/lines");
	
	public static final String MAP_LINE = "Line";
	public static final String MAP_STATUS = "Status";
	
	private List<Map<String,String>> _list;
	SimpleAdapter _adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tube_line);
	    
		this._list = new ArrayList<Map<String,String>>();
		this._adapter = new SimpleAdapter(this,
        								this._list,
        								R.layout.custom_tube_line_view,
        								new String[] {MAP_LINE,MAP_STATUS},
        								new int[] {R.id.line_name_text, R.id.line_status_text}
										)
		/*{ 
			@Override
			public View getView(int position_, View convertView_, ViewGroup parent_) {
				
				return super.getView(position_, convertView_, parent_);
			}
		}*/;
		getLineStatusUpdates();
	}
	
	private Map<String, String> getMapFor(String line_, String status_) {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put(MAP_LINE, line_);
		map.put(MAP_STATUS, status_);
		return map;
	}
	
	private void getLineStatusUpdates() {

		class HttpGetAsyncTask extends AsyncTask<String, Void, Void>{
			
			@Override
			protected Void doInBackground(String... params) {

				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(LINES_TSL_URI);
				
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);

				    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				    String json = reader.readLine();
				    JSONObject jsonObject = new JSONObject(json);
				    
				    for(int i = 0; i < jsonObject.length() - 1; i++){
				    	String index = Integer.toString(i);
				    	JSONObject temp = (JSONObject) jsonObject.get(index);
				    	String line = temp.getString("lineName");
				    	String status = temp.getString("statusDescription");
				    	_list.add(getMapFor(line, status));
				    };
				    

				} catch (ClientProtocolException cpe) {
					System.out.println("Client Protocol Exception :" + cpe);
					cpe.printStackTrace();
				} catch (IOException ioe) {
					System.out.println("IOException :" + ioe);
					ioe.printStackTrace();
				} catch (NullPointerException npe) {
					System.out.println(" Null Pointer Exception :" + npe);
					npe.printStackTrace();
				} catch (JSONException jsone) {
					System.out.println("JSON Exception :" + jsone);
					jsone.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
		    	setListAdapter(_adapter);
			}			
		}

		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tube_line, menu);
		return true;
	}

}
