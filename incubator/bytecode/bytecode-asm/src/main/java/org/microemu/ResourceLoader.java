package org.microemu;

import java.io.InputStream;

public class ResourceLoader {

	public static ClassLoader classLoader;
	
	public static InputStream getResourceAsStream(Object origLoader, String resourceName)  {
		System.out.println("Load MIDlet resource [" + resourceName + "]");
		if (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		InputStream is = classLoader.getResourceAsStream(resourceName);
		if (is == null) {
			System.out.println("WARN Resource not found " + resourceName);
		}
		return is;
	}
	
}
