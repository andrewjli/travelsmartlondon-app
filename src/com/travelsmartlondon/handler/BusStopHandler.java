package com.travelsmartlondon.handler;

import java.net.MalformedURLException;
import java.net.URL;

import com.travelsmartlondon.help.DatabaseClassificator;

public class BusStopHandler {
	private static URL url;//
	
	private static BusStopHandler instance;
	
	private BusStopHandler() { 
		try {
			url = new URL("");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static BusStopHandler getInstance() {
        if (instance == null) {
                synchronized (BusStopHandler.class){
                        if (instance == null) {
                                instance = new BusStopHandler ();
                        }
              }
        }
        return instance;
	}
	
	
}
