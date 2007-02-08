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
package org.microemu.log;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class Logger {
	
    private static final String FQCN = Logger.class.getName();

    private static final Set fqcnSet = new HashSet();
    
    private final static int ERROR = 40000;

    private final static int WARN = 30000;

    private final static int INFO = 20000;

    private final static int DEBUG = 10000;
    
    private static boolean java13 = false;
    
    static {
        fqcnSet.add(FQCN);
        //fqcnSet.add("org.microemu..?.Message");
    }

	public static boolean isDebugEnabled() {
		return true;
	}

	public static boolean isErrorEnabled() {
		return true;
	}
	
    private static String getLocation() {
    	if (java13) {
    		return "";
    	}
    	try {
			StackTraceElement[] ste = new Throwable().getStackTrace();
			for (int i = 0; i < ste.length - 1; i++) {
			    if (fqcnSet.contains(ste[i].getClassName()) && !fqcnSet.contains(ste[i + 1].getClassName())) {
			        int callerIdx = i + 1;
			        // Make Line# clickable in eclipse 
			        return ste[callerIdx].getClassName() + "." + ste[callerIdx].getMethodName() + "("
			                + ste[callerIdx].getFileName() + ":" + ste[callerIdx].getLineNumber() + ")\n";
			    }
			}
		} catch (Throwable e) {
			java13 = true;
		}
        return "";
    }
    
    private static void write(int level, String message, Throwable t) {
    	PrintStream out = System.out; 
    	if (level == ERROR) {
    		out = System.err;
    	}
    	out.println(getLocation() + message);
    	if (t != null) {
    		t.printStackTrace();
    	}
    }
    
	public static void debug(String message) {
		if (isDebugEnabled()) {
			write(DEBUG, message, null);
		}
	}
	
	public static void debug(String message, Throwable t) {
		if (isDebugEnabled()) {
			write(DEBUG, message, t);
		}
	}
	
	public static void debug(String message, String v) {
		if (isDebugEnabled()) {
			write(DEBUG, message + " " + v, null);
		}
	}
	
	public static void debug(String message, String v, String v2) {
		if (isDebugEnabled()) {
			write(DEBUG, message + " " + v + " " + v2, null);
		}
	}
	
	public static void debug(String message, int v) {
		if (isDebugEnabled()) {
			write(DEBUG, message + " " + String.valueOf(v), null);
		}
	}

	public static void debug(String message, int v1, int v2) {
		if (isDebugEnabled()) {
			write(DEBUG, message + " " + String.valueOf(v1) + " " + String.valueOf(v2), null);
		}
	}
	
	public static void debug(String message, boolean v) {
		if (isDebugEnabled()) {
			write(DEBUG, message + " " + v, null);
		}
	}
	
	public static void info(String message) {
		if (isErrorEnabled()) {
			write(INFO, message, null);
		}
	}
	
	public static void warn(String message) {
		if (isErrorEnabled()) {
			write(WARN, message, null);
		}	
	}
	
	public static void error(String message) {
		if (isErrorEnabled()) {
			write(ERROR, "error " + message, null);
		}
	}
	
	public static void error(String message, long v) {
		if (isErrorEnabled()) {
			write(ERROR, "error " + message + " " + v, null);
		}
	}
	
	public static void error(String message, String v) {
		if (isErrorEnabled()) {
			write(ERROR, "error " + message + " " + v, null);
		}
	}
	
	public static void error(String message, String v, Throwable t) {
		if (isErrorEnabled()) {
			write(ERROR, "error " + message + " " + v, t);
		}
	}

	public static void error(Throwable t) {
		if (isErrorEnabled()) {
			write(ERROR, "error " + t, t);
		}
	}
	
	public static void error(String message, Throwable t) {
		if (isErrorEnabled()) {
			write(ERROR, "error " + message + " " + t, null);
		}
	}
}
