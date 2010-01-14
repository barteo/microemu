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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.pki.Certificate;

public class SSLConnectionTest extends BaseGCFTestCase {

	private static final String TEST_PORT = "443";
	
	public static final String CRLF = "\r\n";
	
	
	private void write(OutputStream os, String txt) throws IOException {
		os.write(txt.getBytes());
		os.write(CRLF.getBytes());
	}
	
	private void validateHTTPReply(InputStream is) throws IOException {
		StringBuffer buf = new StringBuffer();
		int ch, ch1 = 0;
		while ((ch = is.read()) != -1) {
			if ((ch == '\n') && (ch1 == '\r')) {
				break;
			}
			buf.append((char) ch);
			ch1 = ch;
		}
		if ((!buf.toString().startsWith("HTTP/1.0 200")) &&
		     (!buf.toString().startsWith("HTTP/1.1 200"))){
			buf.append("[EOF]");
			throw new IOException(buf.toString());
		}
		if (ch == -1) {
			throw new EOFException("No http header from proxy");
		}
		int crCount = 0;
		while ((ch = is.read()) != -1) {
			if ((ch == '\n') && (ch1 == '\r')) {
				crCount++;
				if (crCount == 2) {
					break;
				}
			} else if (ch != '\r') {
				crCount = 0;
			}
			ch1 = ch;
		}
		if (ch == -1) {
			throw new EOFException("No data after http header from proxy");
		}
	}
	
	public void testSecureConnection() throws IOException {
		// Basicaly Performs HTTPS GET
		SecureConnection sc = (SecureConnection) Connector.open("ssl://" + TEST_HOST + ":" + TEST_PORT);
		
		sc.setSocketOption(SocketConnection.LINGER, 5);

		OutputStream os = sc.openOutputStream();
		write(os, "GET /robots.txt HTTP/1.0");
		write(os, "User-Agent: UNTRUSTED/1.0");
		write(os, "Host: " + TEST_HOST);
		os.write(CRLF.getBytes());
		os.flush();
		
		InputStream is = sc.openInputStream();
		validateHTTPReply(is);
		sc.close();
	}

    public void testSecurityInfo() throws IOException {
    	SecureConnection sc = (SecureConnection) Connector.open("ssl://" + TEST_HOST + ":" + TEST_PORT);
        try {
			SecurityInfo si = sc.getSecurityInfo();
			assertNotNull("SecureConnection.getSecurityInfo()", si);
			assertNotNull("SecurityInfo.getProtocolVersion()", si.getProtocolVersion());
			assertNotNull("SecurityInfo.getProtocolName()", si.getProtocolName());
			assertNotNull("SecurityInfo.getCipherSuite()", si.getCipherSuite());
			Certificate cert = si.getServerCertificate(); 
			assertNotNull("SecurityInfo.getServerCertificate()", cert);
			//TODO assertNotNull("Certificate.getSubject()", cert.getSubject());
			assertNotNull("Certificate.getIssuer()", cert.getIssuer());
			assertNotNull("Certificate.getType()", cert.getType());
			assertNotNull("Certificate.getVersion()", cert.getVersion());
			//TODO assertNotNull("Certificate.getSigAlgName()", cert.getSigAlgName());
			//TODO assertTrue("Certificate.getNotBefore()", cert.getNotBefore() >= 0);
			//TODO assertTrue("Certificate.getNotAfter()", cert.getNotAfter() >= 0);
			//TODO String serialNumber = cert.getSerialNumber();
			//TODO assertNotNull("Certificate.getSerialNumber()", serialNumber);
		} finally {
			sc.close();
		}
    }
}
