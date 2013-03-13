package com.travelsmartlondon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TubeIntermediateActivity extends Activity {
	private List<Map<String,String>> _entryList;
	private SimpleAdapter _adapter;
	private Button _closeButton;
	private ListView _mainListView;
	private Map<String, String> _codeToLineMap;
	
	public static final String ID_CODE = "com.travelsmartlondon.ID_CODE";
	public static final String EXTRA_MESSAGE = "come.travelsmartlondon.EXTRA_MESSAGE";
	public static final String TUBE_LINES = "com.travelsmartlondon.TUBE_LINES";
	public static final String HAMMERSMITH_CIRCLE = "Hammersmith or Circle";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tube_countdown_intermediate);
		Intent intent = getIntent();
        final String input = intent.getStringExtra(MapActivity.ID_CODE);
        final String name = intent.getStringExtra(MapActivity.EXTRA_MESSAGE);
        List<String> lines = intent.getStringArrayListExtra(MapActivity.TUBE_LINES);
        setTitle(name);
        
        this._codeToLineMap = populateMap();
        this._entryList = new ArrayList<Map<String,String>>(); 

        this._adapter = new SimpleAdapter(this, _entryList,
        								R.layout.custom_intermediatescreen_view,
        								new String[] {"Line"},
        								new int[] {R.id.line_name_text_intermediate}
        								) {
        	@Override
    		public View getView(int position_, View convertView_, ViewGroup parent_) {
    			View view = super.getView(position_, convertView_, parent_);
    			String line = _entryList.get(position_).get("Line");
    			
    			if(line.equals(TubeLineActivity.BAKERLOO_LINE)) {
    				view.setBackgroundResource(R.drawable.gradient_bakerloo);
    			} else if(line.equals(TubeLineActivity.CENTRAL_LINE)) {
    				view.setBackgroundResource(R.drawable.gradient_central);
    			} else if(line.equals(TubeLineActivity.VICTORIA_LINE)) {
    				view.setBackgroundResource(R.drawable.gradient_victoria);
    			} else if(line.equals(TubeLineActivity.PICCADILLY_LINE)) {
    				view.setBackgroundResource(R.drawable.gradient_piccadilly);
    			} else if(line.equals(TubeLineActivity.METROPOLITAN_LINE)) {
    				view.setBackgroundResource(R.drawable.gradient_metropolitan);
    			} else if(line.equals(HAMMERSMITH_CIRCLE)) {
    				view.setBackgroundResource(R.drawable.gradient_hammersmith_circle);
    			} else if(line.equals(TubeLineActivity.JUBILEE_LINE)) {
    				view.setBackgroundResource(R.drawable.gradient_jubilee);
    			} else if(line.equals(TubeLineActivity.WATERLOO_CITY_LINE)) {
    				view.setBackgroundResource(R.drawable.gradient_waterloo);
    			} else if(line.equals(TubeLineActivity.DISTRICT_LINE)) {
    				view.setBackgroundResource(R.drawable.gradient_district);
    			} else if(line.equals(TubeLineActivity.NORTHERN_LINE)) {
    				view.setBackgroundResource(R.drawable.gradient_northern);
    			} 
    			
    			return view;
        	}
        };
        
        this._closeButton = (Button) findViewById(R.id.close_button);
		this._closeButton.setBackgroundColor(Color.DKGRAY);
		this._closeButton.setOnClickListener(new OnClickListener() {
		    									@Override
		    									public void onClick(View v) {
		    										finish();
		    									}
											});
		for(String line : lines) {
			Map<String, String> linesMap = new ConcurrentHashMap<String, String>();
			linesMap.put("Line", this._codeToLineMap.get(line.trim()));
			this._entryList.add(linesMap);
		}
		
		this._mainListView = (ListView) findViewById(R.id.intermediate_activity_list);
		this._mainListView.setAdapter(this._adapter);
		
		this._mainListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent newIntent = new Intent(TubeIntermediateActivity.this, TubeCountdownActivity.class);
				newIntent.putExtra(ID_CODE, input);
				newIntent.putExtra(EXTRA_MESSAGE, name);
				//intent.putStringArrayListExtra(TUBE_LINES, tubeList.get(marker).getLinesAsArrayList());
				ArrayList<String> lineList = new ArrayList<String>();
				String lineClicked = ((TextView)view.findViewById(R.id.line_name_text_intermediate)).getText().toString();
				if(lineClicked.equals(TubeLineActivity.BAKERLOO_LINE)) {
					lineList.add("B");
				} else if(lineClicked.equals(TubeLineActivity.CENTRAL_LINE)) {
					lineList.add("C");
				} else if(lineClicked.equals(TubeLineActivity.VICTORIA_LINE)) {
					lineList.add("V");
				} else if(lineClicked.equals(TubeLineActivity.DISTRICT_LINE)) {
					lineList.add("D");
				} else if(lineClicked.equals(HAMMERSMITH_CIRCLE)) {
					lineList.add("H");
				} else if(lineClicked.equals(TubeLineActivity.JUBILEE_LINE)) {
					lineList.add("J");
				} else if(lineClicked.equals(TubeLineActivity.METROPOLITAN_LINE)) {
					lineList.add("M");
				} else if(lineClicked.equals(TubeLineActivity.NORTHERN_LINE)) {
					lineList.add("N");
				} else if(lineClicked.equals(TubeLineActivity.PICCADILLY_LINE)) {
					lineList.add("P");
				} else if(lineClicked.equals(TubeLineActivity.WATERLOO_CITY_LINE)) {
					lineList.add("W");
				}
				newIntent.putStringArrayListExtra(TUBE_LINES, lineList);
				startActivity(newIntent);
			} 
		});
	}
	
	private Map<String, String> populateMap() {
		Map<String, String> codeLineMap = new ConcurrentHashMap<String, String>();
		codeLineMap.put("B", TubeLineActivity.BAKERLOO_LINE);
		codeLineMap.put("C", TubeLineActivity.CENTRAL_LINE);
		codeLineMap.put("D", TubeLineActivity.DISTRICT_LINE);
		codeLineMap.put("H", HAMMERSMITH_CIRCLE);
		codeLineMap.put("J", TubeLineActivity.JUBILEE_LINE);
		codeLineMap.put("M", TubeLineActivity.METROPOLITAN_LINE);
		codeLineMap.put("N", TubeLineActivity.NORTHERN_LINE);
		codeLineMap.put("P", TubeLineActivity.PICCADILLY_LINE);
		codeLineMap.put("V", TubeLineActivity.VICTORIA_LINE);
		codeLineMap.put("W", TubeLineActivity.WATERLOO_CITY_LINE);
		return codeLineMap;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tube_intermediate, menu);
		return true;
	}

}
