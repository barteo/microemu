/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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

import javax.microedition.lcdui.Graphics;

/**
 * @author vlads
 *
 */
public class OverrideNewJSR2Canvas  extends BaseTestsCanvas {

		public static final boolean enabled = false;
		
		public OverrideNewJSR2Canvas() {
			super("OverrideNewJSR2");
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
			writeln(g, line++, "Override New JSR2");
			
			String result;
			
			try {
				result = new OverrideNewJSR2Client().doJSR2Stuff("Can use new classes");
				writeln(g, line++, "success");
			} catch (Throwable e) {
				writeln(g, line++, "failure");
				result = e.toString();
			}
			
			writeln(g, line++, result);
		}
}
