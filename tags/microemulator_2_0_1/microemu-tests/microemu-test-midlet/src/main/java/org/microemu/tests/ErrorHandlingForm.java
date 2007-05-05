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
import javax.microedition.lcdui.Displayable;

public class ErrorHandlingForm extends BaseTestsForm {

	static final Command makeErrorCommand = new Command("make error", Command.ITEM, 1);
	
	static final Command catchExceptionCommand = new Command("catch Exception", Command.ITEM, 2);
	
	public ErrorHandlingForm() {
		super("Form with Errors");
		addCommand(makeErrorCommand);
		addCommand(catchExceptionCommand);
    }
	
	
	private void tryCatchTest() {
		System.out.println("test Exception catch bytcode injection");
		try {
			throwExceptionFunction();		
		} catch (IllegalArgumentException e) {
			handleCatchIllegalArgumentException(e);
		}
		try {
			throwExceptionFunction();		
		} catch (Throwable e) {
			handleCatchThrowable(e);
		}
	}

	public static Throwable handleCatchIllegalArgumentException(Throwable t) {
		System.out.println("App caught " + t.toString());
		return t;
	}
	
	public static Throwable handleCatchThrowable(Throwable t) {
		System.out.println("App caught " + t.toString());
		return t;
	}
	
	private void throwExceptionFunction() {
		System.out.println("App will throw new IllegalArgumentException");
		throw new IllegalArgumentException("Emulator should print stack trace");
	}


	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == makeErrorCommand) {
				throw new IllegalArgumentException("Emulator Should still work");
			} else if (c == catchExceptionCommand) {
				tryCatchTest();
			}
		}
		super.commandAction(c, d);
	}
}