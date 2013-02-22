package com.travelsmartlondon.context;

/**
 * TSLApplicationContext singleton context object.
 * <p>Stores session-related cached information</p>
 * @author kamilprzekwas
 *
 */
public class TSLApplicationContext {

	private String _userEmailAddress;
	
	
	private static final TSLApplicationContext INSTANCE = new TSLApplicationContext();
	
	/*
	 * Private Constructor to ensure that the application complies with the Singleton pattern
	 */
	private TSLApplicationContext() {}
	
	public static TSLApplicationContext getInstance() {
		return INSTANCE;
	}
	
	
	public String currentEmailAddress() {
		return this._userEmailAddress;
	}
	
	public boolean checkIfUserIsLoggedIn() {
		return (this._userEmailAddress == null) ? false : true;
	}
	
	public void injectEmailAddressOnceUserLoggedIn(String email_) {
		this._userEmailAddress = email_;
	}
}
