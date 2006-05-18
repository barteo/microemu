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
