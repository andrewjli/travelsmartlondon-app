package com.travelsmartlondon.context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.travelsmartlondon.TubeLineActivity;

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
	
	/**
	 * This is for presentation purposes only and should not be sent to the outside world
	 * @return emailAddress
	 */
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
		if(this._ratings == null) { 
			return false;
		}
		if(this._ratings.get(line_).equals((float) -1.0)) {
			return false;
		} else {
			return true;
		}
	}
	
	public void submitRatingForLine(String line_, Float rating_) throws IllegalArgumentException {
		this._ratings.put(line_, rating_);
	}
	
	public void submitRatingForLine(String line_, String rating_) throws IllegalArgumentException {
		Float rating = Float.parseFloat(rating_);
		this._ratings.put(line_, rating);
	}
	
	public void setRatingForLineToNull(String line_) throws IllegalArgumentException {
		this._ratings.put(line_, (float) -1.0);
	}
	
	public Float fetchOwnRatingForLine(String line_) throws IllegalArgumentException {
		return (this._ratings.get(line_));
	}
	
	public String currentSHA1EmailAddress() {
		return toSHA1(this._emailAddress.getBytes());
	}
	
	public static String toSHA1(byte[] convertme) {
	    MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	    }
	    catch(NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } 
	    return byteArrayToHexString(md.digest(convertme));
	}
	
	private static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
	}
}
