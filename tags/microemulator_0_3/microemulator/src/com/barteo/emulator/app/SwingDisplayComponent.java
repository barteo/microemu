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

import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.JPanel;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.InputMethod;
import com.barteo.emulator.device.j2se.DisplayGraphics;
import com.barteo.emulator.device.j2se.J2SEButton;
import com.barteo.emulator.device.j2se.J2SEDevice;
import com.barteo.emulator.device.j2se.J2SEDeviceDisplay;
import com.barteo.emulator.device.j2se.J2SESoftButton;
import com.barteo.emulator.device.j2se.PositionedImage;


public class SwingDisplayComponent extends JPanel implements DisplayComponent
{
  boolean scrollUp = false;
  boolean scrollDown = false;

  Device prevDevice = null;
	Image offi;
	Graphics offg;


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
    Device device = DeviceFactory.getDevice();
    Rectangle displayRectangle = 
        ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();

    if (prevDevice != device) {
			offi = createImage(displayRectangle.width, displayRectangle.height);
			offg = offi.getGraphics();
    }
    prevDevice = device;
    
    offg.setColor(((J2SEDeviceDisplay) device.getDeviceDisplay()).getBackgroundColor());    
    offg.fillRect(0, 0, displayRectangle.width, displayRectangle.height);

    offg.setColor(((J2SEDeviceDisplay) device.getDeviceDisplay()).getForegroundColor());
    for (Enumeration s = ((J2SEDevice) DeviceFactory.getDevice()).getButtons().elements(); s.hasMoreElements(); ) {
      J2SEButton button = (J2SEButton) s.nextElement();
      if (button instanceof J2SESoftButton) {
        ((J2SESoftButton) button).paint(offg);
      }
    }

    PositionedImage tmpImage;
    
    int inputMode = device.getInputMethod().getInputMode();
    if (inputMode == InputMethod.INPUT_123) {
      tmpImage = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getMode123Image();
      offg.drawImage(tmpImage.getImage(), tmpImage.getRectangle().x, tmpImage.getRectangle().y, this);
    } else if (inputMode == InputMethod.INPUT_ABC_UPPER) {
      tmpImage = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getModeAbcUpperImage();
      offg.drawImage(tmpImage.getImage(), tmpImage.getRectangle().x, tmpImage.getRectangle().y, this);
    } else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
      tmpImage = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getModeAbcLowerImage();
      offg.drawImage(tmpImage.getImage(), tmpImage.getRectangle().x, tmpImage.getRectangle().y, this);
    }

    Rectangle displayPaintable = 
        ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayPaintable();
    Shape oldclip = offg.getClip();
    offg.setClip(displayPaintable);
    offg.translate(displayPaintable.x, displayPaintable.y);
    Font f = offg.getFont();
    
    DisplayGraphics dg = new DisplayGraphics(offg);
    MIDletBridge.getMIDletAccess().getDisplayAccess().paint(dg);
    
    offg.setFont(f);
    offg.translate(-displayPaintable.x, -displayPaintable.y);
    offg.setClip(oldclip);

    if (scrollUp) {
      tmpImage = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getUpImage();
      offg.drawImage(tmpImage.getImage(), tmpImage.getRectangle().x, tmpImage.getRectangle().y, this);
    }
    if (scrollDown) {
      tmpImage = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDownImage();
      offg.drawImage(tmpImage.getImage(), tmpImage.getRectangle().x, tmpImage.getRectangle().y, this);
    }
    
		g.drawImage(offi, 0, 0, null);
  }

  
	public void update(Graphics g)
	{
		paint(g);
	}
  
}
