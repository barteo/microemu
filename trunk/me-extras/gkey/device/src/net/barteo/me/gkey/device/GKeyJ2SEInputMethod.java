/*
 *  MicroEmulator
 *  Copyright (C) 2001-2005 Bartek Teodorczyk <barteo@barteo.net>
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

package net.barteo.me.gkey.device;

import java.awt.event.KeyEvent;

import net.barteo.me.gkey.GetKeyHandler;

import org.microemu.device.j2se.J2SEInputMethod;


public class GKeyJ2SEInputMethod extends J2SEInputMethod 
{
	private GetKeyHandler handler;

	
	public GKeyJ2SEInputMethod(GetKeyHandler handler) 
	{
		this.handler = handler;
	}

	protected boolean commonKeyPressed(int keyCode) 
	{
		if (keyCode == KeyEvent.VK_F8) {
			handler.getKeyPressed();
			return true;
		}
		
		// TODO Auto-generated method stub
		return super.commonKeyPressed(keyCode);
	}
	
}
