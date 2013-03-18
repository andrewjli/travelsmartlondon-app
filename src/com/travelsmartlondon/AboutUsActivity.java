package com.travelsmartlondon;

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class AboutUsActivity extends Activity {
	
	public static final String ABOUT_US_HTML=
							"<p><b>Welcome!</b></p>" +
							"<p>Travel Smart London is available free of charge. For extra features such as Tube Line Rating and Comments, Log in using your Google account from the main menu! The app will use the account stored on your device and will validate it with Google. We will not acceess your account at any point after that, so don't worry about privacy issues!</p>" +
							"<p>To learn more about us, visit our website at <a href=\"http://stud-tfl.cs.ucl.ac.uk\">stud-tfl.cs.ucl.ac.uk</a>.</p>" +
                            "<p>Travel Smart London is a university group project undertaken by 3rd year MEng students at <a href=\"http://www.ucl.ac.uk/\">University College London</a>. Under the supervision of <a href=\"http://www0.cs.ucl.ac.uk/staff/l.capra/\">Dr. Licia Capra</a> and our client, Duncan Wilson, we were commissioned to develop a smart reusable platform to improve the way people travel and plan their journeys. And we are proud to announce that we met all the expected goals!</p>" +
                            "<p>Travel Smart was a greenfield project and the idea as well as requirements originated from a user study and research we carried out at the beginning of the project. We applied Agile software development methodology and due to the nature of the project being open-ended and research based, we continued adding new interesting requirements and features as the project progressed. We firmly believe that every person who finds the daily commute in the city of London tiresome will find our system extremely useful. We hope you liked our project. For any inquires about our API, app or the project in general, contact us <a href=\"mailto:groupproject20122013@gmail.com\">HERE</a>. Thank you for supporting Travel Smart!</p>" + 
                            "<p>Transport data is provided by TSL API offering customised \"smart\" travel data and acts as an intermediary between the app and external data sources such as TfL and Twitter. We are not responsible for any inacurracies.</p>" + 
                            "<p>This application is not affiliated with TfL, Twitter or any other organisation unless explicitly stated.</p>";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		
		TextView view = (TextView) findViewById(R.id.about_us_text_view);
		view.setText(Html.fromHtml(ABOUT_US_HTML));
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_us, menu);
		return true;
	}

}
