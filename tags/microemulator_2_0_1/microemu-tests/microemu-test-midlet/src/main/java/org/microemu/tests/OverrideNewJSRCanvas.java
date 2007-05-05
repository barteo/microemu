/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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

import javax.microedition.lcdui.Graphics;

/**
 * @author vlads
 *  To test if MIDlet can override javax.microedition package on the device.
 */
public class OverrideNewJSRCanvas extends BaseTestsCanvas {

	public static final boolean enabled = true;
	
	public OverrideNewJSRCanvas() {
		super("OverrideNew IO JSR");
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
		writeln(g, line++, "Override New JSR");
		
		String result;
		
		try {
			result = new OverrideNewJSRClient().doJSRStuff("Can use new classes");
			writeln(g, line++, "success");
		} catch (Throwable e) {
			writeln(g, line++, "failure");
			result = e.toString();
		}
		
		writeln(g, line++, result);
	}

}
