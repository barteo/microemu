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
