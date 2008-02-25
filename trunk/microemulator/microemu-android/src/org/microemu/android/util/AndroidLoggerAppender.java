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

package org.microemu.android.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.microemu.android.MicroEmulator;
import org.microemu.log.LoggerAppender;
import org.microemu.log.LoggingEvent;

import android.util.Log;

public class AndroidLoggerAppender implements LoggerAppender {

	public static String formatLocation(StackTraceElement ste) {
		if (ste == null) {
			return "";
		}
		// Make Line# clickable in eclipse
		return ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber()
				+ ")";
	}

	public void append(LoggingEvent event) {
    	String data = "";
    	if (event.hasData()) {
    		data = " [" + event.getFormatedData() + "]";
    	}
		Log.v(MicroEmulator.LOG_TAG, event.getMessage() + data +  "\n\t  " + formatLocation(event.getLocation()));
    	if (event.getThrowable() != null) {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		event.getThrowable().printStackTrace(new PrintStream(baos));
    		Log.v(MicroEmulator.LOG_TAG, baos.toString());
    	}

	}

}
