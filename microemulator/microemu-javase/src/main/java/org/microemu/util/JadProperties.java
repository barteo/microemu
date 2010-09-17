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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class JadProperties extends Manifest {

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
		String name, icon, className;
		int pos;

		if (midletEntries == null) {
			midletEntries = new Vector();

			Attributes attributes = super.getMainAttributes();
			for (Iterator it = attributes.keySet().iterator(); it.hasNext();) {
				Attributes.Name key = (Attributes.Name) it.next();
				if (key.toString().startsWith(MIDLET_PREFIX)) {
					try {
						Integer.parseInt(key.toString().substring(MIDLET_PREFIX.length()));
						String test = getProperty(key.toString());
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
		Attributes attributes = super.getMainAttributes();
		String result = null;
		try {
			result = attributes.getValue(key);
		} catch (IllegalArgumentException e) {
		}
		if (result != null) {
			return result.trim();
		} else {
			return defaultValue;
		}
	}

	public String getProperty(String key) {
		return getProperty(key, null);
	}
	
	/* (non-Javadoc)
	 * @see java.util.jar.Manifest#read(java.io.InputStream)
	 * overwritten since the manifest parser wont accept jads with spaces 
	 * and newlines which is causing problems when passing in jads via
	 * commandline
	 */
	public void read(InputStream is) throws IOException {
	    // TODO Auto-generated method stub
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    byte[] block = new byte[1024];
	    int bytesRead = 0;
	    while (bytesRead >= 0) {
	        bytesRead = is.read(block);
	        if (bytesRead == -1) {
	            break;
	        }
	        bos.write(block, 0, bytesRead);
	    }
	    bos.close();
	    byte[] jadBuffer = bos.toByteArray();
	    is.close();
	    
	    ByteArrayInputStream bin = new ByteArrayInputStream(jadBuffer);
	    ByteArrayInputStream bin2 = new ByteArrayInputStream(jadBuffer);
	    try {
	        super.read(bin);
	    } catch (IOException e) {
	        // didnt like the format, try our own jad parser
	        readJad(bin2);
	    } finally {
	        bin.close();
	        bin2.close();
	    }
	}

    /**
     * @param bin2
     * @throws IOException 
     */
    private void readJad(ByteArrayInputStream bin2) throws IOException {
        BufferedReader din = null;
        try {
            din = new BufferedReader(new InputStreamReader(bin2));
            String line = "";
            while ((line = din.readLine()) != null) {
                int pos = line.indexOf(':');
                final String key;
                final String value;
                if (pos > 0) {
                    key = line.substring(0, pos).trim();
                    value = line.substring(pos + 1).trim();
                    Attributes.Name name = new Attributes.Name(key);
                    this.getMainAttributes().put(name, value);
                }
            }
        } catch (IOException e) {
            
        } finally {
            din.close();
        }
    }

}
