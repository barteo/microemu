package org;

public class TestMain {

	private String filed = "filedValue";
	
	public TestMain() {
		
	}
	
	public static void main(String[] args) {
		String value = System.getProperty("microedition.io.file.FileConnection.version");
		System.out.println("Got value " + value);
		(new TestMain()).stuff();
	}

	
	public void stuff() {
		System.out.println("stuff" + filed);
	}
}
