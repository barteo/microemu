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

import org.microemu.cldc.ClosedConnection;

public class Connection implements ClosedConnection {

	final static String PROTOCOL = "file://";
	
	public static final int CONNECTIONTYPE_SYSTEM_FS = 0;
	
	private static int connectionType = CONNECTIONTYPE_SYSTEM_FS;
	
	public javax.microedition.io.Connection open(String name) throws IOException {
		// file://<host>/<path>
		if (!name.startsWith(PROTOCOL)) {
			throw new IOException("Invalid Protocol " + name);
		}
		switch (connectionType) {
		case CONNECTIONTYPE_SYSTEM_FS:
			return new SystemFileConnection(name.substring(PROTOCOL.length()));
		default:
			throw new IOException("Invalid connectionType configuration");
		}
	}

	static int getConnectionType() {
		return connectionType;
	}

	static void setConnectionType(int connectionType) {
		Connection.connectionType = connectionType;
	}

	
	
}
