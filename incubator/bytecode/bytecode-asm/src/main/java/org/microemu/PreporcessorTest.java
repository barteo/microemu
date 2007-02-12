package org.microemu;

import java.io.FileOutputStream;

import org.objectweb.asm.*;

public class PreporcessorTest {

	public static String TEST_CLASS = "org.TestMain";

	public static void main(String[] args) throws Exception {
		
		saveToFile();
		
		runClass();
		
	}
	
	public static void runClass() throws Exception {
		SystemProperties.setProperty("microedition.platform" , "MicroEmulator-Test");
		
		PreporcessorClassLoader cl = new PreporcessorClassLoader(PreporcessorTest.class.getClassLoader());
		cl.disableClassLoad(SystemProperties.class);
		cl.disableClassLoad(ResourceLoader.class);
		cl.disableClassLoad(OutputStreamRedirector.class);
		ResourceLoader.classLoader = cl;
		
		cl.addClassURL(TEST_CLASS);
		
		Class instrumentedClass = cl.loadClass(TEST_CLASS);
		Runnable instrumentedInstance = (Runnable)instrumentedClass.newInstance();
		instrumentedInstance.run();
	}
	
	public static void saveToFile() throws Exception {
		
		ClassReader cr = new ClassReader("org.TestMain");
        ClassWriter cw = new ClassWriter(false);
        ClassVisitor cv = new ChangeCallsClassVisitor(cw, true);
        cr.accept(cv, false);
        byte[] b = cw.toByteArray();

        // stores the adapted class on disk
        FileOutputStream fos = new FileOutputStream("Test.class");
        fos.write(b);
        fos.close();
		
	}

}
