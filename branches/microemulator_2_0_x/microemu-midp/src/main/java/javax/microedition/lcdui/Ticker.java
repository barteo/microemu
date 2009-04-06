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
 *
 *  Contributor(s):
 *    3GLab
 */
 
package javax.microedition.lcdui;

import org.microemu.DisplayAccess;
import org.microemu.MIDletBridge;


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
      DisplayAccess da = MIDletBridge.getMIDletAccess().getDisplayAccess();
      while (xPos < da.getCurrent().getWidth()) {
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