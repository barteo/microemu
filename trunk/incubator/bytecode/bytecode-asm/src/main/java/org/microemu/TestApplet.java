/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 MicroEmulator Team.
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
package org.microemu;

import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JApplet;

/**
 * @author vlads
 *
 */
public class TestApplet extends JApplet {

	private static final long serialVersionUID = 1L;

	public void paint(Graphics g) {
		g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
		FontMetrics metrics = getFontMetrics(g.getFont());
		int lineHeight = metrics.getHeight() + 2;
		int line = 1;
		g.drawString("Start Class loader test...", 1, lineHeight * (line ++));
		
		
		try {
			SystemProperties.setProperty("microedition.platform" , "MicroEmulator-Test");
			
			PreporcessorClassLoader cl = new PreporcessorClassLoader(PreporcessorTest.class.getClassLoader());
			cl.disableClassLoad(SystemProperties.class);
			cl.disableClassLoad(ResourceLoader.class);
			ResourceLoader.classLoader = cl;
			
			cl.addClassURL(PreporcessorTest.TEST_CLASS);
			
			g.drawString("ClassLoader created...", 1, lineHeight * (line ++));
			
			Class instrumentedClass = cl.loadClass(PreporcessorTest.TEST_CLASS);
			Runnable instrumentedInstance = (Runnable)instrumentedClass.newInstance();
			instrumentedInstance.run();
			
			g.drawString("Looks good!", 1, lineHeight * (line ++));
			
		} catch (Throwable e) {
			
			g.drawString("Error " + e.toString(), 1, lineHeight * (line ++));
			
			e.printStackTrace();
			
		}
	}

}
