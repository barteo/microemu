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
 
package com.barteo.emulator.app.ui.swt;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.swt.SwtDeviceDisplay;


public class SwtDisplayComponent extends Canvas implements DisplayComponent
{
	private Canvas deviceCanvas;
	
	private Runnable redrawRunnable = new Runnable()
	{
		public void run() {
			deviceCanvas.redraw();
		}
	};


	SwtDisplayComponent(Composite parent, Canvas deviceCanvas)
	{
		super(parent, 0);
		
		this.deviceCanvas = deviceCanvas;
	}


  public void paint(SwtGraphics gc) 
  {
    Device device = DeviceFactory.getDevice();
    
    ((SwtDeviceDisplay) device.getDeviceDisplay()).paint(gc);
  }

  
	public void repaint() 
	{
		getDisplay().syncExec(redrawRunnable);
	}
  
}
