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
 
package com.barteo.emulator.app;

import javax.swing.DebugGraphics;
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
  boolean scrollUp = false;
  boolean scrollDown = false;

	Image offi;
	Graphics offg;


  public SwingDisplayComponent() 
  {
    DisplayBridge.setComponent(this);
    setBackground(Device.backgroundColor);
  }


  public void setScrollDown(boolean state) 
  {
    scrollDown = state;
  }


  public void setScrollUp(boolean state) 
  {
    scrollUp = state;
  }


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
      offg.drawImage(Device.mode123Image, Device.mode123ImagePaintable.x, Device.mode123ImagePaintable.y, this);
    } else if (inputMode == InputMethod.INPUT_ABC_UPPER) {
      offg.drawImage(Device.modeAbcUpperImage, Device.modeAbcUpperImagePaintable.x, Device.modeAbcUpperImagePaintable.y, this);
    } else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
      offg.drawImage(Device.modeAbcLowerImage, Device.modeAbcLowerImagePaintable.x, Device.modeAbcLowerImagePaintable.y, this);
    }

    Shape oldclip = offg.getClip();
    offg.setClip(Device.screenPaintable);
    offg.translate(Device.screenPaintable.x, Device.screenPaintable.y);
    DisplayBridge.paint(offg);
    offg.translate(-Device.screenPaintable.x, -Device.screenPaintable.y);
    offg.setClip(oldclip);

    if (scrollUp) {
      offg.drawImage(Device.upImage, Device.upImagePaintable.x, Device.upImagePaintable.y, this);
    }
    if (scrollDown) {
      offg.drawImage(Device.downImage, Device.downImagePaintable.x, Device.downImagePaintable.y, this);
    }
    
		g.drawImage(offi, 0, 0, null);
  }

  
	public void update(Graphics g)
	{
		paint(g);
	}
  
}
