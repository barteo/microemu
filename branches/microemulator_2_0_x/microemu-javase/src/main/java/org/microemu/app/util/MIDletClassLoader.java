/*
 *  MicroEmulator
 *  Copyright (C) 2001-2006 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package org.microemu.app.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @deprecated use MIDletClassLoader 
 */
public class MIDletClassLoader /*extends SystemClassLoader*/ {

//	protected Hashtable entries;
//	
//	private ResURLStreamHandler resUrlStreamHandler;
//	
//	public static final boolean debug = false;
//	
//	public MIDletClassLoader(ClassLoader parent) {
//		super(parent);
//		entries = new Hashtable();
//		resUrlStreamHandler = new ResURLStreamHandler(entries);
//	}
//
//	
//	public void addURL(URL midletSource) throws IOException {
//		 String path = midletSource.toExternalForm();
//		if (path.endsWith(".jar")) {
//			addJarURL(midletSource);
//		} else if (path.startsWith("file:")) {
//			addPathURL(midletSource);
//		} else {
//			throw new IOException("URL Type not supported: " + midletSource);
//		}
//	}
//	
//	private void addPathURL(URL url) throws IOException {
//		String path = url.toExternalForm();
//		path = path.substring("file:".length(), path.length());
//		File classesDir = new File(path);
//		if ((!classesDir.exists()) || (!classesDir.isDirectory())) {
//			throw new IOException("URL Type not supported: " + url);
//		}
//		int baseLen = path.length();
//		for (Enumeration en = new DirectoryEnumeration(classesDir); en.hasMoreElements();) {
//			File file = (File) en.nextElement();
//			if (!file.isDirectory()) {
//				String name = file.getAbsolutePath().substring(baseLen);
//				if (!allowEntryName(name)) {
//					continue;
//				}
//				InputStream is = new FileInputStream(file);
//				byte[] tmp = new byte[(int)file.length()];
//				is.read(tmp);
//				if (debug) {
//					System.out.println("add entry: " + name);
//				}
//				entries.put(name, tmp);
//			}
//		}
//	}
//	
//	private class DirectoryEnumeration implements Enumeration {
//
//		File[] files;
//		
//		int processing;
//
//		Enumeration child = null;
//		
//		DirectoryEnumeration(File dir) {
//			files = dir.listFiles();
//			if (files == null) {
//				throw new Error(dir.getAbsolutePath() + " path does not denote a directory");
//			}
//			processing = 0;
//		}
//
//		public boolean hasMoreElements() {
//			return ((child != null) && (child.hasMoreElements())) || (processing < files.length);
//		}
//
//		public Object nextElement() {
//			if (child != null) {
//				try {
//					return child.nextElement();
//				} catch (NoSuchElementException e) {
//					child = null;
//				}
//			}
//			if (processing >= files.length) {
//				throw new NoSuchElementException();
//			}
//			File next = files[processing++];
//			if (next.isDirectory()) {
//				child = new DirectoryEnumeration(next);
//			}
//			return next;
//		}
//
//	}
//	
//	private void addJarURL(URL midletSource) throws IOException {
//		byte[] cache = new byte[1024];
//		JarInputStream jis = null;
//		try {
//			URLConnection conn = midletSource.openConnection();
//			jis = new JarInputStream(conn.getInputStream());
//			while (true) {
//				JarEntry entry = jis.getNextJarEntry();
//				if (entry != null) {
//					if (!entry.isDirectory()) {
//						if (!allowEntryName(entry.getName())) {
//							continue;
//						}
//						int offset = 0;
//						int i = 0;
//						while ((i = jis.read(cache, offset, cache.length - offset)) != -1) {
//							offset += i;
//							if (offset >= cache.length) {
//								byte newcache[] = new byte[cache.length + 1024];
//								System.arraycopy(cache, 0, newcache, 0, cache.length);
//								cache = newcache;
//							}
//						}
//						byte[] tmp = new byte[offset];
//						System.arraycopy(cache, 0, tmp, 0, offset);
//						if (debug) {
//							System.out.println("add entry: " + entry.getName());
//						}
//						entries.put(entry.getName(), tmp);
//					}
//				} else {
//					break;
//				}
//			}
//		} finally {
//			if (jis != null) {
//				try {
//					jis.close();
//				} catch (IOException ignore) {
//				}
//			}
//		}
//	}
//
//    /**
//     * Loads the class with the specified <a href="#name">binary name</a>.
//     * This implementation of this method searches for classes in the
//     * following order:
//     *
//     * <p><ol>
//     *
//     *   <li><p> Invoke {@link #findLoadedClass(String)} to check if the class
//     *   has already been loaded.  </p></li>
//     *
//     *   <li><p> Invoke the {@link #findClass(String)} method to find the
//     *   class in this class loader.  </p></li>
//     *
//     *   <li><p> Invoke the {@link #loadClass(String) <tt>loadClass</tt>} method
//     *   on the parent class loader.  If the parent is <tt>null</tt> the class
//     *   loader built-in to the virtual machine is used, instead.  </p></li>
//     *
//     * </ol>
//     *
//     */
//	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
//		// First, check if the class has already been loaded
//		Class result = findLoadedClass(name);
//		if (result == null) {
//			if (hasClassData(name)) {
//				result = findClass(name);
//			} else {
//				result = super.loadClass(name, false);
//			}
//		}
//		if (resolve) {
//		    resolveClass(result);
//		}
//		return result;
//	}
//	
//	public Class findClass(String name) throws ClassNotFoundException {
//		Class result = findLoadedClass(name);
//		if (result == null) {
//			byte[] b = loadClassData(name);
//			result = defineClass(name, b, 0, b.length);
//		}
//		return result;
//	}
//
//	public InputStream getResourceAsStream(String name) {
//		String newname;
//		if (name.startsWith("/")) {
//			newname = name.substring(1);
//		} else {
//			newname = name;
//		}
//		byte[] tmp = (byte[]) entries.get(newname);
//		if (tmp != null) {
//			InputStream is = new ByteArrayInputStream(tmp);
//			return is;
//		}
//
//		return getClass().getResourceAsStream(name);
//	}
//
//	private boolean allowEntryName(String name) {
//		return !((name.startsWith("javax/") || name.startsWith("java/")));
//	}
//	
//	private boolean allowClass(String name) {
//		return !((name.startsWith("javax.") || name.startsWith("java.")));
//	}
//	
//	private boolean hasClassData(String name) {
//		if (!allowClass(name)) {
//			return false;
//		}
//		name = name.replace('.', '/') + ".class";
//		return (entries.get(name) != null);
//	}
//
//	protected byte[] loadClassData(String name) throws ClassNotFoundException {
//		name = name.replace('.', '/') + ".class";
//		byte[] result = (byte[]) entries.get(name);
//		if (result == null) {
//			throw new ClassNotFoundException(name);
//		}
//
//		return result;
//	}
//
//	protected URL findResource(String name) {
//		if (entries.containsKey(name)) {
//			try {
//				return new URL(null, "res:" + name, resUrlStreamHandler);
//			} catch (MalformedURLException ex) {
//				ex.printStackTrace();
//				return null;
//			}
//		}
//		return null;
//	}

}
