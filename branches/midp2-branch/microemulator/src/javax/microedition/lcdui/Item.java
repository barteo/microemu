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
 *
 *  Contributor(s):
 *    3GLab
 */
 
package javax.microedition.lcdui;


public abstract class Item
{
	public static final int PLAIN = 0;
	public static final int HYPERLINK = 1;
	public static final int BUTTON = 2;
	
	public static final int LAYOUT_DEFAULT = 0;
	public static final int LAYOUT_LEFT = 1;
	public static final int LAYOUT_RIGHT = 2;
	public static final int LAYOUT_CENTER = 3;
	public static final int LAYOUT_TOP = 0x10;
	public static final int LAYOUT_BOTTOM = 0x20;
	public static final int LAYOUT_VCENTER = 0x30;
	public static final int LAYOUT_NEWLINE_BEFORE = 0x100;
	public static final int LAYOUT_NEWLINE_AFTER = 0x200;
	public static final int LAYOUT_SHRINK = 0x400;
	public static final int LAYOUT_EXPAND = 0x800;
	public static final int LAYOUT_VSHRINK = 0x1000;
	public static final int LAYOUT_VEXPAND = 0x2000;
	public static final int LAYOUT_2 = 0x4000;

	static final int OUTOFITEM = Integer.MAX_VALUE;

  private StringComponent labelComponent;
  Screen owner = null;
	boolean focus = false;
  
  
  Item(String label)
  {
		labelComponent = new StringComponent(label);
  }
  
  
	public void addCommand(Command cmd)
	{
System.out.println("TODO");
	}
	
	
	public void removeCommand(Command cmd)
	{
System.out.println("TODO");
	}
	
	
	public String getLabel()
	{
		return labelComponent.getText();
	}


	public void setLabel(String label)	
	{
		labelComponent.setText(label);
	}

	
	public int getLayout()
	{
System.out.println("TODO");
		return 0;
	}
	

	public int getMinimumHeight()
	{
System.out.println("TODO");
		return 0;
	}


	public int getMinimumWidth()
	{
System.out.println("TODO");
		return 0;
	}


	public int getPreferredHeight()
	{
System.out.println("TODO");
		return 0;
	}


	public int getPreferredWidth()
	{
System.out.println("TODO");
		return 0;
	}
	public void notifyStateChanged()
	{
System.out.println("TODO");
	}


	public void setDefaultCommand(Command cmd)
	{
System.out.println("TODO");
	}


	public void setItemCommandListener(ItemCommandListener l)	
	{
System.out.println("TODO");
	}


	public void setLayout(int layout)
	{
System.out.println("TODO");
	}


	public void setPreferredSize(int width, int height)
	{
System.out.println("TODO");
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
  
  
  Screen getOwner()
  {
    return owner;
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
