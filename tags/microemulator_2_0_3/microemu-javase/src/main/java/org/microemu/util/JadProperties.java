/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class JadProperties extends Properties {

	private static final long serialVersionUID = 1L;

	static String MIDLET_PREFIX = "MIDlet-";

	Vector midletEntries = null;

	String correctedJarURL = null;

	public void clear() {
		super.clear();

		midletEntries = null;
		correctedJarURL = null;
	}

	public String getSuiteName() {
		return getProperty("MIDlet-Name");
	}

	public String getVersion() {
		return getProperty("MIDlet-Version");
	}

	public String getVendor() {
		return getProperty("MIDlet-Vendor");
	}

	public String getProfile() {
		return getProperty("MicroEdition-Profile");
	}

	public String getConfiguration() {
		return getProperty("MicroEdition-Configuration");
	}

	public String getJarURL() {
		if (correctedJarURL != null) {
			return correctedJarURL;
		} else {
			return getProperty("MIDlet-Jar-URL");
		}
	}

	public void setCorrectedJarURL(String correctedJarURL) {
		this.correctedJarURL = correctedJarURL;
	}

	public int getJarSize() {
		return Integer.parseInt(getProperty("MIDlet-Jar-Size"));
	}

	public Vector getMidletEntries() {
		String name, icon, className, test;
		int pos;

		if (midletEntries == null) {
			midletEntries = new Vector();

			for (Enumeration e = propertyNames(); e.hasMoreElements();) {
				test = (String) e.nextElement();
				if (test.startsWith(MIDLET_PREFIX)) {
					try {
						Integer.parseInt(test.substring(MIDLET_PREFIX.length()));
						test = getProperty(test);
						pos = test.indexOf(',');
						name = test.substring(0, pos).trim();
						icon = test.substring(pos + 1, test.indexOf(',', pos + 1)).trim();
						className = test.substring(test.indexOf(',', pos + 1) + 1).trim();
						midletEntries.addElement(new JadMidletEntry(name, icon, className));
					} catch (NumberFormatException ex) {
					}
				}
			}
		}

		return midletEntries;
	}

	public String getProperty(String key, String defaultValue) {
		String result = super.getProperty(key, defaultValue);
		if (result != null) {
			return result.trim();
		} else {
			return null;
		}
	}

	public String getProperty(String key) {
		String result = super.getProperty(key);
		if (result != null) {
			return result.trim();
		} else {
			return null;
		}
	}

}
