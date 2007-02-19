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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Stack;

import nanoxml.XMLElement;

public class MRUList extends Stack/*<XMLItem>*/ {

	private static final long serialVersionUID = 1L;
	
	protected int maxCapacity;
	
	private String itemsName;
	
	private Class classXMLItem;
	
	public MRUList(Class classXMLItem, String itemsName) {
		this.classXMLItem = classXMLItem;
		this.itemsName = itemsName;
	}
	
	public Object push(Object item) {
		if (!(item instanceof XMLItem)) {
			throw new ClassCastException(item.getClass().getName());
		}
		if (size() > maxCapacity) {
			pop();
		}
		remove(item);
		return super.push(item);
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public XMLElement save() {
		XMLElement xml = new XMLElement();
		xml.setAttribute("maxCapacity", String.valueOf(maxCapacity));
		for (Iterator iter = this.iterator(); iter.hasNext();) {
			XMLItem element = (XMLItem) iter.next();
			XMLElement xmlChild = element.save();
			xmlChild.setName(itemsName);
			xml.addChild(xmlChild);
		}
		return xml;
	}
	
	public void read(XMLElement xml) {
		this.maxCapacity = xml.getIntAttribute("maxCapacity", 10);
		for (Enumeration en = xml.enumerateChildren(); en.hasMoreElements(); ) {
            XMLElement xmlChild = (XMLElement) en.nextElement();
            if (xmlChild.getName().equals(itemsName)) {
				try {
					XMLItem element = (XMLItem)classXMLItem.newInstance();
					add(element);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
            }
        }
	}

}
