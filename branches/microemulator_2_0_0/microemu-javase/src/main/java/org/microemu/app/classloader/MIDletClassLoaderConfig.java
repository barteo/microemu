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
package org.microemu.app.classloader;

import java.util.List;
import java.util.Vector;

import org.microemu.app.ConfigurationException;

public class MIDletClassLoaderConfig {

	public static final int DELEGATION_STRICT = 0;
	
	public static final int DELEGATION_DELEGATING = 1;
	
	public static final int DELEGATION_SYSTEM = 2;
	
	int delegationType = DELEGATION_STRICT;
	
	List appclasses = new Vector();
	
	List appclasspath = new Vector();

	public void setDelegationType(String delegationType) throws ConfigurationException {
		if ("strict".equalsIgnoreCase(delegationType)) {
			this.delegationType = DELEGATION_STRICT;
		} else if ("delegating".equalsIgnoreCase(delegationType)) {
			this.delegationType = DELEGATION_DELEGATING;
		} else if ("system".equalsIgnoreCase(delegationType)) {
			if ((appclasses.size() != 0) || (appclasspath.size() != 0)) {
				throw new ConfigurationException("Can't extend system CLASSPATH");
			}
			this.delegationType = DELEGATION_SYSTEM;
		} else {
			throw new ConfigurationException("Unknown delegationType [" + delegationType + "]");
		}
	}
	
	public boolean isClassLoaderDisabled() {
		return (this.delegationType == DELEGATION_SYSTEM);
	}
	
	public void addAppClassPath(String path) throws ConfigurationException {
		if (this.delegationType == DELEGATION_SYSTEM) {
			throw new ConfigurationException("Can't extend system CLASSPATH");
		}
		appclasspath.add(path);
	}
	
	public void addAppClass(String className) throws ConfigurationException {
		if (this.delegationType == DELEGATION_SYSTEM) {
			throw new ConfigurationException("Can't extend system CLASSPATH");
		}
		appclasses.add(className);
	}
	
}
