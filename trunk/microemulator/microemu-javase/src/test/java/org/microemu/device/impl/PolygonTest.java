/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
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
