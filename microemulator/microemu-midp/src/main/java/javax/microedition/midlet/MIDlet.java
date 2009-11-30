/*
 * MicroEmulator 
 * Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 * Copyright (C) 2007-2007 Vlad Skarzhevskyy
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
 * @version $Id$  
 */

package javax.microedition.midlet;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;

import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;

public abstract class MIDlet {

	private boolean destroyed;

	class MIDletAccessor extends MIDletAccess {

		public MIDletAccessor() {
			super(MIDlet.this);
			destroyed = false;
		}

		public void startApp() throws MIDletStateChangeException {
			MIDletBridge.setCurrentMIDlet(midlet);
			midlet.startApp();
		}

		public void pauseApp() {
			midlet.pauseApp();
		}

		public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
			if (!midlet.destroyed) {
				midlet.destroyApp(unconditional);
			}
			DisplayAccess da = getDisplayAccess();
			if (da != null) {
				da.clean();
				setDisplayAccess(null);
			}
			MIDletBridge.destroyMIDletContext(MIDletBridge.getMIDletContext(midlet));
		}
	}

	protected MIDlet() {
		MIDletBridge.registerMIDletAccess(new MIDletAccessor());

		// Initialize Display
		Display.getDisplay(this);
	}

	protected abstract void startApp() throws MIDletStateChangeException;

	protected abstract void pauseApp();

	protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

	public final int checkPermission(String permission) {
		return MIDletBridge.checkPermission(permission);
	}

	public final String getAppProperty(String key) {
		return MIDletBridge.getAppProperty(key);
	}

	public final void notifyDestroyed() {
		destroyed = true;
		MIDletBridge.notifyDestroyed();
	}

	public final void notifyPaused() {
	}

	public final boolean platformRequest(String URL) throws ConnectionNotFoundException {
		return MIDletBridge.platformRequest(URL);
	}

	public final void resumeRequest() {
		// TODO implement
	}

}
