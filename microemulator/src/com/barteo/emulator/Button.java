/*
 * MicroEmulator
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.barteo.emulator;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

/**
 * Button
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
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
