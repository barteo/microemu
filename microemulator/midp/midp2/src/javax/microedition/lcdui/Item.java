/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2005 Andres Navarro
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

import java.util.Vector;

public abstract class Item
{

	static final int OUTOFITEM = Integer.MAX_VALUE;

	public static final int LAYOUT_DEFAULT          = 0x0000;

    public static final int LAYOUT_LEFT             = 0x0001;
    public static final int LAYOUT_RIGHT            = 0x0002;
    public static final int LAYOUT_CENTER           = 0x0003;

    public static final int LAYOUT_TOP              = 0x0010;
    public static final int LAYOUT_BOTTOM           = 0x0020;
    public static final int LAYOUT_VCENTER          = 0x0030;

    public static final int LAYOUT_NEWLINE_BEFORE   = 0x0100;
    public static final int LAYOUT_NEWLINE_AFTER    = 0x0200;

    public static final int LAYOUT_SHRINK           = 0x0400;
    public static final int LAYOUT_EXPAND           = 0x0800;
    public static final int LAYOUT_VSHRINK          = 0x1000;
    public static final int LAYOUT_VEXPAND          = 0x2000;

    public static final int LAYOUT_2                = 0x4000;


    public static final int PLAIN = 0;
    public static final int HYPERLINK = 1;
    public static final int BUTTON = 2;

	StringComponent labelComponent;
	Screen owner = null;
	private boolean focus = false;
	
	// MIDP2
	int layout;
	Vector commands;
    Command defaultCommand;
    ItemCommandListener commandListener;
    // -1 means unlocked, otherwise it is the 
    // application requested preffered size
    // for the actual one use the getPrefXXXX() method
    // package access
    private int prefWidth, prefHeight;
  
  
  Item(String label)
  {
		labelComponent = new StringComponent(label);
		commands = new Vector();
  }
  
	public void addCommand(Command cmd) {
	    if (cmd == null)
	        throw new NullPointerException();
	
	    if (!commands.contains(cmd)) {
	        // Now insert it in order
	        boolean inserted = false;
	          
	        for (int i = 0; i < commands.size(); i++) {
	            if (cmd.getPriority() < ((Command)commands.elementAt(i)).getPriority()) {
	                commands.insertElementAt(cmd, i);
	                inserted = true;
	                break;
	            }
	        }
	        if (!inserted) {
	          // Not inserted just place it at the end
	              commands.addElement(cmd);
	        }
	    	repaintOwner();
        }

	}
  
	public String getLabel()
	{
		return labelComponent.getText();
	}

	public int getLayout() {
		return layout;
	}
	
	public int getMinimumHeight() {
		if (labelComponent != null)
			return labelComponent.getHeight();
		else 
			return 0;
    }

    public int getMinimumWidth() {
    	return getMaximumWidth();
    }
    
    public int getPreferredHeight() {
        int ret = prefHeight;
        int min = getMinimumHeight();
        int max = getMaximumHeight();

        if (ret == -1)
        	return min;
        
        if (ret < min)
        	ret = min;
        else if (ret > max)
        	ret = max;
    	return ret;
    }

    public int getPreferredWidth() {
        int ret = prefWidth;
        int min = getMinimumWidth();
        int max = getMaximumWidth();
        
        if (ret == -1)
        	return max;
        	
        if (ret < min)
        	ret = min;
        else if (ret > max)
        	ret = max;
    	return ret;
    }

	public void notifyStateChanged() {
		Screen owner = getOwner();
		if (owner != null && owner instanceof Form) {
			Form form = (Form) owner;
			form.fireItemStateListener(this);
		}
		
    }

	public void removeCommand(Command cmd) {
        commands.removeElement(cmd);
        if (defaultCommand == cmd)
        	defaultCommand = null;
        repaintOwner();
    }
	
    public void setDefaultCommand(Command cmd) {
        this.defaultCommand = cmd;
        if (cmd != null) {
            // we should repaint even if the command was added
            // because the command layout could become different
            if (commands.contains(cmd))
            	addCommand(cmd);
            else 
            	repaintOwner();
        } else {
        	repaintOwner();
        }
    }

    public void setItemCommandListener(ItemCommandListener l) {
        this.commandListener = l;
    }
    
    public void setLabel(String label)	
	{
		labelComponent.setText(label);
		repaint();
	}

    public void setLayout(int layout) {
    	// TODO validate container is not Alert
    	// on add to Alert validate this is default
    	
    	// notice that the vertical and the horizontal
    	// layout policies can't generate conflict
    	// because the center is the or of the two
    	// others (ie VCENTER == (LEFT | RIGHT))
    	if ((( (layout & LAYOUT_SHRINK) != 0) &&
    		  ((layout & LAYOUT_EXPAND) != 0)) ||
    			( ((layout & LAYOUT_VSHRINK) != 0) &&
    	    	(layout & LAYOUT_VEXPAND) != 0) )
    		throw new IllegalArgumentException(
    				"Bad combination of layout policies");
		this.layout = layout;
    	repaint();
    }

    public void setPreferredSize(int width, int height) {
        if (width < -1 || height < -1) {
            throw new IllegalArgumentException();
        }
        this.prefWidth = width;
        this.prefHeight = height;
        repaint();
    }

    //
    // package access methods
    //
    
    // repaint the owner of this item (if any)
    void repaintOwner() {
        Screen owner = getOwner();
        if (owner != null)
        	owner.repaint();
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
	
	
	protected void repaint()
	{
		if (owner != null) {
			owner.repaint();
		}
	}
	
  
	boolean hasFocus()
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
		// call the default command (if there is one)
		// however subclasses may override this behaviour
		// (ie popup choices uses select to bring the popup)
		if (defaultCommand != null && commandListener != null) {
			commandListener.commandAction(defaultCommand, this);
			return true;
		} else {
			return false;
		}
	}
  

	int traverse(int gameKeyCode, int top, int bottom, boolean action)
	{
		return 0;
	}

	int getMaximumHeight() {
		return Form.getMaximumItemHeight();
	}
	
	int getMaximumWidth() {
		return Form.getMaximumItemWidth();
	}

	ItemCommandListener getItemCommandListener() {
		return this.commandListener;
	}
}
