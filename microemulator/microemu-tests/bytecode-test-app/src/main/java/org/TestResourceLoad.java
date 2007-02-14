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
		String resourceName;
		String expected;
		
		resourceName = "/app-data.txt";
		expected = "private app-data";
		verifyLoadStrings("".getClass().getResourceAsStream(resourceName), "\"\".getClass() " +  resourceName, expected);
		verifyLoadStrings(String.class.getResourceAsStream(resourceName), "String.class. " +  resourceName, expected);
		
	}
	
	public void accessTest() {
		String resourceName;
		String expected;
		
		resourceName = "/container-internal.txt";
		verifyLoadStrings(this.getClass().getResourceAsStream(resourceName), "this.getClass() " + resourceName, null);
		verifyLoadStrings("".getClass().getResourceAsStream(resourceName), "\"\".getClass() " +  resourceName, null);
		
		resourceName = "/app-data.txt";
		expected = "private app-data";
		verifyLoadStrings(this.getClass().getResourceAsStream(resourceName), "this.getClass() " + resourceName, expected);
		verifyLoadStrings("".getClass().getResourceAsStream(resourceName), "\"\".getClass() " +  resourceName, expected);
	}

	public void multipleResources() {
		
		String resourceName = "/strings.txt";
		String expected = "proper MIDlet resources strings";
		verifyLoadStrings(this.getClass().getResourceAsStream(resourceName), "this.getClass() " + resourceName, expected);
		verifyLoadStrings("".getClass().getResourceAsStream(resourceName), "\"\".getClass() " +  resourceName, expected);
		
	}

	
	private void verifyLoadStrings(InputStream inputstream, String resourceName, String expected) {	
		if (inputstream == null) {
			if (expected == null) {
				System.out.println("OK - Resource not found " + resourceName);
				return;
			}
			throw new RuntimeException("Resource not found " + resourceName);
		} else {
			if (expected == null) {
				throw new RuntimeException("Can access resource " + resourceName);
			}
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
		multipleResources();
		accessTest();
		
	}
	
}
