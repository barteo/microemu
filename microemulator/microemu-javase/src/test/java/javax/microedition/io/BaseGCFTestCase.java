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

import org.microemu.SSLContextSetup;
import org.microemu.TestEnvPropertiesHelper;

import junit.framework.TestCase;

/**
 * @author vlads
 *
 */
public class BaseGCFTestCase extends TestCase {

	/**
	 * This is the server where I keep HTTPS and Socket test response server.
	 * If change add server to test-servers.keystore if it self-signed certificate.
	 * 
	 * War sources here https://pyx4j.com/svn/pyx4me/pyx4me-host/pyx4me-test-server
	 * 
	 */
	public static final String TEST_HOST = findNoProxyTestHost();

	/**
	 * File  ${home}/.microemulator/tests.properties is used for configuration
	 * @return
	 */
	private static String findNoProxyTestHost() {
		return TestEnvPropertiesHelper.getProperty("gcf.no-proxy-test-host", "pyx4j.com");
	}

	/**
	 * TODO Support proxy configuration.
	 */
	protected void setUp() throws Exception {
		// Some tests may run via proxy but not all.
		if (System.getProperty("http.proxyHost") == null) {
			//System.setProperty("http.proxyHost", "genproxy");
			//System.setProperty("http.proxyPort", String.valueOf(8900));
		}
	}

	/**
	 * Trust our self-signed test-servers
	 */
	protected void setupSSLContext() {
	    SSLContextSetup.setUp();
	}
}
