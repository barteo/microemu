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
 
package javax.microedition.lcdui;

import com.barteo.emulator.device.Device;


public class Ticker
{

  static int PAINT_TIMEOUT = 250;
  static int PAINT_MOVE = 5;
  static int PAINT_GAP = 10;
  
  Ticker instance = null;

  String text;
  boolean running = false;
  int textPos = 0;
  int resetTextPosTo = -1;
  TickerPaint tickerPaint;


	class TickerPaint implements Runnable
	{

		public void run()
		{
      while (true) {
        if (running) {
          synchronized (instance) {
            if (resetTextPosTo != -1) {
              textPos = resetTextPosTo;
              resetTextPosTo = -1;
            }
            textPos -= PAINT_MOVE;
          }
          Display.getDisplay().repaint();
        }
  			try {
  				Thread.sleep(PAINT_TIMEOUT);
  			} catch (InterruptedException ex) {}
      }
		}

	}


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
      g.drawString(text, textPos, 0, 0);
      int xPos = textPos + stringWidth;
      while (xPos < Device.screenPaintable.width) {
        g.drawString(text, xPos, 0, 0);
        xPos += stringWidth;
      }
      if (textPos + stringWidth < 0) {
        resetTextPosTo = textPos + stringWidth;
      }
    }
    
    return f.getHeight();
  }
  
  
  void setRunning(boolean state)
  {
    if (tickerPaint == null && state == true) {
      tickerPaint = new TickerPaint();
      Thread t = new Thread(tickerPaint);
      t.start();
    }

    running = state;
  }

}