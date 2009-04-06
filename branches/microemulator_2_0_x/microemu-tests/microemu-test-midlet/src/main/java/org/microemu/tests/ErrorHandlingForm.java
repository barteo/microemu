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