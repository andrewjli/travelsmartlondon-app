package com.travelsmartlondon.help;

public class TubeStationBoundary {
	private static final Point[] polygon1 = { new Point(51.5662944500,-0.0996252720), new Point(51.4997875000,-0.1002153930), new Point(51.5002693200,-0.0001239060), new Point(51.5661416100,0.0002494320), new Point(51.5662944500,-0.0996252720) };
    private static final Point[] polygon2 = { new Point(51.5662863000,-0.1661279480), new Point(51.5002985500,-0.1670722140), new Point(51.5003298200,-0.0997224200), new Point(51.5660399200,-0.0995574320), new Point(51.5662863000,-0.1661279480) };
    private static final Point[] polygon3 = { new Point(51.5659920300,-0.2664735140), new Point(51.4997321400,-0.2663392610), new Point(51.5001810500,-0.1668216250), new Point(51.5665557900,-0.1667092710), new Point(51.5659920300,-0.2664735140) };
    private static final Point[] polygon4 = { new Point(51.5672571400,-0.4003080620), new Point(51.5000895400,-0.3998764350), new Point(51.4999949300,-0.2667369770), new Point(51.5662869700,-0.2664632470), new Point(51.5672571400,-0.4003080620) };
    private static final Point[] polygon5 = { new Point(51.7327518500,-0.3996611740), new Point(51.5668828800,-0.4000932290), new Point(51.5659116500,0.0007078540), new Point(51.7337328200,0.0010498210), new Point(51.7327518500,-0.3996611740) };
    private static final Point[] polygon6 = { new Point(51.7312898500,0.0010126500), new Point(51.4999649400,-0.0000889600), new Point(51.5000621200,0.2667505590), new Point(51.7285627500,0.2770656360), new Point(51.7312898500,0.0010126500) };
    private static final Point[] polygon7 = { new Point(51.5000260600,-0.4997822330), new Point(51.4002345400,-0.5000596570), new Point(51.3998299400,0.0330555780), new Point(51.4999801100,0.0342627740), new Point(51.5000260600,-0.4997822330) };
    private static final Point[] polygon8 = { new Point(51.7492432000,-0.6992719480), new Point(51.5004317800,-0.6988988600), new Point(51.4996901400,-0.3999094850), new Point(51.7501986600,-0.3997713550), new Point(51.7492432000,-0.6992719480) };
    
    public static final Boundary BOUNDARY_CENTRAL_1 = new Boundary(polygon1);
    public static final Boundary BOUNDARY_CENTRAL_2 = new Boundary(polygon2);
    public static final Boundary BOUNDARY_CENTRAL_3 = new Boundary(polygon3);
    public static final Boundary BOUNDARY_CENTRAL_4 = new Boundary(polygon4);
    public static final Boundary BOUNDARY_NORTH_5 = new Boundary(polygon5);
    public static final Boundary BOUNDARY_NORTHWEST_6 = new Boundary(polygon6);
    public static final Boundary BOUNDARY_SOUTH_7 = new Boundary(polygon7);
    public static final Boundary BOUNDARY_NORTHEAST_8 = new Boundary(polygon8);
}
