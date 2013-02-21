package com.travelsmartlondon;

import com.travelsmartlondon.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class CrowdednessActivity extends Activity {
	
	public static final String ALMOST_EMPTY = "ALMOST EMPTY";
	public static final String NORMAL = "NORMAL";
	public static final String CROWDED = "CROWDED";
	public static final String VERY_CROWDED = "VERY CROWDED";
	public static final String EXTREMELY_CROWDED = "EXTREMELY CROWDED";
	
	
	private String _currentCrowdedness;
	private int[] _imageViews = {
		    R.id.imageview1,
		    R.id.imageview2,
		    R.id.imageview3,
		    R.id.imageview4,
		    R.id.imageview5
		    };

	@Override
	protected void onCreate(Bundle savedInstanceState_) {
		super.onCreate(savedInstanceState_);
		setContentView(R.layout.crowdedness);
		
		this._currentCrowdedness = NORMAL ;
		
        setTitle("Euston");
		
		TextView crowdedness = (TextView) findViewById(R.id.expectedcrowdedness);
		crowdedness.setText(this._currentCrowdedness);
		
		
		if(this._currentCrowdedness.equals(ALMOST_EMPTY)){
			this.setCrowdednessImages(1);
		} 
		else if (_currentCrowdedness.equals(NORMAL)){
			this.setCrowdednessImages(2);
		}
		else if (_currentCrowdedness.equals(CROWDED)){
			this.setCrowdednessImages(3);
		}
		else if (_currentCrowdedness.equals(VERY_CROWDED)){
			this.setCrowdednessImages(4);
		}
		else if (_currentCrowdedness.equals(EXTREMELY_CROWDED)){
			this.setCrowdednessImages(5);
		}
		
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.time, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setSelection(adapter.getPosition("15:00pm"));
	}
	private void setCrowdednessImages(int m_)
	{
		
		for(int n=0;n<m_;n++)
		{
			ImageView imageview = (ImageView) findViewById(_imageViews[n]);
			imageview.setImageResource(R.drawable.crowdednesswatermark);
		}
	}

}
