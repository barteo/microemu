/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
