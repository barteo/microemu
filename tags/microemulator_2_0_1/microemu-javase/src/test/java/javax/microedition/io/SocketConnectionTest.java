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
package javax.microedition.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SocketConnectionTest extends BaseGCFTestCase {

	private static final String loopbackHost = TEST_HOST;
	//private static final String loopbackHost = "localhost";
	
	private static final String loopbackPort = "9127";
	
	
	public void testLoopback() throws IOException {
		
		SocketConnection sc = (SocketConnection) Connector.open("socket://" + loopbackHost + ":" + loopbackPort);
		
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
		sc.close();

	}
}
