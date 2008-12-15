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

import javax.microedition.pki.Certificate;

public class HttpsConnectionTest extends BaseTestHttpConnection {
	
	private static final String testInetHTTPUrl = "https://" + TEST_HOST + testFile;
	
	protected void setUp() throws Exception {
		super.setUp();
		super.setupSSLContext();
	}
	
    protected HttpConnection openHttpConnection(String query) throws IOException {
    	return (HttpsConnection)Connector.open("https://" + TEST_HOST + query, Connector.READ, true);
    }
    
    public void testConnection() throws IOException {
    	HttpsConnection hc = (HttpsConnection)Connector.open(testInetHTTPUrl, Connector.READ, true);
        try {
			assertEquals("getResponseCode()", HttpConnection.HTTP_OK, hc.getResponseCode());
			assertEquals("getPort()", 443, hc.getPort());
			assertEquals("getProtocol()", "https", hc.getProtocol());
			assertEquals("getURL()", testInetHTTPUrl, hc.getURL());
			assertHttpConnectionMethods(hc);
		} finally {
			hc.close();
		}
    }
    
    public void testSecurityInfo() throws IOException {
    	HttpsConnection hc = (HttpsConnection)Connector.open(testInetHTTPUrl, Connector.READ, true);
        try {
			assertEquals("getResponseCode()", HttpConnection.HTTP_OK, hc.getResponseCode());
			SecurityInfo si = hc.getSecurityInfo();
			assertNotNull("HttpsConnection.getSecurityInfo()", si);
			assertNotNull("SecurityInfo.getProtocolVersion()", si.getProtocolVersion());
			assertNotNull("SecurityInfo.getProtocolName()", si.getProtocolName());
			assertNotNull("SecurityInfo.getCipherSuite()", si.getCipherSuite());
			Certificate cert = si.getServerCertificate(); 
			assertNotNull("SecurityInfo.getServerCertificate()", cert);
			assertNotNull("Certificate.getSubject()", cert.getSubject());
			assertNotNull("Certificate.getIssuer()", cert.getIssuer());
			assertNotNull("Certificate.getType()", cert.getType());
			assertNotNull("Certificate.getVersion()", cert.getVersion());
			assertNotNull("Certificate.getSigAlgName()", cert.getSigAlgName());
			assertTrue("Certificate.getNotBefore()", cert.getNotBefore() >= 0);
			assertTrue("Certificate.getNotAfter()", cert.getNotAfter() >= 0);
			String serialNumber = cert.getSerialNumber();
			assertNotNull("Certificate.getSerialNumber()", serialNumber);
		} finally {
			hc.close();
		}
    }
    
    public void testWrongCertificate() throws IOException {
    	HttpsConnection hc = (HttpsConnection)Connector.open("https://www.pyx4me.com" + testFile, Connector.READ, true);
        try {
        	// Produce java.io.IOException: HTTPS hostname wrong:  should be <www.pyx4me.com>
        	hc.getResponseCode();
        	fail("Should produce IOException");
        } catch (IOException e) {
		} finally {
			hc.close();
		}
    }
}