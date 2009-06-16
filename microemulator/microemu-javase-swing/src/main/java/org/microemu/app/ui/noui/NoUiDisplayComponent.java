/*
 *  MicroEmulator
 *  Copyright (C) 2001-2006 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.noui;

import javax.microedition.lcdui.Displayable;

import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEGraphicsSurface;

public class NoUiDisplayComponent implements DisplayComponent {

	private J2SEGraphicsSurface graphicsSurface;

	private DisplayRepaintListener displayRepaintListener;
	
	public void addDisplayRepaintListener(DisplayRepaintListener l) {
		displayRepaintListener = l;
	}

	public void removeDisplayRepaintListener(DisplayRepaintListener l) {
		if (displayRepaintListener == l) {
			displayRepaintListener = null;
		}
	}

	public void repaintRequest(int x, int y, int width, int height) 
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
			if (graphicsSurface == null) {
				graphicsSurface = new J2SEGraphicsSurface(
						device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
			}
					
			J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay) device.getDeviceDisplay();
			synchronized (graphicsSurface) {
				deviceDisplay.paintDisplayable(graphicsSurface, x, y, width, height);
				if (!deviceDisplay.isFullScreenMode()) {
					deviceDisplay.paintControls(graphicsSurface.getGraphics());
				}
			}

			fireDisplayRepaint(graphicsSurface);
		}	
	}


	private void fireDisplayRepaint(J2SEGraphicsSurface graphicsSurface)
	{
		if (displayRepaintListener != null) {
			displayRepaintListener.repaintInvoked(graphicsSurface);
		}
	}

}
