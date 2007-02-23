/**
 *  MicroEmulator
 *  Copyright (C) 2001,2002 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.cldc.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.microedition.io.HttpConnection;

import org.microemu.microedition.io.ConnectionImplementation;

public class Connection implements HttpConnection, ConnectionImplementation {
	
	protected URLConnection cn;

	protected boolean connected = false;

	public javax.microedition.io.Connection openConnection(String name, int mode, boolean timeouts) throws IOException {
		URL url;
		try {
			url = new URL(name);
		} catch (MalformedURLException ex) {
			throw new IOException(ex.toString());
		}
		cn = url.openConnection();
		cn.setDoOutput(true);

		return this;
	}

	public void close() throws IOException {
		if (cn == null) {
			return;
		}

		if (cn instanceof HttpURLConnection) {
			((HttpURLConnection) cn).disconnect();
		}

		cn = null;
	}

	public String getURL() {
		if (cn == null) {
			return null;
		}

		return cn.getURL().toString();
	}

	public String getProtocol() {
		return "http";
	}

	public String getHost() {
		if (cn == null) {
			return null;
		}

		return cn.getURL().getHost();
	}

	public String getFile() {
		if (cn == null) {
			return null;
		}

		return cn.getURL().getFile();
	}

	public String getRef() {
		if (cn == null) {
			return null;
		}

		return cn.getURL().getRef();
	}

	public String getQuery() {
		if (cn == null) {
			return null;
		}

		//    return cn.getURL().getQuery();
		return null;
	}

	public int getPort() {
		if (cn == null) {
			return -1;
		}

		int port = cn.getURL().getPort();
		if (port == -1) {
			return 80;
		}
		return port;
	}

	public String getRequestMethod() {
		if (cn == null) {
			return null;
		}

		if (cn instanceof HttpURLConnection) {
			return ((HttpURLConnection) cn).getRequestMethod();
		} else {
			return null;
		}
	}

	public void setRequestMethod(String method) throws IOException {
		if (cn == null) {
			throw new IOException();
		}

		if (method.equals(HttpConnection.POST)) {
			cn.setDoOutput(true);
		}

		if (cn instanceof HttpURLConnection) {
			((HttpURLConnection) cn).setRequestMethod(method);
		}
	}

	public String getRequestProperty(String key) {
		if (cn == null) {
			return null;
		}

		return cn.getRequestProperty(key);
	}

	public void setRequestProperty(String key, String value) throws IOException {
		if (cn == null || connected) {
			throw new IOException();
		}

		cn.setRequestProperty(key, value);
	}

	public int getResponseCode() throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		if (cn instanceof HttpURLConnection) {
			return ((HttpURLConnection) cn).getResponseCode();
		} else {
			return -1;
		}
	}

	public String getResponseMessage() throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		if (cn instanceof HttpURLConnection) {
			return ((HttpURLConnection) cn).getResponseMessage();
		} else {
			return null;
		}
	}

	public long getExpiration() throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		return cn.getExpiration();
	}

	public long getDate() throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		return cn.getDate();
	}

	public long getLastModified() throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		return cn.getLastModified();
	}

	public String getHeaderField(String name) throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		return cn.getHeaderField(name);
	}

	public int getHeaderFieldInt(String name, int def) throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		return cn.getHeaderFieldInt(name, def);
	}

	public long getHeaderFieldDate(String name, long def) throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		return cn.getHeaderFieldDate(name, def);
	}

	public String getHeaderField(int n) throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		return cn.getHeaderField(n);
	}

	public String getHeaderFieldKey(int n) throws IOException {
		if (cn == null) {
			throw new IOException();
		}
		if (!connected) {
			cn.connect();
			connected = true;
		}

		return cn.getHeaderFieldKey(n);
	}

	public InputStream openInputStream() throws IOException {
		if (cn == null) {
			throw new IOException();
		}

		connected = true;

		return cn.getInputStream();
	}

	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		if (cn == null) {
			throw new IOException();
		}

		connected = true;

		return cn.getOutputStream();
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}

	public String getType() {
		try {
			return getHeaderField("content-type");
		} catch (IOException ex) {
			return null;
		}
	}

	public String getEncoding() {
		try {
			return getHeaderField("content-encoding");
		} catch (IOException ex) {
			return null;
		}
	}

	public long getLength() {
		try {
			return getHeaderFieldInt("content-length", -1);
		} catch (IOException ex) {
			return -1;
		}
	}

}
