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

import nanoxml.XMLElement;

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
		//TODO make the text presentation shorter.
		return name + " " + url;
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
