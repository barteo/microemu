/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 MicroEmulator Team.
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
package org.microemu;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author vlads
 *
 */
public class OutputStreamRedirector extends PrintStream {

	public final static PrintStream out = outPrintStream();

	public final static PrintStream err = errPrintStream();

	private static class OutputStream2Log extends OutputStream {

		StringBuffer buffer = new StringBuffer();

		OutputStream2Log(boolean error) {

		}

		public void write(int b) throws IOException {
			if ((b == '\n') || (b == '\r')) {
				if (buffer.length() > 0) {
					System.out.println("redirected:[" + buffer.toString() + "]");
					buffer = new StringBuffer();
				}
			} else {
				buffer.append((char) b);
			}
		}

	}

	private OutputStreamRedirector(boolean error) {
		super(new OutputStream2Log(error));
	}

	private static PrintStream outPrintStream() {
		return new OutputStreamRedirector(false);
	}

	private static PrintStream errPrintStream() {
		return new OutputStreamRedirector(true);
	}
}
