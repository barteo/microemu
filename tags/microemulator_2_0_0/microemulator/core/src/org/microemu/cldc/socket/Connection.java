/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.cldc.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.microedition.io.SocketConnection;

import org.microemu.cldc.ClosedConnection;

public class Connection implements SocketConnection, ClosedConnection {
	
	protected Socket socket;

	private int delay;
	
	private int linger;
	
	private int keepalive;
	
	private int rcvbuf;
	
	private int sndbuf;

	public Connection() {
		delay = -1;
		linger = -1;
		keepalive = -1;
		rcvbuf = -1;
		sndbuf = -1;
	}

	public javax.microedition.io.Connection open(String name)
			throws IOException {
		int portSepIndex = name.lastIndexOf(':');
		int port = Integer.parseInt(name.substring(portSepIndex + 1));
		String host = name.substring("socket://".length(), portSepIndex);

		socket = new Socket(host, port);
		
		updateSocketOptions();

		return this;
	}

	public void close() throws IOException {
		if (socket == null) {
			return;
		}

		socket.close();
	}

	public InputStream openInputStream() throws IOException {
		if (socket == null) {
			throw new IOException();
		}

		return socket.getInputStream();
	}

	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		if (socket == null) {
			throw new IOException();
		}

		return socket.getOutputStream();
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}

	public String getAddress() throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException();
		}
		
		return socket.getInetAddress().toString();
	}

	public String getLocalAddress() throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException();
		}
		
		return socket.getLocalAddress().toString();
	}

	public int getLocalPort() throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException();
		}
		
		return socket.getLocalPort();
	}

	public int getPort() throws IOException {
		if (socket == null || socket.isClosed()) {
			throw new IOException();
		}
		
		return socket.getPort();
	}

	public int getSocketOption(byte option) throws IllegalArgumentException,
			IOException {
		if (socket != null && socket.isClosed()) {
			throw new IOException();
		}
		switch (option) {
			case DELAY:
				if (socket != null) {
					if (socket.getTcpNoDelay()) {
						return 1;
					} else {
						return 0;
					}
				} else {
					return delay;
				}
			case LINGER:
				if (socket != null) {
					int value = socket.getSoLinger();
					if (value == -1) {
						return 0;
					} else {
						return value;
					}
				} else {
					return linger;
				}
			case KEEPALIVE:
				if (socket != null) {
					if (socket.getKeepAlive()) {
						return 1;
					} else {
						return 0;
					}
				} else {
					return keepalive;
				}
			case RCVBUF:
				if (socket != null) {
					return socket.getReceiveBufferSize();
				} else {
					return rcvbuf;
				}
			case SNDBUF:
				if (socket != null) {
					return socket.getSendBufferSize();
				} else {
					return sndbuf;
				}
			default:
				throw new IllegalArgumentException();
		}
	}

	public void setSocketOption(byte option, int value)
			throws IllegalArgumentException, IOException {
		if (socket != null && socket.isClosed()) {
			throw new IOException();
		}
		switch (option) {
			case DELAY:
				if (value == 0) {
					delay = 0;
				} else {
					delay = 1;
				}
				if (socket != null) {
					socket.setTcpNoDelay(delay == 0 ? false : true);
				}
				break;
			case LINGER:
				if (value < 0) {
					throw new IllegalArgumentException();
				} else {
					linger = value;
				}
				if (socket != null) {
					socket.setSoLinger(linger == 0 ? false : true, linger);
				}
				break;
			case KEEPALIVE:
				if (value == 0) {
					keepalive = 0;
				} else {
					keepalive = 1;
				}
				if (socket != null) {
					socket.setKeepAlive(keepalive == 0 ? false : true);
				}
				break;
			case RCVBUF:
				if (value <= 0) {
					throw new IllegalArgumentException();
				} else {
					rcvbuf = value;
				}
				if (socket != null) {
					socket.setReceiveBufferSize(rcvbuf);
				}
				break;
			case SNDBUF:
				if (value <= 0) {
					throw new IllegalArgumentException();
				} else {
					sndbuf = value;
				}
				if (socket != null) {
					socket.setSendBufferSize(sndbuf);
				}
				break;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	protected void updateSocketOptions() throws SocketException {
		if (delay != -1) {
			socket.setTcpNoDelay(delay == 0 ? false : true);
		}
		if (linger != -1) {
			socket.setSoLinger(linger == 0 ? false : true, linger);
		}
		if (keepalive != -1) {
			socket.setKeepAlive(keepalive == 0 ? false : true);
		}
		if (rcvbuf != -1) {
			socket.setReceiveBufferSize(rcvbuf);
		}
		if (sndbuf != -1) {
			socket.setSendBufferSize(sndbuf);
		}
	}

}
