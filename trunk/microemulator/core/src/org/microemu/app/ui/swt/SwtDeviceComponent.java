/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.swt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;

import javax.microedition.lcdui.Command;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.microemu.CommandManager;
import org.microemu.DisplayComponent;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.swt.SwtButton;
import org.microemu.device.swt.SwtDeviceDisplay;
import org.microemu.device.swt.SwtImmutableImage;
import org.microemu.device.swt.SwtInputMethod;



public class SwtDeviceComponent extends Canvas
{
	private static SwtDeviceComponent instance;
	private static HashMap colors = new HashMap();

	private SwtDisplayComponent dc;
	
	private Image fBuffer = null;

	private SwtButton prevOverButton;
	private SwtButton overButton;
	private SwtButton pressedButton;
  
  KeyListener keyListener = new KeyListener()
  {
		public void keyPressed(KeyEvent ev)
		{
	    	if (MIDletBridge.getCurrentMIDlet() == null) {
	    		return;
	    	}

	    	((SwtInputMethod) DeviceFactory.getDevice().getInputMethod()).keyPressed(ev);
			
			pressedButton = ((SwtInputMethod) DeviceFactory.getDevice().getInputMethod()).getButton(ev);
			redraw();
		}   
	  
		public void keyReleased(KeyEvent ev)
		{
	    	if (MIDletBridge.getCurrentMIDlet() == null) {
	    		return;
	    	}

	    	((SwtInputMethod) DeviceFactory.getDevice().getInputMethod()).keyReleased(ev);
			
			prevOverButton = pressedButton;
			pressedButton = null;
			redraw();      
		}   
  };
  
	MouseAdapter mouseListener = new MouseAdapter() 
	{    
		public void mouseDown(MouseEvent e) 
		{
	    	if (MIDletBridge.getCurrentMIDlet() == null) {
	    		return;
	    	}

	    	pressedButton = getButton(e.x, e.y);

			// if the displayable is in full screen mode, we should not
			// invoke any associated commands, but send the raw key codes
			// instead
			boolean rawSoftKeys = DeviceFactory.getDevice().getDeviceDisplay().isFullScreenMode();

			if (pressedButton != null) {
				if (pressedButton instanceof SoftButton && !rawSoftKeys) {
					Command cmd = ((SoftButton) pressedButton).getCommand();
					if (cmd != null) {
						CommandManager.getInstance().commandAction(cmd);
					}
				} else {
					Event event = new Event();
					event.widget = e.widget;
					KeyEvent ev = new KeyEvent(event);
					ev.keyCode = pressedButton.getKey();
					((SwtInputMethod) DeviceFactory.getDevice().getInputMethod()).mousePressed(ev);
				}
				redraw();
			}
		}

		public void mouseUp(MouseEvent e) 
		{
	    	if (MIDletBridge.getCurrentMIDlet() == null) {
	    		return;
	    	}

	    	SwtButton prevOverButton = getButton(e.x, e.y);
			if (prevOverButton != null) {
				((SwtInputMethod) DeviceFactory.getDevice().getInputMethod()).mouseReleased(prevOverButton.getKey());
			}
			pressedButton = null;
			redraw();      
		}
	};
  

	MouseMoveListener mouseMoveListener = new MouseMoveListener() 
	{
		public void mouseDragged(MouseEvent e)
		{
		}

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
		super(parent, SWT.NO_BACKGROUND);
		instance = this;
    
		dc = new SwtDisplayComponent(this);    
    
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
		javax.microedition.lcdui.Image tmp = DeviceFactory.getDevice().getNormalImage();

		return new Point(tmp.getWidth(), tmp.getHeight());		
	}
							 
  
	public void paintControl(PaintEvent pe) 
	{
		Point size= getSize();

		if (size.x <= 0 || size.y <= 0)
		 return;

		if (fBuffer != null) {
			org.eclipse.swt.graphics.Rectangle r= fBuffer.getBounds();
			if (r.width != size.x || r.height != size.y) {
			fBuffer.dispose();
			fBuffer= null;
			}
		}
		if (fBuffer == null) {
			fBuffer= new Image(getDisplay(), size.x, size.y);
		}

		SwtGraphics gc = new SwtGraphics(new GC(fBuffer));
		try {
			gc.drawImage(((SwtImmutableImage) DeviceFactory.getDevice().getNormalImage()).getImage()
			        , 0, 0);
    
			Rectangle displayRectangle = 
					((SwtDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getDisplayRectangle();
			gc.translate(displayRectangle.x, displayRectangle.y);
			dc.paint(gc);
			gc.translate(-displayRectangle.x, -displayRectangle.y);

			Rectangle rect;
			if (prevOverButton != null ) {				
				rect = prevOverButton.getRectangle();
				if (rect != null) {
					gc.drawImage(((SwtImmutableImage) DeviceFactory.getDevice().getNormalImage()).getImage(), 
							rect.x, rect.y, rect.width, rect.height,
							rect.x, rect.y, rect.width, rect.height);
				}
				prevOverButton = null;
			}
			if (overButton != null) {
				rect = overButton.getRectangle();
				if (rect != null) {
					gc.drawImage(((SwtImmutableImage) DeviceFactory.getDevice().getOverImage()).getImage(), 
							rect.x, rect.y, rect.width, rect.height,
							rect.x, rect.y, rect.width, rect.height);
				}
			}
			if (pressedButton != null) {
				rect = pressedButton.getRectangle();
				if (rect != null) {
					gc.drawImage(((SwtImmutableImage) DeviceFactory.getDevice().getPressedImage()).getImage(), 
							rect.x, rect.y, rect.width, rect.height,
							rect.x, rect.y, rect.width, rect.height);
				}
			}
		} finally {
			gc.dispose();
		}
		
		pe.gc.drawImage(fBuffer, 0, 0);
	}


	private SwtButton getButton(int x, int y)
	{
		for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e.hasMoreElements(); ) {
			SwtButton button = (SwtButton) e.nextElement();
			if (button.getRectangle() != null) {
				Rectangle tmp = button.getRectangle();
				if (x >= tmp.x && x < tmp.x + tmp.width && y >= tmp.y && y < tmp.y + tmp.height) {
					return button;
				}
			}
		}        
		return null;
	}

  
	private class CreateImageRunnable implements Runnable
	{
		private ImageData data;
		private Image image;
		
		CreateImageRunnable(ImageData data)
		{
			this.data = data;
		}
		
		Image getImage()
		{
			return image;
		}
			
		public void run() 
		{
			image = new Image(instance.getParent().getDisplay(), data);
		}		
	}
	
	
	public static Image createImage(int width, int height)
	{
		return new Image(instance.getDisplay(), width, height);
	}


	public static Image createImage(Image image)
	{
		return new Image(instance.getDisplay(), image, SWT.IMAGE_COPY);
	}
	
	
	public static Image createImage(ImageData data)
	{
		CreateImageRunnable createImageRunnable = instance.new CreateImageRunnable(data);		
		instance.getDisplay().syncExec(createImageRunnable); 
		
		return createImageRunnable.getImage(); 
	}

	
	public static Image createImage(InputStream is) 
	{
		ImageData data = new ImageData(is);
			
		CreateImageRunnable createImageRunnable = instance.new CreateImageRunnable(data);		
		instance.getDisplay().syncExec(createImageRunnable); 
		
		return createImageRunnable.getImage(); 
	}
	
	
	public static Image createImage(InputStream is, ImageFilter filter)
			throws IOException
	{
		try {
			ImageData data = new ImageData(is);
		
			RGB[] rgbs = data.getRGBs();
			if (rgbs != null) {	
				for (int i = 0; i < rgbs.length; i++) {
					rgbs[i] = filter.filterRGB(0, 0, rgbs[i]);
				}
			} else {
				RGB rgb;
				int pixel;
				for (int y = 0; y < data.height; y++) {
					for (int x = 0; x < data.width; x++) {
						pixel = data.getPixel(x, y);
						rgb = new RGB((pixel >> 16) & 255, (pixel >> 8) & 255, pixel & 255);
						rgb = filter.filterRGB(x, y, rgb);					
						data.setPixel(x, y, (rgb.red << 16) + (rgb.green << 8) + rgb.blue);
					}
				}
			}		
				
			CreateImageRunnable createImageRunnable = instance.new CreateImageRunnable(data);		
			instance.getDisplay().syncExec(createImageRunnable); 
			
			return createImageRunnable.getImage(); 
		} catch (SWTException ex) {
			throw new IOException(ex.toString());
		}
	}
	
	
	private class CreateColorRunnable implements Runnable
	{
		private RGB rgb;
		private Color color;
		
		CreateColorRunnable(RGB rgb)
		{
			this.rgb = rgb;
		}
		
		Color getColor()
		{
			return color;
		}
			
		public void run() 
		{
			color = new Color(instance.getParent().getDisplay(), rgb);
		}		
	}


	public static Color getColor(RGB rgb) 
	{
		Color result = (Color) colors.get(rgb);
		
		if (result == null) {
			CreateColorRunnable createColorRunnable = instance.new CreateColorRunnable(rgb);		
			instance.getDisplay().syncExec(createColorRunnable); 		
			result = createColorRunnable.getColor();
			colors.put(rgb, result);
		}
		
		return result;
	}


	private class GetFontMetricsRunnable implements Runnable
	{
		private Font font;
		private FontMetrics fontMetrics;
		
		GetFontMetricsRunnable(Font font)
		{
			this.font = font;
		}
		
		FontMetrics getFontMetrics()
		{
			return fontMetrics;
		}
			
		public void run() 
		{
			SwtGraphics gc = new SwtGraphics(instance.getParent().getDisplay());
			gc.setFont(font);
			fontMetrics = gc.getFontMetrics();
			gc.dispose();
		}		
	}


	private class GetFontRunnable implements Runnable
	{
		private String name;
		private int size;
		private int style;
		private Font font;
		
		GetFontRunnable(String name, int size, int style)
		{
			this.name = name;
			this.size = size;
			this.style = style;
		}
		
		Font getFont()
		{
			return font;
		}
			
		public void run() 
		{
			SwtGraphics gc = new SwtGraphics(instance.getParent().getDisplay());
			gc.setFont(new Font(instance.getParent().getDisplay(), name, size, style));
			font = gc.getFont();
			gc.dispose();
		}		
	}


	private class StringWidthRunnable implements Runnable
	{
		private Font font;
		private String str;
		private int stringWidth;
		
		StringWidthRunnable(Font font, String str)
		{
			this.font = font;
			this.str = str;
		}
		
		int stringWidth()
		{
			return stringWidth;
		}
			
		public void run() 
		{
			SwtGraphics gc = new SwtGraphics(instance.getParent().getDisplay());
			gc.setFont(font);
			stringWidth = gc.stringWidth(str);
			gc.dispose();
		}		
	}


	public static Font getFont(String name, int size, int style) 
	{
		GetFontRunnable getFontRunnable = instance.new GetFontRunnable(name, size, style);
		
		instance.getDisplay().syncExec(getFontRunnable); 
		
		return getFontRunnable.getFont(); 
	}


	public static FontMetrics getFontMetrics(Font font) 
	{
		GetFontMetricsRunnable getFontMetricsRunnable = instance.new GetFontMetricsRunnable(font);
		
		instance.getDisplay().syncExec(getFontMetricsRunnable); 
		
		return getFontMetricsRunnable.getFontMetrics(); 
	}
	
	
	public static int stringWidth(Font font, String str)
	{
		StringWidthRunnable stringWidthRunnable = instance.new StringWidthRunnable(font, str);
		
		instance.getDisplay().syncExec(stringWidthRunnable); 

		return stringWidthRunnable.stringWidth(); 
	}
  
}
