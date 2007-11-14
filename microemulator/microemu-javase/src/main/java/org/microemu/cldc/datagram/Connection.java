/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.UDPDatagramConnection;

import org.microemu.microedition.io.ConnectionImplementation;

/**
 * {@link ConnectionImplementation} for the datagram protocol (UDP).
 */
public class Connection implements DatagramConnection, UDPDatagramConnection, ConnectionImplementation {

	/**
	 * The datagram protocol constant
	 */
	public final static String PROTOCOL = "datagram://";

	/**
	 * The encapsulated {@link DatagramSocket}
	 */
	private DatagramSocket socket;

	/**
	 * The connection address in the format <tt>host:port</tt>
	 */
	private String address;

	public void close() throws IOException {
		socket.close();
	}

	public int getMaximumLength() throws IOException {
		return Math.min(socket.getReceiveBufferSize(), socket.getSendBufferSize());
	}

	public int getNominalLength() throws IOException {
		return getMaximumLength();
	}

	public void send(Datagram dgram) throws IOException {
		socket.send(((DatagramImpl) dgram).getDatagramPacket());
	}

	public void receive(Datagram dgram) throws IOException {
		socket.receive(((DatagramImpl) dgram).getDatagramPacket());
	}

	public Datagram newDatagram(int size) throws IOException {
		return newDatagram(size, address);
	}

	public Datagram newDatagram(int size, String addr) throws IOException {
		Datagram datagram = new DatagramImpl(size);
		datagram.setAddress(addr);
		return datagram;
	}

	public Datagram newDatagram(byte[] buf, int size) throws IOException {
		return newDatagram(buf, size, address);
	}

	public Datagram newDatagram(byte[] buf, int size, String addr) throws IOException {
		Datagram datagram = new DatagramImpl(buf, size);
		datagram.setAddress(addr);
		return datagram;
	}

	public String getLocalAddress() throws IOException {
		InetAddress address = socket.getInetAddress();
		if (address == null) {
			/*
			 * server mode we get the localhost from InetAddress otherwise we
			 * get '0.0.0.0'
			 */
			address = InetAddress.getLocalHost();
		} else {
			/*
			 * client mode we can get the localhost from the socket here
			 */
			address = socket.getLocalAddress();
		}
		return address.getHostAddress();
	}

	public int getLocalPort() throws IOException {
		return socket.getLocalPort();
	}

	public javax.microedition.io.Connection openConnection(String name, int mode, boolean timeouts) throws IOException {
		if (!org.microemu.cldc.http.Connection.isAllowNetworkConnection()) {
			throw new IOException("No network");
		}
		if (!name.startsWith(PROTOCOL)) {
			throw new IOException("Invalid Protocol " + name);
		}
		// TODO currently we ignore the mode
		address = name.substring(PROTOCOL.length());
		int port = -1;
		int index = address.indexOf(':');
		if (index == -1) {
			throw new IOException("port missing");
		}
		port = Integer.parseInt(address.substring(index + 1));
		if (index == 0) {
			// server mode
			socket = new DatagramSocket(port);
		} else {
			// client mode
			String host = address.substring(0, index);
			socket = new DatagramSocket();
			socket.connect(InetAddress.getByName(host), port);
		}
		return this;
	}
}
