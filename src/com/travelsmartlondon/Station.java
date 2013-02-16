package com.travelsmartlondon;

import com.google.android.gms.maps.model.LatLng;


public class Station{
	private String _name;
	private String _code;
	private double _x;
	private double _y;


	public Station(String name, String code, double x, double y){
		this._name = name;
		this._code = code;
		this._x = x;
		this._y = y;
	}
	
	public String getName(){
		return this._name;
	}
	
	public String getCode(){
		return this._code;
	}
	
	public LatLng getCoordinates(){
		return new LatLng(_x,_y);
	}
	
}