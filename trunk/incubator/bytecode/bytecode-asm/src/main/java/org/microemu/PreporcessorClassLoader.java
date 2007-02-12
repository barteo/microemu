package org.microemu;

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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class PreporcessorClassLoader extends URLClassLoader {

	public static boolean traceClassLoading = true;
	
	public static boolean traceSystemClassLoading = true;
	
	private final static boolean debug = false;
	
	private Set notLoadableNames;
	
	/* The context to be used when loading classes and resources */
    private AccessControlContext acc;
    
	public PreporcessorClassLoader(ClassLoader parent) {
		super(new URL[]{}, parent);
		notLoadableNames = new HashSet();
		acc = AccessController.getContext();
	}

	public PreporcessorClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		notLoadableNames = new HashSet();
	}
	
	private void debug(String message) {
		if (debug) {
			System.out.println("ppcl " + this.hashCode() + " " + message);
		}
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
			debug("addClassURL " + path);
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
			debug("addURL " + url);
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
			debug("loadClass " + name);
		}
		// First, check if the class has already been loaded
		Class result = findLoadedClass(name);
		if (result == null) {
			if (allowClassLoad(name)) {
				result = findClass(name);
				if (debug && (result == null)) {
					debug("loadClass not found " + name);
				}
			}
			if (result == null) {
				if (traceSystemClassLoading) {
					System.out.println("Load system class " + name);
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
				debug("Unable to find resource " + name + " " + e.toString());
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
				debug("Unable to find resource for class " + name + " " + e.toString());
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
		if (notLoadableNames.contains(className)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Special case for classes injected to MIDlet 
	 * @param klass
	 */
	public void disableClassLoad(Class klass) {
		disableClassLoad(klass.getName());
	}
	
	public void disableClassLoad(String className) {
		notLoadableNames.add(className);
	}

	public static String getClassResourceName(String className) {
		return className.replace('.', '/').concat(".class");
	}

	protected Class findClass(final String name) {
		if (debug) {
			debug("findClass " + name);
		}
		InputStream is;
		//is = getResourceAsStream(getClassResourceName(name));
		try {
			is = (InputStream) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws ClassNotFoundException {
					return getResourceAsStream(getClassResourceName(name));
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			if (debug) {
				debug("Unable to find resource for class " + name + " " + e.toString());
			}
			return null;
		}
		
		if (is == null) {
			if (debug) {
				debug("Unable to find resource for class " + name);
			}
			return null;
		}
		byte[] b;
		try {
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(false);
			ClassVisitor cv = new ChangeCallsClassVisitor(cw, traceClassLoading);
			cr.accept(cv, false);
			b = cw.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException ignore) {
			}
		}
		if (debug) {
			debug("instrumented " + name);
		}
		return defineClass(name, b, 0, b.length);
	}
}
