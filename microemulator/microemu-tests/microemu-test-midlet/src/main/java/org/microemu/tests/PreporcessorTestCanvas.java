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
package org.microemu.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.lcdui.Graphics;

/**
 * @author vlads
 *
 */
public class PreporcessorTestCanvas extends BaseTestsCanvas {

	public static final boolean enabled = true;
	
	public PreporcessorTestCanvas() {
		super("bytecode test");
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	protected void paint(Graphics g) {
		int width = getWidth();
        int height = getHeight();

		g.setGrayScale(255);
		g.fillRect(0, 0, width, height);
		
		g.setColor(0);
		int line = 0;
		writeln(g, line++, "bytecode test");
		
		System.out.println("print data to console");
		
		try {
			String resourceName = "/app-data.txt";
			String expected = "private app-data";
			
			String result = verifyLoadStrings(String.class.getResourceAsStream(resourceName), "String.class. " +  resourceName, expected);

			writeln(g, line++, "loaded " + result);
		} catch (Throwable e) {
			writeln(g, line++, "failure");
			writeln(g, line++, e.toString());
		}
		
		try {
			String resourceName = "resource-path-text.txt";
			String expected = "not absolute";
			
			String result = verifyLoadStrings(PreporcessorTestCanvas.class.getResourceAsStream(resourceName), "App.class. " +  resourceName, expected);

			writeln(g, line++, "loaded " + result);
		} catch (Throwable e) {
			writeln(g, line++, "failure");
			writeln(g, line++, e.toString());
		}
	}
	
	private String verifyLoadStrings(InputStream inputstream, String resourceName, String expected) {	
		if (inputstream == null) {
			if (expected == null) {
				System.out.println("OK - Resource not found " + resourceName);
				return "{not found}";
			} else {
				System.err.println("Resource not found " + resourceName);
			}
			throw new RuntimeException("Resource not found " + resourceName);
		} else {
			if (expected == null) {
				throw new RuntimeException("Can access resource " + resourceName);
			}
		}
		try {
			InputStreamReader r = new InputStreamReader(inputstream);
			StringBuffer value = new StringBuffer();
			int b;
			while ((b = r.read()) != -1) {
				value.append((char)b);
			}
			if (!expected.equals(value.toString())) {
				throw new RuntimeException("Unexpected resource " + resourceName + " value [" + value + "]\nexpected [" + expected + "]");
			}
			return value.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Resource read error " + resourceName, e);
		} finally {
			try {
				inputstream.close();
			} catch (IOException ignore) {
			}
		}
	}
}
