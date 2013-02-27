package com.travelsmartlondon.line;

public class Line {
	
	public static final String LINE_HAMMERSMITH_CITY = "H";
	public static final String LINE_BAKERLOO = "B";
	public static final String LINE_CENTRAL = "CE";
	public static final String LINE_CIRCLE = "CI";
	public static final String LINE_DISTRICT = "D";
	public static final String LINE_JUBILEE ="J";
	public static final String LINE_METROPOLITAN ="M";
	public static final String LINE_NORTHERN = "N";
	public static final String LINE_PICCADILLY = "P";
	public static final String LINE_VICTORIA = "V";
	public static final String LINE_WATERLOO_CITY = "W";
	
	private String _name;
	private float _rating;
	
	public Line(String name_,float rating_){
		this._name = name_;
		this._rating = _rating;
	}
	
	public void setName(String name_){
		this._name = name_;
	}
	
	public void setRating(float rating){
		this._rating = rating;
	}
	
	public String getName(){
		return this._name;
	}
	
	public float getRating(){
		return this._rating;
	}
	
	
}
