package com.travelsmartlondon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;


public class TubeCountdownActivity extends ListActivity{
	private Button _closeButton;
	private List<HashMap<String,String>> _entryList;
	private SimpleAdapter _adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tube_countdown);
        Intent intent = getIntent();
        String input = intent.getStringExtra(MapActivity.ID_CODE);
        String name = intent.getStringExtra(MapActivity.EXTRA_MESSAGE);

		setTitle(name);
        this._entryList = new ArrayList<HashMap<String,String>>(); 

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
		populateList("High Barnet", "Due", "Northern Line");
		populateList("Morden via Bank", "1 min", "Northern Line");
		populateList("Brixton", "2 min", "Victoria Line");
		populateList("Seven Sisters", "2 min", "Victoria Line");
		populateList("Edgeware", "3 min", "Northern Line");
		populateList("High Barnet", "3 min", "Northern Line");
		populateList("Walthamstow Central", "4 min", "Victoria Line");
		populateList("Kennington via Charing X", "5 min", "Northern Line");
		populateList("High Barnet", "5 min", "Northern Line");
		populateList("Brixton", "7 min", "Victoria Line");
		populateList("Morden", "8 min", "Northern Line");
		populateList("Walthamtow Cetral", "8 min", "Victoria");
		
		setListAdapter(_adapter);
    }
    
    private void populateList(String route, String time, String destination){
    	HashMap<String, String> temp = new HashMap<String, String>();
    	temp.put("Route", route);
    	temp.put("Time", time);
    	temp.put("Destination", destination);
    	_entryList.add(temp);
    }
    


/**
	private void connectWithHttpGet(String request) {

		/**
		AsyncTask is used to handle background operations such as HTTP request and publish on the UI, without having to manipulate threads.
		AsyncTask<Params, Progress, Result>. the 2nd task is not initiated in this application.
		http://developer.android.com/reference/android/os/AsyncTask.html more detail here
	**
		class HttpGetAsyncTask extends AsyncTask<String, Void, Void>{
			
			//Background execution
			@Override
			protected Void doInBackground(String... params) {
				String stationCode = params[0];
				System.out.println("Station Code is :" + stationCode);

				//Creating a httpClient to connect to the Internet
				HttpClient httpClient = new DefaultHttpClient();

				//initialising a GET request to the API
				HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/tube?stop=" + stationCode + "&line=N");
				
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
		    	setListAdapter(adapter);
			}			
		}

		// Initialise the AsyncTask class and executing the background task.
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(request); 
	}
	
	**/

}