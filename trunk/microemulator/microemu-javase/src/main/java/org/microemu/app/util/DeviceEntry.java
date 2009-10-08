/**
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

package org.microemu.app.util;

import org.microemu.device.EmulatorContext;

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