/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@barteo.net>
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
 
package org.microemu.app.ui.swt;

import javax.microedition.lcdui.Displayable;

import org.eclipse.swt.widgets.Canvas;
import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.MutableImage;
import org.microemu.device.swt.SwtDeviceDisplay;
import org.microemu.device.swt.SwtDisplayGraphics;
import org.microemu.device.swt.SwtMutableImage;


//TODO extends Canvas like in swing version
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
	
	
	public MutableImage getDisplayImage()
	{
		return displayImage;
	}


	public void paint(SwtGraphics gc) 
	{
		synchronized (this) {
			if (displayImage != null) {
				gc.drawImage(displayImage.img, 0, 0);
			}
		}
	}

  
	public void repaintRequest(int x, int y, int width, int height) 
	{
		if (!deviceCanvas.isDisposed()) {			
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			DisplayAccess da = ma.getDisplayAccess();
			if (da == null) {
				return;
			}
			Displayable current = da.getCurrent();
			if (current == null) {
				return;
			}

			Device device = DeviceFactory.getDevice();

			SwtMutableImage image = new SwtMutableImage(
					device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
						
			SwtGraphics gc = ((SwtDisplayGraphics) image.getGraphics()).g;
			try {
				SwtDeviceDisplay deviceDisplay = (SwtDeviceDisplay) device.getDeviceDisplay();
				deviceDisplay.paintDisplayable(gc, x, y, width, height);
				if (!deviceDisplay.isFullScreenMode()) {
					deviceDisplay.paintControls(gc);
				}
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
