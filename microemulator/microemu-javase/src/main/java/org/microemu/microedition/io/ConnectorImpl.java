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

import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

import org.microemu.cldc.ClosedConnection;

import com.sun.cdc.io.ConnectionBaseInterface;

/**
 * @author vlads
 * Original MicroEmulator implementation of javax.microedition.Connector
 */
public class ConnectorImpl extends ConnectorAdapter {

	public Connection open(String name, int mode, boolean timeouts) throws IOException {
		Class cl;
		try {
			cl = Class.forName("org.microemu.cldc." + name.substring(0, name.indexOf(':')) + ".Connection");
			Object inst = cl.newInstance();
			if (inst instanceof ConnectionImplementation) {
				return ((ConnectionImplementation)inst).openConnection(name, mode, timeouts);
			} else {
				return ((ClosedConnection)inst).open(name);
			}
		} catch (ClassNotFoundException ex) {
			try {
				cl = Class.forName("com.sun.cdc.io.j2me." + name.substring(0, name.indexOf(':')) + ".Protocol");
				ConnectionBaseInterface base = (ConnectionBaseInterface) cl.newInstance();

				return base.openPrim(name.substring(name.indexOf(':') + 1), mode, timeouts);
			} catch (ClassNotFoundException ex1) {
				System.err.println(ex1);
				throw new ConnectionNotFoundException();
			} catch (InstantiationException ex1) {
				System.err.println(ex);
				throw new ConnectionNotFoundException();
			} catch (IllegalAccessException ex1) {
				System.err.println(ex);
				throw new ConnectionNotFoundException();
			}
		} catch (InstantiationException ex) {
			System.err.println(ex);
			throw new ConnectionNotFoundException();
		} catch (IllegalAccessException ex) {
			System.err.println(ex);
			throw new ConnectionNotFoundException();
		}
	}
}
