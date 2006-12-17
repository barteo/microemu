/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package org.microemu.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.microemu.EmulatorContext;
import org.microemu.app.util.DeviceEntry;


import nanoxml.XMLElement;
import nanoxml.XMLParseException;


public class Config {

	private static File configPath = new File(System.getProperty("user.home") + "/.microemulator/");

	private static Vector devices = new Vector();

	private static int windowX;

	private static int windowY;
  
	private static String recentJadDirectory = ".";

	private static Properties systemProperties;

	public static void loadConfig(String configFileName, DeviceEntry defaultDevice, EmulatorContext emulatorContext) {
		File configFile = new File(configPath, configFileName);
		
		if (defaultDevice == null) {
		    defaultDevice = 
	            new DeviceEntry("Default device", null, "org/microemu/device/device.xml", true, false);
		}
    	defaultDevice.setDefaultDevice(true);
    	devices.add(defaultDevice);

		String xml = "";
		try {
			InputStream dis = new BufferedInputStream(new FileInputStream(configFile));
			while (dis.available() > 0) {
				byte[] b = new byte[dis.available()];
				dis.read(b);
				xml += new String(b);
			}
			parseConfigXML(xml, defaultDevice, emulatorContext);
		} catch (FileNotFoundException ex) {
			loadDefaultConfig();
		} catch (XMLParseException e) {
			System.out.println(e);
			loadDefaultConfig();
		} catch (IOException ex) {
			System.out.println(ex);
			loadDefaultConfig();
		}
		initSystemProperties();
	}

	private static void parseConfigXML(String xml, DeviceEntry defaultDevice, EmulatorContext emulatorContext) throws XMLParseException {

		XMLElement configRoot = new XMLElement();

		configRoot.parseString(xml);
	
		for (Enumeration e = configRoot.enumerateChildren(); e.hasMoreElements();) {
			XMLElement tmp = (XMLElement) e.nextElement();
			if (tmp.getName().equals("devices")) {
				for (Enumeration e_device = tmp.enumerateChildren(); e_device.hasMoreElements();) {
					XMLElement tmp_device = (XMLElement) e_device.nextElement();
					if (tmp_device.getName().equals("device")) {
						boolean devDefault = false;
						if (tmp_device.getStringAttribute("default") != null
								&& tmp_device.getStringAttribute("default").equals("true")) {
							devDefault = true;
							defaultDevice.setDefaultDevice(false);
						}
						String devName = tmp_device.getChildString("name", null);
						String devFile = tmp_device.getChildString("filename", null);
						String devClass = tmp_device.getChildString("class", null);
						String devDescriptor = tmp_device.getChildString("descriptor", null);;
			            if (devDescriptor == null) {
			            	devices.add(new DeviceEntry(devName, devFile, devDefault, devClass, emulatorContext));
			            } else {
			            	devices.add(new DeviceEntry(devName, devFile, devDescriptor, devDefault));
			            }
					}
				}
			} else if (tmp.getName().equals("files")) {
				recentJadDirectory = tmp.getChildString("recentJadDirectory", ".");
			} else if (tmp.getName().equals("windows")) {
				XMLElement main_window = tmp.getChild("main");
				if (main_window != null) {
					windowX = main_window.getChildInteger("x", 0);
					windowY = main_window.getChildInteger("y", 0);
				}
			} else if (tmp.getName().equals("system-properties")) {
				systemProperties = new Properties();
				for (Enumeration e_prop = tmp.enumerateChildren(); e_prop.hasMoreElements(); ) {
		            XMLElement tmp_prop = (XMLElement) e_prop.nextElement();
		            if (tmp_prop.getName().equals("system-property")) {
		            	systemProperties.put(tmp_prop.getStringAttribute("name"), tmp_prop.getStringAttribute("value"));
		            }
		        }
			}
		}
	}				  
  
	private static void loadDefaultConfig() {
	}
  
  
	public static void saveConfig(String configFileName) {
		File configFile = new File(configPath, configFileName);

		XMLElement xmlRoot = new XMLElement();
		xmlRoot.setName("config");

		XMLElement xmlFiles = xmlRoot.addChild("files");
		xmlFiles.addChild("recentJadDirectory", recentJadDirectory);

		XMLElement xmlWindows = xmlRoot.addChild("windows");
		XMLElement xmlMainWindow = xmlWindows.addChild("main");
		xmlMainWindow.addChild("x", String.valueOf(windowX));
		xmlMainWindow.addChild("y", String.valueOf(windowY));

		XMLElement xmlSystemProperties = xmlRoot.addChild("system-properties");
		if (systemProperties != null) {
			for (Iterator i = systemProperties.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				XMLElement xmlProperty = xmlSystemProperties.addChild("system-property");
				xmlProperty.setAttribute("value", (String) e.getValue());
				xmlProperty.setAttribute("name", (String) e.getKey());
			}
		}
		
		XMLElement xmlDevices = xmlRoot.addChild("devices");

		for (Enumeration e = devices.elements(); e.hasMoreElements();) {
			DeviceEntry entry = (DeviceEntry) e.nextElement();
			if (!entry.canRemove()) {
				continue;
			}

			XMLElement xmlDevice = new XMLElement(false, false);
			xmlDevice.setName("device");
			xmlDevices.addChild(xmlDevice);
			if (entry.isDefaultDevice()) {
				xmlDevice.setAttribute("default", "true");
			}
			xmlDevice.addChild("name", entry.getName());
			xmlDevice.addChild("filename", entry.getFileName());
			xmlDevice.addChild("descriptor", entry.getDescriptorLocation());
		}

		configPath.mkdirs();
		try {
			FileWriter fw = new FileWriter(configFile);
			xmlRoot.write(fw);
			fw.close();
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}
	
    private static void initSystemProperties() {
    	// No <system-properties> in config.xml
    	if (systemProperties == null) {
    		systemProperties = new Properties();
    		systemProperties.put("microedition.configuration", "CLDC-1.0");
    		systemProperties.put("microedition.profiles", "MIDP-2.0");
    		// Ask avetana to ignore MIDP profiles and load JSR-82 implementation dll or so
    		systemProperties.put("avetana.forceNativeLibrary", Boolean.TRUE.toString());
    	}
        try {
			for (Iterator i = systemProperties.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry e = (Map.Entry)i.next();
				System.setProperty((String)e.getKey(), (String)e.getValue());
			}
        } catch (SecurityException e) {
			System.out.println("Cannot set SystemProperties: " + e);
		}	
	}  
  
  	public static File getConfigPath() {
		return configPath;
	}

	public static Vector getDevices() {
		return devices;
	}

	public static int getWindowX() {
		return windowX;
	}

	public static void setWindowX(int windowX) {
		Config.windowX = windowX;
	}

	public static int getWindowY() {
		return windowY;
	}

	public static void setWindowY(int windowY) {
		Config.windowY = windowY;
	}

	public static String getRecentJadDirectory() {
		return recentJadDirectory;
	}

	public static void setRecentJadDirectory(String recentJadDirectory) {
		Config.recentJadDirectory = recentJadDirectory;
	}

}
