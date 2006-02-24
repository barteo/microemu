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
 
package com.barteo.emulator.device.j2se;

import java.awt.event.KeyEvent;

import com.barteo.emulator.device.impl.Button;
import com.barteo.emulator.device.impl.Rectangle;


public class J2SEButton implements Button
{
 
  String name;
  Rectangle rectangle;
  int key;
  char[] chars;


  public J2SEButton(String name, Rectangle rectangle, String keyName, char[] chars)
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
    int key = -1;
    
    try {
      key = KeyEvent.class.getField(keyName).getInt(null);
    } catch (Exception ex) {
      System.err.println(ex);
    }
    
    return key;
  }

}
