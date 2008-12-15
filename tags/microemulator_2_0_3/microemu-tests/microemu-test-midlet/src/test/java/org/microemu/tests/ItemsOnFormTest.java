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

import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import junit.framework.TestCase;

public class ItemsOnFormTest  extends TestCase {

	public ItemsOnFormTest() {
		
	}
	
	public ItemsOnFormTest(String name) {
		super(name);
	}
	
	public void testAddItems() {
		int step= 0;
		Item[] items= new Item[1];
		items[step]= new StringItem(null, "one");
		
		ItemsOnForm f = new ItemsOnForm(items);
		for (int i = 1 ; i < 15; i++) {
			f.commandAction(ItemsOnForm.addCommand, f);
		}
	}
}
