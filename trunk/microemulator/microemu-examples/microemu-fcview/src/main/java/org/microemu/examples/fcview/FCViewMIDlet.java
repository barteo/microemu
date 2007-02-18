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
package org.microemu.examples.fcview;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * @author vlads
 *
 */
public class FCViewMIDlet extends MIDlet {

	static FCViewMIDlet instance;

	FilesList list;
	
	public FCViewMIDlet() {
		super();
		instance = this;
		this.list = new FilesList();
	}

	
	protected void startApp() throws MIDletStateChangeException {
		try {
			System.out.println("FileConnection " + System.getProperty("microedition.io.file.FileConnection.version"));
			this.list.setDir(null);
			setCurrentDisplayable(this.list);
		} catch (SecurityException e) {
			Alert alert = new Alert("Error",  "Unable to access the restricted API", null, AlertType.ERROR);
	        alert.setTimeout(Alert.FOREVER);
	        setCurrentDisplayable(alert);
		}
	}


	protected void destroyApp(boolean unconditional) {
		
	}

	protected void pauseApp() {
		
	}

	public static void setCurrentDisplayable(Displayable nextDisplayable) {
		Display display = Display.getDisplay(instance);
		display.setCurrent(nextDisplayable);
	}


	public static void exit() {
		instance.destroyApp(true);
		instance.notifyDestroyed();
	}

}
