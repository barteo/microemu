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
