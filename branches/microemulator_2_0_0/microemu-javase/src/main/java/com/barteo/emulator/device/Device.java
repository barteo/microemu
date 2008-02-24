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

package com.barteo.emulator.device;

import org.microemu.device.ui.UIFactory;

import com.barteo.emulator.EmulatorContext;

/*
 * @deprecated use org.microemu.device.Device
 */
public class Device extends org.microemu.device.impl.DeviceImpl {

	/**
	 * @deprecated
	 */
	public void init(EmulatorContext context) {
		super.init(context);
	}

	/**
	 * @deprecated
	 */
	public void init(EmulatorContext context, String config) {
		super.init(context, config);
	}

	public UIFactory getUIFactory() {
		return null;
	}

}
