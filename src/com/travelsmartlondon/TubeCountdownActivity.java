package com.travelsmartlondon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;


public class TubeCountdownActivity extends ListActivity{
	private Button _closeButton;
	private List<Map<String,String>> _entryList;
	private SimpleAdapter _adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tube_countdown);
        Intent intent = getIntent();
        String input = intent.getStringExtra(MapActivity.ID_CODE);
        String name = intent.getStringExtra(MapActivity.EXTRA_MESSAGE);
        List<String> lines = intent.getStringArrayListExtra(MapActivity.TUBE_LINES);

		setTitle(name);
        this._entryList = new ArrayList<Map<String,String>>(); 

        this._adapter = new SimpleAdapter(this,
        								_entryList,
        								R.layout.custom_row_view,
        								new String[] {"Route","Time","Destination"},
        								new int[] {R.id.text1,R.id.text2, R.id.text3}
        								);
    	
		this._closeButton = (Button) findViewById(R.id.close_button);
		this._closeButton.setOnClickListener(new OnClickListener() {
		    									@Override
		    									public void onClick(View v) {
		    										finish();
		    									}
											});

		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		switch(lines.size()) {
			case 1: 
				httpGetAsyncTask.execute(input, "1", lines.get(0));
				break;
			case 2: 
				httpGetAsyncTask.execute(input, "2", lines.get(0), lines.get(1));
				break;
			case 3: 
				httpGetAsyncTask.execute(input, "3", lines.get(0), lines.get(1), lines.get(2));
				break;
			case 4:
				httpGetAsyncTask.execute(input, "4",  lines.get(0), lines.get(1), lines.get(2), lines.get(3));
				break;
			case 5: 
				httpGetAsyncTask.execute(input, "5", lines.get(0), lines.get(1), lines.get(2), lines.get(3), lines.get(4));
				break;
		}
		
    }
    
    private void populateList(String route, String time, String destination){
    	HashMap<String, String> temp = new HashMap<String, String>();
    	temp.put("Route", route);
    	temp.put("Time", time);
    	temp.put("Destination", destination);
    	_entryList.add(temp);
    }
    


	class HttpGetAsyncTask extends AsyncTask<String, Void, Void>{
		
		//Background execution
		@Override
		protected Void doInBackground(String... params) {
			String stationCode = params[0];
			
			int numberOfLines = Integer.parseInt(params[1]);

			//Creating a httpClient to connect to the Internet
			HttpClient httpClient = new DefaultHttpClient();

			//initialising a GET request to the API
			////tube?stop=@,$$$
			
			for(int n = 2; n < (numberOfLines + 2); n++) {
				String line = params[n];
				HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/tube?stop=" + line.trim() + "," + stationCode.trim());
				
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");
	
				    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				    String json = reader.readLine();
				    JSONObject jsonObject = new JSONObject(json);
				    
				    JSONArray platforms = jsonObject.getJSONArray("Platform");
				    
				    for(int i = 0; i < platforms.length(); i++){
				    	JSONObject platform = platforms.getJSONObject(i);
				    	String platformName = platform.getString("PlatformName");
				    	JSONArray trains = platform.getJSONArray("Trains");
				    	for (int j = 0;  j < trains.length() ; j++) {
				    		JSONObject train = trains.getJSONObject(j);
				    		String destination = train.getString("Destination");
				    		String timeTo = train.getString("TimeTo");
				    		populateList(platformName, timeTo, destination);
				    	}
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
					//Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_LONG).show();
					System.out.println("JSON Exception :" + jsone);
					jsone.printStackTrace();
				} 
			}
			return null;
		}

		//invoked on the UI after the background computation finishes. result of background task is passed onto this stage 
		@Override
		protected void onPostExecute(Void result) {
	    	setListAdapter(_adapter);
		}			
	}
}