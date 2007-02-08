/*
 *  MicroEmulator
 *  Copyright (C) 2001-2007 MicroEmulator Team.
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
