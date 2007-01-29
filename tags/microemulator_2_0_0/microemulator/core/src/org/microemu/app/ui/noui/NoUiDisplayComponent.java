/*
 *  MicroEmulator
 *  Copyright (C) 2001-2006 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.noui;

import java.awt.Graphics;

import javax.microedition.lcdui.Displayable;

import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.MutableImage;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEMutableImage;

public class NoUiDisplayComponent implements DisplayComponent {

	private J2SEMutableImage displayImage = null;
	private DisplayRepaintListener displayRepaintListener;
	
	public void addDisplayRepaintListener(DisplayRepaintListener l) {
		displayRepaintListener = l;
	}

	public void removeDisplayRepaintListener(DisplayRepaintListener l) {
		if (displayRepaintListener == l) {
			displayRepaintListener = null;
		}
	}

	public MutableImage getDisplayImage() {
		return displayImage;
	}

	public void repaint(int x, int y, int width, int height) 
	{
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

		if (device != null) {
			if (displayImage == null) {
				displayImage = new J2SEMutableImage(
						device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
			}
					
			Graphics gc = displayImage.getImage().getGraphics();

			J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay) device.getDeviceDisplay();				
			if (!deviceDisplay.isFullScreenMode()) {
				deviceDisplay.paintControls(gc);
			}
			deviceDisplay.paintDisplayable(gc, x, y, width, height);

			fireDisplayRepaint(displayImage);
		}	
	}


	private void fireDisplayRepaint(MutableImage image)
	{
		if (displayRepaintListener != null) {
			displayRepaintListener.repaintInvoked(image);
		}
	}

}
