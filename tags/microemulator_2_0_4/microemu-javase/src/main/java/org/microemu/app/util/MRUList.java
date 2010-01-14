/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

import nanoxml.XMLElement;

/**
 * 
 * Most Recently Used (MRU) list
 * 
 * @author vlads
 */
public class MRUList implements XMLItem {

	private static final long serialVersionUID = 1L;

	private static final int MAXCAPACITY_DEFAULT = 10;

	protected int maxCapacity = MAXCAPACITY_DEFAULT;

	private Stack items = new Stack/* <XMLItem> */();

	private String itemsName;

	private Class classXMLItem;

	private MRUListListener listener;

	private boolean modified = true;

	public MRUList(Class classXMLItem, String itemsName) {
		this.classXMLItem = classXMLItem;
		this.itemsName = itemsName;
	}

	public Object push(Object item) {
		if (!(item instanceof XMLItem)) {
			throw new ClassCastException(item.getClass().getName());
		}
		modified = true;
		if (items.size() > maxCapacity) {
			items.pop();
		}
		items.remove(item);
		if (items.empty()) {
			items.add(item);
		} else {
			items.insertElementAt(item, 0);
		}
		fireListener(item);
		return item;
	}

	private void fireListener(Object item) {
		if (this.listener != null) {
			this.listener.listItemChanged(item);
		}

	}

	public void setListener(MRUListListener l) {
		if (this.listener != null) {
			throw new IllegalArgumentException();
		}
		this.listener = l;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public void save(XMLElement xml) {
		if (!modified) {
			return;
		}
		xml.removeChildren();
		xml.setAttribute("maxCapacity", String.valueOf(maxCapacity));
		for (Iterator iter = items.iterator(); iter.hasNext();) {
			XMLItem element = (XMLItem) iter.next();
			element.save(xml.addChild(itemsName));
		}
		modified = false;
	}

	public void read(XMLElement xml) {
		modified = false;
		items.removeAllElements();
		this.maxCapacity = xml.getIntAttribute("maxCapacity", MAXCAPACITY_DEFAULT);
		for (Enumeration en = xml.enumerateChildren(); en.hasMoreElements();) {
			XMLElement xmlChild = (XMLElement) en.nextElement();
			if (xmlChild.getName().equals(itemsName)) {
				try {
					XMLItem element = (XMLItem) classXMLItem.newInstance();
					element.read(xmlChild);
					items.add(element);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}

		if (!items.empty()) {
			// Fire Listener in reverse order
			for (ListIterator iter = items.listIterator(items.size()); iter.hasPrevious();) {
				XMLItem element = (XMLItem) iter.previous();
				fireListener(element);
			}
		}
	}

}
