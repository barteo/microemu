/*
 *  MicroEmulator
 *  GeoTools java GIS tookit (c) The Centre for Computational Geography 2002
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.microemu.device.impl;

/**
 * This class is intended for storing georaphical polygons such as boundary data
 * for wards, coastlines etc.<p>
 *
 *
 * @author James Macgill
 * @author Mathieu Van Loon - getPointsAsArrayX, getPointsAsArrayY, dropVector
 * @since 0.5.0
 */
public class Polygon extends Shape {

	/**
	 * Number of points in polygon.
	 */
	public int npoints;

	/**
	 * Array of points.
	 */
	public int xpoints[];

	/**
	 * Array of points.
	 */
	public int[] ypoints;
	
	private Rectangle bounds = new Rectangle();

	/**
	 * Create an empty polygon.
	 */
	public Polygon() {
	}

	/**
	 * Construct a polygon with full details.
	 *
	 * @param id Polygon ID.
	 * @param xcent X-coordinate of centroid.
	 * @param ycent Y-coordinate of centroid.
	 * @param xpoints Array of x values (npoints in size).
	 * @param ypoints Array of y values (npoints in size).
	 * @param npoints Number of points.
	 */
	public Polygon(int[] xpoints, int[] ypoints, int npoints) {

		this.xpoints = new int[npoints];
		this.ypoints = new int[npoints];
		this.npoints = npoints;
		//Add a try here to catch failed array copy
		System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
		System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
		
		for (int i = 0; i < npoints; i++) {
			bounds.add(xpoints[i], ypoints[i]);
		}
	}

	/**
	 * Construct a GeoPolygon based on an existing GeoPolygon.
	 *
	 * @param poly GeoPolygon to clone.
	 */
	public Polygon(Polygon poly) {
		this(poly.xpoints, poly.ypoints, poly.npoints);
	}

	/**
	 * Add a vertex to the polygon.
	 *
	 * @param x X-coordinate of point to add.
	 * @param y Y-coordinate of point to add.
	 */
	public void addPoint(int x, int y) {
		if (npoints > 0) {
			int xtemp[];
			int ytemp[];
			xtemp = xpoints;
			ytemp = ypoints;
			xpoints = new int[npoints + 1];

			System.arraycopy(xtemp, 0, xpoints, 0, npoints);
			xtemp = null;
			ypoints = new int[npoints + 1];
			System.arraycopy(ytemp, 0, ypoints, 0, npoints);
			ytemp = null;
		} else {
			xpoints = new int[1];
			ypoints = new int[1];
		}
		npoints++;
		xpoints[npoints - 1] = x;//-1 to account for 0 array indexing
		ypoints[npoints - 1] = y;
		
		bounds.add(x, y);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Tests to see if a point is contained by this polygon.
	 * @param p The GeoPoint to test.
	 * @return boolean True if point is contained by this polygon.
	 */
	public boolean contains(int x, int y) {

		/*if(!getMultiPartBounds().contains(p)){
		 return false;
		 }*/

		if (getBounds().contains(x, y)) {

			/* Start andys code */
			int number_of_lines_crossed = 0; // holds lines intersecting with 
			number_of_lines_crossed = 0; // set lines crossed = 0 for each polygon
			// lets us know which part start we're looking for	   
			for (int i = 0; i < npoints - 1; i++) {
				//GeoPoint A = new GeoPoint();
				//GeoPoint B = new GeoPoint();        
				//A.y = ypoints[i];                                       // get y-coordinate
				//A.x = xpoints[i];                                       // get x-coordinate 
				//B.y = ypoints[i+1];                                     // get next y-coordinate
				//B.x = xpoints[i+1];                                     // get next x-coordinate

				if (y != ypoints[i + 1]
						&& (y <= Math.max(ypoints[i], ypoints[i + 1]))
						&& (y >= Math.min(ypoints[i], ypoints[i + 1]))
						&& ((xpoints[i] >= x) || (xpoints[i + 1] >= x))) { // if polygon contains a suitable value
					if (((xpoints[i] >= x) && (xpoints[i + 1] >= x))) {
						number_of_lines_crossed++;//simple case
					} else {
						double gradient; //   calc. gradient
						if (xpoints[i] > xpoints[i + 1])
							gradient = ((xpoints[i] - xpoints[i + 1]) / (ypoints[i] - ypoints[i + 1]));
						else
							gradient = ((xpoints[i + 1] - xpoints[i]) / (ypoints[i + 1] - ypoints[i]));
						double x_intersection_axis = (xpoints[i] - (gradient * ypoints[i])); // calc. intersect with x-axis
						double x_intersection_line = (gradient * y)
								+ x_intersection_axis; // calc. intersect with y=const
						//  line extending from location 
						if ((x_intersection_line <= Math.max(xpoints[i],
								xpoints[i + 1]))
								&& // check intersect inside polygon 
								(x_intersection_line >= Math.min(xpoints[i],
										xpoints[i + 1]))
								&& (x_intersection_line >= x)) {
							number_of_lines_crossed++; // increment line counter
						} // end check for inside polygon
					}
				} // end of if polygon points suitable
			} // end of run through points in polygon

			if ((number_of_lines_crossed != 0) && // if number of polygon lines crossed
					(((number_of_lines_crossed % 2) == 1))) { //   by a line in one direction from the
				return true; //   initial location is odd, the location

				//   lies in that polygon

			} // end of run through polygons

		}
		return false; // return stuffing if things get this far

	} // end of method whichPolygon	   

}