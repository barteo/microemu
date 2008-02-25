/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Timer;

public class ThreadUtils {

	private static boolean java13 = false;
	
	private static boolean java14 = false;
	
	/**
     * Creates a new timer whose associated thread has the specified name in Java 1.5.
	 * 
	 * @param name the name of the associated thread
     *
	 */
	public static Timer createTimer(String name) {
		try {
			Constructor c = Timer.class.getConstructor(new Class[] { String.class });
			return (Timer)c.newInstance(new Object[]{name});
		} catch (Throwable e) {
			// In cany case create new Timer
			return new Timer();
		}
	}

	public static String getCallLocation(String fqn) {
		if (!java13) {
			try {
				StackTraceElement[] ste = new Throwable().getStackTrace();
				for (int i = 0; i < ste.length - 1; i++) {
					if (fqn.equals(ste[i].getClassName())) {
						StackTraceElement callLocation = ste[i + 1];
						String nextClassName = callLocation.getClassName();
						if (nextClassName.equals(fqn)) {
							continue;
						}
						return callLocation.toString();
					}
				}
			} catch (Throwable e) {
				java13 = true;
			}
		}
		return null;
	}
	
	public static String getTreadStackTrace(Thread t) {
		if (java14) {
			return "";
		}
		try {
			// Java 1.5 thread.getStackTrace();
			Method m = t.getClass().getMethod("getStackTrace", null);
			
			StackTraceElement[] trace = (StackTraceElement[])m.invoke(t, null);
			StringBuffer b = new StringBuffer();  
			for (int i=0; i < trace.length; i++) {
			    b.append("\n\tat ").append(trace[i]);
			}
			return b.toString();
		} catch (Throwable e) {
			java14 = true;
			return "";
		}
	}
}
