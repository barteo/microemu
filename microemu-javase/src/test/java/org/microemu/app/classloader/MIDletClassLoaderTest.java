/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
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
import org.microemu.app.util.MIDletThread;
import org.microemu.app.util.MIDletTimer;
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

		URLClassLoader ucl = new URLClassLoader(new URL[] { jarURL });

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

		MIDletResourceLoader.traceResourceLoading = true;
		MIDletClassLoader.enhanceCatchBlock = false;
		MIDletClassLoader mcl = new MIDletClassLoader(parent);
		// delegatingToParent = false;
		MIDletClassLoaderConfig clConfig = new MIDletClassLoaderConfig();
		clConfig.delegationType = MIDletClassLoaderConfig.DELEGATION_STRICT;
		mcl.configure(clConfig);
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
		System.out.println("[" + lastEvent.getMessage() + "]");
		assertTrue("error message", lastEvent.getMessage().indexOf("MIDlet caught") != -1);
	}

	public void x_testEnhanceCatchBlock() throws Exception {
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

	public void testTimer() throws Exception {
		ClassLoader parent = MIDletClassLoaderTest.class.getClassLoader();
		URL jarURL = parent.getResource(TEST_APP_JAR);
		assertNotNull("Can't find app jar", jarURL);

		System.setProperty("test.verbose", "1");

		MIDletClassLoader mcl = new MIDletClassLoader(parent);
		mcl.disableClassPreporcessing(Injected.class);
		mcl.disableClassPreporcessing(MIDletThread.class);
		mcl.disableClassPreporcessing(MIDletTimer.class);
		mcl.addURL(jarURL);

		Class instrumentedClass = mcl.loadClass("org.TimerCreationRunner");
		Runnable instrumentedInstance = (Runnable) instrumentedClass.newInstance();
		instrumentedInstance.run();
	}

	public void testTimerCancell() throws Exception {
		ClassLoader parent = MIDletClassLoaderTest.class.getClassLoader();
		URL jarURL = parent.getResource(TEST_APP_JAR);
		assertNotNull("Can't find app jar", jarURL);

		System.setProperty("test.verbose", "1");

		MIDletClassLoader mcl = new MIDletClassLoader(parent);
		mcl.disableClassPreporcessing(Injected.class);
		mcl.disableClassPreporcessing(MIDletThread.class);
		mcl.disableClassPreporcessing(MIDletTimer.class);
		mcl.addURL(jarURL);

		Class instrumentedClass = mcl.loadClass("org.TimerTaskCancelRunner");
		Runnable instrumentedInstance = (Runnable) instrumentedClass.newInstance();
		instrumentedInstance.run();
	}
}
