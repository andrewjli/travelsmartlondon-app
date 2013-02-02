package com.travelsmartlondon;


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

	private ListView main_listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		main_listView = (ListView) findViewById(R.id.main_activity_listView);
	    String[] items = new String[] {"Show Map", "Tube Lines", "Log in", "About Travel Smart London"};
	    ArrayAdapter<String> adapter =
	      new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

	    main_listView.setAdapter(adapter);
	    
	    main_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				if( ((TextView)view).getText().toString() == "Show Map")
				{
					Intent intent = new Intent(MainActivity.this,ShowMapActivity.class);
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
