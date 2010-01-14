/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;

import org.microemu.EmulatorContext;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.IOUtils;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.app.util.MRUList;
import org.microemu.app.util.MidletURLReference;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.impl.Rectangle;
import org.microemu.log.Logger;
import org.microemu.microedition.ImplementationInitialization;

public class Config {

	private static File meHome;

	/**
	 * emulatorID used for multiple instance of MicroEmulator, now redefine home
	 */
	private static String emulatorID;

	private static XMLElement configXml = new XMLElement();

	private static DeviceEntry defaultDevice;

	private static DeviceEntry resizableDevice;

	private static EmulatorContext emulatorContext;

	private static MRUList urlsMRU = new MRUList(MidletURLReference.class, "midlet");

	private static File initMEHomePath() {
		try {
			File meHome = new File(System.getProperty("user.home") + "/.microemulator/");
			if (emulatorID != null) {
				return new File(meHome, emulatorID);
			} else {
				return meHome;
			}
		} catch (SecurityException e) {
			Logger.error("Cannot access user.home", e);
			return null;
		}
	}

	public static void loadConfig(DeviceEntry defaultDevice, EmulatorContext emulatorContext) {
		Config.defaultDevice = defaultDevice;
		Config.emulatorContext = emulatorContext;

		File configFile = new File(getConfigPath(), "config2.xml");
		try {
			if (configFile.exists()) {
				loadConfigFile("config2.xml");
			} else {
				configFile = new File(getConfigPath(), "config.xml");
				if (configFile.exists()) {
					// migrate from config.xml
					loadConfigFile("config.xml");

					for (Enumeration e = getDeviceEntries().elements(); e.hasMoreElements();) {
						DeviceEntry entry = (DeviceEntry) e.nextElement();
						if (!entry.canRemove()) {
							continue;
						}

						removeDeviceEntry(entry);
						File src = new File(getConfigPath(), entry.getFileName());
						File dst = File.createTempFile("dev", ".jar", getConfigPath());
						IOUtils.copyFile(src, dst);
						entry.setFileName(dst.getName());
						addDeviceEntry(entry);
					}
				} else {
					createDefaultConfigXml();
				}
				saveConfig();
			}
		} catch (IOException ex) {
			Logger.error(ex);
			createDefaultConfigXml();
		} finally {
			// Happens in webstart untrusted environment
			if (configXml == null) {
				createDefaultConfigXml();
			}
		}
		urlsMRU.read(configXml.getChildOrNew("files").getChildOrNew("recent"));
		initSystemProperties();
	}

	private static void loadConfigFile(String configFileName) throws IOException {
		File configFile = new File(getConfigPath(), configFileName);
		InputStream is = null;
		String xml = "";
		try {
			InputStream dis = new BufferedInputStream(is = new FileInputStream(configFile));
			while (dis.available() > 0) {
				byte[] b = new byte[dis.available()];
				dis.read(b);
				xml += new String(b);
			}
			configXml = new XMLElement();
			configXml.parseString(xml);
		} catch (XMLParseException e) {
			Logger.error(e);
			createDefaultConfigXml();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	private static void createDefaultConfigXml() {
		configXml = new XMLElement();
		configXml.setName("config");
	}

	public static void saveConfig() {

		urlsMRU.save(configXml.getChildOrNew("files").getChildOrNew("recent"));

		File configFile = new File(getConfigPath(), "config2.xml");

		getConfigPath().mkdirs();
		FileWriter fw = null;
		try {
			fw = new FileWriter(configFile);
			configXml.write(fw);
			fw.close();
		} catch (IOException ex) {
			Logger.error(ex);
		} finally {
			IOUtils.closeQuietly(fw);
		}
	}

	static Map getExtensions() {
		Map extensions = new HashMap();
		XMLElement extensionsXml = configXml.getChild("extensions");
		if (extensionsXml == null) {
			return extensions;
		}
		for (Enumeration en = extensionsXml.enumerateChildren(); en.hasMoreElements();) {
			XMLElement extension = (XMLElement) en.nextElement();
			if (!extension.getName().equals("extension")) {
				continue;
			}
			String className = (String) extension.getChildString("className", null);
			if (className == null) {
				continue;
			}

			Map parameters = new HashMap();
			parameters.put(ImplementationInitialization.PARAM_EMULATOR_ID, Config.getEmulatorID());

			for (Enumeration een = extension.enumerateChildren(); een.hasMoreElements();) {
				XMLElement propXml = (XMLElement) een.nextElement();
				if (propXml.getName().equals("properties")) {
					for (Enumeration e_prop = propXml.enumerateChildren(); e_prop.hasMoreElements();) {
						XMLElement tmp_prop = (XMLElement) e_prop.nextElement();
						if (tmp_prop.getName().equals("property")) {
							parameters.put(tmp_prop.getStringAttribute("name"), tmp_prop.getStringAttribute("value"));
						}
					}
				}
			}

			extensions.put(className, parameters);
		}
		return extensions;
	}

	private static void initSystemProperties() {
		Map systemProperties = null;

		for (Enumeration e = configXml.enumerateChildren(); e.hasMoreElements();) {
			XMLElement tmp = (XMLElement) e.nextElement();
			if (tmp.getName().equals("system-properties")) {
				// Permits null values.
				systemProperties = new HashMap();
				for (Enumeration e_prop = tmp.enumerateChildren(); e_prop.hasMoreElements();) {
					XMLElement tmp_prop = (XMLElement) e_prop.nextElement();
					if (tmp_prop.getName().equals("system-property")) {
						systemProperties.put(tmp_prop.getStringAttribute("name"), tmp_prop.getStringAttribute("value"));
					}
				}
			}
		}

		// No <system-properties> in config2.xml
		if (systemProperties == null) {
			systemProperties = new Properties();
			systemProperties.put("microedition.configuration", "CLDC-1.0");
			systemProperties.put("microedition.profiles", "MIDP-2.0");
			// Ask avetana to ignore MIDP profiles and load JSR-82
			// implementation dll or so
			systemProperties.put("avetana.forceNativeLibrary", Boolean.TRUE.toString());

			XMLElement propertiesXml = configXml.getChildOrNew("system-properties");

			for (Iterator i = systemProperties.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				XMLElement xmlProperty = propertiesXml.addChild("system-property");
				xmlProperty.setAttribute("value", (String) e.getValue());
				xmlProperty.setAttribute("name", (String) e.getKey());
			}

			saveConfig();
		}

		MIDletSystemProperties.setProperties(systemProperties);
	}

	public static File getConfigPath() {
		if (meHome == null) {
			meHome = initMEHomePath();
		}
		return meHome;
	}

	public static Vector getDeviceEntries() {
		Vector result = new Vector();

		if (defaultDevice == null) {
			defaultDevice = new DeviceEntry("Default device", null, DeviceImpl.DEFAULT_LOCATION, true, false);
		}
		defaultDevice.setDefaultDevice(true);
		result.add(defaultDevice);
		
		if (resizableDevice == null) {
			resizableDevice = new DeviceEntry("Resizable device", null, DeviceImpl.RESIZABLE_LOCATION, false, false);
			addDeviceEntry(resizableDevice);
		}

		XMLElement devicesXml = configXml.getChild("devices");
		if (devicesXml == null) {
			return result;
		}

		for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
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
				String devDescriptor = tmp_device.getChildString("descriptor", null);
				;
				if (devDescriptor == null) {
					result.add(new DeviceEntry(devName, devFile, devDefault, devClass, emulatorContext));
				} else {
					result.add(new DeviceEntry(devName, devFile, devDescriptor, devDefault));
				}
			}
		}

		return result;
	}

	public static void addDeviceEntry(DeviceEntry entry) {
		for (Enumeration en = getDeviceEntries().elements(); en.hasMoreElements();) {
			DeviceEntry test = (DeviceEntry) en.nextElement();
			if (test.getDescriptorLocation().equals(entry.getDescriptorLocation())) {
				return;
			}
		}

		XMLElement devicesXml = configXml.getChildOrNew("devices");

		XMLElement deviceXml = devicesXml.addChild("device");
		if (entry.isDefaultDevice()) {
			deviceXml.setAttribute("default", "true");
		}
		deviceXml.addChild("name", entry.getName());
		deviceXml.addChild("filename", entry.getFileName());
		deviceXml.addChild("descriptor", entry.getDescriptorLocation());

		saveConfig();
	}

	public static void removeDeviceEntry(DeviceEntry entry) {
		XMLElement devicesXml = configXml.getChild("devices");
		if (devicesXml == null) {
			return;
		}

		for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
			XMLElement tmp_device = (XMLElement) e_device.nextElement();
			if (tmp_device.getName().equals("device")) {
				String testDescriptor = tmp_device.getChildString("descriptor", null);
				// this is needed by migration config.xml -> config2.xml
				if (testDescriptor == null) {
					devicesXml.removeChild(tmp_device);

					saveConfig();
					continue;
				}
				if (testDescriptor.equals(entry.getDescriptorLocation())) {
					devicesXml.removeChild(tmp_device);

					saveConfig();
					break;
				}
			}
		}
	}

	public static void changeDeviceEntry(DeviceEntry entry) {
		XMLElement devicesXml = configXml.getChild("devices");
		if (devicesXml == null) {
			return;
		}

		for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
			XMLElement tmp_device = (XMLElement) e_device.nextElement();
			if (tmp_device.getName().equals("device")) {
				String testDescriptor = tmp_device.getChildString("descriptor", null);
				if (testDescriptor.equals(entry.getDescriptorLocation())) {
					if (entry.isDefaultDevice()) {
						tmp_device.setAttribute("default", "true");
					} else {
						tmp_device.removeAttribute("default");
					}

					saveConfig();
					break;
				}
			}
		}
	}

	public static Rectangle getDeviceEntryDisplaySize(DeviceEntry entry) {
		XMLElement devicesXml = configXml.getChild("devices");

		if (devicesXml != null) {
			for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
				XMLElement tmp_device = (XMLElement) e_device.nextElement();
				if (tmp_device.getName().equals("device")) {
					String testDescriptor = tmp_device.getChildString("descriptor", null);
					if (testDescriptor.equals(entry.getDescriptorLocation())) {
						XMLElement rectangleXml = tmp_device.getChild("rectangle");
						if (rectangleXml != null) {
							Rectangle result = new Rectangle();
							result.x = rectangleXml.getChildInteger("x", -1);
							result.y = rectangleXml.getChildInteger("y", -1);
							result.width = rectangleXml.getChildInteger("width", -1);
							result.height = rectangleXml.getChildInteger("height", -1);
	
							return result;
						}
					}
				}
			}
		}

		return null;
	}

	public static void setDeviceEntryDisplaySize(DeviceEntry entry, Rectangle rect) {
		if (entry == null) {
			return;
		}
		XMLElement devicesXml = configXml.getChild("devices");
		if (devicesXml == null) {
			return;
		}

		for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
			XMLElement tmp_device = (XMLElement) e_device.nextElement();
			if (tmp_device.getName().equals("device")) {
				String testDescriptor = tmp_device.getChildString("descriptor", null);
				if (testDescriptor.equals(entry.getDescriptorLocation())) {
					XMLElement mainXml = tmp_device.getChildOrNew("rectangle");
					XMLElement xml = mainXml.getChildOrNew("x");
					xml.setContent(String.valueOf(rect.x));
					xml = mainXml.getChildOrNew("y");
					xml.setContent(String.valueOf(rect.y));
					xml = mainXml.getChildOrNew("width");
					xml.setContent(String.valueOf(rect.width));
					xml = mainXml.getChildOrNew("height");
					xml.setContent(String.valueOf(rect.height));

					saveConfig();
					break;
				}
			}
		}
	}

	public static String getRecordStoreManagerClassName() {
		XMLElement recordStoreManagerXml = configXml.getChild("recordStoreManager");
		if (recordStoreManagerXml == null) {
			return null;
		}

		return recordStoreManagerXml.getStringAttribute("class");
	}

	public static void setRecordStoreManagerClassName(String className) {
		XMLElement recordStoreManagerXml = configXml.getChildOrNew("recordStoreManager");
		recordStoreManagerXml.setAttribute("class", className);

		saveConfig();
	}

	public static boolean isLogConsoleLocationEnabled() {
		XMLElement logConsoleXml = configXml.getChild("logConsole");
		if (logConsoleXml == null) {
			return true;
		}

		return logConsoleXml.getBooleanAttribute("locationEnabled", true);
	}

	public static void setLogConsoleLocationEnabled(boolean state) {
		XMLElement logConsoleXml = configXml.getChildOrNew("logConsole");
		if (state) {
			logConsoleXml.setAttribute("locationEnabled", "true");
		} else {
			logConsoleXml.setAttribute("locationEnabled", "false");
		}

		saveConfig();
	}

	public static boolean isWindowOnStart(String name) {
		XMLElement windowsXml = configXml.getChild("windows");
		if (windowsXml == null) {
			return false;
		}

		XMLElement mainXml = windowsXml.getChild(name);
		if (mainXml == null) {
			return false;
		}

		String attr = mainXml.getStringAttribute("onstart", "false");
		if (attr.trim().toLowerCase().equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	public static Rectangle getWindow(String name, Rectangle defaultWindow) {
		XMLElement windowsXml = configXml.getChild("windows");
		if (windowsXml == null) {
			return defaultWindow;
		}

		XMLElement mainXml = windowsXml.getChild(name);
		if (mainXml == null) {
			return defaultWindow;
		}

		Rectangle window = new Rectangle();
		window.x = mainXml.getChildInteger("x", defaultWindow.x);
		window.y = mainXml.getChildInteger("y", defaultWindow.y);
		window.width = mainXml.getChildInteger("width", defaultWindow.width);
		window.height = mainXml.getChildInteger("height", defaultWindow.height);

		return window;
	}

	public static void setWindow(String name, Rectangle window, boolean onStart) {
		XMLElement windowsXml = configXml.getChildOrNew("windows");
		XMLElement mainXml = windowsXml.getChildOrNew(name);
		if (onStart) {
			mainXml.setAttribute("onstart", "true");
		} else {
			mainXml.removeAttribute("onstart");
		}
		XMLElement xml = mainXml.getChildOrNew("x");
		xml.setContent(String.valueOf(window.x));
		xml = mainXml.getChildOrNew("y");
		xml.setContent(String.valueOf(window.y));
		xml = mainXml.getChildOrNew("width");
		xml.setContent(String.valueOf(window.width));
		xml = mainXml.getChildOrNew("height");
		xml.setContent(String.valueOf(window.height));

		saveConfig();
	}

	public static String getRecentDirectory(String key) {
		String defaultResult = ".";

		XMLElement filesXml = configXml.getChild("files");
		if (filesXml == null) {
			return defaultResult;
		}

		return filesXml.getChildString(key, defaultResult);
	}

	public static void setRecentDirectory(String key, String recentJadDirectory) {
		XMLElement filesXml = configXml.getChildOrNew("files");
		XMLElement recentJadDirectoryXml = filesXml.getChildOrNew(key);
		recentJadDirectoryXml.setContent(recentJadDirectory);

		saveConfig();
	}

	public static MRUList getUrlsMRU() {
		return urlsMRU;
	}

	public static String getEmulatorID() {
		return emulatorID;
	}

	public static void setEmulatorID(String emulatorID) {
		Config.emulatorID = emulatorID;
	}

}
