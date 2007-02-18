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
package org.microemu.app.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.microemu.device.Device;
import org.microemu.log.Logger;

/**
 * @author vlads
 * 
 * This class is called by MIDlet to access System Property.
 * Call injection is made by MIDlet ClassLoaded
 * 
 */
public class MIDletSystemProperties {

	/**
	 * This may be a configuration option.
	 */
	public static boolean applyToJavaSystemProperties = true;

	/**
	 * Permits null values. 
	 */
	private static final Map props = new HashMap();

	private static Map systemPropertiesPreserve;

	private static List systemPropertiesDevice;

	private static boolean wanrOnce = true;

	static {
		init();
	}

	private static void init() {
		// This are set in Config
		//setProperty("microedition.configuration", "CLDC-1.1");
		//setProperty("microedition.configuration", "MIDP-2.0");
		setProperty("microedition.platform", "MicroEmulator");
		setProperty("microedition.encoding", System.getProperty("file.encoding"));
	}

	/**
	 * Gets the system property indicated by the specified key.
	 * The only function called by MIDlet
	 * @param key   the name of the system property
	 * @return
	 */
	public static String getProperty(String key) {
		if (props.containsKey(key)) {
			return (String) props.get(key);
		}
		String v = getDynamicProperty(key);
		if (v != null) {
			return v;
		}
		try {
			return System.getProperty(key);
		} catch (SecurityException e) {
			return null;
		}
	}

	private static String getDynamicProperty(String key) {
		if (key.equals("microedition.locale")) {
			return Locale.getDefault().getLanguage();
		}
		return null;
	}

	public static Set getPropertiesSet() {
		return props.entrySet();
	}

	public static String setProperty(String key, String value) {
		if (applyToJavaSystemProperties) {
			try {
				if (value == null) {
					System.getProperties().remove(key);
				} else {
					System.setProperty(key, value);
				}
			} catch (SecurityException e) {
				if (wanrOnce) {
					wanrOnce = false;
					Logger.error("Cannot update Java System.Properties", e);
				}
			}
		}
		return (String) props.put(key, value);
	}

	public static String clearProperty(String key) {
		if (applyToJavaSystemProperties) {
			try {
				System.getProperties().remove(key);
			} catch (SecurityException e) {
				if (wanrOnce) {
					wanrOnce = false;
					Logger.error("Cannot update Java System.Properties", e);
				}
			}
		}
		return (String) props.remove(key);
	}

	public static void setProperties(Map properties) {
		for (Iterator i = properties.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			setProperty((String) e.getKey(), (String) e.getValue());
		}
	}

	public static void setDevice(Device newDevice) {
		// Restore System Properties from previous device activation.
		if (systemPropertiesDevice != null) {
			for (Iterator iter = systemPropertiesDevice.iterator(); iter.hasNext();) {
				clearProperty((String) iter.next());
			}
		}
		if (systemPropertiesPreserve != null) {
			for (Iterator i = systemPropertiesPreserve.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				setProperty((String) e.getKey(), (String) e.getValue());
			}
		}
		systemPropertiesDevice = new Vector();
		systemPropertiesPreserve = new HashMap();
		for (Iterator i = newDevice.getSystemProperties().entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			String key = (String) e.getKey();
			if (props.containsKey(key)) {
				systemPropertiesPreserve.put(key, props.get(key));
			} else {
				systemPropertiesDevice.add(key);
			}
			setProperty(key, (String) e.getValue());
		}
	}
}
