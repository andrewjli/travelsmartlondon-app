package com.travelsmartlondon.handler;

import com.travelsmartlondon.MapActivity;
import com.travelsmartlondon.context.TSLApplicationContext;

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

/**
 * WeatherHandler provides weather information.
 * Deals with http request to the API and displaying the image.
 * @author jihyunhan
 */

public class WeatherHandler {	
	private static final WeatherHandler INSTANCE = new WeatherHandler();
	private String weatherDesc;
	private String weatherIconUrl;
	
	private WeatherHandler(){
		String latitude = "51.52";
		String longitude = "-0.13";
		connectWithHttpGet(latitude, longitude);
		System.out.println("WeatherHandler TEST");
	}
	
	public static WeatherHandler getInstance() {
		return INSTANCE;
	}
	
	
	
	private String connectWithHttpGet(String latitude_, String longitude_) {
		/*
		AsyncTask is used to handle background operations such as HTTP request and publish on the UI, without having to manipulate threads.
		AsyncTask<Params, Progress, Result>. the 2nd task is not initiated in this application.
		http://developer.android.com/reference/android/os/AsyncTask.html more detail here
		*/
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			
			//Background execution
			@Override
			protected String doInBackground(String... params) {
				
				//Creating a httpClient to connect to the Internet
				HttpClient httpClient = new DefaultHttpClient();

				//initialising a GET request to the API
				HttpGet httpGet = new HttpGet("http://free.worldweatheronline.com/feed/weather.ashx?q=" + params[0] +"," + params[1] + "&format=json&num_of_days=5&key=04ae09e61f173958132102");
				
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
				return weatherIconUrl;
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
