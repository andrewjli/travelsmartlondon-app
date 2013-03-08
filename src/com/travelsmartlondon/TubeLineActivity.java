package com.travelsmartlondon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.travelsmartlondon.context.TSLApplicationContext;

public class TubeLineActivity extends ListActivity {
	
	public static final String PICCADILLY_LINE = "Piccadilly";
	public static final String DISTRICT_LINE = "District";
	public static final String VICTORIA_LINE = "Victoria";
	public static final String CIRCLE_LINE = "Circle";
	public static final String HAMMERSMITH_CITY_LINE = "Hammersmith and City";
	public static final String BAKERLOO_LINE = "Bakerloo";
	public static final String WATERLOO_CITY_LINE = "Waterloo and City";
	public static final String CENTRAL_LINE = "Central";
	public static final String JUBILEE_LINE = "Jubilee";
	public static final String METROPOLITAN_LINE = "Metropolitan";
	public static final String NORTHERN_LINE = "Northern";
	public static final String DLR_LINE = "DLR";
	public static final String OVERGROUND_LINE = "Overground";
	public static final String RATING_STRING_NULL = "null";
	
	public static final URI LINES_TSL_URI = URI.create("http://stud-tfl.cs.ucl.ac.uk/lines");
	public static final URI RATING_TSL_URI = URI.create("http://stud-tfl.cs.ucl.ac.uk/getratings?fetchall");
	public static final String POST_RATING_UPDATE_TSL_BASE_URI_STRING =  "http://stud-tfl.cs.ucl.ac.uk/postratings?foruser=";
	public static final String GET_OWN_RATINGS = "http://stud-tfl.cs.ucl.ac.uk/getratings?fetchforuser=";
	
	public static final String MAP_LINE = "Line";
	public static final String MAP_STATUS = "Status";
	public static final String MAP_RATING = "Rating";
	public static final String MAP_RATING_NUMBER = "Number";
	
	private List<Map<String,Object>> _list;
	private SimpleAdapter _adapter;
	private Map<Integer, String> _positionLineMap;
	private Map<String, String> _lineStatusDetails;
	private Map<String, Float> _ratings;
	private Map<String, Integer> _numberOfRatingsPerLine;
	private boolean _isLoggedIn;
	
	@Override
	public void onRestoreInstanceState(Bundle restoreInstanceState_) {
		super.onRestoreInstanceState(restoreInstanceState_);
		getLineStatusUpdates();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState_) {
		super.onCreate(savedInstanceState_);
		setContentView(R.layout.activity_tube_line);
		
		this._positionLineMap = new ConcurrentHashMap<Integer, String>();
		this._list = new ArrayList<Map<String,Object>>();
		this._ratings = new ConcurrentHashMap<String, Float>();
		this._isLoggedIn = TSLApplicationContext.getInstance().checkIfUserIsLoggedIn();
		
		int resource = this._isLoggedIn ? R.layout.custom_tube_line_loggedin_view : R.layout.custom_tube_line_view;
		
		this._adapter = new NotSoSimpleAdapter(this, this._list,resource,
        									new String[] {MAP_LINE,MAP_STATUS, MAP_RATING_NUMBER},
        									new int[] {R.id.line_name_text, R.id.line_status_text, R.id.number_info_text}
											);
		getLineStatusUpdates();
		
	}
	
	private Map<String, Object> getMapFor(String line_, String status_, Float rating_, Integer numRatings_) {
		Map<String, Object> map = new ConcurrentHashMap<String, Object>();
		map.put(MAP_LINE, line_);
		map.put(MAP_STATUS, status_);
		map.put(MAP_RATING, rating_);
		if(numRatings_.equals(1)) {
			map.put(MAP_RATING_NUMBER, "1 user rated");
		} else { 
			map.put(MAP_RATING_NUMBER, numRatings_ + " users rated");
		}
		return map;
	}
	
	/**
	 * Calculates rating and number of users who rated
	 * @param entries_
	 * @param modeReturnRating_ : true-> returns rating; false -> returns number of users
	 * @return
	 * @throws JSONException
	 */
	private Map<String, Float> calculateRating(List<JSONObject> entries_, Boolean modeReturnRating_) throws JSONException {
		Map<String, Float> ratings = new ConcurrentHashMap<String, Float>();
		for(JSONObject obj : entries_) {
			int numberOfZero = obj.getInt("0");
			int numberOfOne = obj.getInt("1");
			int numberOfTwo = obj.getInt("2");
			int numberOfThree = obj.getInt("3");
			int numberOfFour = obj.getInt("4");
			int numberOfFive = obj.getInt("5");
			
			int count = numberOfZero + numberOfOne + numberOfTwo + numberOfThree + numberOfFour + numberOfFive;
			String line = obj.getString("line").replace('_', ' ');
			
			if(modeReturnRating_) {
				float average = (count != 0) ? (numberOfOne + numberOfTwo*2 + numberOfThree*3 + numberOfFour*4 + numberOfFive*5)/count : 0;
				if(TSLApplicationContext.getInstance().lineIsRated(line)) {
					if(count==0) { 
						average = TSLApplicationContext.getInstance().fetchOwnRatingForLine(line);
					} else if(count > 0 && count < 5) { 
						average = (3 * TSLApplicationContext.getInstance().fetchOwnRatingForLine(line) + average) / 4;
					} else if(count > 4 && count < 11) { 
						average = (TSLApplicationContext.getInstance().fetchOwnRatingForLine(line) + average) / 2;
					} else if(count > 10 && count < 31) {
						average = (TSLApplicationContext.getInstance().fetchOwnRatingForLine(line) + average * 3) / 4;
					} else { 
						average = (TSLApplicationContext.getInstance().fetchOwnRatingForLine(line) + average * 5) / 6;
					} 
				}
				ratings.put(line, average);
			} else {
				ratings.put(line, (float) count);
			}
		}
		return ratings;
	}

	
	private Map<String, Integer> mapStringFloatToMapStringInt(Map<String, Float> first_) {
		Map<String, Integer> second = new ConcurrentHashMap<String, Integer>();
		for (String s : first_.keySet()) {
			second.put(s, first_.get(s).intValue());
		}
		return second;
	}
	
	private class PostRatingHttpAsyncTask extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			HttpParams httpParams = new BasicHttpParams();
		    SchemeRegistry registry = new SchemeRegistry();
		    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		    ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, registry);
			HttpClient httpClient = new DefaultHttpClient(cm, httpParams);
			
			int rating = (int) Math.ceil(Float.parseFloat(params[1]));
			String line = params[0].replace(" ","_");
			String userName = params[2];
			
			HttpGet httpPostRatingByHttpGet = new HttpGet(POST_RATING_UPDATE_TSL_BASE_URI_STRING + userName+ "," + line + "=" + rating);
			
			try {
				HttpResponse httpResponse = httpClient.execute(httpPostRatingByHttpGet);
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
				String jsonString = reader.readLine();
				reader.close();
				
				JSONArray jsonArrayRatings = new JSONArray(jsonString);
				
				List<JSONObject> list = new ArrayList<JSONObject>();
			    for(int i = 0; i < jsonArrayRatings.length(); i ++) {
			    	list.add(jsonArrayRatings.getJSONObject(i));
			    }
			    
			    _ratings = calculateRating(list, true);
			    _numberOfRatingsPerLine = mapStringFloatToMapStringInt(calculateRating(list, false));
				
			} catch (ClientProtocolException cpe) {
				System.out.println("Client Protocol Exception :" + cpe);
				cpe.printStackTrace();
			} catch (IOException ioe) {
				System.out.println("IOException :" + ioe);
				ioe.printStackTrace();
			} catch (NullPointerException npe) {
				System.out.println(" Null Pointer Exception :" + npe);
				npe.printStackTrace();
			} catch (JSONException e) {
				System.out.println(" JSONException, malformatted json :" + e);
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	private class FetchStatusUpdatesHttpAsyncTask extends AsyncTask<String, Void, Void>{
		
		@Override
		protected Void doInBackground(String... params) {

			HttpParams httpParams = new BasicHttpParams();
		    SchemeRegistry registry = new SchemeRegistry();
		    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		    ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, registry);
			HttpClient httpClient = new DefaultHttpClient(cm, httpParams);
			HttpGet httpGetStatus = new HttpGet(LINES_TSL_URI);
			HttpGet httpGetRating = new HttpGet(RATING_TSL_URI);
			
			try {
				HttpResponse httpResponseStatus = httpClient.execute(httpGetStatus);
				HttpResponse httpResponseRating = httpClient.execute(httpGetRating);


			    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponseStatus.getEntity().getContent(), "UTF-8"));
			    
					    
			    Map<String, String> linesStatus = new LinkedHashMap<String, String>();
			    _lineStatusDetails = new HashMap<String,String>();
			    
			    try {
			    	String jsonStatus = reader.readLine(); 
			    	
			    	reader = new BufferedReader(new InputStreamReader(httpResponseRating.getEntity().getContent(), "UTF-8"));
			    	String jsonRating = reader.readLine();
			    	
				    JSONArray jsonArrayStatus = new JSONArray(jsonStatus);
				    JSONArray jsonArrayRatings = new JSONArray(jsonRating);
				    
				    if(_isLoggedIn) { 
				    	String userName = TSLApplicationContext.getInstance().currentSHA1EmailAddress();
						HttpGet httpGetOwnRating = new HttpGet(GET_OWN_RATINGS + userName); 
						HttpResponse httpResponseOwnRating = httpClient.execute(httpGetOwnRating);
						reader = new BufferedReader(new InputStreamReader(httpResponseOwnRating.getEntity().getContent(), "UTF-8"));
				    	String jsonOwnRating = reader.readLine();
						JSONObject jsonObjectOwnRatings = new JSONObject(jsonOwnRating);
						updateRatings(jsonObjectOwnRatings);
					}
				    
				    reader.close();
				    
				    List<JSONObject> list = new ArrayList<JSONObject>();
				    for(int i = 0; i < jsonArrayRatings.length(); i ++) {
				    	list.add(jsonArrayRatings.getJSONObject(i));
				    }
				    
				    
				    
				    _ratings = calculateRating(list, true);
				    _numberOfRatingsPerLine = mapStringFloatToMapStringInt(calculateRating(list, false));
				    
				    for(int i = 0; i < jsonArrayStatus.length(); i++){
				    	JSONObject temp = (JSONObject) jsonArrayStatus.getJSONObject(i);
				    	String line = temp.getString("lineName");
				    	String status = temp.getString("statusDescription");
				    	String statusDetails = temp.getString("statusDetails");
				    	if(status.equalsIgnoreCase("Good service")) { 
				    		statusDetails = "There is currently good service on this line.";
				    	}
				    	_lineStatusDetails.put(line, statusDetails);
				    	linesStatus.put(line, status);
				    }
			    } catch(IOException e) {
			    	//no lines returned - this is handled correctly automatically
			    } catch (JSONException e) {
			    	System.err.println("incorrect json handling");
			    }

			    
			    Set<String> lines = linesStatus.keySet();
			    if(!lines.contains(PICCADILLY_LINE)) {
			    	linesStatus.put(PICCADILLY_LINE, "Status unavailable");
			    	_lineStatusDetails.put(PICCADILLY_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(DISTRICT_LINE)) {
			    	linesStatus.put(DISTRICT_LINE, "Status unavailable");
			    	_lineStatusDetails.put(DISTRICT_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(VICTORIA_LINE)) {
			    	linesStatus.put(VICTORIA_LINE, "Status unavailable");
			    	_lineStatusDetails.put(VICTORIA_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(CIRCLE_LINE)) {
			    	linesStatus.put(CIRCLE_LINE, "Status unavailable");
			    	_lineStatusDetails.put(CIRCLE_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(HAMMERSMITH_CITY_LINE)) {
			    	linesStatus.put(HAMMERSMITH_CITY_LINE, "Status unavailable");
			    	_lineStatusDetails.put(HAMMERSMITH_CITY_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(BAKERLOO_LINE)) {
			    	linesStatus.put(BAKERLOO_LINE, "Status unavailable");
			    	_lineStatusDetails.put(BAKERLOO_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(WATERLOO_CITY_LINE)) {
			    	linesStatus.put(WATERLOO_CITY_LINE, "Status unavailable");
			    	_lineStatusDetails.put(WATERLOO_CITY_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(CENTRAL_LINE)) {
			    	linesStatus.put(CENTRAL_LINE, "Status unavailable");
			    	_lineStatusDetails.put(CENTRAL_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(JUBILEE_LINE)) {
			    	linesStatus.put(JUBILEE_LINE, "Status unavailable");
			    	_lineStatusDetails.put(JUBILEE_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(METROPOLITAN_LINE)) {
			    	linesStatus.put(METROPOLITAN_LINE, "Status unavailable");
			    	_lineStatusDetails.put(METROPOLITAN_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(NORTHERN_LINE)) {
			    	linesStatus.put(NORTHERN_LINE, "Status unavailable");
			    	_lineStatusDetails.put(NORTHERN_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(DLR_LINE)) {
			    	linesStatus.put(DLR_LINE, "Status unavailable");
			    	_lineStatusDetails.put(DLR_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} if(!lines.contains(OVERGROUND_LINE)) {
			    	linesStatus.put(OVERGROUND_LINE, "Status unavailable");
			    	_lineStatusDetails.put(OVERGROUND_LINE, "Status unavailable. Station closed or TFL cannot provide status");
				} 
				
				System.out.println(linesStatus.toString());
				
			    
				int i = 0;
			    for(String line : linesStatus.keySet()) {
			    	_positionLineMap.put(i, line);
			    	_list.add(getMapFor(line, linesStatus.get(line), _ratings.get(line), _numberOfRatingsPerLine.get(line)));
			    	i++;
			    }
			    
			} catch (ClientProtocolException cpe) {
				System.out.println("Client Protocol Exception :" + cpe);
				cpe.printStackTrace();
			} catch (IOException ioe) {
				System.out.println("IOException :" + ioe);
				ioe.printStackTrace();
			} catch (NullPointerException npe) {
				System.out.println(" Null Pointer Exception :" + npe);
				npe.printStackTrace();
			}
			return null;
		}

		private void updateRatings(JSONObject jsonOwnRatingObject_) throws JSONException {
			
	    	String piccadilly = jsonOwnRatingObject_.getString(PICCADILLY_LINE);
	    	String district = jsonOwnRatingObject_.getString(DISTRICT_LINE);
	    	String victoria = jsonOwnRatingObject_.getString(VICTORIA_LINE);
	    	String circle = jsonOwnRatingObject_.getString(CIRCLE_LINE);
	    	String hammmersmith = jsonOwnRatingObject_.getString(HAMMERSMITH_CITY_LINE.replace(" ", "_"));
	    	String bakerloo = jsonOwnRatingObject_.getString(BAKERLOO_LINE);
	    	String waterloo = jsonOwnRatingObject_.getString(WATERLOO_CITY_LINE.replace(" ", "_"));
	    	String central = jsonOwnRatingObject_.getString(CENTRAL_LINE);
	    	String jubilee = jsonOwnRatingObject_.getString(JUBILEE_LINE);
	    	String metropolitan = jsonOwnRatingObject_.getString(METROPOLITAN_LINE);
	    	String northern = jsonOwnRatingObject_.getString(NORTHERN_LINE);
	    	String dlr = jsonOwnRatingObject_.getString(DLR_LINE);
	    	String overground = jsonOwnRatingObject_.getString(OVERGROUND_LINE);
	    	
	    	if(piccadilly.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(PICCADILLY_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(PICCADILLY_LINE, piccadilly);
	    	}
	    	if(district.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(DISTRICT_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(DISTRICT_LINE, district);
	    	}
	    	if(victoria.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(VICTORIA_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(VICTORIA_LINE, victoria);
	    	}
	    	if(circle.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(CIRCLE_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(CIRCLE_LINE, circle);
	    	}
	    	if(hammmersmith.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(HAMMERSMITH_CITY_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(HAMMERSMITH_CITY_LINE, hammmersmith);
	    	}
	    	if(bakerloo.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(BAKERLOO_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(BAKERLOO_LINE, bakerloo);
	    	}
	    	if(waterloo.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(WATERLOO_CITY_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(WATERLOO_CITY_LINE, waterloo);
	    	}
	    	if(central.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(CENTRAL_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(CENTRAL_LINE, central);
	    	}
	    	if(jubilee.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(JUBILEE_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(JUBILEE_LINE, jubilee);
	    	}
	    	if(metropolitan.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(METROPOLITAN_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(METROPOLITAN_LINE, metropolitan);
	    	}
	    	if(northern.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(NORTHERN_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(NORTHERN_LINE, northern);
	    	}
	    	if(dlr.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(DLR_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(DLR_LINE, dlr);
	    	}
	    	if(overground.equals(RATING_STRING_NULL)) {
	    		TSLApplicationContext.getInstance().setRatingForLineToNull(OVERGROUND_LINE);
	    	} else {
	    		TSLApplicationContext.getInstance().submitRatingForLine(OVERGROUND_LINE, overground);
	    	}
		}

		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(_adapter);
		}			
	}
	
	private void getLineStatusUpdates() {
		AsyncTask<String, Void, Void> httpGetAsyncTask = new FetchStatusUpdatesHttpAsyncTask();
		httpGetAsyncTask.execute(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tube_line, menu);
		return true;
	}
	
	
	private class NotSoSimpleAdapter extends SimpleAdapter {

		public NotSoSimpleAdapter(Context context_,
				List<? extends Map<String, ?>> data_, int resource_,
				String[] from_, int[] to_) {
			super(context_, data_, resource_, from_, to_);
		}
		
		@SuppressLint("NewApi")
		@Override
		public View getView(int position_, View convertView_, ViewGroup parent_) {
			View view = super.getView(position_, convertView_, parent_);
			
			final String line = _positionLineMap.get(position_);
			
			View viewToClick;
			if(_isLoggedIn) {
				viewToClick = (TextView) view.findViewById(R.id.line_name_text);
			} else { 
				viewToClick = (LinearLayout) view.findViewById(R.id.line_layout);
			}
			
			final View list = viewToClick;
			
			View.OnClickListener listener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(TubeLineActivity.this);
					builder.setTitle("Status details");
					TextView textView = (TextView) list.findViewById(R.id.line_name_text);
					String line = textView.getText().toString();
					
					LayoutInflater inflater = TubeLineActivity.this.getLayoutInflater();
					
					LinearLayout localLayout = (LinearLayout) inflater.inflate(R.layout.status_details_dialog, null);
					TextView rating = (TextView) localLayout.findViewById(R.id.status_detail_message);
					rating.setText(_lineStatusDetails.get(line));
					
					if(_lineStatusDetails.containsKey(line)) {
						builder.setTitle("Status details for " + line + " line:"); 
						builder.setView(localLayout);
					} else {
						builder.setTitle("Error occured. Line status unavailable");
					}
					
					builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					      public void onClick(DialogInterface dialog, int which) {
					    	  dialog.dismiss();
					    } });
					
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			};
			
			list.setOnClickListener(listener);
			
			final LinearLayout ratingLayout = (LinearLayout) view.findViewById(R.id.rating_layout);
			final LinearLayout lowerLayout = (LinearLayout) view.findViewById(R.id.number_info_layout);
			
			boolean rated = TSLApplicationContext.getInstance().lineIsRated(line);
			
			if(_isLoggedIn && !rated) {
				final Button button = (Button) ratingLayout.findViewById(R.id.rate_button);
				final int position = position_;
				if(button != null) {
					button.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							AlertDialog.Builder builder = new AlertDialog.Builder(TubeLineActivity.this);
							builder.setMessage("Give rating and click RATE")
							       .setTitle("Give your rating to " + line + " line");
							LayoutInflater inflater = TubeLineActivity.this.getLayoutInflater();
							
							final LinearLayout localLayout = (LinearLayout) inflater.inflate(R.layout.give_rating_dialog, null);
							final RatingBar rating = (RatingBar) localLayout.findViewById(R.id.rating);
							
							builder.setView(localLayout);
	 
							builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
						               @Override
						               public void onClick(DialogInterface dialog, int id) {
						            	   AsyncTask<String, Void, Void> httpGetTask = new PostRatingHttpAsyncTask();
						            	   String stringFloatRating = (new Float(rating.getRating())).toString();
						           		   httpGetTask.execute(line, stringFloatRating, TSLApplicationContext.getInstance().currentSHA1EmailAddress()); 
						            	   _ratings.put(line, rating.getRating());
						            	   
						            	   TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
						            	   users.setText("You just rated " + line + " line with " + rating.getRating());
						            	   
						            	   RatingBar temporaryRatingBar = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
						            	   temporaryRatingBar.setRating(rating.getRating());
						            	   
						            	   button.setVisibility(View.GONE);
						            	   
						            	   TSLApplicationContext.getInstance().submitRatingForLine(line, rating.getRating());
						                   dialog.dismiss();
						               }
						           })
						           .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
						               public void onClick(DialogInterface dialog, int id) {
						                   dialog.dismiss();
						               }
						           });     
							AlertDialog dialog = builder.create();
							dialog.show();
						}
					});
				} else {
					System.out.println(line + " line gives a NullPointerException");
				}
				
			} else if(_isLoggedIn) {
				RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
				resultRating.setRating(_ratings.get(line));
			}
			
			if(line.equals(PICCADILLY_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_piccadilly);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(PICCADILLY_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(DISTRICT_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_district);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(DISTRICT_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(VICTORIA_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_victoria);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(VICTORIA_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(CIRCLE_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_circle);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(CIRCLE_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(HAMMERSMITH_CITY_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_hammersmith);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(HAMMERSMITH_CITY_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(BAKERLOO_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_bakerloo);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(BAKERLOO_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(WATERLOO_CITY_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_waterloo);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(WATERLOO_CITY_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(CENTRAL_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_central);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(CENTRAL_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(JUBILEE_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_jubilee);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(JUBILEE_LINE));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(METROPOLITAN_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_metropolitan);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(METROPOLITAN_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(NORTHERN_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_northern);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(NORTHERN_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(DLR_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_dlr);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(DLR_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} else if(line.equals(OVERGROUND_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_overground);
				if(_isLoggedIn) {
					RatingBar resultRating = (RatingBar) ratingLayout.findViewById(R.id.rating_bar);
	            	resultRating.setRating(_ratings.get(OVERGROUND_LINE));
	            	TextView users = (TextView) lowerLayout.findViewById(R.id.number_info_text);
	            	setNumberOfUSersInTextView(users, _numberOfRatingsPerLine.get(line));
	            	Button button = (Button) ratingLayout.findViewById(R.id.rate_button); 
	            	if(rated) {
	            		button.setVisibility(View.GONE);
	            	} else {
	            		button.setVisibility(0);
	            	}
				}
			} 
			return view;
		}
		
	}
	
	private void setNumberOfUSersInTextView(TextView textView_, int number_){
		if(number_ == 1) {
			textView_.setText("1 user rated");
		} else { 
			textView_.setText(number_ + " users rated");
		}
	}

}
