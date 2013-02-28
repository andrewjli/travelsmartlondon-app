package com.travelsmartlondon.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class BikeHandler {
	private static final BikeHandler INSTANCE = new BikeHandler();
	private JSONArray jsonBikeArray;
	
	private BikeHandler(){
		String latitude = "51.52395809999999";
		String longitude = "-0.1331019000000424";
		String result = connectWithHttpGet(latitude, longitude);
	}
	

	public static BikeHandler getInstance() {
		return INSTANCE;
	}
	
	public JSONArray getJson(){
		return jsonBikeArray;
	}

	private String connectWithHttpGet(String latitude_, String longitude_) {
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			String results;
			@Override
			protected String doInBackground(String... params) {
				HttpClient httpClient = new DefaultHttpClient();

				HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/bike?getAtCoordinates,lat=" + params[0] + "&long=" + params[1]);
				
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				    String json = reader.readLine();
				    jsonBikeArray = new JSONArray(json);
				    
				    results = jsonBikeArray.get(0).toString();
				    

				    
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

			@Override
			protected void onPostExecute(String result) {
				
			}
		}

		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(latitude_, longitude_);
		return httpGetAsyncTask.results; 
	}
}
