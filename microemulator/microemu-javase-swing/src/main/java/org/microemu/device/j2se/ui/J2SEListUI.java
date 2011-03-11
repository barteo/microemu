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

package org.microemu.device.j2se.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import org.microemu.device.impl.ui.DisplayableImplUI;
import org.microemu.device.ui.ListUI;

public class J2SEListUI extends DisplayableImplUI implements ListUI {

	public J2SEListUI(List list) {
		super(list);
	}

	public int append(String stringPart, Image imagePart) {
		// TODO not yet used
		return -1;
	}

	public void setSelectCommand(Command command) {
		// TODO not yet used
	}

	public int getSelectedIndex() {
		// TODO not yet used
		return 0;
	}

	public void setSelectedIndex(int elementNum, boolean selected) {
		// TODO not yet used
	}

	public String getString(int elementNum) {
		// TODO not yet used
		return null;
	}

	public void delete(int elementNum) {
		// TODO Auto-generated method stub		
	}

	public void deleteAll() {
		// TODO Auto-generated method stub		
	}

	public void insert(int elementNum, String stringPart, Image imagePart) {
		// TODO not yet used
	}

	public void set(int elementNum, String stringPart, Image imagePart) {
		// TODO not yet used
	}

	public int size() {
		// TODO not yet used
		return 0;
	}

}
