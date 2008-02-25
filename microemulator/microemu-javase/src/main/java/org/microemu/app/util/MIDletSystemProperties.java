/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *  @version $Id$
 */
package org.microemu.app.util;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
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
 * This class is called by MIDlet to access System Property. Call injection is
 * made by MIDlet ClassLoaded
 * 
 */
public class MIDletSystemProperties {

	/**
	 * This may be a configuration option. But not for applet and Web Start.
	 */
	public static boolean applyToJavaSystemProperties = true;

	/**
	 * Permits null values.
	 */
	private static final Map props = new HashMap();

	private static Map systemPropertiesPreserve;

	private static List systemPropertiesDevice;

	private static boolean wanrOnce = true;

	private static boolean initialized = false;

	/* The context to be used when starting MicroEmulator */
	private static AccessControlContext acc;

	private static void initOnce() {
		// Can't use static initializer because of applyToJavaSystemProperties
		// in applet
		if (initialized) {
			return;
		}
		initialized = true;
		// This are set in Config
		// setProperty("microedition.configuration", "CLDC-1.1");
		// setProperty("microedition.profiles", "MIDP-2.0");
		setProperty("microedition.platform", "MicroEmulator");
		setProperty("microedition.encoding", getSystemProperty("file.encoding"));
	}

	/**
	 * Allow Access to system properties from MIDlet
	 */
	public static void initContext() {
		acc = AccessController.getContext();
	}

	/**
	 * Gets the system property indicated by the specified key. The only
	 * function called by MIDlet
	 * 
	 * @param key
	 *            the name of the system property
	 * @return
	 */
	public static String getProperty(String key) {
		initOnce();
		if (props.containsKey(key)) {
			return (String) props.get(key);
		}
		String v = getDynamicProperty(key);
		if (v != null) {
			return v;
		}
		try {
			return getSystemProperty(key);
		} catch (SecurityException e) {
			return null;
		}
	}

	public static String getSystemProperty(String key) {
		try {
			if (acc != null) {
				return getSystemPropertySecure(key);
			} else {
				return System.getProperty(key);
			}
		} catch (SecurityException e) {
			return null;
		}
	}

	private static String getSystemPropertySecure(final String key) {
		try {
			return (String) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() {
					return System.getProperty(key);
				}
			}, acc);
		} catch (Throwable e) {
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
		initOnce();
		return props.entrySet();
	}

	public static String setProperty(String key, String value) {
		initOnce();
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
					Logger.debug("Continue ME2 operations with no updates to system Properties");
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
		initOnce();
		for (Iterator i = properties.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			setProperty((String) e.getKey(), (String) e.getValue());
		}
	}

	public static void setDevice(Device newDevice) {
		initOnce();
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
