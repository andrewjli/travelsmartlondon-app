package com.travelsmartlondon.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.travelsmartlondon.TubeLineActivity;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * TSLApplicationContext singleton context object.
 * <p>Stores session-related cached information</p>
 * @author kamilprzekwas
 *
 */
public class TSLApplicationContext {

	private String _emailAddress;
	private Map<String, Float> _ratings;
	
	//public static final String USER_EMAIL_ADDRESS = "username";
	
	private static final TSLApplicationContext INSTANCE = new TSLApplicationContext();
	
	/*
	 * Private Constructor to ensure that the application complies with the Singleton pattern
	 */
	private TSLApplicationContext() {
		this._ratings = new ConcurrentHashMap<String, Float>();
		this._ratings.put(TubeLineActivity.PICCADILLY_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.BAKERLOO_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.CENTRAL_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.CIRCLE_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.DISTRICT_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.DLR_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.HAMMERSMITH_CITY_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.JUBILEE_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.METROPOLITAN_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.NORTHERN_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.OVERGROUND_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.VICTORIA_LINE, (float) -1.0);
		this._ratings.put(TubeLineActivity.WATERLOO_CITY_LINE, (float) -1.0);
	}
	
	public static TSLApplicationContext getInstance() {
		return INSTANCE;
	}
	
	public String currentEmailAddress() {
		return this._emailAddress;
	}
	
	public boolean checkIfUserIsLoggedIn() {
		return (this._emailAddress == null) ? false : true;
	}
	
	public void injectEmailAddressOnceUserLoggedIn(String email_) {
		this._emailAddress = email_;
	}
	
	public boolean lineIsRated(String line_) {
		if(this._ratings.get(line_).equals((float) -1.0)) {
			return false;
		} else {
			return true;
		}
	}
	
	public void submitRatingForLine(String line_, Float rating_) throws IllegalArgumentException {
		this._ratings.put(line_, rating_);
	}
	
	public Float fetchOwnRatingForLine(String line_) throws IllegalArgumentException {
		return (this._ratings.get(line_));
	}
}
