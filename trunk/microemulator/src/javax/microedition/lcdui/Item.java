/*
 * @(#)Item.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package javax.microedition.lcdui;


public abstract class Item
{

	static final int OUTOFITEM = Integer.MAX_VALUE;

  StringComponent labelComponent;
  Screen owner;
	boolean focus = false;
  
  
  Item(String label)
  {
		labelComponent = new StringComponent(label);
  }
  
  
	public String getLabel()
	{
		return labelComponent.getText();
	}


	public void setLabel(String label)	
	{
		labelComponent.setText(label);
	}

	
  int getHeight()
	{
		return labelComponent.getHeight();
	}
	
	
	boolean isFocusable()
	{
		return false;
	}
	
	
  void keyPressed(int keyCode)
  {
  }
  
    
  abstract int paint(Graphics g);
	
	
	void paintContent(Graphics g)
	{
		labelComponent.paint(g);
	}
	
	
	void repaint()
	{
		if (owner != null) {
			owner.repaint();
		}
	}
	
  
	boolean isFocus()
	{
		return focus;
	}
  
  
	void setFocus(boolean state)
	{
		focus = state;
	}
  
  
  void setOwner(Screen owner)
  {
    this.owner = owner;
  }
	
	
	boolean select()
	{
		return false;
	}
  

	int traverse(int gameKeyCode, int top, int bottom, boolean action)
	{
		return 0;
	}
	
}
