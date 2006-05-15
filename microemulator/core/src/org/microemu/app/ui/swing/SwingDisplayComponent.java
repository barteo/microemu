/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.swing;

import java.awt.Component;
import java.awt.Graphics;

import org.microemu.DisplayComponent;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.MutableImage;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEMutableImage;


public class SwingDisplayComponent implements DisplayComponent 
{
	private Component deviceCanvas;
	private J2SEMutableImage displayImage = null;
	private DisplayRepaintListener displayRepaintListener;


	SwingDisplayComponent(Component deviceCanvas)
	{
		this.deviceCanvas = deviceCanvas;
	}
	
	
	public void init()
	{
	    displayImage = null;
	}


	public void addDisplayRepaintListener(DisplayRepaintListener l) 
	{
		displayRepaintListener = l;
	}


	public void removeDisplayRepaintListener(DisplayRepaintListener l) 
	{
		if (displayRepaintListener == l) {
			displayRepaintListener = null;
		}
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
				displayImage = new J2SEMutableImage(
						device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
			}
					
			Graphics gc = displayImage.getImage().getGraphics();
			((J2SEDeviceDisplay) device.getDeviceDisplay()).paint(gc);

			fireDisplayRepaint(displayImage);
		}	

		deviceCanvas.repaint();
	}


	private void fireDisplayRepaint(MutableImage image)
	{
		if (displayRepaintListener != null) {
			displayRepaintListener.repaintInvoked(image);
		}
	}

}
