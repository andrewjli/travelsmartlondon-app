package com.travelsmartlondon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.travelsmartlondon.station.BikeDock;
import com.travelsmartlondon.station.BusStop;
import com.travelsmartlondon.station.Station;
import com.travelsmartlondon.station.TubeStation;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MapActivity extends FragmentActivity implements OnMarkerClickListener{ 
    public static final String ID_CODE = "com.travelsmartlondon.ID_CODE";
    public static final String EXTRA_MESSAGE = "come.travelsmartlondon.EXTRA_MESSAGE";
    private static final int DIALOG_ALERT = 10;
    
    private Marker tempMarker;
	private GoogleMap map;
	private Map<Marker, Station> busList = new HashMap<Marker, Station>();
	private List<Marker> busMarkerList = new ArrayList<Marker>();
	private Map<Marker, Station> bikeList = new HashMap<Marker, Station>();
	private List<Marker> bikeMarkerList = new ArrayList<Marker>();
	private Map<Marker, Station> tubeList = new HashMap<Marker, Station>();
	private List<Marker> tubeMarkerList = new ArrayList<Marker>();
	
	private String latitude;
	private String longitude;
	
	private String weatherDesc;
	private String weatherIconUrl;
	private Drawable weatherIcon;

	/*
	Dummy tube stations for the purpose of the demo 
	TODO: Real-time live data
	 */
	BusStop goodge = new BusStop("Goodge Street Station", "54958", 529487,181895);
	BusStop percy = new BusStop("Percy Street","71293", 529650, 181656);
	BusStop ucl = new BusStop("University College Hospital", "57596",529548, 182199);
	BusStop ucl1 = new BusStop("University College Hospital", "76205",529373, 182201);

	TubeStation goodgestation = new TubeStation("Goodge Street Station", "GST",51.520424996045500000, -0.134662152092394320);
	TubeStation warrenstation = new TubeStation("Warren Street Station", "76205",51.524511517547950000, -.138272313383213400);
	TubeStation eustonstation = new TubeStation("Euston Station", "76205",51.528596260899460000, -.133289718799315530);
	TubeStation tcrstation = new TubeStation("Tottenham Court Road Station", "76205", 51.516209552513860000, -.130870518140944360);

	BikeDock warren = new BikeDock("Warren Street Station, Euston", "239", 51.52443845,-0.138019439);
	BikeDock gower = new BikeDock("Gower Place , Euston", "65", 51.52522753,-0.13518856);
	BikeDock euston = new BikeDock("Euston Road, Euston", "69", 51.5262363,-0.134407652);
	BikeDock tavistock = new BikeDock("Tavistock Place, Bloomsbury", "89", 51.5262503,-0.123509611);
	BikeDock malet = new BikeDock("Malet Street, Bloomsbury", "12", 51.52168078,-0.130431727);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(false);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.523524,-0.132823), 15));
		
		latitude = "51.52395809999999";
		longitude = "-0.1331019000000424";

		weatherHttpRequest(latitude, longitude);
		
		addMarker(goodge, map);
		addMarker(percy, map);
		addMarker(warren, map);
		addMarker(ucl, map);
		addMarker(ucl1, map);
		addMarker(gower, map);
		addMarker(euston, map);
		addMarker(tavistock, map);
		addMarker(malet, map);
		addMarker(goodgestation, map);
		addMarker(warrenstation, map);
		addMarker(eustonstation, map);
		addMarker(tcrstation, map);
		
		ToggleButton busToggle = (ToggleButton) findViewById(R.id.bus_toggle);
		busToggle.setChecked(true);
		busToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		        	for(Marker marker: busMarkerList){
		        		marker.setVisible(true);
		        	}
		        } else {
		        	for(Marker marker: busMarkerList){
		        		marker.setVisible(false);
		        	}
		        }
		    }
		});

		ToggleButton bikeToggle = (ToggleButton) findViewById(R.id.bike_toggle);
		bikeToggle.setChecked(true);
		bikeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		        	for(Marker marker: bikeMarkerList){
		        		marker.setVisible(true);
		        	}
		        } else {
		        	for(Marker marker: bikeMarkerList){
		        		marker.setVisible(false);
		        	}
		        }
		    }
		});

		ToggleButton tubeToggle = (ToggleButton) findViewById(R.id.tube_toggle);
		tubeToggle.setChecked(true);
		tubeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		        	for(Marker marker: tubeMarkerList){
		        		marker.setVisible(true);
		        	}
		        } else {
		        	for(Marker marker: tubeMarkerList){
		        		marker.setVisible(false);
		        	}
		        }
		    }
		});

		map.setOnMarkerClickListener(this);
	}
	
//    @Override
//    public void onCameraChange(final CameraPosition position) {
//        mCameraTextView.setText(position.toString());
//    }

	@Override
	public boolean onMarkerClick(Marker marker) {
		if(busList.containsKey(marker)){
			System.out.println("Event Listened");
			Intent intent = new Intent(this, BusCountdownActivity.class);
			intent.putExtra(ID_CODE, busList.get(marker).getCode());
			intent.putExtra(EXTRA_MESSAGE, busList.get(marker).getName());
			startActivity(intent);
		}
		if(bikeList.containsKey(marker)){
			marker.setTitle(bikeList.get(marker).getName());
			if(bikeList.get(marker)==gower){
				marker.setSnippet("13/17 Bikes");				
			}
			
			if(bikeList.get(marker)==warren){
				marker.setSnippet("4/26 Bikes");				
			}
			
			marker.showInfoWindow();
		}
		if(tubeList.containsKey(marker)){
			setTempMarker(marker);
			showDialog(DIALOG_ALERT);
		}
		return true;
	}
	
	protected Dialog onCreateDialog(int id){
		final CharSequence[] i = {"Show arrivals","Show smart data"};
		switch (id){
		case DIALOG_ALERT:
			Builder builder = new AlertDialog.Builder(this);
	        builder.setItems(i, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int which) {
	                	   if(which == 0){
	                		   showTubeIntent(getTempMarker());
	                	   }else{
	                		   showCrowdednessIntent();
	                	   }
	               }
	        });
	        AlertDialog dialog = builder.create();
	        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        dialog.show();
		}
		return super.onCreateDialog(id);
	}
	
	private void showTubeIntent(Marker marker){
		System.out.println("Event Listened");
		Intent intent = new Intent(this, TubeCountdownActivity.class);
		intent.putExtra(ID_CODE, "54958");
		intent.putExtra(EXTRA_MESSAGE, "Euston Station");
		startActivity(intent);
	}
	
	private void showCrowdednessIntent(){
		System.out.println("Event Listened");
		Intent intent = new Intent(this, CrowdednessActivity.class);
		startActivity(intent);
	}
	
	private void setTempMarker(Marker marker){
		tempMarker = marker;
	}
	
	private Marker getTempMarker(){
		return tempMarker;
	}
	
	/*
	void showDialog(){
        android.app.FragmentManager fm = getFragmentManager();
        TubeChoiceFragment tubeChoiceDialog = new TubeChoiceFragment();
        tubeChoiceDialog.show(fm, "fragment_edit_name");
	}

 	*/
	
	private void addMarker(Station station, GoogleMap map){
		if(station.getClass() == BusStop.class){
			Marker marker = map.addMarker(new MarkerOptions().position(station.getCoordinates()));
			busList.put(marker,station);
			busMarkerList.add(marker);
		}
		if(station.getClass() == BikeDock.class){
			Marker marker = map.addMarker(new MarkerOptions().position(station.getCoordinates()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			bikeList.put(marker,station);
			bikeMarkerList.add(marker);
		}
		if(station.getClass() == TubeStation.class){
			Marker marker = map.addMarker(new MarkerOptions().position(station.getCoordinates()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
			tubeList.put(marker,station);
			tubeMarkerList.add(marker);
		}
		
	}


	private void weatherHttpRequest(String latitude_, String longitude_) {
		class HttpGetAsyncTask extends AsyncTask<String, Void, String>{
			
			@Override
			protected String doInBackground(String... params) {
				HttpClient httpClient = new DefaultHttpClient();

				HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/weather?loc=" + params[0] +"," + params[1]);
				
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				    String json = reader.readLine();
				    JSONObject jsonObject = new JSONObject(json);
				    
				    weatherDesc = jsonObject.getString("WeatherDesc");
				    System.out.println(weatherDesc);
				    weatherIconUrl = jsonObject.getString("IconURL");
				    System.out.println(weatherIconUrl);

				        InputStream is = (InputStream) new URL(weatherIconUrl).getContent();
				        Drawable image = Drawable.createFromStream(is, "src name");
				    
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
				TextView weatherDescTextView = (TextView) findViewById(R.id.weather_desc);
				weatherDescTextView.setText(weatherDesc);

				ImageView weatherIconImageView = (ImageView) findViewById(R.id.weather_icon);
				weatherIconImageView.setImageDrawable(weatherIcon);
				
			}
		}
		HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
		httpGetAsyncTask.execute(latitude_, longitude_);
	}

}
