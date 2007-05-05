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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import nanoxml.XMLElement;

import org.microemu.log.Logger;

/**
 * @author vlads
 *
 */
public class MidletURLReference implements XMLItem {


	private String name;
	
	private String url;

	public MidletURLReference() {
		super();
	}
	
	/**
	 * @param name MIDlet name
	 * @param url  URL to locate this URL
	 */
	public MidletURLReference(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}

	public boolean equals(Object obj) {
		 if (!(obj instanceof MidletURLReference)) {
			 return false;
		 }
		 return ((MidletURLReference)obj).url.equals(url); 
	}
	 
	public String toString() {
		// make the text presentation shorter.
		URL u;
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
			Logger.error(e);
			return url;
		}
		StringBuffer b = new StringBuffer();
		
		String scheme = u.getProtocol();
		if (scheme.equals("file") || scheme.startsWith("http")) {
			b.append(scheme).append("://");
			if (u.getHost() != null) {
				b.append(u.getHost());
			}
			Vector pathComponents = new Vector();
			final String pathSeparator = "/";
			StringTokenizer st = new StringTokenizer(u.getPath(), pathSeparator);
			while (st.hasMoreTokens()) {
				pathComponents.add(st.nextToken());
			}
			if (pathComponents.size() > 3) {
				b.append(pathSeparator);
				b.append(pathComponents.get(0));
				b.append(pathSeparator).append("...").append(pathSeparator);
				b.append(pathComponents.get(pathComponents.size()-2));
				b.append(pathSeparator);
				b.append(pathComponents.get(pathComponents.size()-1));
			} else {
				b.append(u.getPath());
			}
			
		} else {
			b.append(url);
		}
		if (name != null) {
			b.append(" - ");
			b.append(name);
		}
		return b.toString();
	}
	
	public void read(XMLElement xml) {
		name = xml.getChildString("name", "");
		url = xml.getChildString("url", "");
	}

	public void save(XMLElement xml) {
		xml.removeChildren();
		xml.addChild("name", name);
		xml.addChild("url", url);
	}

	public String getName() {
		return this.name;
	}

	public String getUrl() {
		return this.url;
	}
	
}
