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
import java.io.Serializable;

import org.microemu.app.util.MIDletOutputStreamRedirector;
import org.microemu.app.util.MIDletResourceLoader;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.log.Logger;

/**
 * @author vlads
 *
 * This code is added to MIDlet application to solve problems with security policy  while running in Applet and Webstart.
 * Also solves resource resource loading paterns commonly used in MIDlet and not aceptable in Java SE application
 * The calls to this code is injected by ClassLoader or "Save for Web...".
 * 
 * This class is used instead injected one when application is running in Applet with MicroEmulator. 
 *
 * Serializable is just internal flag to verify tha proper class is loaded by application.
 */
public final class Injected implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * This allow redirection of stdout to MicroEmulator console
	 */
	public final static PrintStream out = outPrintStream();

	public final static PrintStream err = errPrintStream();

	static {
		Logger.addLogOrigin(Injected.class);
	}
	
	/**
	 * We don't need to instantiate the class, all access is static
	 */
	private Injected() {
		
	}
	
	private static PrintStream outPrintStream() {
		//return System.out;
		return MIDletOutputStreamRedirector.out;
	}

	private static PrintStream errPrintStream() {
		//return System.err;
		return MIDletOutputStreamRedirector.err;
	}
	
	/**
	 * Redirect throwable.printStackTrace() to MicroEmulator console
	 */
	public static void printStackTrace(Throwable t) {
		Logger.error("MIDlet caught", t);
	}
	
	/**
	 * This code Ingected By MicroEmulator to enable access to System properties while running in Applet
     *
     * @param      key   the name of the system property.
     * @return     the string value of the system property,
     *             or <code>null</code> if there is no property with that key.
	 */
	public static String getProperty(String key) {
		return MIDletSystemProperties.getProperty(key);
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
		return MIDletResourceLoader.getResourceAsStream(origClass, name);
	}

	/**
	 * TODO fix ChangeCallsMethodVisitor
	 */
	public static Throwable handleCatchThrowable(Throwable t) {
		Logger.error("MIDlet caught", t);
		return t;
	}
}
