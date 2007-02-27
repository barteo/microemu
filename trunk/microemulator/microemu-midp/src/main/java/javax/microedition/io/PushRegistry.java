/*
 *  MicroEmulator
 *  Copyright (C) 2001-2006 Bartek Teodorczyk <barteo@barteo.net>
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

package javax.microedition.io;

import java.io.IOException;

import org.microemu.microedition.ImplFactory;
import org.microemu.microedition.io.PushRegistryDelegate;

public class PushRegistry {

	private static PushRegistryDelegate impl;

	static {
		impl = (PushRegistryDelegate) ImplFactory.getImplementation(PushRegistry.class, PushRegistryDelegate.class);
	}

	public static void registerConnection(String connection, String midlet, String filter)
			throws ClassNotFoundException, IOException {
		impl.registerConnection(connection, midlet, filter);
	}

	public static boolean unregisterConnection(String connection) {
		return impl.unregisterConnection(connection);
	}

	public static String[] listConnections(boolean available) {
		return impl.listConnections(available);
	}

	public static String getMIDlet(String connection) {
		return impl.getMIDlet(connection);
	}

	public static String getFilter(String connection) {
		return impl.getFilter(connection);
	}

	public static long registerAlarm(String midlet, long time) throws ClassNotFoundException,
			ConnectionNotFoundException {
		return impl.registerAlarm(midlet, time);
	}

}
