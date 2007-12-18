/*
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2007-2007 Vlad Skarzhevskyy
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
 *    3GLab
 *    
 *  @version $Id$    
 */

package org.microemu;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import javax.microedition.midlet.MIDlet;

import org.microemu.app.launcher.Launcher;

/**
 * 
 * Enables access to MIDlet and MIDletAccess by threadLocal
 *
 */
public class MIDletBridge {

	static ThreadLocal /*<MIDletContext>*/ threadMIDletContexts = new ThreadLocal();
	
	static Map /*<MIDlet, MIDletContext>*/ midletContexts = new WeakHashMap();

	static MicroEmulator emulator = null;

	static MIDlet currentMIDlet = null;

	public static void setMicroEmulator(MicroEmulator emulator) {
		MIDletBridge.emulator = emulator;
	}
	
	public static MicroEmulator getMicroEmulator() {
		return emulator;
	}
	
	public static void setThreadMIDletContext(MIDletContext midletContext) {
		threadMIDletContexts.set(midletContext);
	}
	
	public static void registerMIDletAccess(MIDletAccess accessor) {
		MIDletContext c = (MIDletContext)threadMIDletContexts.get();
		if (c == null) {
			//throw new Error("setThreadMIDletContext should be called");
			c = new MIDletContext();
			setThreadMIDletContext(c);
		}
		c.setMIDletAccess(accessor);
		registerMIDletContext(c);
	}
	
	public static void registerMIDletContext(MIDletContext midletContext) {
		midletContexts.put(midletContext.getMIDlet(), midletContext);
	}

	public static MIDletContext getMIDletContext(MIDlet midlet) {
		return (MIDletContext)midletContexts.get(midlet);
	}
	
	public static MIDletContext getMIDletContext() {
		MIDletContext c = (MIDletContext)threadMIDletContexts.get();
		if (c != null) {
			return c;
		}
		return getMIDletContext(currentMIDlet);
	}

	public static void setCurrentMIDlet(MIDlet midlet) {
		currentMIDlet = midlet;
	}

	public static MIDlet getCurrentMIDlet() {
		MIDletContext c = getMIDletContext();
		if (c == null) {
			return null;
		}
		return c.getMIDlet();
	}
	
	public static MIDletAccess getMIDletAccess() {
		MIDletContext c = getMIDletContext();
		if (c == null) {
			return null;
		}
		return c.getMIDletAccess();
	}

	public static MIDletAccess getMIDletAccess(MIDlet midlet) {
		return getMIDletContext(midlet).getMIDletAccess();
	}

	
	public static RecordStoreManager getRecordStoreManager() {
		return emulator.getRecordStoreManager();
	}

	public static String getAppProperty(String key) {
		return emulator.getAppProperty(key);
	}
	
	public static InputStream getResourceAsStream(String name) {
		return emulator.getResourceAsStream(name);
	}


	public static void notifyDestroyed() {
		MIDletContext midletContext = getMIDletContext();
		emulator.notifyDestroyed(midletContext);
		destroyMIDletContext(midletContext);
	}

	public static void destroyMIDletContext(MIDletContext midletContext) {
		if (midletContext == null) {
			return;
		}
		emulator.destroyMIDletContext(midletContext);
		if (midletContexts.containsValue(midletContext)) {
			for (Iterator i = midletContexts.entrySet().iterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				if (entry.getValue() == midletContext) {
					midletContexts.remove(entry.getKey());
					break;
				}
			}
		}
	}
	
	public static boolean platformRequest(String URL) {
		return emulator.platformRequest(URL);
	}

	public static void clear() {
		
		currentMIDlet = null;
		
		// Preserve only Launcher Context
		for (Iterator i = midletContexts.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			MIDlet test = ((MIDletContext) entry.getValue()).getMIDlet();
			if (test instanceof Launcher) {
				midletContexts.clear();
				midletContexts.put(entry.getKey(), entry.getValue());
				return;
			}
		}
		// No Launcher found
		midletContexts.clear();
	}


}
