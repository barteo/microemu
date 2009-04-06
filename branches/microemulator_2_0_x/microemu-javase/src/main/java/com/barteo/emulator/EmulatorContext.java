/*
 *  MicroEmulator
 *  Copyright (C) 2002-2006 Bartek Teodorczyk <barteo@barteo.net>
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

package com.barteo.emulator;

import java.io.InputStream;

import org.microemu.DisplayComponent;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.Message;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;

/*
 * @deprecated use org.microemu.EmulatorContext
 */
public class EmulatorContext implements org.microemu.EmulatorContext {

	private org.microemu.EmulatorContext context;

	public EmulatorContext(org.microemu.EmulatorContext context) {
		this.context = context;
	}

	public DeviceDisplay getDeviceDisplay() {
		return context.getDeviceDisplay();
	}

	public FontManager getDeviceFontManager() {
		return context.getDeviceFontManager();
	}

	public InputMethod getDeviceInputMethod() {
		return context.getDeviceInputMethod();
	}

	public DisplayComponent getDisplayComponent() {
		return context.getDisplayComponent();
	}

	public InputStream getResourceAsStream(String name) {
		return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
	}

	public boolean platformRequest(final String URL) {
		new Thread(new Runnable() {
			public void run() {
				Message.info("MIDlet requests that the device handle the following URL: " + URL);
			}
		}).start();

		return false;
	}
}
