package com.travelsmartlondon.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

/**
 * BusStopHandler class for asynchronously handling requests to TSL API 
 * for bus stop locations.
 * TODO: RESTify, full implementation, asycnhronous
 * @author kamilprzekwas
 *
 */
public class BusStopHandler {
	public static final URI uri = URI.create("http://stud-tfl.cs.ucl.ac.uk/stops?");//
	
	private static BusStopHandler instance;
	
	/* DO NOT ERASE THIS COMMENT
	 http://stud-tfl.cs.ucl.ac.uk/stops?rad=51.52395809999999,-0.1331019000000424,1000
	 */
	private BusStopHandler() {  }
	
	public static BusStopHandler getInstance() {
        if (instance == null) {
                synchronized (BusStopHandler.class){
                        if (instance == null) {
                                instance = new BusStopHandler ();
                        }
              }
        }
        return instance;
	}
	
	public String getBusStopsInRadius(String latitude_, String longitude_, String radius_) {
		
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			
			@Override
			protected String doInBackground(String... params) {
				
				HttpClient httpClient = new DefaultHttpClient();
				
				

				HttpGet httpGet = new HttpGet(uri);
				
				try {
					//executing the httpGet request then assigning the execution result to HttpResponse
					HttpResponse httpResponse = httpClient.execute(httpGet);
					System.out.println("httpResponse");

					//reading in and instantiating the JSON objects from the content 
					//that was assigned to httpRequest, in UTF8
				    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				    String json = reader.readLine();
				    JSONObject jsonObject = new JSONObject(json);
				    
				    String weatherDesc = jsonObject.getJSONObject("data").getJSONArray("weather").getJSONObject(0).getJSONArray("weatherDesc").getJSONObject(0).getString("value");
				    System.out.println(weatherDesc);
				    String weatherIconUrl = jsonObject.getJSONObject("data").getJSONArray("weather").getJSONObject(0).getJSONArray("weatherIconUrl").getJSONObject(0).getString("value");
				    System.out.println(weatherIconUrl);
				    
				    
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
			protected void onPostExecute(String result) {
			}			
		}

		// Initialise the AsyncTask class and executing the background task.
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(latitude_, longitude_);
		return longitude_; 
	}
}
