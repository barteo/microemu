/*
 *  MicroEmulator
 *  Copyright (C) 2007 Ludovic Dewailly <ludovic.dewailly@dreameffect.org>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
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
		assertEquals(Connection.PROTOCOL + "localhost:1234", datagram.getAddress());
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
		assertEquals(Connection.PROTOCOL + "localhost:123", datagram.getAddress());
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
		assertEquals(Connection.PROTOCOL + "localhost:1234", datagram.getAddress());
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
		assertEquals(Connection.PROTOCOL + "localhost:123", datagram.getAddress());
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
