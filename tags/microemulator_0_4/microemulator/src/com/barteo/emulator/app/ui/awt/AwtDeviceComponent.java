/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@it.pl>
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

package com.barteo.emulator.app.ui.awt;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;

import javax.microedition.lcdui.Command;

import com.barteo.emulator.CommandManager;
import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.SoftButton;
import com.barteo.emulator.device.applet.AppletButton;
import com.barteo.emulator.device.applet.AppletDevice;
import com.barteo.emulator.device.applet.AppletDeviceDisplay;
import com.barteo.emulator.device.applet.AppletInputMethod;


public class AwtDeviceComponent extends Panel
{
	AwtDeviceComponent instance;
	AwtDisplayComponent dc;

	AppletButton prevOverButton;
	AppletButton overButton;
	AppletButton pressedButton;
  
	Image offi;
	Graphics offg;
	
	KeyListener keyListener = new KeyListener()
	{
		public void keyPressed(KeyEvent ev) 
		{
			((AppletInputMethod) DeviceFactory.getDevice().getInputMethod()).keyboardKeyPressed(ev);
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
			((AppletInputMethod) DeviceFactory.getDevice().getInputMethod()).keyboardKeyReleased(ev);
			prevOverButton = pressedButton;
			pressedButton = null;
			repaint();      
		}

		public void keyTyped(KeyEvent e) 
		{
		}		
	};
      
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
					KeyEvent ev = new KeyEvent(instance, 0, 0, 0, key, KeyEvent.CHAR_UNDEFINED);
					DeviceFactory.getDevice().getInputMethod().keyPressed(ev.getKeyCode());
				}
				repaint();
			}
		}


		public void mouseReleased(MouseEvent e) 
		{
			AppletButton prevOverButton = getButton(e.getX(), e.getY());
			if (prevOverButton != null) {
				int key = prevOverButton.getKey();
				KeyEvent ev = new KeyEvent(instance, 0, 0, 0, key, KeyEvent.CHAR_UNDEFINED);

				DeviceFactory.getDevice().getInputMethod().keyReleased(ev.getKeyCode());
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
  
  
	public AwtDeviceComponent() 
	{
		instance = this;
    
		dc = new AwtDisplayComponent(this);    
    
		addKeyListener(keyListener);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseMotionListener);
	}
  
  
	public DisplayComponent getDisplayComponent()
	{
		return dc;
	}
  
  
	public void init()
	{
		validate();
	}
  
  
	public void paint(Graphics g) 
	{
		if (offg == null || 
				offi.getWidth(null) != getSize().width || offi.getHeight(null) != getSize().height) {
			offi = createImage(getSize().width, getSize().height);
			offg = offi.getGraphics();
		}

		offg.drawImage(((AppletDevice) DeviceFactory.getDevice()).getNormalImage(), 0, 0, this);
    
		Rectangle displayRectangle = 
				((AppletDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getDisplayRectangle();
		offg.translate(displayRectangle.x, displayRectangle.y);
		dc.paint(offg);
		offg.translate(-displayRectangle.x, -displayRectangle.y);

		Rectangle rect;
		if (prevOverButton != null ) {
			rect = prevOverButton.getRectangle();    
			offg.drawImage(((AppletDevice) DeviceFactory.getDevice()).getNormalImage(), 
					rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
					rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
			prevOverButton = null;
		}
		if (overButton != null) {
			rect = overButton.getRectangle();    
			offg.drawImage(((AppletDevice) DeviceFactory.getDevice()).getOverImage(), 
					rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
					rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
		}
		if (pressedButton != null) {
			rect = pressedButton.getRectangle();    
			offg.drawImage(((AppletDevice) DeviceFactory.getDevice()).getPressedImage(), 
					rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,
					rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
		}
    
		g.drawImage(offi, 0, 0, null);
	}


	public void update(Graphics g)
	{
		paint(g);
	}
 
  
	private AppletButton getButton(int x, int y)
	{
		for (Enumeration e = ((AppletDevice) DeviceFactory.getDevice()).getButtons().elements(); e.hasMoreElements(); ) {
			AppletButton button = (AppletButton) e.nextElement();
			Rectangle tmp = new Rectangle(button.getRectangle());
			if (x >= tmp.x && x < tmp.x + tmp.width && y >= tmp.y && y < tmp.y + tmp.height) {
				return button;
			}
		}        
		return null;
	}

  
	private AppletButton getButton(KeyEvent ev)
	{
		for (Enumeration e = ((AppletDevice) DeviceFactory.getDevice()).getButtons().elements(); e.hasMoreElements(); ) {
			AppletButton button = (AppletButton) e.nextElement();
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
