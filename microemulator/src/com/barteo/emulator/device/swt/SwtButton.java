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
 
package com.barteo.emulator.device.swt;

import org.eclipse.swt.graphics.Rectangle;


public class SwtButton 
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
        if (c == Character.toLowerCase(chars[i])) {
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
System.out.println(keyName);  	
    int key = -1;

	// TODO poprawic KeyEvent	
    
/*    try {
      key = KeyEvent.class.getField(keyName).getInt(null);
    } catch (Exception ex) {
      System.err.println(ex);
    }*/
    
    return key;
  }

}
