/*
 *  @(#)SwingDisplayComponent.java  07/07/2001
 *
 *  Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.barteo.emulator.app;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JPanel;

import com.barteo.emulator.Button;
import com.barteo.emulator.Resource;
import com.barteo.emulator.SoftButton;
import com.barteo.emulator.device.Device;
import com.barteo.midp.lcdui.DisplayBridge;
import com.barteo.midp.lcdui.InputMethod;


public class SwingDisplayComponent extends JPanel implements com.barteo.midp.lcdui.DisplayComponent
{
  Container parent;

  Image up_arrow, down_arrow;
  Image input_123, input_abc_upper, input_abc_lower;
  boolean scrollUp = false;
  boolean scrollDown = false;

	Image offi;
	Graphics offg;

  /**
   *  Constructor for the DisplayComponent object
   *
   *@param  a_parent  Description of Parameter
   */
  public SwingDisplayComponent(Container a_parent) 
  {
    parent = a_parent;
    DisplayBridge.setComponent(this);
    setBackground(Device.backgroundColor);

    try {
      up_arrow = Resource.getInstance().getImage("/com/barteo/emulator/resources/up.png");
      down_arrow = Resource.getInstance().getImage("/com/barteo/emulator/resources/down.png");
      input_123 = Resource.getInstance().getImage("/com/barteo/emulator/resources/123.png");
      input_abc_upper = Resource.getInstance().getImage("/com/barteo/emulator/resources/abc_upper.png");
      input_abc_lower = Resource.getInstance().getImage("/com/barteo/emulator/resources/abc_lower.png");
    } catch (IOException ex) {
      System.err.println(ex);
      ex.printStackTrace();
    }
  }


  /**
   *  Sets the scrollDown attribute of the DisplayComponent object
   *
   *@param  state  The new scrollDown value
   */
  public void setScrollDown(boolean state) 
  {
    scrollDown = state;
  }


  /**
   *  Sets the scrollUp attribute of the DisplayComponent object
   *
   *@param  state  The new scrollUp value
   */
  public void setScrollUp(boolean state) 
  {
    scrollUp = state;
  }


  /**
   *  Description of the Method
   *
   *@param  g  Description of Parameter
   */
  public void paint(Graphics g) 
  {
    if (offg == null) {
			offi = createImage(Device.screenRectangle.width, Device.screenRectangle.height);
			offg = offi.getGraphics();
    }
    
    offg.setColor(Device.backgroundColor);    
    offg.fillRect(0, 0, Device.screenRectangle.width, Device.screenRectangle.height);

    offg.setColor(Device.foregroundColor);
    for (Enumeration s = Device.getDeviceButtons().elements(); s.hasMoreElements(); ) {
      Button button = (Button) s.nextElement();
      if (button instanceof SoftButton) {
        ((SoftButton) button).paint(offg);
      }
    }
    
    int inputMode = InputMethod.getInputMethod().getInputMode();
    if (inputMode == InputMethod.INPUT_123) {
      offg.drawImage(input_123, 0, 0, this);
    } else if (inputMode == InputMethod.INPUT_ABC_UPPER) {
      offg.drawImage(input_abc_upper, 0, 0, this);
    } else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
      offg.drawImage(input_abc_lower, 0, 0, this);
    }

    Shape oldclip = offg.getClip();
    offg.setClip(Device.screenPaintable);
    offg.translate(Device.screenPaintable.x, Device.screenPaintable.y);
    DisplayBridge.paint(offg);
    offg.translate(-Device.screenPaintable.x, -Device.screenPaintable.y);
    offg.setClip(oldclip);

    if (scrollUp) {
      offg.drawImage(up_arrow, 40, 115, this);
    }
    if (scrollDown) {
      offg.drawImage(down_arrow, 48, 115, this);
    }
    
		g.drawImage(offi, 0, 0, null);
  }

  
	public void update(Graphics g)
	{
		paint(g);
	}
  
}
