/*
 *  MicroEmulator
 *  Copyright (C) 2001-2006 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package org.microemu.app.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

public class ResURLConnection extends URLConnection {
	
	private static final String PREFIX = "res:";

	private Hashtable entries;
	
	protected ResURLConnection(URL url, Hashtable entries) {
		super(url);
		
		this.entries = entries;
	}

	public void connect() throws IOException {
	}

	public InputStream getInputStream() throws IOException {
		String location = url.toString();
		int idx = location.indexOf(PREFIX);
		if (idx == -1) {
			throw new IOException();
		}
		location = location.substring(idx + PREFIX.length());
		byte[] data = (byte[]) entries.get(location);
		if (data == null) {
			throw new IOException();
		}
		return new ByteArrayInputStream(data);
	}
	
}
