package com.travelsmartlondon.context;

import java.util.Map;

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
	
	//public static final String USER_EMAIL_ADDRESS = "username";
	
	private static final TSLApplicationContext INSTANCE = new TSLApplicationContext();
	
	/*
	 * Private Constructor to ensure that the application complies with the Singleton pattern
	 */
	private TSLApplicationContext() {}
	
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

	/*
	private Map<String, Object> _preferenceMap = new ConcurrentHashMap<String, Object>(); 
	
	@Override
	public boolean contains(String preference_) {
		return this._preferenceMap.containsKey(preference_);
	}

	@Override
	public Editor edit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ?> getAll() {
		return this._preferenceMap;
	}

	@Override
	public boolean getBoolean(String key_, boolean defValue_) {
		return (this._preferenceMap.containsKey(key_) ? (Boolean) this._preferenceMap.get(key_) : defValue_);
	}

	@Override
	public float getFloat(String key_, float defValue_) {
		return (this._preferenceMap.containsKey(key_) ? (Float) this._preferenceMap.get(key_) : defValue_);
	}

	@Override
	public int getInt(String key_, int defValue_) {
		return (this._preferenceMap.containsKey(key_) ? (Integer) this._preferenceMap.get(key_) : defValue_);
	}

	@Override
	public long getLong(String key_, long defValue_) {
		return (this._preferenceMap.containsKey(key_) ? (Long) this._preferenceMap.get(key_) : defValue_);
	}

	@Override
	public String getString(String key_, String defValue_) {
		return (this._preferenceMap.containsKey(key_) ? (String) this._preferenceMap.get(key_) : defValue_);
	}

	@Override
	public Set<String> getStringSet(String key_, Set<String> defValue_) {
		Set<String> set = (Set<String>) this._preferenceMap.get(key_);
		return (this._preferenceMap.containsKey(key_) ? set : defValue_);
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		// TODO Auto-generated method stub
		
	}
	*/
}
