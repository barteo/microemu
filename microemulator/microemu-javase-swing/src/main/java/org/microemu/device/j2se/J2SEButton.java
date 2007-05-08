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
 
package org.microemu.device.j2se;

import java.awt.event.KeyEvent;
import java.util.Hashtable;

import org.microemu.device.InputMethod;
import org.microemu.device.impl.Button;
import org.microemu.device.impl.Shape;



public class J2SEButton implements Button
{
 
  private String name;
  private Shape shape;
  private int keyboardKey;
  private int keyCode;
  private Hashtable inputToChars;


  /**
 * @param name
 * @param shape
 * @param keyCode - Integer.MIN_VALUE when unspecified
 * @param keyName
 * @param chars
 */
  public J2SEButton(String name, Shape shape, int keyCode, String keyName, Hashtable inputToChars)
  {
    this.name = name;
    this.shape = shape;
    this.keyboardKey = parseKeyboardKey(keyName);

    if (keyCode == Integer.MIN_VALUE) {
	    if (keyName != null) {
	    	this.keyCode = this.keyboardKey;
	    } else {
	    	this.keyCode = -1;
	    }
    } else {
    	this.keyCode = keyCode;
    }
    this.inputToChars = inputToChars;
  }
  
  
  public int getKeyboardKey()
  {
	  return keyboardKey;
  }
  
  
  public int getKeyCode()
  {
    return keyCode;
  }
  
  
  public char[] getChars(int inputMode)
  {
      char[] result = null;
      switch (inputMode) {
          case InputMethod.INPUT_123 : 
              result = (char[]) inputToChars.get("123");
              break;
          case InputMethod.INPUT_ABC_LOWER : 
              result = (char[]) inputToChars.get("abc");
              break;
          case InputMethod.INPUT_ABC_UPPER : 
              result = (char[]) inputToChars.get("ABC");
              break;
      }
      if (result == null) {
          result = (char[]) inputToChars.get("common");
      }
      if (result == null) {
          result = new char[0];
      }
      
      return result;
  }
  
  
  public boolean isChar(char c, int inputMode)
  {
      c = Character.toLowerCase(c);
      char[] chars = getChars(inputMode);
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

  
  public Shape getShape()
  {
    return shape;
  }
  
  
  	private int parseKeyboardKey(String keyName) 
  	{
		int key;

		try {
			key = KeyEvent.class.getField(keyName).getInt(null);
		} catch (Exception ex) {
			try {
				key = Integer.parseInt(keyName);
			} catch (NumberFormatException ex1) {
				key = -1;
			}
		}

		return key;
	}

}
