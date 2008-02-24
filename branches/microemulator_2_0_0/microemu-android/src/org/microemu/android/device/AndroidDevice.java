/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.android.device;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.microedition.android.lcdui.Alert;
import javax.microedition.android.lcdui.Canvas;
import javax.microedition.android.lcdui.Form;
import javax.microedition.android.lcdui.Image;
import javax.microedition.android.lcdui.List;
import javax.microedition.android.lcdui.TextBox;

import org.microemu.EmulatorContext;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.ui.AndroidCanvasUI;
import org.microemu.android.device.ui.AndroidListUI;
import org.microemu.android.device.ui.AndroidTextBoxUI;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.DisplayableUI;
import org.microemu.device.ui.ListUI;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.device.ui.UIFactory;

public class AndroidDevice implements Device {

	private EmulatorContext emulatorContext;
	
	private MicroEmulatorActivity activity;
	
	private UIFactory ui = new UIFactory() {

		public DisplayableUI createAlertUI(Alert alert) {
			// TODO Not yet implemented
			return new AndroidCanvasUI(activity, null);
		}

		public CanvasUI createCanvasUI(Canvas canvas) {
			return new AndroidCanvasUI(activity, canvas);
		}

		public DisplayableUI createFormUI(Form form) {
			// TODO Not yet implemented
			return new AndroidCanvasUI(activity, null);
		}

		public ListUI createListUI(List list) {
			return new AndroidListUI(activity, list);
		}
		
		public TextBoxUI createTextBoxUI(TextBox textBox) {
			return new AndroidTextBoxUI(activity, textBox);
		}

	};
	
	private Map systemProperties = new HashMap();
	
	private Vector softButtons = new Vector();
	
	public AndroidDevice(EmulatorContext emulatorContext, MicroEmulatorActivity activity) {
		this.emulatorContext = emulatorContext;
		this.activity = activity;
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public Vector getButtons() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeviceDisplay getDeviceDisplay() {
		return emulatorContext.getDeviceDisplay();
	}

	public FontManager getFontManager() {
		return emulatorContext.getDeviceFontManager();
	}

	public InputMethod getInputMethod() {
		return emulatorContext.getDeviceInputMethod();
	}
	
	public UIFactory getUIFactory() {
		return ui;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getNormalImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getOverImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getPressedImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector getSoftButtons() {
		return softButtons;
	}

	public Map getSystemProperties() {
		return systemProperties;
	}

	public boolean hasPointerEvents() {
		return true;
	}

	public boolean hasPointerMotionEvents() {
		return true;
	}

	public boolean hasRepeatEvents() {
		return true;
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public boolean vibrate(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
