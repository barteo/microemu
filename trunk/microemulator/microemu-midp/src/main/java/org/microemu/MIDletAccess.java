/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 *  Contributor(s):
 *    Andres Navarro
 *    
 *  @version $Id$    
 */

package org.microemu;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.microemu.DisplayAccess;

/**
 * 
 * Enables access to MIDlet protected methods.
 *
 */
public abstract class MIDletAccess {
	
	public MIDlet midlet;

	private DisplayAccess displayAccess;

	private GameCanvasKeyAccess gameCanvasKeyAccess;

	public MIDletAccess(MIDlet amidlet) {
		midlet = amidlet;
	}

	public GameCanvasKeyAccess getGameCanvasKeyAccess() {
		return gameCanvasKeyAccess;
	}

	public void setGameCanvasKeyAccess(GameCanvasKeyAccess aGameCanvasKeyAccess) {
		gameCanvasKeyAccess = aGameCanvasKeyAccess;
	}

	public DisplayAccess getDisplayAccess() {
		return displayAccess;
	}

	public void setDisplayAccess(DisplayAccess adisplayAccess) {
		displayAccess = adisplayAccess;
	}

	public abstract void startApp() throws MIDletStateChangeException;

	public abstract void pauseApp();

	public abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

}