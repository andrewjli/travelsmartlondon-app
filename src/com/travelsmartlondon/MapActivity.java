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
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
	private static final int DIALOG_ALERT = 10;
	private static final String RADIUS = "1000";

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
	private Map<Marker, Station> tubeList = new HashMap<Marker, Station>();
	private List<Marker> tubeMarkerList = new ArrayList<Marker>();

	private WeatherHttpGetAsyncTask weatherHttpGetAsyncTask = new WeatherHttpGetAsyncTask();
	private BikeHttpGetAsyncTask bikeHttpGetAsyncTask = new BikeHttpGetAsyncTask();
	private BusStopHttpGetAsyncTask busStopHttpGetAsyncTask = new BusStopHttpGetAsyncTask();
	//private QueryTubeStationAsyncTask tubeGetAsyncTask = new QueryTubeStationAsyncTask();


	private String _latitude;
	private String _longitude;

	private String weatherDesc;
	private String weatherIconUrl;
	private Drawable weatherIcon;

	private JSONArray jsonBikeArray;
	private JSONArray jsonBusStopArray;

	/*
	Dummy tube stations for the purpose of the demo 
	TODO: Real-time live data
	 */

	TubeStation goodgestation = new TubeStation("Goodge Street Station", "GST",51.520424996045500000, -0.134662152092394320);
	TubeStation warrenstation = new TubeStation("Warren Street Station", "76205",51.524511517547950000, -.138272313383213400);
	TubeStation eustonstation = new TubeStation("Euston Station", "76205",51.528596260899460000, -.133289718799315530);
	TubeStation tcrstation = new TubeStation("Tottenham Court Road Station", "76205", 51.516209552513860000, -.130870518140944360);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(false);
		map.getUiSettings().setCompassEnabled(false);

		_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		_isConnected = checkInternetConnection();

		boolean enabledGPS = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if(enabledGPS)
		{
			_provider = LocationManager.GPS_PROVIDER;
		}
		else{
			_provider = LocationManager.NETWORK_PROVIDER;
		}

		if(!_isConnected){
			showNoInternetDialog();
		}
		else{
			_currentLocation = getCurrentLocation();

			_latitude = Double.toString(_currentLocation.getLatitude());
			_longitude = Double.toString(_currentLocation.getLongitude());

			weatherHttpGetAsyncTask.execute(_latitude, _longitude);
			bikeHttpGetAsyncTask.execute(_latitude, _longitude);
			busStopHttpGetAsyncTask.execute(_latitude, _longitude, RADIUS);
			//tubeGetAsyncTask.execute(_latitude,_longitude);
			

			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_currentLocation.getLatitude()
					,_currentLocation.getLongitude()), 15));


			addMarker(goodgestation, map);
			addMarker(warrenstation, map);
			addMarker(eustonstation, map);
			addMarker(tcrstation, map);


			busToggle = (ToggleButton) findViewById(R.id.bus_toggle);
			busToggle.setChecked(true);
			bikeToggle = (ToggleButton) findViewById(R.id.bike_toggle);
			bikeToggle.setChecked(true);
			tubeToggle = (ToggleButton) findViewById(R.id.tube_toggle);
			tubeToggle.setChecked(true);

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
			map.setOnCameraChangeListener(this);
		}
	}

	//    @Override
	//    public void onCameraChange(final CameraPosition position) {
	//        mCameraTextView.setText(position.toString());
	//    }

	public void onCameraChange(final CameraPosition position) {
		double latitude = position.target.latitude;
		double longitude = position.target.longitude;
		_latitude = Double.toString(latitude);
		_longitude = Double.toString(longitude);
		if(!(bikeHttpGetAsyncTask.getStatus() == AsyncTask.Status.FINISHED)){
			System.out.println("Pending Http Request Canceled");
			bikeHttpGetAsyncTask.cancel(true);
		}
		if(!(busStopHttpGetAsyncTask.getStatus() == AsyncTask.Status.FINISHED)){
			busStopHttpGetAsyncTask.cancel(true);
		}
		bikeHttpGetAsyncTask = new BikeHttpGetAsyncTask();
		bikeHttpGetAsyncTask.execute(_latitude, _longitude);
		busStopHttpGetAsyncTask = new BusStopHttpGetAsyncTask();
		busStopHttpGetAsyncTask.execute(_latitude, _longitude, RADIUS);
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
		dialog.setMessage("No internet connection was dectedted./n " +
				"Please enable mobile internet or wifi to use map capabilities");
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

		boolean internet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();

		if(internet || wifi)
		{
			return true;
		}

		return false;
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
			showDialog(DIALOG_ALERT);
		}else{
			marker.showInfoWindow();
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

	private void addMarker(Station station, GoogleMap map){
		if(station.getClass() == TubeStation.class){
			Marker marker = map.addMarker(new MarkerOptions().position(station.getCoordinates()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
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
				System.out.println(weatherDesc);
				weatherIconUrl = jsonObject.getString("IconURL");
				System.out.println(weatherIconUrl);

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
			//			TextView weatherDescTextView = (TextView) findViewById(R.id.weather_desc);
			//			weatherDescTextView.setText(weatherDesc);

			ImageView weatherIconImageView = (ImageView) findViewById(R.id.weather_icon);
			weatherIconImageView.setImageDrawable(weatherIcon);


		}
	}

	class BikeHttpGetAsyncTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/bike?getAtCoordinates,lat=" + params[0] + "&long=" + params[1]);

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
							.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bike_icon)))
							.title(jsonCurrentObject.getString("name"))
							.snippet(jsonCurrentObject.getString("nbBikes") + "/" + jsonCurrentObject.getString("dbDocks") + " bikes available"));
					if(!bikeToggle.isChecked()){
						marker.setVisible(false);
					}
					bikeMarkerList.add(marker);
					index++;
				}
			} catch (JSONException e) {
				System.out.println("Error in the JSON parsing");
				e.printStackTrace();
			}
		}

	}

	class BusStopHttpGetAsyncTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();

			HttpGet httpGet = new HttpGet("http://stud-tfl.cs.ucl.ac.uk/stops?rad=" + params[0] + "," + params[1] + "," + params[2]);

			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);

				BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				String json = reader.readLine();
				JSONObject jsonObject = new JSONObject(json);
				jsonBusStopArray = jsonObject.getJSONArray("stations");

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
							.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bus_icon))));
					busList.put(marker, new BusStop(jsonCurrentObject.getString("stopName"), jsonCurrentObject.getString("stopCode")));
					if(!busToggle.isChecked()){
						marker.setVisible(false);
					}
					busMarkerList.add(marker);
					index++;
				}
			} catch (JSONException e) {
				System.out.println("Error in the JSON parsing");
				e.printStackTrace();
			}
		}
	}
	
	/*class QueryTubeStationAsyncTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			
			Point currentLocation = new Point(Double.parseDouble(params[0]), Double.parseDouble(params[1]));
			int mapArea = DatabaseClassificator.getInstance().checkTubeStationMapArea(currentLocation);
			
			StationDAO stationDao = new StationDAO(MapActivity.this);
			stationDao.open();
			List<Station> stationsInArea = stationDao.getStationsByArea(mapArea);
			stationDao.close();
			
			return null;
			
		}
		
		@Override
		protected void onPostExecute(String result) {
		}
		
	}*/
	

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