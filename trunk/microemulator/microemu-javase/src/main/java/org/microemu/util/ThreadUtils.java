/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu.util;

import java.lang.reflect.Constructor;
import java.util.Timer;

public class ThreadUtils {

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

}
