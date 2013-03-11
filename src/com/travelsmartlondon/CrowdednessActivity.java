package com.travelsmartlondon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.travelsmartlondon.context.TSLApplicationContext;
import com.travelsmartlondon.entry.CommentEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class CrowdednessActivity extends ListActivity {

	public static final String ALMOST_EMPTY = "ALMOST EMPTY";
	public static final String NORMAL = "NORMAL";
	public static final String CROWDED = "CROWDED";
	public static final String VERY_CROWDED = "VERY CROWDED";
	public static final String EXTREMELY_CROWDED = "EXTREMELY CROWDED";

	private String _crowdednessLevel;
	private String _crowdQueryTime;
	private String _currentStationName;
	private String _currentStationNLC;
	private boolean _isLoggedIn;
	private ArrayAdapter<CommentEntry> _commentAdapter;
	private Spinner spinner;

	private Intent intent;

	private int[] _imageViews = { R.id.imageview1, R.id.imageview2,
			R.id.imageview3, R.id.imageview4, R.id.imageview5 };

	@Override
	protected void onCreate(Bundle savedInstanceState_) {
		super.onCreate(savedInstanceState_);
		setContentView(R.layout.crowdedness);

		_isLoggedIn = TSLApplicationContext.getInstance()
				.checkIfUserIsLoggedIn();

		intent = getIntent();
		_currentStationName = intent.getStringExtra(MapActivity.TUBE_NAME);
		setTitle(_currentStationName);

		_currentStationNLC = intent.getStringExtra(MapActivity.STATION_NLC);

		HttpGetCrowdednessAsyncTask asyncTask = new HttpGetCrowdednessAsyncTask();
		asyncTask.execute(_currentStationNLC,
				intent.getStringExtra(MapActivity.CURRENT_TIME));

		// Gets the latest comments from the api.
		refreshCommentsList();

		TextView crowdedness = (TextView) findViewById(R.id.crowdednessLevel);
		crowdedness.setText(this._crowdednessLevel);

		spinner = (Spinner) findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.time, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		Button button = (Button) findViewById(R.id.give_comment_comment);
		button.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (_isLoggedIn) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CrowdednessActivity.this);
					builder.setTitle(_currentStationName);
					builder.setMessage("Post your comment:");
					final EditText input = new EditText(
							CrowdednessActivity.this);
					builder.setView(input);
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {

									String comment = input.getText().toString();

									HttpPostCommentAsyncTask asyncTaskpostComments = new HttpPostCommentAsyncTask();
									asyncTaskpostComments.execute(
											_currentStationNLC, comment);

									refreshCommentsList();
								}

							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}

							});
					builder.show();

				}
				
				else{
					
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CrowdednessActivity.this);
					builder.setTitle(_currentStationName);
					builder.setMessage("Please log in to post a comment!");
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									
								}});
					builder.show();
				}
			}

		});
	}

	// gets the latest comments from the api
	private void refreshCommentsList() {
		HttpGetCommentsAsyncTask asyncTaskComments = new HttpGetCommentsAsyncTask();
		asyncTaskComments.execute(_currentStationNLC);
	}

	protected void onResume() {
		super.onResume();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				String timeToQuery = parent.getItemAtPosition(pos).toString();
				if (!timeToQuery.equals("Time")) {
					timeToQuery = timeToQuery.substring(0, 2)
							+ timeToQuery.substring(3, 5);

					HttpGetCrowdednessAsyncTask asyncTask = new HttpGetCrowdednessAsyncTask();
					asyncTask.execute(_currentStationNLC, timeToQuery);

				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void setCrowdednessImages(int m_) {
		for (int image : _imageViews) {
			ImageView imageview = (ImageView) findViewById(image);
			imageview.setVisibility(View.INVISIBLE);
		}

		for (int n = 0; n < m_; n++) {
			ImageView imageview = (ImageView) findViewById(_imageViews[n]);
			imageview.setVisibility(View.VISIBLE);
		}
	}

	private class HttpGetCrowdednessAsyncTask extends
			AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String stationNLC = params[0];
			String queryTime = params[1];

			_crowdQueryTime = queryTime.substring(0, 2) + ":"
					+ queryTime.substring(2, 4);

			HttpClient httpClient = new DefaultHttpClient();

			HttpGet httpGet = new HttpGet(
					"http://stud-tfl.cs.ucl.ac.uk/crowd?stop=" + stationNLC
							+ "," + queryTime);

			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(httpResponse.getEntity()
								.getContent(), "UTF-8"));
				_crowdednessLevel = reader.readLine();
				_crowdednessLevel = _crowdednessLevel.substring(1,
						_crowdednessLevel.length() - 1);

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

			if (_crowdednessLevel.equals(ALMOST_EMPTY)) {
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(ALMOST_EMPTY);
				crowdednessLevel.setTextColor(Color.rgb(0, 255, 0));
				setCrowdednessImages(1);
			} else if (_crowdednessLevel.equals(NORMAL)) {
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(NORMAL);
				crowdednessLevel.setTextColor(Color.rgb(0, 255, 0));
				setCrowdednessImages(2);
			} else if (_crowdednessLevel.equals(CROWDED)) {
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(CROWDED);
				crowdednessLevel.setTextColor(Color.rgb(255, 255, 0));
				setCrowdednessImages(3);
			} else if (_crowdednessLevel.equals(VERY_CROWDED)) {
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(VERY_CROWDED);
				crowdednessLevel.setTextColor(Color.rgb(255, 140, 0));
				setCrowdednessImages(4);
			} else if (_crowdednessLevel.equals(EXTREMELY_CROWDED)) {
				TextView crowdednessLevel = (TextView) findViewById(R.id.crowdednessLevel);
				crowdednessLevel.setText(EXTREMELY_CROWDED);
				crowdednessLevel.setTextColor(Color.rgb(238, 64, 0));
				setCrowdednessImages(5);
			}
		}

	}

	private class HttpPostCommentAsyncTask extends
			AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String stationNLC = params[0];
			String comment = "";
			try {
				comment = URLEncoder.encode(params[1], "UTF-8");
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			HttpClient httpClient = new DefaultHttpClient();

			HttpPost httppost;
			try {
				String hashedUser = TSLApplicationContext.getInstance().currentSHA1EmailAddress();
				httppost = new HttpPost(
						"http://stud-tfl.cs.ucl.ac.uk/postcomment?forStation="
								+ stationNLC + ",user="+hashedUser+
										",comment="
								+ comment);

				httpClient.execute(httppost);

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	private class HttpGetCommentsAsyncTask extends
			AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String stationNLC = params[0];

			HttpClient httpClient = new DefaultHttpClient();

			HttpGet httpGet = new HttpGet(
					"http://stud-tfl.cs.ucl.ac.uk/getcomments?fetchAllForStation="
							+ stationNLC);

			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(httpResponse.getEntity()
								.getContent(), "UTF-8"));

				String jsonString = reader.readLine();

				reader.close();

				JSONArray jsonArrayComments = new JSONArray(jsonString);

				ArrayList<CommentEntry> comments = new ArrayList<CommentEntry>();

				for (int n = 1; n < jsonArrayComments.length(); n++) {

					JSONObject commentEntryObject = jsonArrayComments
							.getJSONObject(n);

					String userName = commentEntryObject.getString("userName");
					String comment = URLDecoder.decode(
							commentEntryObject.getString("comment"), "UTF-8");

					CommentEntry commentEntry = new CommentEntry(userName,
							comment);

					comments.add(0, commentEntry);
				}

				_commentAdapter = new CommentAdapter(CrowdednessActivity.this,
						R.layout.comment_row, comments);

			} catch (ClientProtocolException cpe) {
				System.out.println("Client Protocol Exception :" + cpe);
				cpe.printStackTrace();
			} catch (IOException ioe) {
				System.out.println("IO Exception :" + ioe);
				ioe.printStackTrace();
			} catch (JSONException je) {
				System.out.println("JSON Exception :" + je);
				je.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(_commentAdapter);
		}

	}

	private class CommentAdapter extends ArrayAdapter<CommentEntry> {

		private ArrayList<CommentEntry> _comments;

		public CommentAdapter(Context context, int textViewResourceId,
				ArrayList<CommentEntry> comments) {
			super(context, textViewResourceId, comments);
			this._comments = comments;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View newView = convertView;
			if (newView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				newView = layoutInflater.inflate(R.layout.comment_row, null);
			}
			CommentEntry cm = _comments.get(position);
			if (cm != null) {
				TextView commentText = (TextView) newView
						.findViewById(R.id.comment);
				ImageView commentImg = (ImageView) newView
						.findViewById(R.id.commentImg);
				if (commentText != null) {
					commentText.setText(cm.getComment());
				}
				if (commentImg != null) {
					commentImg.setImageResource(R.drawable.speech_bubble);
				}
			}
			return newView;
		}

	}
}
