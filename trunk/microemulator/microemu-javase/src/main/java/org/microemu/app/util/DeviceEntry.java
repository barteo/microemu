/**
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

package org.microemu.app.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.microemu.EmulatorContext;
import org.microemu.app.Common;
import org.microemu.app.Config;

import com.barteo.emulator.device.Device;
import org.microemu.log.Logger;

public class DeviceEntry {
	
	private String name;

	private String fileName;

	private String descriptorLocation;

	private boolean defaultDevice;

	private boolean canRemove;

	/**
	 * @deprecated
	 */
	private String className;

	/**
	 * @deprecated
	 */
	private EmulatorContext emulatorContext;

	public DeviceEntry(String name, String fileName, String descriptorLocation, boolean defaultDevice) {
		this(name, fileName, descriptorLocation, defaultDevice, true);
	}

	public DeviceEntry(String name, String fileName, String descriptorLocation, boolean defaultDevice, boolean canRemove) {
		this.name = name;
		this.fileName = fileName;
		this.descriptorLocation = descriptorLocation;
		this.defaultDevice = defaultDevice;
		this.canRemove = canRemove;
	}

	/**
	 * @deprecated use new DeviceEntry(String name, String fileName, String descriptorLocation, boolean defaultDevice);
	 */
	public DeviceEntry(String name, String fileName, boolean defaultDevice, String className,
			EmulatorContext emulatorContext) {
		this(name, fileName, null, defaultDevice, true);

		this.className = className;
		this.emulatorContext = emulatorContext;
	}

	public boolean canRemove() {
		return canRemove;
	}

	public String getDescriptorLocation() {
		if (descriptorLocation == null) {
			URL[] urls = new URL[1];
			try {
				urls[0] = new File(Config.getConfigPath(), fileName).toURL();
				ClassLoader classLoader = Common.createExtensionsClassLoader(urls);
				Class deviceClass = Class.forName(className, true, classLoader);
				Device device = (Device) deviceClass.newInstance();

				com.barteo.emulator.EmulatorContext oldContext = new com.barteo.emulator.EmulatorContext(emulatorContext);

				device.init(oldContext);
				descriptorLocation = device.getDescriptorLocation();
			} catch (MalformedURLException ex) {
				Logger.error(ex);
			} catch (ClassNotFoundException ex) {
				Logger.error(ex);
			} catch (InstantiationException ex) {
				Logger.error(ex);
			} catch (IllegalAccessException ex) {
				Logger.error(ex);
			}

		}

		return descriptorLocation;
	}

	public String getFileName() {
		return fileName;
	}

	/**
	 * @deprecated
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public boolean isDefaultDevice() {
		return defaultDevice;
	}

	public void setDefaultDevice(boolean b) {
		defaultDevice = b;
	}

	public boolean equals(DeviceEntry test) {
		if (test == null) {
			return false;
		}
		if (test.getDescriptorLocation().equals(getDescriptorLocation())) {
			return true;
		}

		return false;
	}

	public String toString() {
		if (defaultDevice) {
			return name + " (default)";
		} else {
			return name;
		}
	}

}