/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  @version $Id$
 */
package org.microemu.app.classloader;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.microemu.Injected;
import org.microemu.app.util.EventCatureLoggerAppender;
import org.microemu.app.util.IOUtils;
import org.microemu.app.util.MIDletResourceLoader;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.log.Logger;
import org.microemu.log.LoggingEvent;

/**
 * @author vlads
 *
 */
public class MIDletClassLoaderTest extends TestCase {

	public static final String TEST_APP_JAR = "bytecode-test-app.jar"; 
	
	public static final String TEST_CLASS = "org.TestMain";
	
	EventCatureLoggerAppender capture;

	private boolean enhanceCatchBlockSave;
	
	protected void setUp() throws Exception {
		super.setUp();
		capture = new EventCatureLoggerAppender();
		Logger.addAppender(capture);
		enhanceCatchBlockSave = MIDletClassLoader.enhanceCatchBlock;
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		Logger.removeAppender(capture);
		MIDletClassLoader.enhanceCatchBlock = enhanceCatchBlockSave; 
	}
	
	public void testGetResourceAsStream() throws Exception {
		
		ClassLoader parent = MIDletClassLoaderTest.class.getClassLoader();
		
		URL jarURL = parent.getResource(TEST_APP_JAR);
		assertNotNull("Can't find app jar", jarURL);
		
		URLClassLoader ucl = new URLClassLoader(new URL[]{jarURL});
		
		final String testFile = "META-INF/MANIFEST.MF";
		
		InputStream is = null;
		try {
			is = ucl.getResourceAsStream(testFile);
			assertNotNull("URLClassLoader", is);
		} finally {
			IOUtils.closeQuietly(is);
		}
		
		MIDletClassLoader mcl = new MIDletClassLoader(parent);
		mcl.addURL(jarURL);
		try {
			is = ucl.getResourceAsStream(testFile);
			assertNotNull("MIDletClassLoader", is);
		} finally {
			IOUtils.closeQuietly(is);
		}
		
	}
	
	public void testApplication() throws Exception {
		ClassLoader parent = MIDletClassLoaderTest.class.getClassLoader();
		URL jarURL = parent.getResource(TEST_APP_JAR);
		assertNotNull("Can't find app jar", jarURL);
		
		System.setProperty("test.verbose", "1");
		
		MIDletSystemProperties.setProperty("test.property1", "1");
		MIDletSystemProperties.setProperty("microedition.platform", null);
		
		MIDletClassLoader.enhanceCatchBlock = false;
		MIDletClassLoader mcl = new MIDletClassLoader(parent);
		mcl.disableClassPreporcessing(Injected.class);
		MIDletResourceLoader.classLoader = mcl;
		mcl.addURL(jarURL);
		
		Class instrumentedClass = mcl.loadClass(TEST_CLASS);
		Runnable instrumentedInstance = (Runnable) instrumentedClass.newInstance();
		instrumentedInstance.run();
		
		LoggingEvent lastEvent = capture.getLastEvent();
		assertNotNull("got event", lastEvent);
		assertEquals("All tests OK", lastEvent.getMessage());
		StackTraceElement ste = lastEvent.getLocation();
		assertEquals("MethodName", "run", ste.getMethodName());
		assertEquals("ClassName", TEST_CLASS, ste.getClassName());

	}
	
	private void runEnhanceCatchBlock(MIDletClassLoader mcl, String name) throws Exception {
		Class instrumentedClass = mcl.loadClass(name);
		Runnable instrumentedInstance = (Runnable) instrumentedClass.newInstance();
		instrumentedInstance.run();
		
		LoggingEvent lastEvent = capture.getLastEvent();
		assertNotNull("got event", lastEvent);
		assertNotNull("got message", lastEvent.getMessage());
		System.out.println("[" +lastEvent.getMessage() + "]");
		assertTrue("error message", lastEvent.getMessage().contains("MIDlet caught"));
	}
	
	public void testEnhanceCatchBlock() throws Exception {
		ClassLoader parent = MIDletClassLoaderTest.class.getClassLoader();
		URL jarURL = parent.getResource(TEST_APP_JAR);
		assertNotNull("Can't find app jar", jarURL);
		
		System.setProperty("test.verbose", "1");
		
		MIDletClassLoader.enhanceCatchBlock = true;
		MIDletClassLoader mcl = new MIDletClassLoader(parent);
		mcl.disableClassPreporcessing(Injected.class);
		mcl.addURL(jarURL);
		runEnhanceCatchBlock(mcl, "org.catchBlock.CatchThrowable");
	}
}
