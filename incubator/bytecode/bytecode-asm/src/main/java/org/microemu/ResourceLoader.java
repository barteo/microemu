package org.microemu;

import java.io.InputStream;

public class ResourceLoader {

	public static ClassLoader classLoader;
	
	private static boolean java13 = false;
	
	private static final String FQCN = ResourceLoader.class.getName();
	
	public static InputStream getResourceAsStream(Class origClass, String resourceName)  {
		System.out.println("Load MIDlet resource [" + resourceName + "]");
		if (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		if (classLoader != origClass.getClassLoader()) {
			// showWarning
			if (!java13) {
				try {
					StackTraceElement[] ste = new Throwable().getStackTrace();
					for (int i = 0; i < ste.length - 1; i++) {
						if (FQCN.equals(ste[i].getClassName())) {
							StackTraceElement callLocation = ste[i + 1];
							System.out.println("WARN attempt to load resource [" + resourceName + "] using System ClasslLoader from " + callLocation.toString());
						}
					}
				} catch (Throwable e) {
					java13 = true;
				}
			}
		}
			
		InputStream is = classLoader.getResourceAsStream(resourceName);
		if (is == null) {
			System.out.println("WARN Resource not found " + resourceName);
		}
		return is;
	}
	
}
