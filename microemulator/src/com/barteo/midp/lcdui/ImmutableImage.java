/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.midp.lcdui;

import java.awt.Image;
import java.io.IOException;

import javax.microedition.lcdui.*;

import com.barteo.emulator.Resource;


public class ImmutableImage extends ImageImpl
{


	public ImmutableImage(String name)
	{
		try {
			img = Resource.getInstance().getImage(name);
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}


	public ImmutableImage(byte[] imageData, int imageOffset, int imageLength)
	{
		try {
			img = Resource.getInstance().getImage(imageData, imageOffset, imageLength);	
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

}
