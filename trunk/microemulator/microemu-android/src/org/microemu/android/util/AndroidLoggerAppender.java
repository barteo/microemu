/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
