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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import javax.swing.JPanel;
import javax.swing.UIManager;

import javax.microedition.lcdui.Command;
import com.barteo.emulator.Button;
import com.barteo.emulator.DefaultInputMethod;
import com.barteo.emulator.SoftButton;
import com.barteo.emulator.XYConstraints;
import com.barteo.emulator.XYLayout;
import com.barteo.emulator.device.Device;
import com.barteo.midp.lcdui.CommandManager;
import com.barteo.midp.lcdui.InputMethod;


public class SwingDeviceComponent extends JPanel
{
  SwingDeviceComponent instance;
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
      if (pressedButton != null) {
        if (pressedButton instanceof SoftButton) {
          Command cmd = ((SoftButton) pressedButton).getCommand();
          if (cmd != null) {
            CommandManager.getInstance().commandAction(cmd);
          }
        } else {
          int key = pressedButton.getKey();
          KeyEvent ev = new KeyEvent(instance, 0, 0, 0, key);
          InputMethod.getInputMethod().keyPressed(ev.getKeyCode());
        }
        repaint();
      }
    }


    public void mouseReleased(MouseEvent e) 
    {
      Button prevOverButton = getButton(e.getX(), e.getY());
      if (prevOverButton != null) {
        int key = prevOverButton.getKey();
        KeyEvent ev = new KeyEvent(instance, 0, 0, 0, key);

        InputMethod.getInputMethod().keyReleased(ev.getKeyCode());
      }
      pressedButton = null;
      repaint();      
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
    instance = this;
    
    XYLayout xy = new XYLayout();
    setLayout(xy);

    dc = new SwingDisplayComponent();
    add(dc, new XYConstraints(Device.screenRectangle));    

    addMouseListener(mouseListener);
    addMouseMotionListener(mouseMotionListener);
  }
  
  
  public void keyPressed(KeyEvent ev)
  {
    ((DefaultInputMethod) InputMethod.getInputMethod()).keyboardKeyPressed(ev);
    pressedButton = getButton(ev);
    repaint();
    if (pressedButton instanceof SoftButton) {
      Command cmd = ((SoftButton) pressedButton).getCommand();
      if (cmd != null) {
        CommandManager.getInstance().commandAction(cmd);
      }
    }      
  }
   
  
  public void keyReleased(KeyEvent ev)
  {
    ((DefaultInputMethod) InputMethod.getInputMethod()).keyboardKeyReleased(ev);
    prevOverButton = pressedButton;
    pressedButton = null;
    repaint();      
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
 
  
  private Button getButton(int x, int y)
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

  
  private Button getButton(KeyEvent ev)
  {
    for (Enumeration e = Device.getDeviceButtons().elements(); e.hasMoreElements(); ) {
      Button button = (Button) e.nextElement();
      if (ev.getKeyCode() == button.getKey()) {
        return button;
      }
      if (button.isChar(ev.getKeyChar())) {
        return button;
      }
    }        
    return null;
  }
  
}
