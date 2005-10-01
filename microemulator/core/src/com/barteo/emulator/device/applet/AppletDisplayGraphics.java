/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contributor(s): 
 *   3GLab
 */

package com.barteo.emulator.device.applet;

import java.awt.Graphics2D;

import com.barteo.emulator.device.MutableImage;
import com.barteo.emulator.device.j2se.J2SEDisplayGraphics;

public class AppletDisplayGraphics extends J2SEDisplayGraphics 
{

	public AppletDisplayGraphics(Graphics2D a_g, MutableImage a_image) 
	{
		super(a_g, a_image);
	}
	
}
