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
