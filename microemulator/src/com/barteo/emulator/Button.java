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
 
package com.barteo.emulator;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;


public class Button 
{
 
  String name;
  Rectangle rectangle;
  int key;


  public Button(String name, Rectangle rectangle, String keyName)
  {
    this.name = name;
    this.rectangle = rectangle;

    try {
      key = KeyEvent.class.getField(keyName).getInt(null);
    } catch (Exception ex) {
      System.err.println(ex);
    }
  }
  
  
  public int getKey()
  {
    return key;
  }
  
  
  public String getName()
  {
    return name;
  }

  
  public Rectangle getRectangle()
  {
    return rectangle;
  }

}
