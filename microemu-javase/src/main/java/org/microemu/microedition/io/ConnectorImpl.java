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
package org.microemu.microedition.io;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

import org.microemu.cldc.ClosedConnection;
import org.microemu.log.Logger;

import com.sun.cdc.io.ConnectionBaseInterface;

/**
 * @author vlads
 * Original MicroEmulator implementation of javax.microedition.Connector
 */
public class ConnectorImpl extends ConnectorAdapter {

	/* The context to be used when loading classes */
    private AccessControlContext acc;
    
    // TODO make this configurable
	public static boolean debugConnectionInvocations = false;
    
	private final boolean needPrivilegedCalls = isWebstart();  
	
    public ConnectorImpl() {
    	acc = AccessController.getContext();
    }
    
    private static boolean isWebstart() {
    	try {
			return (System.getProperty("javawebstart.version") != null);
		} catch (SecurityException e) {
			// This is the case for Applet.
			return false;
		}
    }
    
    public Connection open(final String name, final int mode, final boolean timeouts) throws IOException {
    	try {
			return (Connection) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws IOException {
					if (debugConnectionInvocations || needPrivilegedCalls) {
						return openSecureProxy(name, mode, timeouts, needPrivilegedCalls);
					} else {
						return openSecure(name, mode, timeouts);
					}
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			}
			throw new IOException(e.toString());
		}
	}
	
    private Connection openSecureProxy(String name, int mode, boolean timeouts, boolean needPrivilegedCalls) throws IOException {
    	Connection origConnection =  openSecure(name, mode, timeouts);
    	Class connectionClass = null;
    	Class[] interfaces = origConnection.getClass().getInterfaces();
    	for (int i = 0; i < interfaces.length; i++) {
    		if (Connection.class.isAssignableFrom(interfaces[i])) {
    			connectionClass = interfaces[i];
    			break;
    		}
		}
    	if (connectionClass == null) {
    		throw new ClassCastException(origConnection.getClass().getName() + " Connection expected");
    	}
    	return (Connection) Proxy.newProxyInstance(
    			 ConnectorImpl.class.getClassLoader(), 
                 interfaces, 
                 new ConnectionInvocationHandler(origConnection, needPrivilegedCalls));
    }
    
	private Connection openSecure(String name, int mode, boolean timeouts) throws IOException {
		String className = null;
		try {
			try {
				className = "org.microemu.cldc." + name.substring(0, name.indexOf(':')) + ".Connection";
				Class cl = Class.forName(className);
				Object inst = cl.newInstance();
				if (inst instanceof ConnectionImplementation) {
					return ((ConnectionImplementation) inst).openConnection(name, mode, timeouts);
				} else {
					return ((ClosedConnection) inst).open(name);
				}
			} catch (ClassNotFoundException e) {
				try {
					className = "com.sun.cdc.io.j2me." + name.substring(0, name.indexOf(':')) + ".Protocol";
					Class cl = Class.forName(className);
					ConnectionBaseInterface base = (ConnectionBaseInterface) cl.newInstance();
					return base.openPrim(name.substring(name.indexOf(':') + 1), mode, timeouts);
				} catch (ClassNotFoundException ex) {
					Logger.debug("connection class not found");
					throw new ConnectionNotFoundException();
				}
			}
		} catch (InstantiationException e) {
			Logger.error("Unable to create", className, e);
			throw new ConnectionNotFoundException();
		} catch (IllegalAccessException e) {
			Logger.error("Unable to create", className, e);
			throw new ConnectionNotFoundException();
		}
	}
}
