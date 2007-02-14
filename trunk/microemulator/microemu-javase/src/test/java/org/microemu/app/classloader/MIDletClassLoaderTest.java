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

import org.microemu.app.util.IOUtils;

/**
 * @author vlads
 *
 */
public class MIDletClassLoaderTest extends TestCase {

	private static final String TEST_APP_JAR = "bytecode-test-app.jar"; 
	
	private static final String TEST_CLASS = "org.TestMain";
	
	public void testGetResourceAsStream() throws Exception {
		
		ClassLoader parent = MIDletClassLoaderTest.class.getClassLoader();
		
		URL jarURL = parent.getResource(TEST_APP_JAR);
		assertNotNull("Can't find app jar", jarURL);
		
		URLClassLoader ucl = new URLClassLoader(new URL[]{jarURL}, parent);
		
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
}
