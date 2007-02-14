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

import java.util.Hashtable;
import java.util.Locale;

/**
 * @author vlads
 * 
 * This class is called by MIDlet to access System Property.
 * Call injection is made by MIDlet ClassLoaded
 * 
 */
public class MIDletSystemProperties {

	private static final Hashtable props = new Hashtable();
	
	static {
		init();
	}
	
	private static void init() {
		props.put("microedition.configuration", "CLDC-1.1");
		props.put("microedition.configuration", "MIDP-2.0");
		props.put("microedition.platform", "MicroEmulator");
		props.put("microedition.locale",  Locale.getDefault().getLanguage());
	}
	
	/**
	 * Gets the system property indicated by the specified key.
	 * The only function called by MIDlet
	 * @param key   the name of the system property
	 * @return
	 */
	public static String getProperty(String key) {
		String v = (String)props.get(key);
		if (v != null) {
			return v;
		}
		try {
			return System.getProperty(key);
		} catch (SecurityException e) {
			return null;
		}
	}
	
	public static String setProperty(String key, String value) {
		return (String) props.put(key , value);
	}
	
	public static String clearProperty(String key) {
		return (String) props.remove(key);
	}
}
