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
package org.microemu.midp.examples.simpledemo;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

public class ListPanel extends BaseExamplesList {

	String options[] = { "Option A", "Option B", "Option C", "Option D" };

	List lists[] = { new List("Exclusive", List.EXCLUSIVE, options, null),
			new List("Implicit", List.IMPLICIT, options, null),
			new List("Multiple", List.MULTIPLE, options, null) };

	public ListPanel() {
		super("List", List.IMPLICIT);

		for (int i = 0; i < lists.length; i++) {
			lists[i].addCommand(BaseExamplesForm.backCommand);
			lists[i].setCommandListener(this);
			append(lists[i].getTitle(), null);
		}

	}

	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == List.SELECT_COMMAND) {
				SimpleDemoMIDlet.setCurrentDisplayable(lists[getSelectedIndex()]);
			} 
		} else if (c == BaseExamplesForm.backCommand) {
			for (int i = 0; i < lists.length; i++) {
				if (d == lists[i]) {
					SimpleDemoMIDlet.setCurrentDisplayable(this);
				}
			}
		}
		super.commandAction(c, d);
	}

}
