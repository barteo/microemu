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
package org.microemu.microedition;

import java.util.HashMap;
import java.util.Map;

public class ImplFactory {
	
	private static Map implementations = new HashMap();
	
	private static final String INTERFACE_NAME_SUFIX = "Delegate";  
	
	private static final String IMPLEMENTATION_NAME_SUFIX = "Impl";
	
	public static void register(Class implementation) {
		 
	}
	
//	public static Implementation getImplementation(Class origClass, Object[] constructorArgs) {
//		//TO-DO constructorArgs
//		return getImplementation(origClass);
//	}
	
	public static Implementation getImplementation(Class origClass, Class delegateInterface) {
		// if called from implementation constructor return null to avoid recurive calls!
		//TODO can be done using thread stack analyse or ThreadLocal 
		
		try {
			String name = delegateInterface.getName();
			if (name.endsWith(INTERFACE_NAME_SUFIX)) {
				name = name.substring(0, name.length() - INTERFACE_NAME_SUFIX.length());
			}
			name += IMPLEMENTATION_NAME_SUFIX;
			Class implClass;
			if (true) {
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				implClass = cl.loadClass(name);
			} else {
				implClass = ImplFactory.class.forName(name);
			}
			return (Implementation)implClass.newInstance();
		} catch (Throwable e) {
			throw new Error("Can't create implementation");
		}
	}
}
