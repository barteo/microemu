/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *
 *  @version $Id$
 */
package org.microemu.device.impl;

import junit.framework.TestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vlads
 *
 */
public class PolygonTest extends TestCase {
	
	private static final boolean debug = false;

	Pattern rx = Pattern.compile("(?:(\\d+)\\s?[,]\\s?(\\d+)(?:\\s?[;]\\s?)?)");
	
	private void verify(String polygon, String points, boolean expected) {
		Polygon pl = new Polygon();
		//This is know to work fine.
		java.awt.Polygon awt = new java.awt.Polygon();

		
		Matcher mp = rx.matcher(polygon);
		while (mp.find()) {
			int px = Integer.parseInt(mp.group(1));
			int py = Integer.parseInt(mp.group(2));
			pl.addPoint(px, py);
			awt.addPoint(px, py);
		}
		Matcher mxy = rx.matcher(points);
		while (mxy.find()) {
			int x = Integer.parseInt(mxy.group(1));
			int y = Integer.parseInt(mxy.group(2));
			String xy = "[" + x + ", " + y + "]";
			assertEquals("awt " + polygon + " contains " + xy, expected, awt.contains(x, y));
			
			if (debug && expected != pl.contains(x, y)) {
				System.out.println("error impl " + polygon + " contains " + xy + "? expected:" + expected);
				pl.contains(x, y);
				awt.contains(x, y);
			} else if (debug) {
				System.out.println("OK impl " + polygon + " contains " + xy + "? expected:" + expected);
			}
			assertEquals("impl " + polygon + " contains " + xy, expected, pl.contains(x, y));
		}
		
		// Test other point
		int x = pl.getBounds().x - 2;
		int y= pl.getBounds().y - 2;
		int w = pl.getBounds().width + 4;
		int h = pl.getBounds().height + 4;
		for(int xx = x ; xx <= w; xx ++) {
			for(int yy = y ; yy <= h; yy ++) {
				int xt = x + xx;
				int yt = y + yy;
				boolean exp = awt.contains(xt, yt);
				String xy = "[" + xt + ", " + yt + "]";
				assertEquals("impl " + polygon + " contains " + xy, exp, pl.contains(xt, yt));
			}
		}
	}
	
	
	public void testTriangleContains() {
		String triangle = "4,10; 10,5; 12,14";
		String pointIn = "9,9; 9,7; 10,11; 11,12; 7,11";
		String pointOut = "6,6;  10,4; 13,7; 13,15; 10,14; 5,12";
		verify(triangle, pointIn , true);
		verify(triangle, pointOut , false);
		
		String triangleBack = "4,10; 12,14; 10,5;";
		verify(triangleBack, pointIn , true);
		verify(triangleBack, pointOut , false);
		
		String triangleBack2 = "12,14; 10,5; 4,10; ";
		verify(triangleBack2, pointIn , true);
		verify(triangleBack2, pointOut , false);
	}
	
	public void testComplexContains() {
		verify("4,10; 10,5; 17,3; 19,6; 14,8; 12,14; 7,12", "", true);
		verify("4,10; 10,5; 17,3; 19,6; 17,12; 12,14; 7,12", "", true);
		verify("7,12; 12,14; 17,12; 19,6; 17,3; 10,5; 4,10;", "", true);
	}
	
}
