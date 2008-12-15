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
package org.microemu;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author vlads
 *
 * This code is added to MIDlet application to solve problems while running in applet.
 * The code is attached to application jar.
 * 
 * You need to use "Save for Web..." in MicroEmulator Tools Menu.
 * The result jar is safe to run on any other Emulator or device. 
 * 
 * This class is not used while application is running in Applet with MicroEmulator. 
 * Different class with the same name is used from microemu-javase-applet.jar.    
 */
public final class Injected {

	/**
	 * This allow redirection of stdout to MicroEmulator console
	 */
	public final static PrintStream out = outPrintStream();

	public final static PrintStream err = errPrintStream();

	/**
	 * We don't need to instantiate the class, all access is static
	 */
	private Injected() {
		
	}
	
	private static PrintStream outPrintStream() {
		return System.out;
	}

	private static PrintStream errPrintStream() {
		return System.err;
	}
	
	/**
	 * Redirect throwable.printStackTrace() to MicroEmulator console
	 */
	public static void printStackTrace(Throwable t) {
		t.printStackTrace();
	}
	
	/**
	 * This code Ingected By MicroEmulator to enable access to System properties while running in Applet
     *
     * @param      key   the name of the system property.
     * @return     the string value of the system property,
     *             or <code>null</code> if there is no property with that key.
	 */
	public static String getProperty(String key) {
		try {
			return System.getProperty(key);
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * 
	 * Returns an input stream for reading the specified resource.
     *
     * <p> The search order is described in the documentation for {@link
     * #getResource(String)}.  </p>
     *
     * @param  origClass
     * @param  name  The resource name
     *
     * @return  An input stream for reading the resource, or <tt>null</tt>
     *          if the resource could not be found
	 */
	public static InputStream getResourceAsStream(Class origClass, String name)  {
		return origClass.getResourceAsStream(name);
	}
	
	/**
	 * Enhanced Catch Block
	 */
	public static Throwable handleCatchThrowable(Throwable t) {
		return t;
	}
	
}
