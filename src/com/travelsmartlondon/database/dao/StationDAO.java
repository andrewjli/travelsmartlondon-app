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

public class StationDAO {
	
	private SQLiteDatabase _tslDB;
	private DBSQLiteHelper _dbHelper;
	
	public StationDAO(Context context){
		_dbHelper = new DBSQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		boolean dbAvailable = _dbHelper.openDataBase();
	    if(dbAvailable){
	    	_tslDB = _dbHelper.getReadableDatabase();
	    }
	    else{
	    	System.out.println("********************Database does not exist***************");
	    }
	  }

	public void close() {    
		_dbHelper.close();
	  }
	
	public List<TubeStation> getStationsByArea(int area_){
		
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
				return null;
		}
		
		stations = getStationsFromDB(table);
		
		return stations;
	}

	private ArrayList<TubeStation> getStationsFromDB(String table_) {
		
		ArrayList<TubeStation> stations = new ArrayList<TubeStation>();
		
		Cursor cursor = _tslDB.query(table_,null,null,null,null,null,null);
		
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()){
			TubeStation station = cursorToStation(cursor);
			stations.add(station);
			cursor.moveToNext();
		}
		
		//Close the cursor
		cursor.close();
	    return stations;
		
	}

	private TubeStation cursorToStation(Cursor cursor_) {
		
		ArrayList<String> lines = new ArrayList<String>();
		for(int n=3;n<8;n++){
			// n refers to the columns in the stations table corresponding to lines
			lines.add(cursor_.getString(n));
		}
		
		
		TubeStation station = new TubeStation(cursor_.getString(0),cursor_.getInt(1),cursor_.getString(2),
				cursor_.getDouble(8), cursor_.getDouble(9),lines);
		
		
		return station;
	}
	
	
	
}
