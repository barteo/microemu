/*
 *  MicroEmulator
 *  Copyright (C) 2002-2006 Bartek Teodorczyk <barteo@barteo.net>
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

package com.barteo.emulator;

import java.io.InputStream;

import org.microemu.DisplayComponent;
import org.microemu.MIDletBridge;
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

}
