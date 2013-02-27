package com.travelsmartlondon.help;

/**
*
* @author kamilprzekwas
*/
class Boundary {
   private final Point[] _points; // Points making up the boundary

   public Boundary(final Point[] points_) {
       this._points = points_;
   }
   
   /**
    * Return true if the given point is contained inside the boundary.
    * See: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
    * @param test The point to check
    * @return true if the point is inside the boundary, false otherwise
    *
    */
   public boolean contains(Point test) {
     int i;
     int j;
     boolean result = false;
     for (i = 0, j = this._points.length - 1; i < this._points.length; j = i++) {
       if ((this._points[i].getY() > test.getY()) != (this._points[j].getY() > test.getY()) &&
           (test.getX() < (this._points[j].getX() - this._points[i].getX()) * (test.getY() - this._points[i].getY()) / (this._points[j].getY()-this._points[i].getY()) + this._points[i].getX())) {
         result = !result;
        }
     }
     return result;
   }
}
