package org.microemu;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class PreporcessorClassLoader extends URLClassLoader {

	public static boolean traceClassLoading = true;
	
	private final static boolean debug = true;
	
	private Set notLoadableNames;
	
	public PreporcessorClassLoader(ClassLoader parent) {
		super(new URL[]{}, parent);
		notLoadableNames = new HashSet();
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
			System.out.println("Add URL " + path);
		}
		addURL(new URL(path.substring(0, path.length() - resource.length())));
    }
	
	public void addURL(URL url) {
		super.addURL(url);
	}
    
	/**
	 * Loads the class with the specified <a href="#name">binary name</a>.
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
			System.out.println("loadClass " + name);
		}
		// First, check if the class has already been loaded
		Class result = findLoadedClass(name);
		if (result == null) {
			if (allowClassLoad(name)) {
				result = findClass(name);
				if (debug && (result == null)) {
					System.out.println("loadClass not found " + name);
				}
			}
			if (result == null) {
				result = super.loadClass(name, false);
			}
		}
		if (resolve) {
			resolveClass(result);
		}
		return result;
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

	public String getClassResourceName(String className) {
		return className.replace('.', '/') + ".class";
	}

	protected Class findClass(final String name) {
		if (debug) {
			System.out.println("findClass " + name);
		}
		final InputStream is = getResourceAsStream(getClassResourceName(name));
		if (is == null) {
			if (debug) {
				System.out.println("Unable to findClass " + name);
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
			System.out.println("instrumented " + name);
		}
		return defineClass(name, b, 0, b.length);
	}
}
