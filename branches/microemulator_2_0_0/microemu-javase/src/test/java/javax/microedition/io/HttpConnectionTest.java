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

public class HttpConnectionTest extends BaseTestHttpConnection {

	private static final String testInetHTTPUrl = "http://" + TEST_HOST + testFile;

    public void testConnection() throws IOException {
    	HttpConnection hc = (HttpConnection)Connector.open(testInetHTTPUrl, Connector.READ, true);
        try {
			assertEquals("getResponseCode()", HttpConnection.HTTP_OK, hc.getResponseCode());
			assertEquals("getPort()", 80, hc.getPort());
			assertEquals("getProtocol()", "http", hc.getProtocol());
			assertEquals("getURL()", testInetHTTPUrl, hc.getURL());
			assertHttpConnectionMethods(hc);
		} finally {
			hc.close();
		}
    }
    
    protected HttpConnection openHttpConnection(String query) throws IOException {
    	return (HttpConnection)Connector.open("http://" + TEST_HOST + query, Connector.READ, true);
    }
}
