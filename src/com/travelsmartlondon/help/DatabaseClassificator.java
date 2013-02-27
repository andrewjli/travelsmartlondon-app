package com.travelsmartlondon.help;

import com.travelsmartlondon.database.TubeStationsMapArea;

public class DatabaseClassificator {
	
	private static DatabaseClassificator instance;
	
	private DatabaseClassificator() { }
	
	public static DatabaseClassificator getInstance() {
        if (instance == null) {
                synchronized (DatabaseClassificator.class){
                        if (instance == null) {
                                instance = new DatabaseClassificator ();
                        }
              }
        }
        return instance;
	}

	public int checkTubeStationMapArea(final Point p_) {

        boolean test1 = TubeStationBoundary.BOUNDARY_CENTRAL_1.contains(p_);
        boolean test2 = TubeStationBoundary.BOUNDARY_CENTRAL_2.contains(p_);
        boolean test3 = TubeStationBoundary.BOUNDARY_CENTRAL_3.contains(p_);
        boolean test4 = TubeStationBoundary.BOUNDARY_CENTRAL_4.contains(p_);
        boolean test5 = TubeStationBoundary.BOUNDARY_NORTH_5.contains(p_);
        boolean test6 = TubeStationBoundary.BOUNDARY_NORTHWEST_6.contains(p_);
        boolean test7 = TubeStationBoundary.BOUNDARY_SOUTH_7.contains(p_);
        boolean test8 = TubeStationBoundary.BOUNDARY_NORTHEAST_8.contains(p_);
        
        if(test1) 
            return TubeStationsMapArea.AREA_CENTRAL_1;
        if(test2) 
            return TubeStationsMapArea.AREA_CENTRAL_2;
        if(test3) 
            return TubeStationsMapArea.AREA_CENTRAL_3;
        if(test4) 
            return TubeStationsMapArea.AREA_CENTRAL_4;
        if(test5) 
            return TubeStationsMapArea.AREA_NORTH_5;
        if(test6) 
            return TubeStationsMapArea.AREA_NORTHWEST_6;
        if(test7) 
            return TubeStationsMapArea.AREA_SOUTH_7;
        if(test8) 
            return TubeStationsMapArea.AREA_NORTHEAST_8;
        else 
        	return 0;
	}
}
