package com.travelsmartlondon;

import com.google.android.gms.maps.model.LatLng;


public class Station{
	String name;
	String code;
	double x;
	double y;


	public Station(String name, String code, double x, double y){
		this.name = name;
		this.code = code;
		this.x = x;
		this.y = y;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getCode(){
		return this.code;
	}
	
	public LatLng getCoordinates(){
		return new LatLng(x,y);
	}
	
}