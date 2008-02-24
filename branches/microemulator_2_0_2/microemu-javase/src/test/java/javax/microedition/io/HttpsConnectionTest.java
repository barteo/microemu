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