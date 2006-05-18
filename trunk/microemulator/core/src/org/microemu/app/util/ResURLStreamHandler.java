package org.microemu.app.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Hashtable;



public class ResURLStreamHandler extends URLStreamHandler {

	private Hashtable entries;
	
	public ResURLStreamHandler(Hashtable entries)
	{
		this.entries = entries;
	}
	
	protected URLConnection openConnection(URL url) throws IOException {
		return new ResURLConnection(url, entries);
	}

}
