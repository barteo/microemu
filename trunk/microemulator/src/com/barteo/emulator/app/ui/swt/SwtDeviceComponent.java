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

package com.barteo.emulator.app.ui.swt;

import java.io.InputStream;
import java.util.Enumeration;

import javax.microedition.lcdui.Command;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.barteo.emulator.CommandManager;
import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.SoftButton;
import com.barteo.emulator.device.swt.SwtButton;
import com.barteo.emulator.device.swt.SwtDevice;
import com.barteo.emulator.device.swt.SwtDeviceDisplay;
import com.barteo.emulator.device.swt.SwtInputMethod;


public class SwtDeviceComponent extends Canvas
{
	static SwtDeviceComponent instance;
	SwtDisplayComponent dc;

	SwtButton prevOverButton;
	SwtButton overButton;
	SwtButton pressedButton;
  
  KeyListener keyListener = new KeyListener()
  {
		public void keyPressed(KeyEvent ev)
		{
			((SwtInputMethod) DeviceFactory.getDevice().getInputMethod()).keyboardKeyPressed(ev);
			pressedButton = getButton(ev);
			redraw();
			if (pressedButton instanceof SoftButton) {
				Command cmd = ((SoftButton) pressedButton).getCommand();
				if (cmd != null) {
					CommandManager.getInstance().commandAction(cmd);
				}
			}      
		}   
	  
		public void keyReleased(KeyEvent ev)
		{
			((SwtInputMethod) DeviceFactory.getDevice().getInputMethod()).keyboardKeyReleased(ev);
			prevOverButton = pressedButton;
			pressedButton = null;
			redraw();      
		}   
  };
  
	MouseAdapter mouseListener = new MouseAdapter() 
	{    
		public void mouseDown(MouseEvent e) 
		{
			pressedButton = getButton(e.x, e.y);
			if (pressedButton != null) {
				if (pressedButton instanceof SoftButton) {
					Command cmd = ((SoftButton) pressedButton).getCommand();
					if (cmd != null) {
						CommandManager.getInstance().commandAction(cmd);
					}
				} else {
					DeviceFactory.getDevice().getInputMethod().keyPressed(pressedButton.getKey());
				}
				redraw();
			}
		}

		public void mouseUp(MouseEvent e) 
		{
			SwtButton prevOverButton = getButton(e.x, e.y);
			if (prevOverButton != null) {
				DeviceFactory.getDevice().getInputMethod().keyReleased(prevOverButton.getKey());
			}
			pressedButton = null;
			redraw();      
		}
	};
  

	MouseMoveListener mouseMoveListener = new MouseMoveListener() 
	{
		// TODO poprawic mouseDragged	
/*		public void mouseDragged(MouseEvent e)
		{
			overButton = getButton(e.x, e.y);
		}*/

		public void mouseMove(MouseEvent e)
		{
			prevOverButton = overButton;
			overButton = getButton(e.x, e.y);
			if (overButton != prevOverButton) {
				redraw();
			}
		}    
	};
  
  
	public SwtDeviceComponent(Composite parent) 
	{
		super(parent, 0);
		instance = this;
    
		dc = new SwtDisplayComponent(parent, this);    
    
		addKeyListener(keyListener);
		addMouseListener(mouseListener);
		addMouseMoveListener(mouseMoveListener);
		addPaintListener(new PaintListener() 
		{
			public void paintControl(PaintEvent e) 
			{
				SwtDeviceComponent.this.paintControl(e);
			}
		});
	}
  
  
	public DisplayComponent getDisplayComponent()
	{
		return dc;
	}
  
  
	public Point computeSize(int wHint, int hHint, boolean changed)
	{
		Rectangle tmp = ((SwtDevice) DeviceFactory.getDevice()).getNormalImage().getBounds();
		
		return new Point(tmp.width, tmp.height);		
	}
							 
  
	public void paintControl(PaintEvent pe) 
	{
		SwtGraphics gc = new SwtGraphics(pe.gc);
		
		gc.drawImage(((SwtDevice) DeviceFactory.getDevice()).getNormalImage(), 0, 0);
    
		Rectangle displayRectangle = 
				((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getDisplayRectangle();
		gc.translate(displayRectangle.x, displayRectangle.y);
		dc.paint(gc);
		gc.translate(-displayRectangle.x, -displayRectangle.y);

		Rectangle rect;
		if (prevOverButton != null ) {
			rect = prevOverButton.getRectangle();    
			gc.drawImage(((SwtDevice) DeviceFactory.getDevice()).getNormalImage(), 
					rect.x, rect.y, rect.width, rect.height,
					rect.x, rect.y, rect.width, rect.height);
			prevOverButton = null;
		}
		if (overButton != null) {
			rect = overButton.getRectangle();   
			gc.drawImage(((SwtDevice) DeviceFactory.getDevice()).getOverImage(), 
					rect.x, rect.y, rect.width, rect.height,
					rect.x, rect.y, rect.width, rect.height);
		}
		if (pressedButton != null) {
			rect = pressedButton.getRectangle();    
			gc.drawImage(((SwtDevice) DeviceFactory.getDevice()).getPressedImage(), 
					rect.x, rect.y, rect.width, rect.height,
					rect.x, rect.y, rect.width, rect.height);
		}
	}


	private SwtButton getButton(int x, int y)
	{
		for (Enumeration e = ((SwtDevice) DeviceFactory.getDevice()).getButtons().elements(); e.hasMoreElements(); ) {
			SwtButton button = (SwtButton) e.nextElement();
			Rectangle tmp = new Rectangle(button.getRectangle().x, button.getRectangle().y,
					button.getRectangle().width, button.getRectangle().height);
			if (x >= tmp.x && x < tmp.x + tmp.width && y >= tmp.y && y < tmp.y + tmp.height) {
				return button;
			}
		}        
		return null;
	}

  
	private SwtButton getButton(KeyEvent ev)
	{
		for (Enumeration e = ((SwtDevice) DeviceFactory.getDevice()).getButtons().elements(); e.hasMoreElements(); ) {
			SwtButton button = (SwtButton) e.nextElement();
System.out.println(ev.keyCode +"+"+ ev.character +"+"+ button.isChar(ev.character));			
			if (ev.keyCode == button.getKey()) {
				return button;
			}
			if (button.isChar(ev.character)) {
				return button;
			}
		}
		        
		return null;
	}
	
	
	public static Image createImage(InputStream is) 
	{
		return new Image(instance.getParent().getDisplay(), is);
	}


	public static Color createColor(int rgb) 
	{
		return new Color(instance.getParent().getDisplay(),
				(rgb >> 16) % 256,
				(rgb >> 8) % 256,
				rgb % 256);
	}


	public static FontMetrics getFontMetrics(String name, int size, int style) 
	{
		SwtGraphics gc = new SwtGraphics(instance.getParent().getDisplay());
		gc.setFont(new Font(instance.getParent().getDisplay(), name, size, style));
		return gc.getFontMetrics();
	}
  
}
