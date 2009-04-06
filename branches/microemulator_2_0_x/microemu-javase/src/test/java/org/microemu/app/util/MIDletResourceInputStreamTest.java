/**
 *  MicroEmulator
 *  Copyright (C) 2006-2008 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2008 Vlad Skarzhevskyy
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
package org.microemu.app.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author vlads
 * 
 */
public class MIDletResourceInputStreamTest extends TestCase {

	private void setData(byte data[]) {
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) i;
		}
	}

	public void testReadPart() throws IOException {
		int max = 10;
		byte data[] = new byte[max];
		setData(data);
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		MIDletResourceInputStream mis = new MIDletResourceInputStream(is);
		byte data2[] = new byte[max + 5];
		int rc = mis.read(data2);
		assertEquals("read part", max, rc);
	}

}
