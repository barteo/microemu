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
package org.microemu.app.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.microemu.log.Logger;


/**
 * @author vlads
 * 
 * This class allow redirection of stdout and stderr from MIDlet to MicroEmulator logger console
 * 
 */
public class MIDletOutputStreamRedirector extends PrintStream {

	public final static PrintStream out = outPrintStream();

	public final static PrintStream err = errPrintStream();

	static {
		Logger.addLogOrigin(MIDletOutputStreamRedirector.class);
		Logger.addLogOrigin(OutputStream2Log.class);
	}
	
	private static class OutputStream2Log extends OutputStream {

		boolean isErrorStream;
		
		StringBuffer buffer = new StringBuffer();

		OutputStream2Log(boolean error) {
			this.isErrorStream = error;
		}

		public void write(int b) throws IOException {
			if ((b == '\n') || (b == '\r')) {
				if (buffer.length() > 0) {
					if (isErrorStream) {
						Logger.error(buffer.toString());
					} else {
						Logger.info(buffer.toString());
					}
					buffer = new StringBuffer();
				}
			} else {
				buffer.append((char) b);
			}
		}

	}

	private MIDletOutputStreamRedirector(boolean error) {
		super(new OutputStream2Log(error));
	}

	private static PrintStream outPrintStream() {
		return new MIDletOutputStreamRedirector(false);
	}

	private static PrintStream errPrintStream() {
		return new MIDletOutputStreamRedirector(true);
	}

	//Override methods to be able to get proper stack trace

	public void print(boolean b) {
		super.print(b);
	}

	public void print(char c) {
		super.print(c);
	}

	public void print(char[] s) {
		super.print(s);
	}

	public void print(double d) {
		super.print(d);
	}

	public void print(float f) {
		super.print(f);
	}

	public void print(int i) {
		super.print(i);
	}

	public void print(long l) {
		super.print(l);
	}

	public void print(Object obj) {
		super.print(obj);
	}

	public void print(String s) {
		super.print(s);
	}

	public void println() {
		super.println();
	}

	public void println(boolean x) {
		super.println(x);
	}

	public void println(char x) {
		super.println(x);
	}

	public void println(char[] x) {
		super.println(x);
	}

	public void println(double x) {
		super.println(x);
	}

	public void println(float x) {
		super.println(x);
	}

	public void println(int x) {
		super.println(x);
	}

	public void println(long x) {
		super.println(x);
	}

	public void println(Object x) {
		super.println(x);
	}

	public void println(String x) {
		super.println(x);
	}

	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len);
	}

	public void write(int b) {
		super.write(b);
	}
	
}
