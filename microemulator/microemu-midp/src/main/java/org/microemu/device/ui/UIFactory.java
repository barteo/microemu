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

package org.microemu.device.ui;

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

public interface UIFactory {
	
	EventDispatcher createEventDispatcher(Display display);
	
	CommandUI createCommandUI(Command command);
	
	/*
	 *  DisplayableUI
	 */
	
	AlertUI createAlertUI(Alert alert);

	CanvasUI createCanvasUI(Canvas canvas);
	
	FormUI createFormUI(Form form);

	ListUI createListUI(List list);
	
	TextBoxUI createTextBoxUI(TextBox textBox);
	
	/*
	 *  ItemUI
	 */

	ChoiceGroupUI createChoiceGroupUI(ChoiceGroup choiceGroup, int choiceType);

	CustomItemUI createCustomItemUI(CustomItemAccess customItemAccess);

	DateFieldUI createDateFieldUI(DateField dateField);

	GaugeUI createGaugeUI(Gauge gauge);
	
	ImageStringItemUI createImageStringItemUI(Item item);

	TextFieldUI createTextFieldUI(TextField textField);

}
