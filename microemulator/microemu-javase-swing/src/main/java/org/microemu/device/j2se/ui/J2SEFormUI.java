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

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ItemStateListener;

import org.microemu.device.impl.ui.DisplayableImplUI;
import org.microemu.device.ui.FormUI;
import org.microemu.device.ui.ItemUI;

public class J2SEFormUI extends DisplayableImplUI implements FormUI {

	private ItemStateListener itemStateListener;
	
	public J2SEFormUI(Form form) {
		super(form);
	}

	public int append(ItemUI item) {
		// TODO not yet used
		return 0;
	}

	public void delete(int itemNum) {
		// TODO not yet used		
	}

	public void deleteAll() {
		// TODO not yet used		
	}

	public void insert(int itemNum, ItemUI item) {
		// TODO not yet used		
	}

	public void set(int itemNum, ItemUI item) {
		// TODO Auto-generated method stub		
	}

	public void setItemStateListener(ItemStateListener itemStateListener) {
		this.itemStateListener = itemStateListener;
	}
	
	public ItemStateListener getItemStateListener() {
		return itemStateListener;
	}

}
