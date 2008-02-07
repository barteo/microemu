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

package org.microemu.device.swt;

import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.swt.ui.SwtCanvasUI;
import org.microemu.device.swt.ui.SwtListUI;
import org.microemu.device.swt.ui.SwtTextBoxUI;
import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.DisplayableUI;
import org.microemu.device.ui.ListUI;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.device.ui.UIFactory;

public class SwtDevice extends DeviceImpl {

	private UIFactory ui = new UIFactory() {

		public DisplayableUI createAlertUI() {
			// TODO Not yet implemented
			return new SwtCanvasUI();
		}

		public CanvasUI createCanvasUI() {
			return new SwtCanvasUI();
		}

		public DisplayableUI createFormUI() {
			// TODO Not yet implemented
			return new SwtCanvasUI();
		}

		public ListUI createListUI() {
			// TODO Not yet implemented
			return new SwtListUI();
		}

		public TextBoxUI createTextBoxUI() {
			return new SwtTextBoxUI();
		}

	};

	public UIFactory getUIFactory() {
		return ui;
	}

}
