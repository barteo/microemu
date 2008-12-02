/**
 *  MicroEmulator
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
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
 *
 *  @version $Id$
 */
package org.microemu.iphone.device;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;

import joc.Scope;

import org.microemu.EmulatorContext;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.AlertUI;
import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.EventDispatcher;
import org.microemu.device.ui.FormUI;
import org.microemu.device.ui.ListUI;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.device.ui.UIFactory;
import org.microemu.iphone.MicroEmulator;
import org.microemu.iphone.device.ui.IPhoneAlertUI;
import org.microemu.iphone.device.ui.IPhoneCanvasUI;
import org.microemu.iphone.device.ui.IPhoneCommandUI;
import org.microemu.iphone.device.ui.IPhoneFormUI;
import org.microemu.iphone.device.ui.IPhoneListUI;
import org.microemu.iphone.device.ui.IPhoneTextBoxUI;

public class IPhoneDevice implements Device {

	private EmulatorContext emulatorContext;

	private UIFactory ui = new UIFactory() {

		public EventDispatcher createEventDispatcher(Display display) {
			final EventDispatcher eventDispatcher = new EventDispatcher() {

				@Override
				public void run() {
					Scope scope = new Scope();
					try {
						super.run();
					} finally {
						scope.close();
					}
				}

				@Override
				protected void post(Event event) {
					microEmulator.post(event);
				}

			};

			Thread thread = new Thread(eventDispatcher, EventDispatcher.EVENT_DISPATCHER_NAME);
			thread.setDaemon(true);
			thread.start();

			return eventDispatcher;
		}

		public AlertUI createAlertUI(Alert alert) {
			return new IPhoneAlertUI(microEmulator, alert);
		}

		public CanvasUI createCanvasUI(Canvas canvas) {
			return new IPhoneCanvasUI(microEmulator, canvas);
		}

		public FormUI createFormUI(Form form) {
			return new IPhoneFormUI(microEmulator, form);
		}

		public ListUI createListUI(List list) {
			return new IPhoneListUI(microEmulator, list);
		}

		public TextBoxUI createTextBoxUI(TextBox textBox) {
			return new IPhoneTextBoxUI(microEmulator, textBox);
		}
		public CommandUI createCommandUI(Command command) {
			return new IPhoneCommandUI(microEmulator, command);
		}

	};

	private MicroEmulator microEmulator;

	private Map systemProperties = new HashMap();

	private Vector softButtons = new Vector();

	public IPhoneDevice(EmulatorContext emulatorContext, MicroEmulator microEmulator) {
		this.emulatorContext = emulatorContext;
		this.microEmulator = microEmulator;
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

	public UIFactory getUIFactory() {
		return ui;
	}

	public boolean hasPointerEvents() {
		return true;
	}

	public boolean hasPointerMotionEvents() {
		return true;
	}

	public boolean hasRepeatEvents() {
		// TODO Auto-generated method stub
		return false;
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public boolean vibrate(int duration) {
		// TODO Auto-generated method stub
		return false;
	}

}
