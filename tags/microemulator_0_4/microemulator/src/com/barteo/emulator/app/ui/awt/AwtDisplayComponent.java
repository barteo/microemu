/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.app.ui.awt;

import java.awt.Component;
import java.awt.Graphics;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.app.ui.DisplayRepaintListener;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.MutableImage;
import com.barteo.emulator.device.applet.AppletDeviceDisplay;
import com.barteo.emulator.device.applet.AppletMutableImage;


public class AwtDisplayComponent implements DisplayComponent
{
	private Component deviceCanvas;
	private AppletMutableImage displayImage = null;


	AwtDisplayComponent(Component deviceCanvas)
	{
		this.deviceCanvas = deviceCanvas;
	}


	public void addDisplayRepaintListener(DisplayRepaintListener l)
	{
	}


	public void removeDisplayRepaintListener(DisplayRepaintListener l)
	{
	}


	public MutableImage getDisplayImage()
	{
		return displayImage;
	}


	public void paint(Graphics g) 
	{
		if (displayImage != null) {
			g.drawImage(displayImage.getImage(), 0, 0, null);
		}
	}

  
	public void repaint() 
	{
		Device device = DeviceFactory.getDevice();
	
		if (device != null) {
			if (displayImage == null) {
				displayImage = new AppletMutableImage(device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
			}
	
			Graphics gc = displayImage.getImage().getGraphics();
			((AppletDeviceDisplay) device.getDeviceDisplay()).paint(gc);
		}
	
		deviceCanvas.repaint();
	}
  
}
