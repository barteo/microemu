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

package net.barteo.me.gkey.device;

import javax.microedition.midlet.MIDlet;

import net.barteo.me.gkey.GetKeyHandler;

import org.microemu.EmulatorContext;
import org.microemu.app.launcher.Launcher;
import org.microemu.device.InputMethod;
import org.microemu.device.j2se.J2SEDevice;


public class GKeyJ2SEDevice extends J2SEDevice implements GetKeyHandler
{
    private InputMethod inputMethod;
    

	public void init(EmulatorContext context) 
	{
	  	super.init(context, "/net/barteo/me/gkey/device/device.xml");
	}
	
	
	public InputMethod getInputMethod() 
	{
		if (inputMethod == null) {
			inputMethod = new GKeyJ2SEInputMethod(this);
		}

		return inputMethod;
	}


	public void getKeyPressed() 
	{
		Launcher launcher = getEmulatorContext().getLauncher();
		MIDlet midlet = launcher.getCurrentMIDlet();
		if (midlet == launcher) {
			midlet = launcher.startSelectedMIDlet();
		}
		if (midlet instanceof GetKeyHandler) {
			((GetKeyHandler) midlet).getKeyPressed();
		}
	}	  	  

}
