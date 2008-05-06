/**
 *  MicroEmulator
 *  Copyright (C) 2001-2008 Bartek Teodorczyk <barteo@barteo.net>
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

import java.io.IOException;
import java.io.InputStream;

/**
 * Special InputStream wrapper for loading resources is needed to change
 * behavior of read(byte[] b) under Java SE. All existing Java ME
 * implementations always read all bytes available in one read call.
 */
public class MIDletResourceInputStream extends InputStream {

	private InputStream is;

	public MIDletResourceInputStream(InputStream is) {
		this.is = is;
	}

	public int available() throws IOException {
		return is.available();
	}

	public int read() throws IOException {
		return is.read();
	}

	public int read(byte[] b) throws IOException {
		int result = 0;
		int count = 0;
		do {
			count = is.read(b, result, b.length - result);
			if (count != -1) {
				result += count;
				if (result == b.length) {
					return result;
				}
			}
		} while (count != -1);

		return -1;
	}

}
