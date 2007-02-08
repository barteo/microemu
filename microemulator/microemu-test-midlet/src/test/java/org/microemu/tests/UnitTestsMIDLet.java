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

import javax.microedition.midlet.MIDletStateChangeException;

import junit.framework.TestCase;

import cldcunit.runner.TestRunner;

public class UnitTestsMIDLet extends TestRunner implements MIDletUnderTests {

	protected void startApp() throws MIDletStateChangeException {
		Manager.midletInstance = this;
		try {
			start(new TestCase[] { new ItemsOnFormTest() });
		} catch (Exception e) {
			System.out.println("Exception while setting up tests: " + e);
			e.printStackTrace();
		}
	}
	
	public void showMainPage() {
		
	}

}
