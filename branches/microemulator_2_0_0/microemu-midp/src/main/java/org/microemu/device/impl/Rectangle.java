/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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
