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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Set;

import org.microemu.log.Logger;

/**
 * @author vlads
 *
 */
public class MIDletClassLoader extends URLClassLoader {

	//TODO make this configurable
	
	public static boolean traceClassLoading = false;
	
	public static boolean traceSystemClassLoading = false;
	
	private final static boolean debug = false;
	
	private Set noPreporcessingNames;
	
	/* The context to be used when loading classes and resources */
    private AccessControlContext acc;
    
	public MIDletClassLoader(ClassLoader parent) {
		super(new URL[]{}, parent);
		noPreporcessingNames = new HashSet();
		acc = AccessController.getContext();
	}

	public MIDletClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		noPreporcessingNames = new HashSet();
	}
	
    /**
     * Appends the Class Location URL to the list of URLs to search for
     * classes and resources.
     *
     * @param Class Name
     */
	public void addClassURL(String className) throws MalformedURLException {
		String resource = getClassResourceName(className);
		URL url = getParent().getResource(resource);
		if (url == null) {
			throw new MalformedURLException("Unable to find class " + className + " URL");
		}
		String path = url.toExternalForm();
		if (debug) {
			Logger.debug("addClassURL ", path);
		}
		addURL(new URL(path.substring(0, path.length() - resource.length())));
    }
	
	public static URL getClassURL(ClassLoader parent, String className) throws MalformedURLException {
		String resource = getClassResourceName(className);
		URL url = parent.getResource(resource);
		if (url == null) {
			throw new MalformedURLException("Unable to find class " + className + " URL");
		}
		String path = url.toExternalForm();
		return new URL(path.substring(0, path.length() - resource.length()));
    }
	
	public void addURL(URL url) {
		if (debug) {
			Logger.debug("addURL ", url.toString());
		}
		super.addURL(url);
	}
    
	/**
	 * Loads the class with the specified <a href="#name">binary name</a>.
	 * 
	 * <p> Search order is reverse to standard implemenation</p>
	 * 
	 * This implementation of this method searches for classes in the
	 * following order:
	 *
	 * <p><ol>
	 *
	 *   <li><p> Invoke {@link #findLoadedClass(String)} to check if the class
	 *   has already been loaded.  </p></li>
	 *
	 *   <li><p> Invoke the {@link #findClass(String)} method to find the
	 *   class in this class loader URLs.  </p></li>
	 *
	 *   <li><p> Invoke the {@link #loadClass(String) <tt>loadClass</tt>} method
	 *   on the parent class loader.  If the parent is <tt>null</tt> the class
	 *   loader built-in to the virtual machine is used, instead.  </p></li>
	 *
	 * </ol>
	 *
	 */
	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (debug) {
			Logger.debug("loadClass",  name);
		}
		// First, check if the class has already been loaded
		Class result = findLoadedClass(name);
		if (result == null) {
			try {
				result = findClass(name);
				if (debug && (result == null)) {
					Logger.debug("loadClass not found", name);
				}
			} catch (ClassNotFoundException e) {
				if (traceSystemClassLoading) {
					Logger.info("Load system class", name);
				}
				// This will call our findClass again if Class is not found in parent
				result = super.loadClass(name, false);
				if (result == null) {
					throw new ClassNotFoundException(name);
				}
			}
		}
		if (resolve) {
			resolveClass(result);
		}
		return result;
	}
	
	/**
     * Finds the resource with the given name.  A resource is some data
     * (images, audio, text, etc) that can be accessed by class code in a way
     * that is independent of the location of the code.
     *
     * <p> The name of a resource is a '<tt>/</tt>'-separated path name that
     * identifies the resource.
     *
     * <p> Search order is reverse to standard implemenation</p>
     * 
     * <p> This method will first use {@link #findResource(String)} to find the resource. 
     * That failing, this method will NOT invoke the parent class loader. </p>
     *
     * @param  name
     *         The resource name
     *
     * @return  A <tt>URL</tt> object for reading the resource, or
     *          <tt>null</tt> if the resource could not be found or the invoker
     *          doesn't have adequate  privileges to get the resource.
     *
     */
    
	public URL getResource(final String name) {
		try {
			return (URL) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() {
					return findResource(name);
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			if (debug) {
				Logger.error("Unable to find resource " + name + " ", e);
			}
			return null;
		}
	}

	/**
	 * Allow access to resources
	 */
	public InputStream getResourceAsStream(String name) {
		final URL url = getResource(name);
		if (url == null) {
			return null;
		}

		try {
			return (InputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws IOException {
					return url.openStream();
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			if (debug) {
				Logger.debug("Unable to find resource for class " + name + " ", e);
			}
			return null;
		}

	}
	
	public boolean allowClassLoad(String className) {
		if (className.startsWith("java.")) {
			return false;
		}
		/* No real device support overloading this package */
		if (className.startsWith("javax.microedition.")) {
			return false;
		}
//		Class loadedByParent = getParent().findLoadedClass(className);
//		if (loadedByParent != null) {
//			return false;
//		}
		if (noPreporcessingNames.contains(className)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Special case for classes injected to MIDlet 
	 * @param klass
	 */
	public void disableClassPreporcessing(Class klass) {
		disableClassPreporcessing(klass.getName());
	}
	
	public void disableClassPreporcessing(String className) {
		noPreporcessingNames.add(className);
	}

	public static String getClassResourceName(String className) {
		return className.replace('.', '/').concat(".class");
	}

	protected Class findClass(final String name) throws ClassNotFoundException {
		if (debug) {
			Logger.debug("findClass", name);
		}
		if (!allowClassLoad(name)) {
			throw new ClassNotFoundException(name);
		}
		InputStream is;
		try {
			is = (InputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws ClassNotFoundException {
					return getResourceAsStream(getClassResourceName(name));
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			if (debug) {
				Logger.debug("Unable to find resource for class " + name + " ", e);
			}
			throw new ClassNotFoundException(name);
		}
		
		if (is == null) {
			if (debug) {
				Logger.debug("Unable to find resource for class", name);
			}
			throw new ClassNotFoundException(name);
		}
		byte[] b;
		try {
			if (traceClassLoading) {
				Logger.info("Load MIDlet class", name);
			}
			b = ClassPreporcessor.instrument(is);
		} finally {
			try {
				is.close();
			} catch (IOException ignore) {
			}
		}
		if (debug) {
			Logger.debug("instrumented ", name);
		}
		return defineClass(name, b, 0, b.length);
	}
}
