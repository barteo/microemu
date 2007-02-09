package org.microemu;

import java.util.Hashtable;

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
		return (String)props.get(key);
	}
}
