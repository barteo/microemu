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
package org.microemu.app.util;

import java.io.InputStream;

import org.microemu.Injected;
import org.microemu.log.Logger;

/**
 * @author vlads
 *
 * Use MIDletResourceLoader to load resources.
 * To solve resource resource loading paterns commonly used in MIDlet and not aceptable in Java SE application
 * when System class is called to load resource 
 * 
 * j2me example:
 * 
 *  String.class.getResourceAsStream(resourceName)
 *
 */
public class MIDletResourceLoader {
	
	//TODO make this configurable
	
	public static boolean traceResourceLoading = false;
	
	/**
	 * @deprecated find better solution to share variable
	 */
	public static ClassLoader classLoader;
	
	private static boolean java13 = false;
	
	private static final String FQCN = Injected.class.getName();
	
	public static InputStream getResourceAsStream(Class origClass, String resourceName)  {
		if (traceResourceLoading) {
			Logger.debug("Loading MIDlet resource", resourceName);
		}
		if (resourceName.startsWith("/")) {
			resourceName = resourceName.substring(1);
		}
		if (classLoader != origClass.getClassLoader()) {
			// showWarning
			if (!java13) {
				try {
					StackTraceElement[] ste = new Throwable().getStackTrace();
					for (int i = 0; i < ste.length - 1; i++) {
						if (FQCN.equals(ste[i].getClassName())) {
							StackTraceElement callLocation = ste[i + 1];
							Logger.warn("attempt to load resource [" + resourceName + "] using System ClasslLoader from " + callLocation.toString());
						}
					}
				} catch (Throwable e) {
					java13 = true;
				}
			}
		}
			
		InputStream is = classLoader.getResourceAsStream(resourceName);
		if (is == null) {
			Logger.debug("Resource not found ", resourceName);
		}
		return is;
	}
}
