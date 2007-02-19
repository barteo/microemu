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
package org.microemu;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;

import org.microemu.app.util.MIDletOutputStreamRedirector;
import org.microemu.app.util.MIDletResourceLoader;
import org.microemu.app.util.MIDletSystemProperties;

/**
 * @author vlads
 *
 * This code is added to MIDlet application to solve problems with security policy  while running in Applet and Webstart.
 * Also solves resource resource loading paterns commonly used in MIDlet and not aceptable in Java SE application
 * The calls to this code is injected by ClassLoader or "Prepare for Applet".
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

}
