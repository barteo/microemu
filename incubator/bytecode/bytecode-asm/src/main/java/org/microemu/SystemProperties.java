package org.microemu;

import java.util.Hashtable;

/**
 * This class is called by MIDlet to access System Property.
 * Call injection is made by MIDlet ClassLoaded
 * 
 * @author vlads
 *
 */
public class SystemProperties {

	private static final Hashtable props = new Hashtable();
	
	static {
		init();
	}
	
	private static void init() {
		props.put("microedition.io.file.FileConnection.version", "1.0");
		props.put("microedition.configuration", "CLDC-1.1");
		props.put("microedition.configuration", "MIDP-2.0");
		props.put("microedition.platform" , "MicroEmulator");
		props.put("microedition.locale", "EN_GB");
	}
	
	public static String getProperty(String key) {
		String v = (String)props.get(key);
		if (v != null) {
			return v;
		}
		try {
			return System.getProperty(key);
		} catch (SecurityException e) {
			return null;
		}
	}
	
	public static String setProperty(String key, String value) {
		return (String) props.put(key , value);
	}
	
	public static String clearProperty(String key) {
		return (String) props.remove(key);
	}
	
}
