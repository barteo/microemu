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
 
package com.barteo.emulator.applet;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.barteo.emulator.Button;
import com.barteo.emulator.DefaultInputMethod;
import com.barteo.emulator.MicroEmulator;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.SoftButton;
import com.barteo.emulator.device.Device;
import com.barteo.midp.lcdui.CommandManager;
import com.barteo.midp.lcdui.DisplayBridge;
import com.barteo.midp.lcdui.FontManager;
import com.barteo.midp.lcdui.InputMethod;


public class Main extends Applet implements MicroEmulator, com.barteo.midp.lcdui.DisplayComponent
{
  Main instance;
  MIDlet midlet;

  Font defaultFont;
  
  Button prevOverButton;
  Button overButton;
  Button pressedButton;

	Image offi;

  boolean scrollUp = false;
  boolean scrollDown = false;

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

  KeyListener keyListener = new KeyListener()
  {
    
    public void keyTyped(KeyEvent ev)
    {
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
    
  };

    
  public void init()
  {
    instance = this;

    FontManager.getInstance().setComponent(this);
    
    MIDletBridge.setMicroEmulator(this);

    if (!Device.getInstance().isInitialized()) {
      return;
    }
      
    addKeyListener(keyListener);

    addMouseListener(mouseListener);
    addMouseMotionListener(mouseMotionListener);
    
    DisplayBridge.setComponent(this);

    String midletName = getParameter("midlet");
		if (midletName == null) {
			System.out.println("There is no midlet parameter");
			return;
		}

    Class midletClass;
		try {
			midletClass = Class.forName(midletName);
		} catch (ClassNotFoundException ex) {
			System.out.println("Cannot find " + midletName + " MIDlet class");
			return;
		}

    try {
      midlet = (MIDlet) midletClass.newInstance();
    } catch (Exception ex) {
      System.out.println("Cannot initialize " + midletClass + " MIDlet class");
      System.out.println(ex);        
      return;
    }

    resize(Device.deviceRectangle.getSize());
    
    return;
  }


  public void start()
	{
    try {
      MIDletBridge.getAccess(midlet).startApp();
		} catch (MIDletStateChangeException ex) {
      System.err.println(ex);
		}
    requestFocus();
  }


	public void stop() 
  {
    MIDletBridge.getAccess(midlet).pauseApp();
  }


	public void destroy()
	{
    try {
			MIDletBridge.getAccess(midlet).destroyApp(true);
		} catch (MIDletStateChangeException ex) {
  		System.err.println(ex);
		}
	}


  public String getAppProperty(String key)
  {
    return null;
  }

  
  public void notifyDestroyed()
	{
  }
  
  
  public void notifySoftkeyLabelsChanged()
  {
  }
  
  
  public String getAppletInfo()
	{
    return "Title: MicroEmulator \nAuthor: Bartek Teodorczyk, 2001";
  }


  public String[][] getParameterInfo()
	{
    String[][] info = {
		    {"midlet", "MIDlet class name", "The MIDlet class name. This field is mandatory."},
    };

		return info;
  }

                       
  public void paint(Graphics g) 
  {
    g.drawImage(Device.normalImage, 0, 0, this);
    
    g.translate(Device.screenRectangle.x, Device.screenRectangle.y);
    
    g.setColor(Device.backgroundColor);
    g.fillRect(0, 0, Device.screenRectangle.width, Device.screenRectangle.height);
    
    g.setColor(Device.foregroundColor);
    for (Enumeration s = Device.getDeviceButtons().elements(); s.hasMoreElements(); ) {
      Button button = (Button) s.nextElement();
      if (button instanceof SoftButton) {
        ((SoftButton) button).paint(g);
      }
    }

    int inputMode = InputMethod.getInputMethod().getInputMode();
    if (inputMode == InputMethod.INPUT_123) {
      g.drawImage(Device.mode123Image, Device.mode123ImagePaintable.x, Device.mode123ImagePaintable.y, this);
    } else if (inputMode == InputMethod.INPUT_ABC_UPPER) {
      g.drawImage(Device.modeAbcUpperImage, Device.modeAbcUpperImagePaintable.x, Device.modeAbcUpperImagePaintable.y, this);
    } else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
      g.drawImage(Device.modeAbcLowerImage, Device.modeAbcLowerImagePaintable.x, Device.modeAbcLowerImagePaintable.y, this);
    }

    Shape oldclip = g.getClip();
    g.setClip(Device.screenPaintable);
    g.translate(Device.screenPaintable.x, Device.screenPaintable.y);
    DisplayBridge.paint(g);
    g.translate(-Device.screenPaintable.x, -Device.screenPaintable.y);
    g.setClip(oldclip);

    if (scrollUp) {
      g.drawImage(Device.upImage, Device.upImagePaintable.x, Device.upImagePaintable.y, this);
    }
    if (scrollDown) {
      g.drawImage(Device.downImage, Device.downImagePaintable.x, Device.downImagePaintable.y, this);
    }
    
    g.translate(-Device.screenRectangle.x, -Device.screenRectangle.y);

    Rectangle rect;
    if (prevOverButton != null ) {
      rect = prevOverButton.getRectangle();    
      g.drawImage(Device.normalImage, 
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
      prevOverButton = null;
    }
    if (overButton != null) {
      rect = overButton.getRectangle();    
      g.drawImage(Device.overImage, 
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
    }
    if (pressedButton != null) {
      rect = pressedButton.getRectangle();    
      g.drawImage(Device.pressedImage, 
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
          rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
    }    
  }


	public void update(Graphics g)
	{
    if (offi == null || 
        offi.getWidth(null) != getSize().width || offi.getHeight(null) != getSize().height) {
			offi = createImage(getSize().width, getSize().height);
    }

		Graphics offg = offi.getGraphics();
		paint(offg);

    g.drawImage(offi, 0, 0, null);
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

  
  public void setScrollDown(boolean state) 
  {
    scrollDown = state;
  }


  public void setScrollUp(boolean state) 
  {
    scrollUp = state;
  }
  
}
