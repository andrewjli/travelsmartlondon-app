package com.travelsmartlondon.station;

import com.google.android.gms.maps.model.LatLng;


public abstract class Station{
	private String _name;
	private String _code;
	protected double _latitude;
	protected double _longitude;


	public Station(String name, String code, double x, double y){
		this._name = name;
		this._code = code;
		this._latitude = x;
		this._longitude = y;
	}
	
	public String getName(){
		return this._name;
	}
	
	public String getCode(){
		return this._code;
	}
	
	public LatLng getCoordinates(){
		return new LatLng(_latitude,_longitude);
	}
	
}