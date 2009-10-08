/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.android.device;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import org.microemu.CustomItemAccess;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.ui.AndroidAlertUI;
import org.microemu.android.device.ui.AndroidCanvasUI;
import org.microemu.android.device.ui.AndroidChoiceGroupUI;
import org.microemu.android.device.ui.AndroidCommandUI;
import org.microemu.android.device.ui.AndroidCustomItemUI;
import org.microemu.android.device.ui.AndroidDateFieldUI;
import org.microemu.android.device.ui.AndroidFormUI;
import org.microemu.android.device.ui.AndroidGaugeUI;
import org.microemu.android.device.ui.AndroidImageStringItemUI;
import org.microemu.android.device.ui.AndroidListUI;
import org.microemu.android.device.ui.AndroidTextBoxUI;
import org.microemu.android.device.ui.AndroidTextFieldUI;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.EmulatorContext;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.AlertUI;
import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.ChoiceGroupUI;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.CustomItemUI;
import org.microemu.device.ui.DateFieldUI;
import org.microemu.device.ui.EventDispatcher;
import org.microemu.device.ui.FormUI;
import org.microemu.device.ui.GaugeUI;
import org.microemu.device.ui.ImageStringItemUI;
import org.microemu.device.ui.ListUI;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.device.ui.TextFieldUI;
import org.microemu.device.ui.UIFactory;

import android.content.Context;
import android.os.Vibrator;

public class AndroidDevice implements Device {

	private EmulatorContext emulatorContext;
	
	private MicroEmulatorActivity activity;
	
	private UIFactory ui = new UIFactory() {

		public EventDispatcher createEventDispatcher(Display display) {
			EventDispatcher eventDispatcher = new EventDispatcher() {

				@Override
				protected void post(Event event) {
					activity.post(event);
				}
				
			};
			
			Thread thread = new Thread(eventDispatcher, EventDispatcher.EVENT_DISPATCHER_NAME);
			thread.setDaemon(true);
			thread.start();

			return eventDispatcher;
		}

		public CommandUI createCommandUI(Command command) {
			return new AndroidCommandUI(activity, command);
		}

		public AlertUI createAlertUI(Alert alert) {
			return new AndroidAlertUI(activity, alert);
		}

		public CanvasUI createCanvasUI(Canvas canvas) {
			return new AndroidCanvasUI(activity, canvas);
		}
		
		public FormUI createFormUI(Form form) {
			return new AndroidFormUI(activity, form);
		}

		public ListUI createListUI(List list) {
			return new AndroidListUI(activity, list);
		}
		
		public TextBoxUI createTextBoxUI(TextBox textBox) {
			return new AndroidTextBoxUI(activity, textBox);
		}

		public ChoiceGroupUI createChoiceGroupUI(ChoiceGroup choiceGroup, int choiceType) {
			return new AndroidChoiceGroupUI(activity, choiceGroup, choiceType);
		}

		public CustomItemUI createCustomItemUI(CustomItemAccess customItemAccess) {
			return new AndroidCustomItemUI(activity, customItemAccess);
		}

		public DateFieldUI createDateFieldUI(DateField dateField) {
			return new AndroidDateFieldUI(activity, dateField);
		}
		
		public GaugeUI createGaugeUI(Gauge gauge) {
			return new AndroidGaugeUI(activity, gauge);
		}

		public ImageStringItemUI createImageStringItemUI(Item item) {
			return new AndroidImageStringItemUI(activity, item);
		}

		public TextFieldUI createTextFieldUI(TextField textField) {
			return new AndroidTextFieldUI(activity, textField);
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

	public boolean vibrate(int duration) {
		Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
		if (vibrator != null) {
			vibrator.vibrate(duration);
			return true;
		} else {
			return false;
		}
	}

}
