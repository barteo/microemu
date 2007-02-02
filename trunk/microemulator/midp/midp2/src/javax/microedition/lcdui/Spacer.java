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

package javax.microedition.lcdui;

public class Spacer extends Item {
	
	public Spacer(int minWidth, int minHeight) {
		super(null);
		setMinimumSize(minWidth, minHeight);
	}

	public void setLabel(String label) {
		throw new IllegalStateException("Spacer items can't have labels");
	}

	public void addCommand(Command cmd) {
		throw new IllegalStateException("Spacer items can't have commands");
	}
	
	public void setDefaultCommand(Command cmd) {
		throw new IllegalStateException("Spacer items can't have commands");
	}
	
	public void setMinimumSize(int minWidth, int minHeight) {
		if (minWidth < 0 || minHeight < 0)
			throw new IllegalArgumentException();
		
		  // TODO implement
	}
	
	// Item methods
	int paint(Graphics g) {
		return 0;
	}

}
