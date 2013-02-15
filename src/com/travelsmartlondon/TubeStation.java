package com.travelsmartlondon;

import java.util.ArrayList;

public class TubeStation extends Station{

	ArrayList<String> lines = new ArrayList<String>();
	
	public TubeStation(String name, String code, double x, double y) {
		super(name, code, x, y);
	}
}
