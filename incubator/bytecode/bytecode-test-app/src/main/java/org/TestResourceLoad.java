package org;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class illustrate Resource usage paterns commonly used in MIDlet and not aceptable in Java SE application 
 * @author vlads
 */

public class TestResourceLoad implements Runnable {

	public void loadStringsUsingSystemClassLoaded() {
		String resourceName = "/strings.txt";
		String expected = "proper MIDlet resources strings";
		InputStream inputstream = "".getClass().getResourceAsStream(resourceName);
		if (inputstream == null) {
			throw new RuntimeException("Resource not found " + resourceName);
		}
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(inputstream));
			String value = r.readLine();
			if (!expected.equals(value)) {
				throw new RuntimeException("Unexpected resource " + resourceName + " value [" + value + "]\nexpected [" + expected + "]");
			}
		} catch (IOException e) {
			throw new RuntimeException("Resource read error " + resourceName, e);
		} finally {
			try {
				inputstream.close();
			} catch (IOException ignore) {
			}
		}
	}

	public void run() {
		
		System.out.println("ClassLoader " + this.getClass().getClassLoader().hashCode() +  " TestResourceLoad");
		
		loadStringsUsingSystemClassLoaded();
		
	}
	
}
