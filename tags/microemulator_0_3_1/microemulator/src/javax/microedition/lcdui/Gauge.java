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
 *
 *  Contributor(s):
 *    3GLab
 */
 
package javax.microedition.lcdui;

import com.barteo.emulator.device.DeviceFactory;


public class Gauge extends Item
{
  static int HEIGHT = 15;
  
  int value;
  int maxValue;
  boolean interactive;


  public Gauge(String label, boolean interactive, int maxValue, int initialValue)
  {
    super(label);
    
    this.interactive = interactive;
    
    setMaxValue(maxValue);
    setValue(initialValue);
  }


  public void setValue(int value)
  {
    if (value < 0) {
      value = 0;
    }
    if (value > maxValue) {
      value = maxValue;
    }
    
    this.value = value;
    repaint();
  }


  public int getValue()
  {
    return value;
  }


  public void setMaxValue(int maxValue)
  {
    if (maxValue <= 0) {
      throw new IllegalArgumentException();
    }
    
    this.maxValue = maxValue;
    setValue(getValue());
  }


  public int getMaxValue()
  {
    return maxValue;
  }


  public boolean isInteractive()
  {
    return interactive;
  }


  public void setLabel(String label)
  {
    super.setLabel(label);
  }


	int getHeight()
	{
		return super.getHeight() + HEIGHT;
	}

  
	boolean isFocusable()
	{
		return interactive;
	}

  
  void keyPressed(int keyCode)
  {
    if (Display.getGameAction(keyCode) == Canvas.LEFT && value > 0) {
      value--;
      repaint();
    } else if (Display.getGameAction(keyCode) == Canvas.RIGHT && value < maxValue) {
      value++;
      repaint();
    }
  }

  
  int paint(Graphics g)
  {    
    super.paintContent(g);
    
		g.translate(0, super.getHeight());
    
 		if (isFocus()) {
      g.drawRect(2, 2, 
          DeviceFactory.getDevice().getDeviceDisplay().getWidth() - 5, HEIGHT - 5);
    }
    
    int width = (DeviceFactory.getDevice().getDeviceDisplay().getWidth() - 8) * value / maxValue;
    g.fillRect(4, 4, width, HEIGHT - 8);
		g.translate(0, -super.getHeight());
    
    return getHeight();
  }


	int traverse(int gameKeyCode, int top, int bottom, boolean action)
	{
		Font f = Font.getDefaultFont();

		if (gameKeyCode == 1) {
			if (top > 0) {
				return -top;
			} else {
				return Item.OUTOFITEM;
			}
		}
		if (gameKeyCode == 6) {
			if (getHeight() > bottom) {
				return getHeight() - bottom;
			} else {
				return Item.OUTOFITEM;
			}
		}

		return 0;
	}
  
}