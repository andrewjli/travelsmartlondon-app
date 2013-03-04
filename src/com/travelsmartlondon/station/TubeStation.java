package com.travelsmartlondon.station;

import java.util.ArrayList;
import java.util.List;

import com.travelsmartlondon.line.Line;

public class TubeStation extends Station{

	private List<String> _lines = new ArrayList<String>();
	private int _nlc;
	
	public TubeStation(String name_, String code_, double latitude_, double longitude_) {
		super(name_, code_, latitude_, longitude_);
		
	}
	
	public TubeStation(String name_,int nlc_, String code_, double latitude_,double longitude_,
			List<String> lines_)
	{
		super(name_, code_, latitude_, longitude_);
		this._nlc = nlc_;
		this._lines = lines_;
	}
	
	public ArrayList<String> getLinesAsArrayList(){
		ArrayList<String> arrayList = new ArrayList<String>();
		for(String line : this._lines) {
			if(!line.equals(" *")) {
				arrayList.add(line);
			}
		}
		return arrayList;
	}
	
}
