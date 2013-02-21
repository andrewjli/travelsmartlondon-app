package com.travelsmartlondon;


import com.travelsmartlondon.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ListView _mainListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this._mainListView = (ListView) findViewById(R.id.main_activity_listView);
	    String[] items = new String[] {"Show Map", "Tube Lines", "Log in", "About Travel Smart London"};
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
	    this._mainListView.setAdapter(adapter);
	    this._mainListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if( ((TextView)view).getText().toString() == "Show Map"){
					Intent intent = new Intent(MainActivity.this,MapActivity.class);
					startActivity(intent);
				} else if(((TextView)view).getText().toString() == "Tube Lines"){
					//TODO: Implement the screen
				} else if(((TextView)view).getText().toString() == "Log in") {
					Intent intent = new Intent(MainActivity.this,LoginActivity.class);
					startActivity(intent);
				}
			}
	    });
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	 */
}
