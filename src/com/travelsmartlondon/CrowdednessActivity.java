package com.travelsmartlondon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class CrowdednessActivity extends Activity {
	
	public static final String ALMOST_EMPTY = "ALMOST EMPTY";
	public static final String NORMAL = "NORMAL";
	public static final String CROWDED = "CROWDED";
	public static final String VERY_CROWDED = "VERY CROWDED";
	public static final String EXTREMELY_CROWDED = "EXTREMELY CROWDED";
	
	
	private String _crowdednessLevel;
	private String _crowdQueryTime;
	private String _currentStationNLC;
	private Spinner spinner;
	
	private Intent intent;
	
	private int[] _imageViews = {
		    R.id.imageview1,
		    R.id.imageview2,
		    R.id.imageview3,
		    R.id.imageview4,
		    R.id.imageview5
		    };

	@Override
	protected void onCreate(Bundle savedInstanceState_) {
		super.onCreate(savedInstanceState_);
		setContentView(R.layout.crowdedness);
		
		intent = getIntent();
        setTitle(intent.getStringExtra(MapActivity.TUBE_NAME));
        
        _currentStationNLC = intent.getStringExtra(MapActivity.STATION_NLC);
		HttpGetAsyncTask asyncTask = new HttpGetAsyncTask();
		asyncTask.execute(_currentStationNLC,
				intent.getStringExtra(MapActivity.CURRENT_TIME));
	
		
		TextView crowdedness = (TextView) findViewById(R.id.crowdednessLevel);
		crowdedness.setText(this._crowdednessLevel);
		
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.time, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
	}
	
	protected void onResume(){
		super.onResume();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
				String timeToQuery = parent.getItemAtPosition(pos).toString();
				if(!timeToQuery.equals("Select time")){
					timeToQuery = timeToQuery.substring(0, 2)+timeToQuery.substring(3, 5);
					
					HttpGetAsyncTask asyncTask = new HttpGetAsyncTask();
					asyncTask.execute(_currentStationNLC,timeToQuery);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	private void setCrowdednessImages(int m_)
	{
		for(int image : _imageViews){
			ImageView imageview = (ImageView) findViewById(image);
			imageview.setVisibility(View.INVISIBLE);
		}
		
		for(int n=0;n<m_;n++)
		{
			ImageView imageview = (ImageView) findViewById(_imageViews[n]);
			imageview.setVisibility(View.VISIBLE);
		}
	}
	
	class HttpGetAsyncTask extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			
			String stationNLC = params[0];
			String queryTime = params[1];
			
			_crowdQueryTime = queryTime.substring(0, 2)+":"+queryTime.substring(2,4);
			
			HttpClient httpClient = new DefaultHttpClient();
			
			HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/crowd?stop="+stationNLC+","+queryTime);
			
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					
				    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				    _crowdednessLevel = reader.readLine();
				    _crowdednessLevel = _crowdednessLevel.substring(1, _crowdednessLevel.length()-1);
					
				} catch (ClientProtocolException cpe) {
					System.out.println("Client Protocol Exception :" + cpe);
					cpe.printStackTrace();
				} catch (IOException ioe) {
					System.out.println("IOException :" + ioe);
					ioe.printStackTrace();
				}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			TextView crowdednessTime = (TextView) findViewById(R.id.crowdTime);
			crowdednessTime.setText(_crowdQueryTime);
	    	
			if(_crowdednessLevel.equals(ALMOST_EMPTY)){
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(ALMOST_EMPTY);
				crowdednessLevel.setTextColor(Color.rgb(0, 255, 0));
				setCrowdednessImages(1);
			} 
			else if (_crowdednessLevel.equals(NORMAL)){
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(NORMAL);
				crowdednessLevel.setTextColor(Color.rgb(0, 255, 0));
				setCrowdednessImages(2);
			}
			else if (_crowdednessLevel.equals(CROWDED)){
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(CROWDED);
				crowdednessLevel.setTextColor(Color.rgb(255, 255,0));
				setCrowdednessImages(3);
			}
			else if (_crowdednessLevel.equals(VERY_CROWDED)){
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(VERY_CROWDED);
				crowdednessLevel.setTextColor(Color.rgb(255, 140, 0));
				setCrowdednessImages(4);
			}
			else if (_crowdednessLevel.equals(EXTREMELY_CROWDED)){
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(EXTREMELY_CROWDED);
				crowdednessLevel.setTextColor(Color.rgb(238, 64, 0));
				setCrowdednessImages(5);
			}
		}
	
	}
}
