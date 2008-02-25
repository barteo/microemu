/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.impl;

public class Rectangle extends Shape {
	
	private boolean initialized;
	
	public int x;

	public int y;

	public int width;

	public int height;

	public Rectangle() {
		this.initialized = false;
	}
	
	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.initialized = true;
	}

	public Rectangle(Rectangle rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.width = rect.width;
		this.height = rect.height;
		
		this.initialized = false;
	}
	
	public void add(int newx, int newy) {
		if (initialized) {
			if (newx < x) {
				width += x - newx;
				x = newx;			
			} else if (newx > x + width) {
				width = newx - x;
			}
			if (newy < y) {
				height += y - newy;
				y = newy;
			} else if (newy > y + height) {
				height = newy - y;
			}
		} else {
			x = newx;
			y = newy;
			initialized = true;
		}
	}

	public boolean contains(int x, int y) {
		if (x >= this.x && x < this.x + this.width && y >= this.y
				&& y < this.y + this.height) {
			return true;
		} else {
			return false;
		}
	}

	public Rectangle getBounds() {
		return this;
	}
	
	public String toString() {
	    StringBuffer buf = new StringBuffer();
	    buf.append(x).append(",").append(y).append(" ").append(this.width).append("x").append(this.height);
	    return buf.toString();
	}

}
