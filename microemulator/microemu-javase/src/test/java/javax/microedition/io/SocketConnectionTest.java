/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *
 *  @version $Id$
 */
package javax.microedition.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SocketConnectionTest extends BaseGCFTestCase {

	private static final String loopbackHost = TEST_HOST;
	//private static final String loopbackHost = "localhost";
	
	private static final String loopbackPort = "9127";
	
	private static final String serverPort = "7127";
	
	
	private void runLoopbackTest(String url) throws IOException {
		System.out.println("Connecting to " + url);
		SocketConnection sc = (SocketConnection) Connector.open(url);
		
		try {
			sc.setSocketOption(SocketConnection.LINGER, 5);

			InputStream is = sc.openInputStream();
			OutputStream os = sc.openOutputStream();

			String testData = "OK\r\n"; 
			
			os.write(testData.getBytes());
			os.flush();
			
			StringBuffer buf = new StringBuffer(); 
			
			int ch = 0;
			int count = 0;
			while (ch != -1) {
				ch = is.read();
				buf.append((char)ch);
				count ++;
				if (count >= testData.length()) {
					break;
				}
			}

			assertEquals("Data received", buf.toString(), testData);
			
			is.close();
			os.close();
		} finally {
			sc.close();
		}

	}
	
	public void testLoopback() throws IOException {
		runLoopbackTest("socket://" + loopbackHost + ":" + loopbackPort);
	}
	
	private class ServerThread extends Thread {
		
		ServerSocketConnection scn;
		
		boolean started = true;
		
		boolean finished = true;
		
		ServerThread(ServerSocketConnection scn) {
			super.setDaemon(true);
			this.scn = scn;
		}
		
		public void run() {
			try {
				// Wait for a connection.
				SocketConnection sc = (SocketConnection) scn.acceptAndOpen();
				
				InputStream is = sc.openInputStream();
				OutputStream os = sc.openOutputStream();
				
				int ch = 0;
				int count = 0;
				while (ch != -1) {
					ch = is.read();
					if (ch == -1) {
						break;
					}
					os.write(ch);
					os.flush();
					count ++;
				}
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				finished = true;
			}
		}
	}
	
	static public void assertNotEquals(String message, String expected, String actual) {
		assertFalse(message + " [" + expected + " == "+ actual + "]", expected.equals(actual));
	}
	
	public void testServerSocketConnection() throws IOException, InterruptedException {
		ServerSocketConnection scn = (ServerSocketConnection) Connector.open("socket://:" + serverPort);

		try {
			ServerThread t = new ServerThread(scn);
			t.start();
			while (!t.started) {
				Thread.sleep(20);
			}
			assertEquals("Server Port", Integer.valueOf(serverPort).intValue(), scn.getLocalPort());
			assertNotEquals("Server Host", "0.0.0.0", scn.getLocalAddress());
			assertNotEquals("Server Host", "localhost", scn.getLocalAddress());
			//assertNotEquals("Server Host", "127.0.0.1", scn.getLocalAddress());
			
			runLoopbackTest("socket://" + scn.getLocalAddress() + ":" + scn.getLocalPort());
		} finally {
			scn.close();
		}
	}
}
