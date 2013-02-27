package com.travelsmartlondon.help;

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

        boolean test1 = TubeStationBoundary.BOUNDARY_1.contains(p_);
        boolean test2 = TubeStationBoundary.BOUNDARY_2.contains(p_);
        boolean test3 = TubeStationBoundary.BOUNDARY_3.contains(p_);
        boolean test4 = TubeStationBoundary.BOUNDARY_4.contains(p_);
        boolean test5 = TubeStationBoundary.BOUNDARY_5.contains(p_);
        boolean test6 = TubeStationBoundary.BOUNDARY_6.contains(p_);
        boolean test7 = TubeStationBoundary.BOUNDARY_7.contains(p_);
        boolean test8 = TubeStationBoundary.BOUNDARY_8.contains(p_);
        
        
        
		return 0;
	}
}
