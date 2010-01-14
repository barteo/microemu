/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */
 
package org.microemu.device.swt;

import java.util.Hashtable;

import javax.microedition.lcdui.Canvas;

import org.eclipse.swt.SWT;
import org.microemu.device.InputMethod;
import org.microemu.device.impl.Button;
import org.microemu.device.impl.Shape;



public class SwtButton implements Button
{
 
  String name;
  Shape shape;
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
  public SwtButton(String name, Shape shape, int keyCode, String keyName, Hashtable inputToChars)
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
	  	if (keyName == null) {
	  		return -1;
	  	}
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
			try {
				return Integer.parseInt(keyName);
			} catch (NumberFormatException ex) {
				return -1;
			}
		}
  }

}
