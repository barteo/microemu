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
package org.microemu.log;

import java.io.PrintStream;

/**
 * @author vlads
 * 
 */
public class StdOutAppender implements LoggerAppender {

	public static boolean enabled = true;
	
	public static String formatLocation(StackTraceElement ste) {
		if (ste == null) {
			return "";
		}
		// Make Line# clickable in eclipse
		return ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber()
				+ ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.log.LoggerAppender#append(org.microemu.log.LoggingEvent)
	 */
	public void append(LoggingEvent event) {
		if (!enabled) {
			return;
		}
		PrintStream out = System.out; 
    	if (event.getLevel() == LoggingEvent.ERROR) {
    		out = System.err;
    	}
    	String data = "";
    	if (event.hasData()) {
    		data = " [" + event.getFormatedData() + "]";
    	}
    	String location = formatLocation(event.getLocation());
    	if (location.length() > 0) {
    		location = "\n\t  " + location;
    	}
    	out.println(event.getMessage() + data + location);
    	if (event.getThrowable() != null) {
    		event.getThrowable().printStackTrace(out);
    	}

	}

}
