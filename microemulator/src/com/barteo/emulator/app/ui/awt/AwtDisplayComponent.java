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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Rectangle;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.j2se.J2SEDeviceDisplay;


public class AwtDisplayComponent extends Panel implements DisplayComponent
{
  Device prevDevice = null;
	Image offi;
	Graphics offg;


  public void paint(Graphics g) 
  {
    Device device = DeviceFactory.getDevice();
    Rectangle displayRectangle = 
        ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();

    if (prevDevice != device) {
			offi = createImage(displayRectangle.width, displayRectangle.height);
			offg = offi.getGraphics();
    }
    prevDevice = device;
    
    ((J2SEDeviceDisplay) device.getDeviceDisplay()).paint(offg);
    
		g.drawImage(offi, 0, 0, null);
  }

  
	public void update(Graphics g)
	{
		paint(g);
	}
  
}
