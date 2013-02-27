package com.travelsmartlondon.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.travelsmartlondon.database.DBSQLiteHelper;
import com.travelsmartlondon.database.TubeStationsMapArea;
import com.travelsmartlondon.station.Station;
import com.travelsmartlondon.station.TubeStation;

public class stationDAO {
	
	private SQLiteDatabase tslDB;
	private DBSQLiteHelper dbHelper;
	
	public stationDAO(Context context){
		dbHelper = new DBSQLiteHelper(context);
	}
	
	public void open() throws SQLException {
	    tslDB = dbHelper.getWritableDatabase();
	  }

	public void close() {    
		dbHelper.close();
	  }
	
	public ArrayList<TubeStation> getStationsByArea(int area_){
		
		List<TubeStation> stations = new ArrayList<TubeStation>();
		
		String table;
				
		switch(area_) { 
			case TubeStationsMapArea.AREA_CENTRAL_1 :
				table = "stations4";
				break;
			case TubeStationsMapArea.AREA_CENTRAL_2:
				table = "stations3";
				break;
			case TubeStationsMapArea.AREA_CENTRAL_3:
				table = "stations2";
				break;
			case TubeStationsMapArea.AREA_CENTRAL_4:
				table = "stations1";
				break;
			case TubeStationsMapArea.AREA_NORTH_5:
				table = "stations5";
				break;
			case TubeStationsMapArea.AREA_NORTHWEST_6:
				table = "stations8";
				break;
			case TubeStationsMapArea.AREA_SOUTH_7:
				table = "stations7";
				break;
			case TubeStationsMapArea.AREA_NORTHEAST_8:
				table = "stations6";
				break;
			default:
				table = 
		}
		
		Cursor cursor = tslDB.query(table,null,null,null,null,null,null);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()){
			Station station = cursorToStation(cursor);
		}
		
		return null;
	}

	private TubeStation cursorToStation(Cursor cursor) {
		
		
		
		Station station = new TubeStation(cursor.getString(0),);
		return null;
	}
	
	
	
}
