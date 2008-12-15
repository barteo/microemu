/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
package org.microemu.tests;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

public class ItemsOnForm extends Form implements CommandListener, DisplayableUnderTests {

	public static final Command addCommand = new Command("add", Command.ITEM, 1);
	
	public ItemsOnForm(Item[] items) {
		super("Form with Items", items);
		addCommand(addCommand);
		addCommand(DisplayableUnderTests.backCommand);
		setCommandListener(this);
    }
	
	public ItemsOnForm() {
		this(null);
	}

	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == DisplayableUnderTests.backCommand) {
				Manager.midletInstance.showMainPage();
			} if (c == addCommand) {
				append(new StringItem("si:", "StringItem" + Manager.sequenceNext()));
			}
		}
	}

}
