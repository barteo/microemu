/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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

package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;


public abstract class Layer 
{

	public final int getHeight()
	{
		throw new RuntimeException("TODO");
	}
	
	
	public final int getWidth()
	{
		throw new RuntimeException("TODO");
	}


	public final int getX()
	{
		throw new RuntimeException("TODO");
	}


	public final int getY()
	{
		throw new RuntimeException("TODO");
	}	


	public final boolean isVisible()
	{
		throw new RuntimeException("TODO");
	}


	public void move(int dx, int dy)
	{
		throw new RuntimeException("TODO");
	}


	public abstract void paint(Graphics g);


	public void setPosition(int x, int y)
	{
		throw new RuntimeException("TODO");
	}


	public void setVisible(boolean visible)				
	{
		throw new RuntimeException("TODO");
	}

}
