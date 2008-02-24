/*
 *  MicroEmulator
 *  Copyright (C) 2007 Ludovic Dewailly <ludovic.dewailly@dreameffect.org>
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
 */

package org.microemu.cldc.datagram;

import java.io.IOException;
import java.net.InetAddress;

import javax.microedition.io.Datagram;

import junit.framework.TestCase;

/**
 * TestCase for {@link Connection}
 */
public class ConnectionTest extends TestCase {

	public void testNewDatagramInt() throws Exception {
		Connection conn = new Connection();
		conn.openConnection(Connection.PROTOCOL + "localhost:1234", 0, false);
		Datagram datagram = conn.newDatagram(512);
		assertNotNull(datagram);
		assertEquals("localhost:1234", datagram.getAddress());
		assertEquals(512, datagram.getData().length);
		assertEquals(512, datagram.getLength());
		assertEquals(0, datagram.getOffset());
		conn.close();
	}

	public void testNewDatagramIntString() throws Exception {
		Connection conn = new Connection();
		conn.openConnection(Connection.PROTOCOL + "localhost:1234", 0, false);
		Datagram datagram = conn.newDatagram(512, "localhost:123");
		assertNotNull(datagram);
		assertEquals("localhost:123", datagram.getAddress());
		assertEquals(512, datagram.getData().length);
		assertEquals(512, datagram.getLength());
		assertEquals(0, datagram.getOffset());
		conn.close();
	}

	public void testNewDatagramByteArrayInt() throws Exception {
		Connection conn = new Connection();
		conn.openConnection(Connection.PROTOCOL + "localhost:1234", 0, false);
		byte[] data = new byte[1024];
		Datagram datagram = conn.newDatagram(data, data.length);
		assertNotNull(datagram);
		assertEquals("localhost:1234", datagram.getAddress());
		assertEquals(data, datagram.getData());
		assertEquals(data.length, datagram.getLength());
		assertEquals(0, datagram.getOffset());
		conn.close();
	}

	public void testNewDatagramByteArrayIntString() throws Exception {
		Connection conn = new Connection();
		conn.openConnection(Connection.PROTOCOL + "localhost:1234", 0, false);
		byte[] data = new byte[1024];
		Datagram datagram = conn.newDatagram(data, data.length, "localhost:123");
		assertNotNull(datagram);
		assertEquals("localhost:123", datagram.getAddress());
		assertEquals(data, datagram.getData());
		assertEquals(data.length, datagram.getLength());
		assertEquals(0, datagram.getOffset());
		conn.close();
	}

	public void testGetLocalAddress() throws Exception {
		Connection conn = new Connection();
		// server connection
		conn.openConnection(Connection.PROTOCOL + ":1234", 0, false);
		assertEquals(InetAddress.getLocalHost().getHostAddress(), conn.getLocalAddress());
		conn.close();
		// client connection
		conn.openConnection(Connection.PROTOCOL + InetAddress.getLocalHost().getHostAddress() + ":1234", 0, false);
		assertEquals(InetAddress.getLocalHost().getHostAddress(), conn.getLocalAddress());
		conn.close();
	}

	public void testGetLocalPort() throws Exception {
		Connection conn = new Connection();
		// server connection
		conn.openConnection(Connection.PROTOCOL + ":1234", 0, false);
		assertEquals(1234, conn.getLocalPort());
		conn.close();
		// client connection
		conn.openConnection(Connection.PROTOCOL + "localhost:1234", 0, false);
		assertTrue(1234 != conn.getLocalPort());
		conn.close();
	}

	public void testOpenConnection() throws Exception {
		Connection conn = new Connection();
		boolean exception;
		// invalid connection string
		try {
			conn.openConnection("http://test", 0, false);
			exception = false;
		} catch (IOException e) {
			exception = true;
		}
		assertTrue(exception);
		// missing port
		try {
			conn.openConnection(Connection.PROTOCOL + "localhost", 0, false);
			exception = false;
		} catch (IOException e) {
			exception = true;
		}
		assertTrue(exception);
		// client connection
		assertNotNull(conn.openConnection(Connection.PROTOCOL + "localhost:1234", 0, false));
		conn.close();
		// server connection
		assertNotNull(conn.openConnection(Connection.PROTOCOL + ":1234", 0, false));
		conn.close();
	}
}
