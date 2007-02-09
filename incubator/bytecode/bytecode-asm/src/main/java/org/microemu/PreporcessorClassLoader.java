package org.microemu;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class PreporcessorClassLoader extends ClassLoader {

	private Set notLoadableNames;
	
	public PreporcessorClassLoader(ClassLoader parent) {
		super(parent);
		notLoadableNames = new HashSet();
	}

	protected synchronized Class loadClassStd(String name, boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
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
		// First, check if the class has already been loaded
		Class result = findLoadedClass(name);
		if (result == null) {
			if (allowClassLoad(name)) {
				result = findClass(name);
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
//		Class loadedByParent = getParent().findLoadedClass(className);
//		if (loadedByParent != null) {
//			return false;
//		}
		if (notLoadableNames.contains(className)) {
			return false;
		}
		return true;
	}
	
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
		final InputStream is = getResourceAsStream(getClassResourceName(name));
		if (is == null) {
			return null;
		}
		byte[] b;
		try {
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(false);
			ClassVisitor cv = new ChangeCallsClassVisitor(cw);
			cr.accept(cv, false);
			b = cw.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			try {
				is.close();
			} catch (IOException ignore) {
			}
		}
		return defineClass(name, b, 0, b.length);
	}
}
