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

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MainTestMIDlet extends MIDlet implements CommandListener, MIDletUnderTests {

	static final Command exitCommand = new Command("Exit", Command.EXIT, 1);

	List menuList = null;

	Vector testPanels;
	
	public MainTestMIDlet() {
		
	}

	protected void startApp() throws MIDletStateChangeException {
		Manager.midletInstance = this;
		
		if (menuList == null) {
			testPanels = new Vector();
			testPanels.addElement(new ItemsOnForm());
			testPanels.addElement(new ErrorHandlingForm());
			testPanels.addElement(new ErrorHandlingCanvas());
			if (OverrideNewJSRCanvas.enabled) {
				testPanels.addElement(new OverrideNewJSRCanvas());
			}
			if (OverrideNewJSR2Canvas.enabled) {
				testPanels.addElement(new OverrideNewJSR2Canvas());
			}
			if (OverrideNewJSR2Canvas.enabled) {
				testPanels.addElement(new OverrideNewJSR2Canvas());
			}
			if (PreporcessorTestCanvas.enabled) {
				testPanels.addElement(new PreporcessorTestCanvas());
			}

			menuList = new List("Manual Tests", List.IMPLICIT);

			for (Enumeration iter = testPanels.elements(); iter.hasMoreElements();) {
				menuList.append(((Displayable) iter.nextElement()).getTitle(), null);
			}
			menuList.addCommand(exitCommand);
			menuList.setCommandListener(this);
		}
		setCurrentDisplayable(menuList);
	}
	
	public void commandAction(Command c, Displayable d) {
		if (d == menuList) {
			if (c == List.SELECT_COMMAND) {
				setCurrentDisplayable((Displayable)testPanels.elementAt(menuList.getSelectedIndex()));
			} else if (c == exitCommand) {
				try {
					destroyApp(true);
				} catch (MIDletStateChangeException e) {
				}
				notifyDestroyed();
			}
		}
	}
	
	public void showMainPage() {
		setCurrentDisplayable(menuList);
	}
	
	public void setCurrentDisplayable(Displayable nextDisplayable) {
		Display display = Display.getDisplay(this);
		//Displayable current = display.getCurrent();
		display.setCurrent(nextDisplayable);
	}
	
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}



}
