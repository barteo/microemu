package org;

public class TestMain implements Runnable {

	public static void main(String[] args) {
		(new TestMain()).run();
	}

	public void run() {
		
		System.out.println("ClassLoader " + this.getClass().getClassLoader().hashCode() +  " TestMain");
		
		assertProperty("microedition.io.file.FileConnection.version", "1.0");
		assertProperty("microedition.platform", "MicroEmulator-Test");
		
		System.out.println("System.getProperty OK");
		
		(new TestResourceLoad()).run();
		(new TestStaticInitializer()).run();
		
		System.out.println("All test OK");
	}
	
	private void assertProperty(String key, String expected) {
		String value = System.getProperty(key);
		System.out.println("Got System.getProperty " + key + " value [" + value + "]");
		if (!expected.equals(value)) {
			throw new RuntimeException("Unexpected property value " + value + " expected " + expected);
		}
	}
}
