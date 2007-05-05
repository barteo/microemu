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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.StringTokenizer;

import org.microemu.app.util.IOUtils;
import org.microemu.log.Logger;

/**
 * 
 * Class loader for device and other Extensions
 * 
 * @author vlads
 *
 */
public class ExtensionsClassLoader extends URLClassLoader {

	private final static boolean debug = false;
	
	/* The context to be used when loading classes and resources */
    private AccessControlContext acc;
    
	public ExtensionsClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		acc = AccessController.getContext();
	}
	
	public void addURL(URL url) {
		super.addURL(url);
	}
	
	public void addClasspath(String classpath) {
		StringTokenizer st = new StringTokenizer(classpath, ";");
		while (st.hasMoreTokens()) {
			try {
				String path = st.nextToken();
				if (path.startsWith("file:")) {
					addURL(new URL(path));	
				} else {
					addURL(new URL(IOUtils.getCanonicalFileURL(new File(path))));
				}
			} catch (MalformedURLException e) {
				throw new Error(e);
			}
		}
	}
	 
	
	/**
	 * Finds the resource with the given name. A resource is some data (images,
	 * audio, text, etc) that can be accessed by class code in a way that is
	 * independent of the location of the code.
	 * 
	 * <p>
	 * The name of a resource is a '<tt>/</tt>'-separated path name that
	 * identifies the resource.
	 * 
	 * <p>
	 * Search order is reverse to standard implemenation
	 * </p>
	 * 
	 * <p>
	 * This method will first use {@link #findResource(String)} to find the
	 * resource. That failing, this method will NOT invoke the parent class
	 * loader.
	 * </p>
	 * 
	 * @param name
	 *            The resource name
	 * 
	 * @return A <tt>URL</tt> object for reading the resource, or
	 *         <tt>null</tt> if the resource could not be found or the invoker
	 *         doesn't have adequate privileges to get the resource.
	 * 
	 */
	public URL getResource(final String name) {
		try {
			URL url = (URL) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() {
					return findResource(name);
				}
			}, acc);
			if (url != null) {
				return url;
			}
		} catch (PrivilegedActionException e) {
			if (debug) {
				Logger.error("Unable to find resource " + name + " ", e);
			}
		}
		return super.getResource(name);
	}

}
