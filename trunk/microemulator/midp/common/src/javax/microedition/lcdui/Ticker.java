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
 *
 *  Contributor(s):
 *    3GLab
 */
 
package javax.microedition.lcdui;

import org.microemu.device.DeviceFactory;


public class Ticker
{

  static int PAINT_TIMEOUT = 250;
  static int PAINT_MOVE = 5;
  static int PAINT_GAP = 10;
  
  Ticker instance = null;

  String text;
  int textPos = 0;
  int resetTextPosTo = -1;


  public Ticker(String str)
  {
    if (str == null) {
      throw new NullPointerException();
    }
    instance = this;
    
    text = str;
  }


  public String getString()
  {
    return text;
  }


  public void setString(String str)
  {
    if (str == null) {
      throw new NullPointerException();
    }
    text = str;
  }
  

  int getHeight()
  {
    return Font.getDefaultFont().getHeight();
  }
  
  
  int paintContent(Graphics g)
  {
		Font f = Font.getDefaultFont();
    
    synchronized (instance) {
      int stringWidth = f.stringWidth(text) + PAINT_GAP;
      g.drawString(text, textPos, 0, Graphics.LEFT | Graphics.TOP);
      int xPos = textPos + stringWidth;
      while (xPos < DeviceFactory.getDevice().getDeviceDisplay().getWidth()) {
        g.drawString(text, xPos, 0, Graphics.LEFT | Graphics.TOP);
        xPos += stringWidth;
      }
      if (textPos + stringWidth < 0) {
        resetTextPosTo = textPos + stringWidth;
      }
    }
    
    return f.getHeight();
  }
  
}