package org;

public class TestMain implements Runnable {

	public static void main(String[] args) {
		(new TestMain()).run();
	}

	public void run() {
		assert("microedition.io.file.FileConnection.version", "1.0");
		assert("microedition.platform", "MicroEmulator-Test");
		
		(new TestResourceLoad()).run();
		
		System.out.println("OK");
	}
	
	private void assert(String key, String expected) {
		String value = System.getProperty(key);
		System.out.println("Got System.getProperty " + key + " value [" + value + "]");
		if (!expected.equals(value)) {
			throw new RuntimeException("Unexpected property value " + value + " expected " + expected);
		}
	}
}
