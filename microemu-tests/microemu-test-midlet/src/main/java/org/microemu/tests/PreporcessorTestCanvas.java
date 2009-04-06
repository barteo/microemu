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

	/*
	 * (non-Javadoc)
	 * 
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

			String result = verifyLoadStrings(String.class.getResourceAsStream(resourceName), "String.class. "
					+ resourceName, expected);

			writeln(g, line++, "loaded " + result);
		} catch (Throwable e) {
			writeln(g, line++, "failure");
			writeln(g, line++, e.toString());
		}

		try {
			String resourceName = "resource-path-text.txt";
			String expected = null;

			String result = verifyLoadStrings(PreporcessorTestCanvas.class.getResourceAsStream(resourceName),
					"App.class. " + resourceName, expected);

			writeln(g, line++, "loaded " + result);
		} catch (Throwable e) {
			writeln(g, line++, "failure");
			writeln(g, line++, e.toString());
		}

		try {
			String resourceName = "resource-package.txt";
			String expected = "package relative";

			String result = verifyLoadStrings(PreporcessorTestCanvas.class.getResourceAsStream(resourceName),
					"App.class. " + resourceName, expected);

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
				value.append((char) b);
			}
			if (!expected.equals(value.toString())) {
				throw new RuntimeException("Unexpected resource " + resourceName + " value [" + value + "]\nexpected ["
						+ expected + "]");
			}
			return value.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Resource read error " + resourceName + " " + e.getMessage());
		} finally {
			try {
				inputstream.close();
			} catch (IOException ignore) {
			}
		}
	}
}
