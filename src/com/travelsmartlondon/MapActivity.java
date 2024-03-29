package com.travelsmartlondon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.travelsmartlondon.database.dao.StationDAO;
import com.travelsmartlondon.help.DatabaseClassificator;
import com.travelsmartlondon.help.Point;
import com.travelsmartlondon.station.Station;
import com.travelsmartlondon.station.TubeStation;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MapActivity extends FragmentActivity implements OnMarkerClickListener, OnCameraChangeListener{ 
	public static final String ID_CODE = "com.travelsmartlondon.ID_CODE";
	public static final String EXTRA_MESSAGE = "come.travelsmartlondon.EXTRA_MESSAGE";
	public static final String TUBE_LINES = "com.travelsmartlondon.TUBE_LINES";
	public static final String STATION_NLC = "com.travelsmartlondon.STATION_NLC";
	public static final String CURRENT_TIME = "com.travelsmart.CURRENT_TIME";
	public static final String TUBE_NAME="com.travelsmart.TUBE_NAME";
	private static final int TAP_STATION = 10;
	private static final String RADIUS = "1500";

	ToggleButton busToggle;
	ToggleButton bikeToggle;
	ToggleButton tubeToggle;

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private boolean _isConnected;
	private GoogleMap _map;
	private Location  _currentLocation;
	private LocationManager _locationManager;
	private LocationListener _locationListener;
	private String _provider;

	private Marker tempMarker;
	private GoogleMap map;
	private Map<Marker, BusStop> busList = new HashMap<Marker, BusStop>();
	private List<Marker> busMarkerList = new ArrayList<Marker>();
	private List<Marker> bikeMarkerList = new ArrayList<Marker>();
	private Map<Marker, TubeStation> tubeList = new HashMap<Marker, TubeStation>();
	private List<Marker> tubeMarkerList = new ArrayList<Marker>();

	private WeatherHttpGetAsyncTask weatherHttpGetAsyncTask = new WeatherHttpGetAsyncTask();
	private BikeHttpGetAsyncTask bikeHttpGetAsyncTask = new BikeHttpGetAsyncTask();
	private BusStopHttpGetAsyncTask busStopHttpGetAsyncTask = new BusStopHttpGetAsyncTask();
	private QueryTubeStationAsyncTask tubeGetAsyncTask = new QueryTubeStationAsyncTask();


	private String _latitude;
	private String _longitude;
	private Double _lastUpdatedLatitude;
	private Double _lastUpdatedLongitude;

	private String weatherDesc;
	private String weatherIconUrl;
	private String temperature;
	private Drawable weatherIcon;

	private JSONArray jsonBikeArray;
	private JSONArray jsonBusStopArray;
	
	private List<TubeStation> _stationsInArea;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(false);
		map.getUiSettings().setCompassEnabled(false);
		//map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.523524,-0.132823), 15));

		_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		_isConnected = checkInternetConnection();

		boolean enabledGPS = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if(enabledGPS){
			_provider = LocationManager.GPS_PROVIDER;
		}else{
			_provider = LocationManager.NETWORK_PROVIDER;
		}

		if(!_isConnected){
			showNoInternetDialog();
		}
		else{
			_currentLocation = getCurrentLocation();
		}
		
		if(_currentLocation == null){
			_latitude = Double.toString(51.523524);
			_longitude = Double.toString(-0.132823);
		}
		else{
			_latitude = Double.toString(_currentLocation.getLatitude());
			_longitude = Double.toString(_currentLocation.getLongitude());
		}

		weatherHttpGetAsyncTask.execute(_latitude, _longitude);
		bikeHttpGetAsyncTask.execute(_latitude, _longitude, RADIUS);
		busStopHttpGetAsyncTask.execute(_latitude, _longitude, RADIUS);
		tubeGetAsyncTask.execute(_latitude, _longitude);

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(_latitude),Double.parseDouble(_longitude)), 15));
		
		busToggle = (ToggleButton) findViewById(R.id.bus_toggle);
		busToggle.setChecked(true);
		busToggle.setBackgroundColor(Color.WHITE);
		bikeToggle = (ToggleButton) findViewById(R.id.bike_toggle);
		bikeToggle.setChecked(true);
		bikeToggle.setBackgroundColor(Color.WHITE);
		tubeToggle = (ToggleButton) findViewById(R.id.tube_toggle);
		tubeToggle.setChecked(true);
		tubeToggle.setBackgroundColor(Color.WHITE);

		busToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					for(Marker marker: busMarkerList){
						marker.setVisible(true);
					}
					buttonView.setAlpha((float) 0.9);
				} else {
					buttonView.setAlpha((float) 0.4);
					for(Marker marker: busMarkerList){
						marker.setVisible(false);
					}
				}
			}
		});

		bikeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					for(Marker marker: bikeMarkerList){
						marker.setVisible(true);
					}
					buttonView.setAlpha((float) 0.9);
				} else {
					for(Marker marker: bikeMarkerList){
						marker.setVisible(false);
					}
					buttonView.setAlpha((float) 0.4);
				}
			}
		});

		tubeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					for(Marker marker: tubeMarkerList){
						marker.setVisible(true);
					}
					buttonView.setAlpha((float) 0.9);
				} else {
					for(Marker marker: tubeMarkerList){
						marker.setVisible(false);
					}
					buttonView.setAlpha((float) 0.4);
				}
			}
		});
		map.setOnMarkerClickListener(this);
		map.setOnCameraChangeListener(this);
	}

	public void onCameraChange(final CameraPosition position) {
		double newLatitude = position.target.latitude;
		double newLongitude = position.target.longitude;
		double dist = distFrom(newLatitude, newLongitude, Double.parseDouble(_latitude), Double.parseDouble(_longitude));
		System.out.println(dist);
		if(dist > 1000){
			_latitude = Double.toString(newLatitude);	
			_longitude = Double.toString(newLongitude);
			if(bikeHttpGetAsyncTask.getStatus() != AsyncTask.Status.FINISHED){
				System.out.println("Pending Http Request Canceled");
				bikeHttpGetAsyncTask.cancel(true);
			}
			if(busStopHttpGetAsyncTask.getStatus() != AsyncTask.Status.FINISHED){
				busStopHttpGetAsyncTask.cancel(true);
			}
			bikeHttpGetAsyncTask = new BikeHttpGetAsyncTask();
			bikeHttpGetAsyncTask.execute(_latitude, _longitude, RADIUS);
			busStopHttpGetAsyncTask = new BusStopHttpGetAsyncTask();
			busStopHttpGetAsyncTask.execute(_latitude, _longitude, RADIUS);
			tubeGetAsyncTask = new QueryTubeStationAsyncTask();
			tubeGetAsyncTask.execute(_latitude, _longitude);
		}
	}

	private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 6371000;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return dist;
	    }
	
	private Location getCurrentLocation() {

		Location newLocation;

		newLocation = _locationManager.getLastKnownLocation(_provider);
		boolean betterNewLocation = isBetterLocation(newLocation, _currentLocation);

		if(betterNewLocation){
			return newLocation;
		}


		return _currentLocation;
	}

	public void showNoInternetDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Internet connection!");
		dialog.setMessage("No internet connection was dectedted.Please enable mobile internet or wifi to use map capabilities");
		dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});

		dialog.show();
	}


	public boolean checkInternetConnection(){
		ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();

		boolean internet = false;
		if(!wifi) { 
			if(connectivityManager.getActiveNetworkInfo() != null){
			internet  = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
			}
		}

		if(!internet && !wifi)
		{
			return false;
		}

		return true;
	}

	protected boolean isBetterLocation(Location newLocation_, Location currentBestLocation_) {
		if (currentBestLocation_ == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation_.getTime() - currentBestLocation_.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation_.getAccuracy() - currentBestLocation_.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if(busList.containsKey(marker)){
			System.out.println("Event Listened");
			Intent intent = new Intent(this, BusCountdownActivity.class);
			intent.putExtra(ID_CODE, busList.get(marker).getCode());
			intent.putExtra(EXTRA_MESSAGE, busList.get(marker).getName());
			startActivity(intent);
		}

		if(tubeList.containsKey(marker)){
			setTempMarker(marker);
			showDialog(TAP_STATION);
		}else{
			marker.showInfoWindow();
		}
		return true;
	}

	protected Dialog onCreateDialog(int id){
		final CharSequence[] i = {"Show arrivals","Show smart data"};
		switch (id){
		case TAP_STATION:
			Builder builder = new AlertDialog.Builder(this);
			builder.setItems(i, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0){
						showTubeIntent(getTempMarker());
					}else{
						showCrowdednessIntent(getTempMarker());
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
		Intent intent = new Intent(this, TubeIntermediateActivity.class);
		intent.putExtra(ID_CODE, tubeList.get(marker).getCode());
		intent.putExtra(EXTRA_MESSAGE, tubeList.get(marker).getName());
		intent.putStringArrayListExtra(TUBE_LINES, tubeList.get(marker).getLinesAsArrayList());
		startActivity(intent);
	}

	private void showCrowdednessIntent(Marker marker){
		System.out.println("Event Listened");
		Intent intent = new Intent(this, CrowdednessActivity.class);
		
		//Get the current Time
		Time currentTime = new Time();
		currentTime.setToNow();
		String timeValue = currentTime.toString();
		timeValue = timeValue.substring(9, 13);
		

		intent.putExtra(TUBE_NAME, tubeList.get(marker).getName());
		intent.putExtra(STATION_NLC,Integer.toString(tubeList.get(marker).getNLC()));
		intent.putExtra(CURRENT_TIME, timeValue);
		startActivity(intent);
	}

	private void setTempMarker(Marker marker){
		tempMarker = marker;
	}

	private Marker getTempMarker(){
		return tempMarker;
	}

	private void addMarker(TubeStation station, GoogleMap map){
		if(station.getClass() == TubeStation.class){
			Marker marker = map.addMarker(new MarkerOptions().position(station.getCoordinates()).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tube_icon_coloured))));
			if(!tubeToggle.isChecked()) {
				marker.setVisible(false);
			} else {
				marker.setVisible(true);
			}
			tubeList.put(marker,station);
			tubeMarkerList.add(marker);
		}
	}

	class WeatherHttpGetAsyncTask extends AsyncTask<String, Void, String>{
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
				weatherIconUrl = jsonObject.getString("IconURL");
				temperature = jsonObject.getString("Temperature");
				
				InputStream is = (InputStream) new URL(weatherIconUrl).getContent();
				weatherIcon = Drawable.createFromStream(is, "src name");

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
				System.out.println("JSON Exception :" + jsone);
				jsone.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			ImageView weatherIconImageView = (ImageView) findViewById(R.id.weather_icon);
			weatherIconImageView.setImageDrawable(weatherIcon);
			weatherIconImageView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Toast toast = Toast.makeText(getApplicationContext(), "Today's weather forecast: " + "\n" +  temperature + "\u00B0" + "C, "  + weatherDesc, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();

				}
			});
		}
	}

	class BusStopHttpGetAsyncTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();

			HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/stops?loc=" + params[0] + "," + params[1] + "," + params[2]);

			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);

				BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				String json = reader.readLine();
				jsonBusStopArray = new JSONArray(json);

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
				System.out.println("JSON Exception :" + jsone);
				jsone.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			for(Marker marker: busMarkerList){
				marker.remove();
			}
			try {
				int index = 0;
				while(!jsonBusStopArray.isNull(index)){
					JSONObject jsonCurrentObject = jsonBusStopArray.getJSONObject(index);
					Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(jsonCurrentObject.getDouble("lat"), jsonCurrentObject.getDouble("long")))
							.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bus_icon_coloured))));
					busList.put(marker, new BusStop(jsonCurrentObject.getString("stopName"), jsonCurrentObject.getString("stopCode")));
					if(!busToggle.isChecked()){
						marker.setVisible(false);
					}
					busMarkerList.add(marker);
					index++;
				}
			} catch (NullPointerException npe) {
				System.out.println(" Null Pointer Exception :" + npe);
				npe.printStackTrace();
			} catch (JSONException jsone) {
				System.out.println("JSON Exception :" + jsone);
				jsone.printStackTrace();
			}
		}
	}

	class BikeHttpGetAsyncTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/bike?loc=" + params[0] + "," + params[1] + "," + params[2]);

			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				String json = reader.readLine();
				jsonBikeArray = new JSONArray(json);

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
				System.out.println("JSON Exception :" + jsone);
				jsone.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			for(Marker marker: bikeMarkerList){
				marker.remove();
			}
			try {
				int index = 0;
				while(!jsonBikeArray.isNull(index)){
					JSONObject jsonCurrentObject = jsonBikeArray.getJSONObject(index);
					Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(jsonCurrentObject.getDouble("lat"), jsonCurrentObject.getDouble("long")))
							.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bike_icon_coloured)))
							.title(jsonCurrentObject.getString("name"))
							.snippet(jsonCurrentObject.getString("nbBikes") + "/" + jsonCurrentObject.getString("dbDocks") + " bikes available"));
					if(!bikeToggle.isChecked()){
						marker.setVisible(false);
					}
					bikeMarkerList.add(marker);
					index++;
				}
			} catch (NullPointerException npe) {
				System.out.println(" Null Pointer Exception :" + npe);
				npe.printStackTrace();
			} catch (JSONException jsone) {
				System.out.println("JSON Exception :" + jsone);
				jsone.printStackTrace();
			}
		}
	}

	class QueryTubeStationAsyncTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {

			Point currentLocation = new Point(Double.parseDouble(params[0]), Double.parseDouble(params[1]));
			int mapArea = DatabaseClassificator.getInstance().checkTubeStationMapArea(currentLocation);

			StationDAO stationDao = new StationDAO(MapActivity.this);
			stationDao.open();
			_stationsInArea = stationDao.getStationsByArea(mapArea);
			stationDao.close();

			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			for(Marker marker : tubeMarkerList) {
				marker.remove();
			}
			if(_stationsInArea != null) {
				for(TubeStation station : _stationsInArea) {
					addMarker(station, map);
				}
			}
		}

	}


	class BusStop{
		private String _name;
		private String _code;
		public BusStop(String name_, String code_){
			this._name = name_;
			this._code = code_;

		}
		public String getName() {
			return this._name;
		}
		public String getCode() {
			return this._code;
		}
	}
}