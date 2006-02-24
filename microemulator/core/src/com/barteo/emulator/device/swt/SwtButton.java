/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 
package com.barteo.emulator.device.swt;

import javax.microedition.lcdui.Canvas;

import org.eclipse.swt.SWT;

import com.barteo.emulator.device.impl.Button;
import com.barteo.emulator.device.impl.Rectangle;


public class SwtButton implements Button
{
 
  String name;
  Rectangle rectangle;
  int key;
  char[] chars;


  public SwtButton(String name, Rectangle rectangle, String keyName, char[] chars)
  {
    this.name = name;
    this.rectangle = rectangle;

    this.key = getKeyCode(keyName);
    this.chars = chars;
    if (chars != null) {
	    for (int i = 0; i < this.chars.length; i++) {
	    	this.chars[i] = Character.toLowerCase(this.chars[i]);
	    }
    }
  }
  
  
  public int getKey()
  {
    return key;
  }
  
  
  public char[] getChars()
  {
    return chars;
  }
  
  
  public boolean isChar(char c)
  {
    c = Character.toLowerCase(c);
    if (chars != null) {
      for (int i = 0; i < chars.length; i++) {
        if (c == chars[i]) {
          return true;
        } 
      }
    }
    
    return false;
  }
  
  
  public String getName()
  {
    return name;
  }

  
  public Rectangle getRectangle()
  {
    return rectangle;
  }
  
  
  private int getKeyCode(String keyName)
  {
		// TODO poprawic KeyEvent	
		if (keyName.equals("VK_LEFT")) {
			return SWT.ARROW_LEFT;
		} else if (keyName.equals("VK_RIGHT")) {
			return SWT.ARROW_RIGHT;
		} else if (keyName.equals("VK_UP")) {
			return SWT.ARROW_UP;
		} else if (keyName.equals("VK_DOWN")) {
			return SWT.ARROW_DOWN;
		} else if (keyName.equals("VK_ENTER")) {
			return SWT.CR;
		} else if (keyName.equals("VK_F1")) {
			return SWT.F1;
		} else if (keyName.equals("VK_F2")) {
			return SWT.F2;
		} else if (keyName.equals("VK_0")) {
			return Canvas.KEY_NUM0;
		} else if (keyName.equals("VK_1")) {
			return Canvas.KEY_NUM1;
		} else if (keyName.equals("VK_2")) {
			return Canvas.KEY_NUM2;
		} else if (keyName.equals("VK_3")) {
			return Canvas.KEY_NUM3;
		} else if (keyName.equals("VK_4")) {
			return Canvas.KEY_NUM4;
		} else if (keyName.equals("VK_5")) {
			return Canvas.KEY_NUM5;
		} else if (keyName.equals("VK_6")) {
			return Canvas.KEY_NUM6;
		} else if (keyName.equals("VK_7")) {
			return Canvas.KEY_NUM7;
		} else if (keyName.equals("VK_8")) {
			return Canvas.KEY_NUM8;
		} else if (keyName.equals("VK_9")) {
			return Canvas.KEY_NUM9;
		} else if (keyName.equals("VK_MULTIPLY")) {
			return Canvas.KEY_STAR;
		} else if (keyName.equals("VK_MODECHANGE")) {
			return Canvas.KEY_POUND;
		} else {
			return -1;
		}
  }

}
