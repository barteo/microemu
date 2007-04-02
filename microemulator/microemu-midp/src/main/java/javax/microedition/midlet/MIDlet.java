/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package javax.microedition.midlet;

import javax.microedition.io.ConnectionNotFoundException;

import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;

public abstract class MIDlet {
	
	private boolean destroyed;
	
	private MIDletAccess access = new MIDletAccessor(this);

	class MIDletAccessor extends MIDletAccess {
		
		public MIDletAccessor(MIDlet amidlet) {
			super(amidlet);

			MIDletBridge.setAccess(amidlet, this);

			destroyed = false;
		}

		public void startApp() throws MIDletStateChangeException {
			if (MIDletBridge.getCurrentMIDlet() != midlet) {
				MIDletBridge.setCurrentMIDlet(midlet);
			}
			MIDletBridge.getRecordStoreManager().init();
			midlet.startApp();
		}

		public void pauseApp() {
			midlet.pauseApp();
		}

		public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
			if (!destroyed) {
				midlet.destroyApp(unconditional);
			}
			DisplayAccess da = getDisplayAccess();
			if (da != null) {
				getDisplayAccess().clean();
				setDisplayAccess(null);
			}
		}
	}

	protected MIDlet() {
	}

	protected abstract void startApp() throws MIDletStateChangeException;

	protected abstract void pauseApp();

	protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

	public final int checkPermission(String permission) {
		// TODO
		return 0;
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
