package com.travelsmartlondon;

import com.google.android.gms.maps.model.LatLng;
import uk.me.jstott.jcoord.*;


public class BusStop extends Station{

	public BusStop(String name, String code, double x, double y){
		super(name, code, x, y);
	}

	//TFL provides the bus stop coordinates in UK OSGB eastings and northings format. this method converts it into LatLng
	public LatLng getCoordinates(){
		uk.me.jstott.jcoord.LatLng temp = (new OSRef(_x,_y).toLatLng());
		temp.toWGS84();
		return new LatLng(temp.getLat(),temp.getLng());
	}
	
}
