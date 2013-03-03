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
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
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
	
	public static final URI LINES_TSL_URI = URI.create("http://stud-tfl.cs.ucl.ac.uk/lines");
	
	public static final String MAP_LINE = "Line";
	public static final String MAP_STATUS = "Status";
	
	private List<Map<String,String>> _list;
	private SimpleAdapter _adapter;
	private Map<Integer, String> _positionLineMap;
	private Map<String, String> _lineStatusDetails;
	private boolean _isLoggedIn;
	
	private Map<Integer, Boolean> _ratingBarWasDisplayedAtPosition;

	/*
	@Override
	public void onStart() {
	    getLineStatusUpdatesOnResume();
	}
	*/
	
	
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
		this._list = new ArrayList<Map<String,String>>();
		this._ratingBarWasDisplayedAtPosition = initialiseWasDisplayedAtPositionMap();
		this._isLoggedIn = TSLApplicationContext.getInstance().checkIfUserIsLoggedIn();
		
		int resource = this._isLoggedIn ? R.layout.custom_tube_line_loggedin_view : R.layout.custom_tube_line_view;
		
		this._adapter = new NotSoSimpleAdapter(this, this._list,resource,
        									new String[] {MAP_LINE,MAP_STATUS},
        									new int[] {R.id.line_name_text, R.id.line_status_text}
											);
		getLineStatusUpdates();
		
	}
	
	private Map<Integer, Boolean> initialiseWasDisplayedAtPositionMap() {
		Map<Integer, Boolean> map = new ConcurrentHashMap<Integer, Boolean>();
		for(int i = 0; i < 20; i++) {
			map.put(i, false);
		}
		return map;
	}
	
	private Map<String, String> getMapFor(String line_, String status_) {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put(MAP_LINE, line_);
		map.put(MAP_STATUS, status_);
		return map;
	}
	
	private class FetchStatusUpdatesHttpAsyncTask extends AsyncTask<String, Void, Void>{
		
		@Override
		protected Void doInBackground(String... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(LINES_TSL_URI);
			
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);

			    BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			    
			    String json = "";
			    
			    Map<String, String> linesStatus = new LinkedHashMap<String, String>();
			    _lineStatusDetails = new HashMap<String,String>();
			    
			    try {
				    json = reader.readLine(); 
				    JSONObject jsonObject = new JSONObject(json);
				    
				    for(int i = 0; i < jsonObject.length() - 1; i++){
				    	String index = Integer.toString(i);
				    	JSONObject temp = (JSONObject) jsonObject.get(index);
				    	String line = temp.getString("lineName");
				    	String status = temp.getString("statusDescription");
				    	String statusDetails = temp.getString("statusDetails");
				    	_lineStatusDetails.put(line, statusDetails);
				    	linesStatus.put(line, status);
			    };
			    } catch(IOException e) {
			    	//no lines returned - this is handled correctly automatically
			    } catch (JSONException e) {
			    	//no lines returned - this is handled correctly automatically
			    }

			    int i=0;
			    
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
			    
			    for(String line : linesStatus.keySet()) {
			    	_positionLineMap.put(i, line);
			    	_list.add(getMapFor(line, linesStatus.get(line)));
			    	i++;
			    }
			    
			    System.out.println(_list.toString());
			    

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

		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(_adapter);
		}			
	}
	
	private void getLineStatusUpdatesOnResume() {
		/*AsyncTask<String, Void, Void> httpGetAsyncTask = new FetchStatusUpdatesHttpAsyncTask() {
			@Override
			protected void onPostExecute(Void result) {
		    	for(Integer i : _positionLineMap.keySet()) {
		    		View view = (View) _adapter.getView(i, findViewById(R.id.tube_list_item), (ViewGroup) findViewById(R.layout.custom_tube_line_view));
		    		((TextView) view.findViewById(R.id.line_status_text)).setText(_list.get(i).get(1));
		    	}
			}	
		};
		httpGetAsyncTask.execute(); */
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
			
			System.out.println("getView called at position: " + position_);
			System.out.println("station at this position is: " + _positionLineMap.get(position_));
			
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
					if(_lineStatusDetails.containsKey(line)) {
					builder.setTitle(_lineStatusDetails.get(line)); 
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
			
			final String line = _positionLineMap.get(position_);

			if(_isLoggedIn && !_ratingBarWasDisplayedAtPosition.get(position_)) {
				final LinearLayout ratingLayout = (LinearLayout) view.findViewById(R.id.rating_layout);
				Button button = (Button) ratingLayout.findViewById(R.id.rate_button);
				final int position = position_;
				if(button != null) {
					button.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// 1. Instantiate an AlertDialog.Builder with its constructor
							AlertDialog.Builder builder = new AlertDialog.Builder(TubeLineActivity.this);
	
							// 2. Chain together various setter methods to set the dialog characteristics
							builder.setMessage("Give rating and click RATE")
							       .setTitle("Give your rating to " + line + " line");
							LayoutInflater inflater = TubeLineActivity.this.getLayoutInflater();
							
							final LinearLayout localLayout = (LinearLayout) inflater.inflate(R.layout.give_rating_dialog, null);
							final RatingBar rating = (RatingBar) localLayout.findViewById(R.id.rating);
							
							builder.setView(localLayout);
	 
							builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
						               @Override
						               public void onClick(DialogInterface dialog, int id) {
						            	   RatingBar resultRating = new RatingBar(TubeLineActivity.this, null, android.R.attr.ratingBarStyleIndicator);
						            	   ratingLayout.removeAllViews();
						            	   resultRating.setRating(rating.getRating());
						            	   ratingLayout.addView(resultRating);
						            	   _ratingBarWasDisplayedAtPosition.put(position,true);
						                   dialog.dismiss();
						               }
						           })
						           .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
						               public void onClick(DialogInterface dialog, int id) {
						                   dialog.dismiss();
						               }
						           });     
							// 3. Get the AlertDialog from create()
							AlertDialog dialog = builder.create();
							dialog.show();
						}
					});
				} else {
					System.out.println(line + " line gives a NullPointerException");
				}
				
			}
			
			
			if(line.equals(PICCADILLY_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_piccadilly);
			} else if(line.equals(DISTRICT_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_district);
			} else if(line.equals(VICTORIA_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_victoria);
			} else if(line.equals(CIRCLE_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_circle);
			} else if(line.equals(HAMMERSMITH_CITY_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_hammersmith);
			} else if(line.equals(BAKERLOO_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_bakerloo);
			} else if(line.equals(WATERLOO_CITY_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_waterloo);
			} else if(line.equals(CENTRAL_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_central);
			} else if(line.equals(JUBILEE_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_jubilee);
			} else if(line.equals(METROPOLITAN_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_metropolitan);
			} else if(line.equals(NORTHERN_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_northern);
			} else if(line.equals(DLR_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_dlr);
			} else if(line.equals(OVERGROUND_LINE)) {
				view.setBackgroundResource(R.drawable.gradient_overground);
			} 
			
			return view;
		}
		
	}

}
