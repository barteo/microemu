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

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.app.ui.DisplayRepaintListener;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.MutableImage;
import com.barteo.emulator.device.swt.DisplayGraphics;
import com.barteo.emulator.device.swt.SwtDeviceDisplay;
import com.barteo.emulator.device.swt.SwtMutableImage;


public class SwtDisplayComponent implements DisplayComponent
{
	private Canvas deviceCanvas;
	private SwtMutableImage displayImage = null;
	private DisplayRepaintListener displayRepaintListener;
	
	private Runnable redrawRunnable = new Runnable()
	{
		public void run() 
		{
			if (!deviceCanvas.isDisposed()) {
				deviceCanvas.redraw();
			}
		}
	};


	SwtDisplayComponent(Canvas deviceCanvas)
	{
		this.deviceCanvas = deviceCanvas;
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


	public void paint(SwtGraphics gc) 
	{
		synchronized (this) {
			if (displayImage != null) {
				gc.drawImage(displayImage.img, 0, 0);
			}
		}
	}

  
	public void repaint() 
	{
		if (!deviceCanvas.isDisposed()) {			
			Device device = DeviceFactory.getDevice();

			SwtMutableImage image = new SwtMutableImage(
					device.getDeviceDisplay().getWidth(),
					device.getDeviceDisplay().getHeight());
						
			SwtGraphics gc = ((DisplayGraphics) image.getGraphics()).g;
			try {
				((SwtDeviceDisplay) device.getDeviceDisplay()).paint(gc);
			} finally {
				gc.dispose();
			}

			synchronized (this) {
				if (displayImage != null) {
					displayImage.img.dispose();
				}
				displayImage = image;
			}
			
			fireDisplayRepaint(displayImage);	
	
			deviceCanvas.getDisplay().asyncExec(redrawRunnable);
		}
	}
	
	
	private void fireDisplayRepaint(MutableImage image)
	{
		if (displayRepaintListener != null) {
			displayRepaintListener.repaintInvoked(image);
		}
	}
  
}
