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

package javax.microedition.lcdui;


public abstract class CustomItem extends Item 
{
	protected static int KEY_PRESS = 4;
	protected static int KEY_RELEASE = 8;
	protected static int KEY_REPEAT = 0x10;

	protected static int POINTER_DRAG = 0x80;
	protected static int POINTER_PRESS = 0x20;
	protected static int POINTER_RELEASE = 0x40;

	protected static int NONE = 0;
	protected static int TRAVERSE_HORIZONTAL = 1;
	protected static int TRAVERSE_VERTICAL = 2;
	
	
	protected CustomItem(String label)
	{
		super(label);
System.out.println("TODO");
	}
	

	public int getGameAction(int keyCode)
	{
System.out.println("TODO");
		return 0;
	}
	
	
	protected int getInteractionModes()
	{
System.out.println("TODO");
		return 0;
	}
	
	
	protected abstract int getMinContentHeight();

	protected abstract int getMinContentWidth();
			
	protected abstract int getPrefContentHeight(int width);
		
	protected abstract int getPrefContentWidth(int height);
	
	
	protected void hideNotify()
	{
System.out.println("TODO");
	}
	
	
	protected void invalidate()
	{
System.out.println("TODO");
	}
	
	
	protected void keyPressed(int keyCode)
	{
System.out.println("TODO");
	}
	
	
	protected void keyReleased(int keyCode)
	{
System.out.println("TODO");
	}
	
		
	protected void keyRepeated(int keyCode)
	{
System.out.println("TODO");
	}
	
	
	protected abstract void paint(Graphics g, int w, int h);
	
	
	protected void pointerDragged(int x, int y)
	{
System.out.println("TODO");
	}
	
	
	protected void pointerPressed(int x, int y)
	{
System.out.println("TODO");
	}
	
	
	protected void pointerReleased(int x, int y)
	{
System.out.println("TODO");
	}
	
	
	protected void repaint()
	{
System.out.println("TODO");
	}
	
	
	protected void repaint(int x, int y, int w, int h)
	{
System.out.println("TODO");
	}
	
	
	protected void showNotify()
	{
System.out.println("TODO");
	}
	
	
	protected void sizeChanged(int w, int h)
	{
System.out.println("TODO");
	}
	
	
	protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout)
	{
System.out.println("TODO");
		return false;
	}
	
	
	protected void traverseOut()
	{
System.out.println("TODO");
	}
		

	int paint(Graphics g) 
	{
System.out.println("TODO");
		return 0;
	}

}
