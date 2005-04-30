/*
 *  MicroEmulator
 *  Copyright (C) 2001-2005 Bartek Teodorczyk <barteo@barteo.net>
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

package net.barteo.me.gkey;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


public class GetKeyMIDletTest extends MIDlet implements CommandListener, GetKeyHandler
{
    public final static Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);

    private List menu = new List("Menu", List.IMPLICIT);
    private Alert alert = new Alert("GetKey pressed");
	
	
	public GetKeyMIDletTest()
	{
		menu.append("Menu item 1", null);
		menu.append("Menu item 2", null);
		menu.addCommand(CMD_EXIT);
		menu.setCommandListener(this);
	}

	
	protected void startApp() 
			throws MIDletStateChangeException 
	{
		Display.getDisplay(this).setCurrent(menu);
	}


	protected void pauseApp() 
	{
	}


	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException 
	{
	}


	public void commandAction(Command c, Displayable d) 
	{
		if (d == menu) {
			if (c == CMD_EXIT) {
		        try {
					destroyApp(true);
			        notifyDestroyed();
				} catch (MIDletStateChangeException ex) {
					ex.printStackTrace();
				}
			}
		}
	}


	public void getKeyPressed() 
	{
		Display.getDisplay(this).setCurrent(alert, menu);
	}

}
