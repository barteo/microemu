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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.UDPDatagramConnection;

import junit.framework.TestCase;

/**
 * Test end to end communication over datagram.
 */
public class EndToEndDatagramTest extends TestCase {

	private static final String MESSAGE = "Hello World!";

	private static final String HOST = "localhost";

	private static final int PORT = 1234;

	private static final int COUNT = 10;

	private static final String UTF8 = "UTF-8";

	private static final int BUFFER_SIZE = 1024;

	/**
	 * Tests connection from J2SE datagram socket to J2ME datagram connection
	 */
	public void testJ2SEtoJ2ME() throws Exception {
		System.out.println("\n--------------------------------------------\n");
		createJ2SEServer().start();
		Thread.sleep(100);
		Thread clientThread = createJ2MEClient(false);
		clientThread.start();
		// wait for client thread to complete
		clientThread.join();
		System.out.println("\n--------------------------------------------\n");
	}

	/**
	 * Tests connection from J2ME datagram connection to J2SE datagram socket
	 */
	public void testJ2MEtoJ2SE() throws Exception {
		System.out.println("\n--------------------------------------------\n");
		createJ2MEServer(false).start();
		Thread.sleep(100);
		Thread clientThread = createJ2SEClient();
		clientThread.start();
		// wait for client thread to complete
		clientThread.join();
		System.out.println("\n--------------------------------------------\n");
	}

	/**
	 * Tests connection from J2ME datagram connection to J2ME datagram
	 * connection
	 */
	public void testJ2MEtoJ2ME() throws Exception {
		System.out.println("\n--------------------------------------------\n");
		createJ2MEServer(true).start();
		Thread.sleep(100);
		Thread clientThread = createJ2MEClient(true);
		clientThread.start();
		// wait for client thread to complete
		clientThread.join();
		System.out.println("\n--------------------------------------------\n");
	}

	/**
	 * Tests connection from J2SE datagram socket to J2SE datagram socket
	 */
	public void testJ2SEtoJ2SE() throws Exception {
		System.out.println("\n--------------------------------------------\n");
		createJ2SEServer().start();
		Thread.sleep(100);
		Thread clientThread = createJ2SEClient();
		clientThread.start();
		// wait for client thread to complete
		clientThread.join();
		System.out.println("\n--------------------------------------------\n");
	}

	private Thread createJ2MEServer(final boolean useDataStream) throws Exception {
		// create server connection
		final UDPDatagramConnection serverConn = (UDPDatagramConnection) Connector.open(Connection.PROTOCOL + ":"
				+ PORT, Connector.READ_WRITE);
		final int[] counter = { 0 };
		return new Thread(new Runnable() {
			public void run() {
				try {
					Datagram request = serverConn.newDatagram(BUFFER_SIZE);
					for (int i = 0; i < COUNT; i++) {
						request.reset();
						// wait for message to be received
						System.out.println("Server (J2ME): Waiting for request...");
						serverConn.receive(request);
						System.out.println("Server (J2ME): Request received from " + request.getAddress());
						// check its integrity
						if (useDataStream) {
							assertEquals(MESSAGE, request.readUTF());
						} else {
							assertEquals(MESSAGE, new String(request.getData(), 0, request.getLength(), UTF8));
						}
						// and respond
						Datagram response = serverConn.newDatagram(BUFFER_SIZE);
						if (useDataStream) {
							response.writeUTF(MESSAGE);
						} else {
							byte[] data = MESSAGE.getBytes(UTF8);
							response.setData(data, 0, data.length);
						}
						response.setAddress(request);
						serverConn.send(response);
						System.out.println("Server (J2ME): Datagram sent to " + response.getAddress());
						counter[0]++;
					}
					assertEquals(COUNT, counter[0]);
				} catch (Exception e) {
					fail("Error in server thread" + e.toString());
					e.printStackTrace();
				} finally {
					try {
						serverConn.close();
					} catch (IOException e) {
						fail("Unable to close server connection: " + e.toString());
					}
				}
			}
		}, "J2ME Server Thread");
	}

	private Thread createJ2MEClient(final boolean useDataStream) throws Exception {
		// create client connection
		final UDPDatagramConnection clientConn = (UDPDatagramConnection) Connector.open(Connection.PROTOCOL + HOST
				+ ":" + PORT, Connector.READ_WRITE);
		final int[] counter = { 0 };
		return new Thread(new Runnable() {
			public void run() {
				try {
					Datagram request = clientConn.newDatagram(BUFFER_SIZE);
					for (int i = 0; i < COUNT; i++) {
						request.reset();
						// send the request to the server
						if (useDataStream) {
							request.writeUTF(MESSAGE);
						} else {
							byte[] data = MESSAGE.getBytes(UTF8);
							request.setData(data, 0, data.length);
						}
						clientConn.send(request);
						System.out.println("Client: Request sent to " + request.getAddress());
						// then wait for the response
						Datagram response = clientConn.newDatagram(BUFFER_SIZE);
						System.out.println("Client: Waiting for response...");
						clientConn.receive(response);
						System.out.println("Client: Response received from " + request.getAddress());
						if (useDataStream) {
							assertEquals(MESSAGE, response.readUTF());
						} else {
							assertEquals(MESSAGE, new String(response.getData(), 0, request.getLength(), UTF8));
						}
						counter[0]++;
					}
					assertEquals(COUNT, counter[0]);
				} catch (Exception e) {
					fail("Error in client thread" + e.toString());
					e.printStackTrace();
				} finally {
					try {
						clientConn.close();
					} catch (IOException e) {
						fail("Unable to close client connection: " + e.toString());
						e.printStackTrace();
					}
				}
			}
		}, "J2ME Client Thread");
	}

	private Thread createJ2SEServer() throws Exception {
		// create server connection
		final DatagramSocket serverConn = new DatagramSocket(PORT);
		final int[] counter = { 0 };
		return new Thread(new Runnable() {
			public void run() {
				try {
					DatagramPacket request;
					for (int i = 0; i < COUNT; i++) {
						request = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
						// wait for message to be received
						System.out.println("Server (J2SE): Waiting for request...");
						serverConn.receive(request);
						System.out.println("Server (J2SE): Request received from " + request.getAddress() + ":"
								+ request.getPort());
						// check its integrity
						assertEquals(MESSAGE, new String(request.getData(), 0, request.getLength(), UTF8));
						// and respond
						DatagramPacket response = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
						byte[] data = MESSAGE.getBytes(UTF8);
						response.setData(data, 0, data.length);
						response.setAddress(request.getAddress());
						response.setPort(request.getPort());
						serverConn.send(response);
						System.out.println("Server (J2SE): Datagram sent to " + response.getAddress());
						counter[0]++;
					}
					assertEquals(COUNT, counter[0]);
				} catch (Exception e) {
					fail("Error in server thread" + e.toString());
					e.printStackTrace();
				} finally {
					serverConn.close();
				}
			}
		}, "J2SE Server Thread");
	}

	private Thread createJ2SEClient() throws Exception {
		// create client connection
		final DatagramSocket clientConn = new DatagramSocket();
		clientConn.connect(InetAddress.getByName(HOST), PORT);
		final int[] counter = { 0 };
		return new Thread(new Runnable() {
			public void run() {
				try {
					DatagramPacket request;
					// wait for message to be received
					for (int i = 0; i < COUNT; i++) {
						byte[] data = MESSAGE.getBytes(UTF8);
						request = new DatagramPacket(data, data.length);
						request.setAddress(InetAddress.getByName(HOST));
						request.setPort(PORT);
						// send the request to the server
						clientConn.send(request);
						System.out.println("Client (J2SE): Request sent to " + request.getAddress() + ":"
								+ request.getPort());
						// then wait for the response
						DatagramPacket response = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
						System.out.println("Client (J2SE): Waiting for response...");
						clientConn.receive(response);
						System.out.println("Client (J2SE): Response received from " + request.getAddress() + ":"
								+ request.getPort());
						assertEquals(MESSAGE, new String(response.getData(), 0, response.getLength(), UTF8));
						counter[0]++;
					}
					assertEquals(COUNT, counter[0]);
				} catch (Exception e) {
					fail("Error in client thread" + e.toString());
					e.printStackTrace();
				} finally {
					clientConn.close();
				}
			}
		}, "J2SE Client Thread");
	}
}
