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

import junit.framework.TestCase;

/**
 * TestCase for {@link DatagramImpl}.
 */
public class DatagramImplTest extends TestCase {

	public void testDatagramImplInt() {
		boolean exception;
		try {
			new DatagramImpl(-1);
			exception = false;
		} catch (IllegalArgumentException e) {
			exception = true;
		}
		assertTrue(exception);
		DatagramImpl datagram = new DatagramImpl(100);
		assertEquals(100, datagram.getLength());
	}

	public void testDatagramImplByteArrayInt() {
		byte[] buffer = new byte[100];
		DatagramImpl datagram = new DatagramImpl(buffer, 100);
		assertEquals(buffer, datagram.getData());
		assertEquals(100, datagram.getLength());
	}

	public void testGetAddress() throws Exception {
		DatagramImpl datagram = new DatagramImpl(100);
		String address = "localhost:1234";
		datagram.setAddress(address);
		assertEquals(address, datagram.getAddress());
	}

	public void testGetData() {
		byte[] data = new byte[20];
		DatagramImpl datagram = new DatagramImpl(data, data.length);
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) i;
			assertEquals(datagram.getData()[i], data[i]);
		}
	}

	public void testGetLength() {
		DatagramImpl datagram = new DatagramImpl(20);
		for (int i = 1; i < 20; i++) {
			datagram.setLength(i);
			assertEquals(i, datagram.getLength());
		}
	}

	public void testSetAddressString() throws Exception {
		DatagramImpl datagram = new DatagramImpl(20);
		boolean exception;
		try {
			datagram.setAddress((String) null);
			exception = false;
		} catch (NullPointerException e) {
			exception = true;
		}
		assertTrue(exception);
		// missing port
		try {
			datagram.setAddress("localhost");
			exception = false;
		} catch (IllegalArgumentException e) {
			exception = true;
		}
		assertTrue(exception);
		datagram.setAddress("localhost:1111");
		assertEquals("localhost:1111", datagram.getAddress());

	}

	public void testSetAddressDatagram() throws Exception {
		DatagramImpl datagram1 = new DatagramImpl(20);
		DatagramImpl datagram2 = new DatagramImpl(20);
		datagram1.setAddress("localhost:1111");
		datagram2.setAddress(datagram1);
		assertEquals("localhost:1111", datagram2.getAddress());
	}

	public void testReadWriteBoolean() throws Exception {
		DatagramImpl datagram = new DatagramImpl(100);
		boolean value = true;
		for (int i = 0; i < 10; i++) {
			datagram.writeBoolean(value);
			value = !value;
		}
		value = true;
		for (int i = 0; i < 10; i++) {
			assertEquals(value, datagram.readBoolean());
			value = !value;
		}
		datagram.reset();
		value = true;
		for (int i = 0; i < 10; i++) {
			assertEquals(value, datagram.readBoolean());
			value = !value;
		}
		datagram.reset();
		value = false;
		for (int i = 0; i < 10; i++) {
			datagram.writeBoolean(value);
			value = !value;
		}
		value = false;
		for (int i = 0; i < 10; i++) {
			assertEquals(value, datagram.readBoolean());
			value = !value;
		}
	}

	public void testReadWriteByte() throws Exception {
		DatagramImpl datagram = new DatagramImpl(100);
		for (int i = 0; i < 10; i++) {
			datagram.writeByte(i);
		}
		for (int i = 0; i < 10; i++) {
			assertEquals(i, datagram.readByte());
		}
		datagram.reset();
		for (int i = 0; i < 10; i++) {
			assertEquals(i, datagram.readByte());
		}
		datagram.reset();
		for (int i = 0; i < 10; i++) {
			datagram.writeByte(i + 10);
		}
		for (int i = 0; i < 10; i++) {
			assertEquals(i + 10, datagram.readByte());
		}
	}

	public void testReadWriteChar() throws Exception {
		DatagramImpl datagram = new DatagramImpl(100);
		for (int i = 0; i < 10; i++) {
			datagram.writeChar((int) 'a' + i);
		}
		for (int i = 0; i < 10; i++) {
			assertEquals((int) 'a' + i, datagram.readChar());
		}
		datagram.reset();
		for (int i = 0; i < 10; i++) {
			assertEquals((int) 'a' + i, datagram.readChar());
		}
		datagram.reset();
		for (int i = 0; i < 10; i++) {
			datagram.writeChar((int) 'A' + i);
		}
		for (int i = 0; i < 10; i++) {
			assertEquals((int) 'A' + i, datagram.readChar());
		}
	}

	public void testReadWriteUTF() throws Exception {
		String[] text = new String[] { "HelloWorld!", "Some text", "", "Good Stuff" };
		DatagramImpl datagram = new DatagramImpl(100);
		for (int i = 0; i < text.length; i++) {
			datagram.writeUTF(text[i]);
		}
		for (int i = 0; i < text.length; i++) {
			assertEquals(text[i], datagram.readUTF());
		}
		datagram.reset();
		for (int i = 0; i < text.length; i++) {
			assertEquals(text[i], datagram.readUTF());
		}
		datagram.reset();
		for (int i = 0; i < text.length; i++) {
			datagram.writeUTF("xxx" + text[i]);
		}
		for (int i = 0; i < text.length; i++) {
			assertEquals("xxx" + text[i], datagram.readUTF());
		}
	}

	public void testSkipBytes() throws Exception {
		byte[] data = new byte[20];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) i;
		}
		DatagramImpl datagram = new DatagramImpl(data, data.length);
		for (int i = 0; i < data.length; i++) {
			datagram.reset();
			datagram.skipBytes(i);
			for (int j = 0; j < data.length - i; j++) {
				assertEquals(data[i + j], datagram.readByte());
			}
		}
	}
}
