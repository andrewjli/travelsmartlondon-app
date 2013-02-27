package com.travelsmartlondon.help;

/**
 * Two dimensional cartesian point.
 */
public class Point {
    private final double _x;
    private final double _y;
  
    public Point (final double x_, final double y_) {
        this._x = x_;
        this._y = y_;
    }
    
    public double getX() {
        return this._x;
    }
    
    public double getY() {
        return this._y;
    }
}