/**
 *  MicroEmulator
 *  Copyright (C) 2002-2005 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu.device;

import java.util.Map;
import java.util.Vector;

import javax.microedition.lcdui.Image;

public interface Device {

	public abstract void init();

	public abstract void destroy();

	public abstract String getName();

	public abstract InputMethod getInputMethod();

	public abstract FontManager getFontManager();

	public abstract DeviceDisplay getDeviceDisplay();

	public abstract Image getNormalImage();

	public abstract Image getOverImage();

	public abstract Image getPressedImage();

	public abstract Vector getSoftButtons();

	public abstract Vector getButtons();

	public abstract boolean hasPointerEvents();

	public abstract boolean hasPointerMotionEvents();

	public abstract boolean hasRepeatEvents();

	public abstract boolean vibrate(int duration);

	public abstract Map getSystemProperties();

}