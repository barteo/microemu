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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.microedition.io.Connection;

import org.microemu.log.Logger;

/**
 * Dynamic proxy class for GCF Connections returend to MIDlet
 * Used to debug excetions thrown to MIDlet
 * Makes PrivilegedCalls when rinning in Webstart
 * 
 * @author vlads
 */
public class ConnectionInvocationHandler implements InvocationHandler {

	private Connection originalConnection; 
	
	/* The context to be used when connecting to network */
    private AccessControlContext acc;
    
	static {
		Logger.addLogOrigin(ConnectionInvocationHandler.class);
	}
	
	public ConnectionInvocationHandler(Connection con, boolean needPrivilegedCalls) {
		this.originalConnection = con;
		if (needPrivilegedCalls) {
			this.acc = AccessController.getContext();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		if (ConnectorImpl.debugConnectionInvocations) {
			Logger.debug("invoke", method);
		}
		try {
			if (this.acc != null) {
			return AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws InvocationTargetException, IllegalAccessException {
					return method.invoke(originalConnection, args);
				}
			}, acc);
			} else {
				return method.invoke(this.originalConnection, args);
			}
		} catch (PrivilegedActionException e) {
			if (e.getCause() instanceof InvocationTargetException) {
				if (ConnectorImpl.debugConnectionInvocations) {
	        		Logger.error("Connection." + method.getName(), e.getCause().getCause());
	        	}
				throw e.getCause().getCause();
			} else {
				if (ConnectorImpl.debugConnectionInvocations) {
	        		Logger.error("Connection." + method.getName(), e.getCause());
	        	}
				throw e.getCause();
			}
        } catch (InvocationTargetException e) {
        	if (ConnectorImpl.debugConnectionInvocations) {
        		Logger.error("Connection." + method.getName(), e.getCause());
        	}
            throw e.getCause();
        }
	}

}
