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
package org.microemu.cldc.file;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Vector;

import javax.microedition.io.Connection;

import org.microemu.log.Logger;
import org.microemu.microedition.ImplementationUnloadable;
import org.microemu.microedition.io.ConnectorAdapter;

/**
 * @author vlads
 * 
 */
public class FileSystemConnectorImpl extends ConnectorAdapter implements ImplementationUnloadable {

	public final static String PROTOCOL = org.microemu.cldc.file.Connection.PROTOCOL;

	/* The context to be used when acessing filesystem */
	private AccessControlContext acc;

	private String fsRoot;

	private List openConnection = new Vector();

	FileSystemConnectorImpl(String fsRoot) {
		acc = AccessController.getContext();
		this.fsRoot = fsRoot;
	}

	public Connection open(final String name, int mode, boolean timeouts) throws IOException {
		// file://<host>/<path>
		if (!name.startsWith(PROTOCOL)) {
			throw new IOException("Invalid Protocol " + name);
		}

		Connection con = (Connection) doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				return new FileSystemFileConnection(fsRoot, name.substring(PROTOCOL.length()),
						FileSystemConnectorImpl.this);
			}
		}, acc);
		openConnection.add(con);
		return con;
	}

	static Object doPrivilegedIO(PrivilegedExceptionAction action, AccessControlContext context) throws IOException {
		try {
			return AccessController.doPrivileged(action, context);
		} catch (PrivilegedActionException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			}
			throw new IOException(e.toString());
		}
	}

	void notifyMIDletDestroyed() {
		if (openConnection.size() > 0) {
			Logger.warn("Still has " + openConnection.size() + " open file connections");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.microemu.microedition.ImplementationUnloadable#unregisterImplementation()
	 */
	public void unregisterImplementation() {
		FileSystem.unregisterImplementation(this);
	}

	void notifyClosed(FileSystemFileConnection con) {
		openConnection.remove(con);
	}

}
