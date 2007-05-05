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
package org.microemu;

import javax.microedition.midlet.MIDlet;

import org.microemu.app.launcher.Launcher;

/**
 * 
 * Represents context for running MIDlet.
 * Enables access to MIDlet, MIDletAccess by threadLocal using MIDletBridge.
 * Created before MIDlet.
 * 
 * Usage: MIDletBridge.getMIDletContext();
 * 
 * @author vlads
 *
 */
public class MIDletContext {

	private MIDletAccess midletAccess;
	
	public MIDletContext() {
		
	}
	
	public MIDletAccess getMIDletAccess() {
		return midletAccess;
	}
	
	protected void setMIDletAccess(MIDletAccess midletAccess) {
		this.midletAccess = midletAccess;	
	}
	
	public MIDlet getMIDlet() {
		if (midletAccess == null) {
			return null;
		}
		return midletAccess.midlet;
	}
	
	public boolean isLauncher() {
		return (getMIDlet() instanceof Launcher);
	}
}
