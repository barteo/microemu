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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class BaseTestHttpConnection extends BaseGCFTestCase {

	protected static final String testFile = "/robots.txt";

	protected static final String testServlet = "/pyx4me-test-server/";
	
	protected abstract HttpConnection openHttpConnection(String query) throws IOException;


    private String getData(HttpConnection hc) throws IOException {
        InputStream is = hc.openInputStream();
        StringBuffer buf = new StringBuffer();
        try {
            int ch;
            while ((ch = is.read()) != -1) {
                buf.append((char)ch);
            }
            return buf.toString();
        } catch (IOException e) {
            if ("socket closed".equals(e.getMessage())) {
                System.out.println("InputStream socket closed");
                return buf.toString();
            } else {
                throw e;
            }
        } finally {
            is.close();
        }
    }
    
    protected void assertHttpConnectionMethods(HttpConnection hc) throws IOException {
		assertEquals("getHost()", TEST_HOST, hc.getHost());
		assertEquals("getFile()", testFile, hc.getFile());
		assertEquals("getRequestMethod()", HttpConnection.GET, hc.getRequestMethod());
		String server = hc.getHeaderField("Server");
		assertTrue("getHeaderField()", ((server.indexOf("Apache") != -1) || (server.indexOf("Sun-ONE") != -1)) );
		Calendar year2005 = Calendar.getInstance();
		year2005.set(Calendar.YEAR, 2005);
		long past2005 = year2005.getTime().getTime();
		assertTrue("getDate()", hc.getDate() > past2005);
		assertTrue("getExpiration()", hc.getDate() > past2005);
		assertTrue("getLastModified()", hc.getDate() > past2005);
		assertTrue("getResponseMessage()", hc.getResponseMessage().endsWith("OK"));
    }
    
    public void testResponseCode() throws IOException {
    	HttpConnection hc = openHttpConnection(testFile + ".gone");
        try {
			assertEquals("getResponseCode()", HttpConnection.HTTP_NOT_FOUND, hc.getResponseCode());
		} finally {
			hc.close();
		}
    }
    
    public void testInputStream() throws IOException {
    	HttpConnection hc = openHttpConnection(testFile);
        try {
			assertEquals("getResponseCode()", HttpConnection.HTTP_OK, hc.getResponseCode());
			String data = getData(hc);
			assertTrue("data recived", data.startsWith("User-agent: *"));
		} finally {
			hc.close();
		}
    }

    private String getDataVIADataInputStream(DataInputStream is) throws IOException {
        StringBuffer buf = new StringBuffer();
        try {
            int ch;
            while ((ch = is.read()) != -1) {
                buf.append((char)ch);
            }
            return buf.toString();
        } catch (IOException e) {
            if ("socket closed".equals(e.getMessage())) {
                System.out.println("InputStream socket closed");
                return buf.toString();
            } else {
                throw e;
            }
        } finally {
            is.close();
        }
    }
    
    public void testDataInputStream() throws IOException {
    	HttpConnection hc = openHttpConnection(testFile);
        try {
			assertEquals("getResponseCode()", HttpConnection.HTTP_OK, hc.getResponseCode());
			String data = getDataVIADataInputStream(hc.openDataInputStream());
			assertTrue("data recived", data.startsWith("User-agent: *"));
		} finally {
			hc.close();
		}
    }
    
    public void testContentConnection() throws IOException {
    	HttpConnection hc = openHttpConnection(testFile);
        try {
			assertEquals("getResponseCode()", HttpConnection.HTTP_OK, hc.getResponseCode());
			ContentConnection c = hc;
			 int len = (int)c.getLength();
			 assertTrue("getLength()", len > 10);
			String data = getDataVIADataInputStream(hc.openDataInputStream());
			assertTrue("data recived", data.startsWith("User-agent: *"));
		} finally {
			hc.close();
		}
    }
    
    public void testHeaders() throws IOException {
		HttpConnection hc = openHttpConnection(testServlet + "header");
		try {
			hc.setRequestMethod(HttpConnection.POST);
			Hashtable r = new Hashtable();
			r.put("tkey1", "tvalue1");
			r.put("Test-Key-Val", "t-value-1");
			r.put("tkey2", "t; value: =/../");
			r.put("tkey3", "X=DQAAG4AADTwpw:yj=b4m4G9DWbK-es8VnRXfT1w:gmproxy=GUG9jB5Ph9g:gmyj=SRs7Qh_KFZc;");
			for (Enumeration e = r.keys(); e.hasMoreElements();) {
				String name = (String) e.nextElement();
				String value = (String) r.get(name);
				hc.setRequestProperty(name, value);
			}
			assertEquals(HttpConnection.HTTP_OK, hc.getResponseCode());

			for (Enumeration e = r.keys(); e.hasMoreElements();) {
				String name = (String) e.nextElement();
				String value = (String) r.get(name);
				assertEquals(value, hc.getHeaderField("Re-" + name));
			}

			assertEquals(HttpConnection.POST, hc.getHeaderField("test-method"));
			hc.close();

			// Test GET
			hc = openHttpConnection(testServlet + "header");
			hc.setRequestMethod(HttpConnection.GET);
			for (Enumeration e = r.keys(); e.hasMoreElements();) {
				String name = (String) e.nextElement();
				String value = (String) r.get(name);
				hc.setRequestProperty(name, value);
			}
			assertEquals(HttpConnection.HTTP_OK, hc.getResponseCode());
			for (Enumeration e = r.keys(); e.hasMoreElements();) {
				String name = (String) e.nextElement();
				String value = (String) r.get(name);
				assertEquals(value, hc.getHeaderField("Re-" + name));
			}

			assertEquals(HttpConnection.GET, hc.getHeaderField("test-method"));
		} finally {
			hc.close();
		}
	}
    
    private String sendData(HttpConnection hc) throws IOException {
        String msg = "Sending\n " + " Some$$Data\r\n\rend";
        hc.setRequestProperty("X-Wap-Proxy-Cookie", "none");
        hc.setRequestProperty("Cache-Control", "no-cache, no-transform");
        hc.setRequestProperty("Content-Type", "application/binary");
        hc.setRequestProperty("Content-Length", "" + (msg.getBytes().length));
        hc.setRequestProperty("User-agent", "UNTRUSTED/1.0");
        hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        hc.setRequestProperty("Test-data-crc", String.valueOf(msg.hashCode()));
        OutputStream os = hc.openOutputStream();
        try {
            os.write(msg.getBytes());
        } finally {
            os.close();
        }
        return msg;
    }
    
    public void testBody() throws IOException {
		String msg;
		HttpConnection hc = openHttpConnection(testServlet + "body");
		try {
			msg = sendData(hc);
			assertEquals(HttpConnection.HTTP_OK, hc.getResponseCode());
			assertNull("No Data error", hc.getHeaderField("Test-data-crc-error"));
			assertEquals("Data crc", String.valueOf(msg.hashCode()), hc.getHeaderField("Test-data-crc"));
			assertEquals(msg, getData(hc));
			hc.close();

			hc = openHttpConnection(testServlet + "body");
			msg = sendData(hc);
			assertEquals(HttpConnection.HTTP_OK, hc.getResponseCode());
			assertNull("No Data error", hc.getHeaderField("Test-data-crc-error"));
			assertEquals("Data crc", String.valueOf(msg.hashCode()), hc.getHeaderField("Test-data-crc"));
			assertEquals(msg, getData(hc));
		} finally {
			hc.close();
		}
	}
}
