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
	private int x;
	private int y;
	
	protected int height;
	protected int width;
	
	
	Layer()
	{
		this.x = 0;
		this.y = 0;
	}
	

	public final int getHeight()
	{
		return height;
	}
	
	
	public final int getWidth()
	{
		return width;
	}


	public final int getX()
	{
		return x;
	}


	public final int getY()
	{
		return y;
	}	


	public final boolean isVisible()
	{
//		throw new RuntimeException("TODO");
		return true;
	}


	public void move(int dx, int dy)
	{
		setPosition(getX() + dx, getY() + dy);
	}


	public abstract void paint(Graphics g);


	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}


	public void setVisible(boolean visible)				
	{
		throw new RuntimeException("TODO");
	}

}
