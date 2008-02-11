/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.j2se;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;

import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.j2se.ui.J2SECanvasUI;
import org.microemu.device.j2se.ui.J2SEListUI;
import org.microemu.device.j2se.ui.J2SETextBoxUI;
import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.DisplayableUI;
import org.microemu.device.ui.ListUI;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.device.ui.UIFactory;

public class J2SEDevice extends DeviceImpl {

	private UIFactory ui = new UIFactory() {

		public DisplayableUI createAlertUI(Alert alert) {
			// TODO Not yet implemented
			return new J2SECanvasUI(null);
		}

		public CanvasUI createCanvasUI(Canvas canvas) {
			return new J2SECanvasUI(canvas);
		}

		public DisplayableUI createFormUI(Form form) {
			// TODO Not yet implemented
			return new J2SECanvasUI(null);
		}

		public ListUI createListUI(List list) {
			return new J2SEListUI(list);
		}

		public TextBoxUI createTextBoxUI(TextBox textBox) {
			return new J2SETextBoxUI(textBox);
		}

	};

	public UIFactory getUIFactory() {
		return ui;
	}

}
