/*
 *  MicroEmulator
 *  Copyright (C) 2001,2002 Bartek Teodorczyk <barteo@it.pl>
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import javax.swing.JPanel;
import javax.swing.UIManager;

import javax.microedition.lcdui.Command;
import com.barteo.emulator.Button;
import com.barteo.emulator.SoftButton;
import com.barteo.emulator.XYConstraints;
import com.barteo.emulator.XYLayout;
import com.barteo.emulator.device.Device;
import com.barteo.midp.lcdui.CommandManager;
import com.barteo.midp.lcdui.InputMethod;


public class SwingDeviceComponent extends JPanel
{
	SwingDisplayComponent dc;

  Button prevOverButton;
  Button overButton;
  Button pressedButton;
  
	Image offi;
	Graphics offg;
      
  MouseAdapter mouseListener = new MouseAdapter() 
  {
    
    public void mousePressed(MouseEvent e) 
    {
      pressedButton = getButton(e.getX(), e.getY());
      repaint();
      
      int key = getKey(e);
      
      if (key != 0) {
        InputMethod.getInputMethod().keyPressed(key);
        return;
      }

      Button tmp = getButton(e.getX(), e.getY());      
      if (tmp instanceof SoftButton) {
        Command cmd = ((SoftButton) tmp).getCommand();
        if (cmd != null) {
          CommandManager.getInstance().commandAction(cmd);
        }
      }      
    }


    public void mouseReleased(MouseEvent e) 
    {
      prevOverButton = pressedButton;
      pressedButton = null;
      repaint();
      
      int key = getKey(e);

      if (key != 0) {
        InputMethod.getInputMethod().keyReleased(key);
        return;
      }
    }


    int getKey(MouseEvent e) 
    {
      int key = 0;
      
      Button button = getButton(e.getX(), e.getY());
      if (button != null && !(button instanceof SoftButton)) {
        key = button.getKey();
      }

      return key;
    }

  };
  

  MouseMotionListener mouseMotionListener = new MouseMotionListener() 
  {

    public void mouseDragged(MouseEvent e)
    {
      overButton = getButton(e.getX(), e.getY());
    }

    
    public void mouseMoved(MouseEvent e)
    {
      prevOverButton = overButton;
      overButton = getButton(e.getX(), e.getY());
      if (overButton != prevOverButton) {
        repaint();
      }
    }
    
  };

  
  public SwingDeviceComponent() 
  {
    XYLayout xy = new XYLayout();
    setLayout(xy);

    dc = new SwingDisplayComponent();
    add(dc, new XYConstraints(Device.screenRectangle));    

    addMouseListener(mouseListener);
    addMouseMotionListener(mouseMotionListener);
  }
  

  public void paint(Graphics g) 
  {
    if (offg == null || 
        offi.getWidth(null) != getSize().width || offi.getHeight(null) != getSize().height) {
			offi = createImage(getSize().width, getSize().height);
			offg = offi.getGraphics();
    }

    Dimension size = getSize();
    offg.setColor(UIManager.getColor("text"));
    offg.fillRect(0, 0, size.width, size.height);
    offg.drawImage(Device.normalImage, 0, 0, this);
    
    offg.translate(Device.screenRectangle.x, Device.screenRectangle.y);
    dc.paint(offg);
    offg.translate(-Device.screenRectangle.x, -Device.screenRectangle.y);

    Rectangle rect;
    if (prevOverButton != null ) {
      rect = prevOverButton.getRectangle();    
      offg.drawImage(Device.normalImage, 
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
      prevOverButton = null;
    }
    if (overButton != null) {
      rect = overButton.getRectangle();    
      offg.drawImage(Device.overImage, 
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
    }
    if (pressedButton != null) {
      rect = pressedButton.getRectangle();    
      offg.drawImage(Device.pressedImage, 
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
    }
    
 		g.drawImage(offi, 0, 0, null);
  }


	public void update(Graphics g)
	{
		paint(g);
	}
 
  
  Button getButton(int x, int y)
  {
    for (Enumeration e = Device.getDeviceButtons().elements(); e.hasMoreElements(); ) {
      Button button = (Button) e.nextElement();
      Rectangle tmp = new Rectangle(button.getRectangle());
      if (x >= tmp.x && x < tmp.x + tmp.width && y >= tmp.y && y < tmp.y + tmp.height) {
        return button;
      }
    }        
    return null;
  }  
  
}
