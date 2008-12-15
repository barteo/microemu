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
