/*
 *  @(#)DisplayComponent.java  07/07/2001
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
package com.barteo.midp.lcdui;

import java.awt.*;
import java.io.IOException;
import java.util.Enumeration;
import com.barteo.emulator.Resource;
import com.barteo.emulator.SoftButton;
import com.barteo.emulator.device.Device;

/**
 *  Description of the Class
 *
 *@author     barteo
 *@created    3 wrzesieñ 2001
 */
public class DisplayComponent extends Canvas 
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
  public DisplayComponent(Container a_parent) 
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
			offi = createImage(Device.screenRectangleWidth, Device.screenRectangleHeight);
			offg = offi.getGraphics();
    }
    
    for (Enumeration s = Device.getSoftButtons().elements(); s.hasMoreElements(); ) {
      ((SoftButton) s.nextElement()).paint(offg);
    }

    offg.setColor(Device.backgroundColor);
    offg.fillRect(0, 0, input_abc_upper.getWidth(this), input_abc_upper.getHeight(this));
    offg.setColor(Device.foregroundColor);
    int inputMode = InputMethod.getInputMethod().getInputMode();
    if (inputMode == InputMethod.INPUT_123) {
      offg.drawImage(input_123, 0, 0, this);
    } else if (inputMode == InputMethod.INPUT_ABC_UPPER) {
      offg.drawImage(input_abc_upper, 0, 0, this);
    } else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
      offg.drawImage(input_abc_lower, 0, 0, this);
    }

    Shape oldclip = offg.getClip();
    offg.setClip(Device.screenPaintableX, Device.screenPaintableY, Device.screenPaintableWidth, Device.screenPaintableHeight);
    offg.translate(Device.screenPaintableX, Device.screenPaintableY);
    DisplayBridge.paint(offg);
    offg.translate(-Device.screenPaintableX, -Device.screenPaintableY);
    offg.setClip(oldclip);

    offg.setColor(Device.backgroundColor);
    offg.fillRect(40, 120, up_arrow.getWidth(this), up_arrow.getHeight(this));
    offg.fillRect(48, 120, down_arrow.getWidth(this), down_arrow.getHeight(this));
    if (scrollUp) {
      offg.setColor(Device.foregroundColor);
      offg.drawImage(up_arrow, 40, 120, this);
    }
    if (scrollDown) {
      offg.setColor(Device.foregroundColor);
      offg.drawImage(down_arrow, 48, 120, this);
    }
    
		g.drawImage(offi, 0, 0, null);
  }

}
