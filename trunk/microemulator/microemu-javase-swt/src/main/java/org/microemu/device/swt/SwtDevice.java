/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.swt;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import org.microemu.CustomItemAccess;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.impl.ui.CommandImplUI;
import org.microemu.device.swt.ui.SwtAlertUI;
import org.microemu.device.swt.ui.SwtCanvasUI;
import org.microemu.device.swt.ui.SwtFormUI;
import org.microemu.device.swt.ui.SwtListUI;
import org.microemu.device.swt.ui.SwtTextBoxUI;
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

public class SwtDevice extends DeviceImpl {

	private UIFactory ui = new UIFactory() {

		public EventDispatcher createEventDispatcher(Display display) {
			EventDispatcher eventDispatcher = new EventDispatcher();
			Thread thread = new Thread(eventDispatcher, EventDispatcher.EVENT_DISPATCHER_NAME);
			thread.setDaemon(true);
			thread.start();

			return eventDispatcher;
		}

		public AlertUI createAlertUI(Alert alert) {
			return new SwtAlertUI(alert);
		}

		public CanvasUI createCanvasUI(Canvas canvas) {
			return new SwtCanvasUI(canvas);
		}

		public CommandUI createCommandUI(Command command) {
			return new CommandImplUI(command);
		}

		public FormUI createFormUI(Form form) {
			return new SwtFormUI(form);
		}

		public ListUI createListUI(List list) {
			return new SwtListUI(list);
		}

		public TextBoxUI createTextBoxUI(TextBox textBox) {
			return new SwtTextBoxUI(textBox);
		}

		@Override
		public ChoiceGroupUI createChoiceGroupUI(ChoiceGroup choiceGroup,
				int choiceType) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CustomItemUI createCustomItemUI(CustomItemAccess customItemAccess) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DateFieldUI createDateFieldUI(DateField dateField) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public GaugeUI createGaugeUI(Gauge gauge) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ImageStringItemUI createImageStringItemUI(Item item) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TextFieldUI createTextFieldUI(TextField textField) {
			// TODO Auto-generated method stub
			return null;
		}

	};

	public UIFactory getUIFactory() {
		return ui;
	}

}
