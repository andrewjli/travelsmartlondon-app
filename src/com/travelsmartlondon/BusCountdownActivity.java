package com.travelsmartlondon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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

public class BusCountdownActivity extends ListActivity{
	
	private Button _closeButton;
	private ArrayList<HashMap<String,String>> _list;
	SimpleAdapter _adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState_) {
        super.onCreate(savedInstanceState_);
        setContentView(R.layout.bus_countdown);
        
        Intent intent = getIntent();
        String input = intent.getStringExtra(MapActivity.ID_CODE);
        String name = intent.getStringExtra(MapActivity.EXTRA_MESSAGE);

		setTitle(name);
        this._list = new ArrayList<HashMap<String,String>>(); 
        this._adapter = new SimpleAdapter(this,
					        			  this._list,
					        			  R.layout.custom_row_view,
					        			  new String[] {"Route","Time","Destination"},
					        			  new int[] {R.id.text1,R.id.text2, R.id.text3});

		this._closeButton = (Button) findViewById(R.id.close_button);
		this._closeButton.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
		});
		connectWithHttpGet(input);
    }
    
    private void populateList(String route_, String time_, String destination_){
    	HashMap<String, String> temp = new HashMap<String, String>();
    	temp.put("Route", route_);
    	temp.put("Time", time_);
    	temp.put("Destination", destination_);
    	_list.add(temp);
    }

	private void connectWithHttpGet(String request_) {
		/*
		AsyncTask is used to handle background operations such as HTTP request and publish on the UI, without having to manipulate threads.
		AsyncTask<Params, Progress, Result>. the 2nd task is not initiated in this application.
		http://developer.android.com/reference/android/os/AsyncTask.html more detail here
		*/
		class HttpGetAsyncTask extends AsyncTask<String, Void, Void>{
			
			//Background execution
			@Override
			protected Void doInBackground(String... params) {
				String busCode = params[0];
				System.out.println("Bus Code is :" + busCode);

				//Creating a httpClient to connect to the Internet
				HttpClient httpClient = new DefaultHttpClient();

				//initialising a GET request to the API
				HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/bus?StopCode1=" + busCode);
				
				try {
					//executing the httpGet request then assigning the execution result to HttpResponse
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					//reading in and instantiating the JSON objects from the content 
					//that was assigned to httpRequest, in UTF8
				    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				    String json = reader.readLine();
				    JSONObject jsonObject = new JSONObject(json);
				    
				    //JSON parser - pretty intuitive!
				    for(int i = 0; i < jsonObject.length()-1; i++){
				    	String index = Integer.toString(i);
				    	populateList(jsonObject.getJSONObject(index).get("Route").toString(),
				    			jsonObject.getJSONObject(index).get("Time").toString(),
				    			"towards " + jsonObject.getJSONObject(index).get("Destination").toString()
				    			);
				    };

					//Error handler ******* to be completed in more detail
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
				return null;
			}

			//invoked on the UI after the background computation finishes. result of background task is passed onto this stage 
			@Override
			protected void onPostExecute(Void result) {
		    	setListAdapter(_adapter);
			}			
		}

		// Initialise the AsyncTask class and executing the background task.
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(request_); 
	}

}