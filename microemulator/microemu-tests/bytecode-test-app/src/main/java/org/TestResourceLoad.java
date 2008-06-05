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
package org;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class illustrate Resource usage paterns commonly used in MIDlet and not
 * aceptable in Java SE application
 * 
 * @author vlads
 */

public class TestResourceLoad implements Runnable {

	public void loadStringsUsingSystemClassLoaded() {
		String resourceName;
		String expected;

		resourceName = "/app-data.txt";
		expected = "private app-data";
		verifyLoadStrings("".getClass().getResourceAsStream(resourceName), "\"\".getClass() " + resourceName, expected);
		verifyLoadStrings(String.class.getResourceAsStream(resourceName), "String.class. " + resourceName, expected);

	}

	public void accessTest() {
		String resourceName;
		String expected;

		resourceName = "/container-internal.txt";
		verifyNotLoaddable(this.getClass().getResourceAsStream(resourceName), "this.getClass() " + resourceName);
		verifyNotLoaddable("".getClass().getResourceAsStream(resourceName), "\"\".getClass() " + resourceName);

		resourceName = "/app-data.txt";
		expected = "private app-data";
		verifyLoadStrings(this.getClass().getResourceAsStream(resourceName), "this.getClass() " + resourceName,
				expected);
		verifyLoadStrings("".getClass().getResourceAsStream(resourceName), "\"\".getClass() " + resourceName, expected);
	}

	public void multipleResources() {

		String resourceName = "/strings.txt";
		String expected = "proper MIDlet resources strings";
		verifyLoadStrings(this.getClass().getResourceAsStream(resourceName), "this.getClass() " + resourceName,
				expected);
		verifyLoadStrings("".getClass().getResourceAsStream(resourceName), "\"\".getClass() " + resourceName, expected);

	}

	public void packageResources() {

		String resourceName = "resource-package.txt";
		String expected = "package relative";
		verifyLoadStrings(TestResourceLoad.class.getResourceAsStream(resourceName), "this.class " + resourceName,
				expected);
	}

	private void verifyNotLoaddable(InputStream inputstream, String resourceName) {
		verifyLoadStrings(inputstream, resourceName, null);
	}

	private void verifyLoadStrings(InputStream inputstream, String resourceName, String expected) {
		if (inputstream == null) {
			if (expected == null) {
				if (TestMain.verbose) {
					System.out.println("OK - Resource not found " + resourceName);
				}
				return;
			}
			throw new RuntimeException("Resource not found " + resourceName);
		} else {
			if (expected == null) {
				throw new RuntimeException("Can access resource " + resourceName);
			}
		}
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(inputstream));
			String value = r.readLine();
			if (!expected.equals(value)) {
				throw new RuntimeException("Unexpected resource " + resourceName + " value [" + value + "]\nexpected ["
						+ expected + "]");
			}
		} catch (IOException e) {
			throw new RuntimeException("Resource read error " + resourceName, e);
		} finally {
			try {
				inputstream.close();
			} catch (IOException ignore) {
			}
		}
	}

	public void run() {

		if (TestMain.verbose) {
			System.out.println("ClassLoader " + this.getClass().getClassLoader().hashCode() + " TestResourceLoad");
		}

		loadStringsUsingSystemClassLoaded();
		multipleResources();
		accessTest();
		packageResources();

	}

}
