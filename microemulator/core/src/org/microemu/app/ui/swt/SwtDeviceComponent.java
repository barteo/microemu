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
import java.util.Iterator;

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
import org.microemu.device.Device;
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
	
	private SoftButton initialPressedSoftButton;

	private boolean mousePressed;
  
  KeyListener keyListener = new KeyListener()
  {
		public void keyPressed(KeyEvent ev)
		{
	    	if (MIDletBridge.getCurrentMIDlet() == null) {
	    		return;
	    	}

	    	Device device = DeviceFactory.getDevice();
	    	
			for (Iterator it = device.getButtons().iterator(); it.hasNext(); ) {
				SwtButton button = (SwtButton) it.next();
				if (ev.keyCode == button.getKeyboardKey()) {
					ev.keyCode = button.getKeyCode();
					break;
				}
			}
	    		    	
	    	((SwtInputMethod) device.getInputMethod()).keyPressed(ev);
			
			pressedButton = ((SwtInputMethod) device.getInputMethod()).getButton(ev);
			if (pressedButton != null) {
				org.microemu.device.impl.Shape shape = pressedButton.getShape();
				if (shape != null) {
					Rectangle r = shape.getBounds();
		  			redraw(r.x, r.y, r.width, r.height, true);
				}
			} else {
				redraw();
			}
		}   
	  
		public void keyReleased(KeyEvent ev)
		{
	    	if (MIDletBridge.getCurrentMIDlet() == null) {
	    		return;
	    	}

	    	Device device = DeviceFactory.getDevice();
	    	
			for (Iterator it = device.getButtons().iterator(); it.hasNext(); ) {
				SwtButton button = (SwtButton) it.next();
				if (ev.keyCode == button.getKeyboardKey()) {
					ev.keyCode = button.getKeyCode();
					break;
				}
			}

			((SwtInputMethod) device.getInputMethod()).keyReleased(ev);
			
			prevOverButton = pressedButton;
			pressedButton = null;
			if (prevOverButton != null) {
				org.microemu.device.impl.Shape shape = prevOverButton.getShape();
				if (shape != null) {
					Rectangle r = shape.getBounds();
		  			redraw(r.x, r.y, r.width, r.height, true);
				}
			} else {
				redraw();
			}
		}   
  };
  
	MouseAdapter mouseListener = new MouseAdapter() 
	{    
		public void mouseDown(MouseEvent e) 
		{
	    	if (MIDletBridge.getCurrentMIDlet() == null) {
	    		return;
	    	}
	    	
			Device device = DeviceFactory.getDevice();
			org.microemu.device.impl.Rectangle rect = ((SwtDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();
			SwtInputMethod inputMethod = (SwtInputMethod) device.getInputMethod();
			// if the displayable is in full screen mode, we should not
			// invoke any associated commands, but send the raw key codes
			// instead
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();

			if (rect.x <= e.x && (rect.x + rect.width) > e.x
					&& rect.y <= e.y && (rect.y + rect.height) > e.y) {
				if (device.hasPointerEvents()) {
					if (!fullScreenMode) {
						Iterator it = device.getSoftButtons().iterator();
						while (it.hasNext()) {
							SoftButton button = (SoftButton) it.next();
							if (button.isVisible()) {
								org.microemu.device.impl.Rectangle pb = button.getPaintable();
								if (pb != null && pb.contains(e.x - rect.x, e.y - rect.y)) {
									initialPressedSoftButton = button;
									button.setPressed(true);
									dc.repaint(pb.x, pb.y, pb.width, pb.height);
									break;
								}
							}
						}
					}
					if (fullScreenMode) {
						inputMethod.pointerPressed(e.x - rect.x, e.y - rect.y);
					} else {
						org.microemu.device.impl.Rectangle pb = ((SwtDeviceDisplay) device.getDeviceDisplay()).getDisplayPaintable();
						inputMethod.pointerPressed(e.x - rect.x - pb.x, e.y - rect.y - pb.y);
					}
				}
			} else {
		    	pressedButton = getButton(e.x, e.y);	
				if (pressedButton != null) {
					if (pressedButton instanceof SoftButton && !fullScreenMode) {
						Command cmd = ((SoftButton) pressedButton).getCommand();
						if (cmd != null) {
							CommandManager.getInstance().commandAction(cmd);
						}
					} else {
						Event event = new Event();
						event.widget = e.widget;
						KeyEvent ev = new KeyEvent(event);
						ev.keyCode = pressedButton.getKeyCode();
						inputMethod.mousePressed(ev);
					}
					// optimize for some video cards.
					Rectangle r = pressedButton.getShape().getBounds();
					redraw(r.x, r.y, r.width, r.height, true);
				}
			}
			
			mousePressed = true;
		}

		public void mouseUp(MouseEvent e) 
		{
	    	if (MIDletBridge.getCurrentMIDlet() == null) {
	    		return;
	    	}

			Device device = DeviceFactory.getDevice();
			org.microemu.device.impl.Rectangle rect = ((SwtDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();
			SwtInputMethod inputMethod = (SwtInputMethod) device.getInputMethod();
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
			if (rect.x <= e.x && (rect.x + rect.width) > e.x
					&& rect.y <= e.y && (rect.y + rect.height) > e.y) {
				if (device.hasPointerEvents()) {
					if (!fullScreenMode) {
						if (initialPressedSoftButton != null && initialPressedSoftButton.isPressed()) {
							initialPressedSoftButton.setPressed(false);
							org.microemu.device.impl.Rectangle pb = initialPressedSoftButton.getPaintable();
							if (pb != null) {
								dc.repaint(pb.x, pb.y, pb.width, pb.height);
								if (pb.contains(e.x - rect.x, e.y - rect.y)) {
									Command cmd = initialPressedSoftButton.getCommand();
									if (cmd != null) {
										CommandManager.getInstance().commandAction(cmd);
									}
								}
							}
						}
						initialPressedSoftButton = null;
					}
					if (fullScreenMode) {
						inputMethod.pointerReleased(e.x - rect.x, e.y - rect.y);
					} else {
						org.microemu.device.impl.Rectangle pb = ((SwtDeviceDisplay) device.getDeviceDisplay()).getDisplayPaintable();
						inputMethod.pointerReleased(e.x - rect.x - pb.x, e.y - rect.y - pb.y);
					}
				}
			} else {
		    	SwtButton prevOverButton = getButton(e.x, e.y);
				if (prevOverButton != null) {
					inputMethod.mouseReleased(prevOverButton.getKeyCode());
				}
				pressedButton = null;
				//	optimize for some video cards.
				if (prevOverButton != null) {
					Rectangle r = prevOverButton.getShape().getBounds();
					redraw(r.x, r.y, r.width, r.height, true);
				} else {
					redraw();
				}
			}
			
			mousePressed = false;
		}
	};
  

	MouseMoveListener mouseMoveListener = new MouseMoveListener() 
	{
		public void mouseMove(MouseEvent e)
		{
			prevOverButton = overButton;
			overButton = getButton(e.x, e.y);
			if (overButton != prevOverButton) {
	      		// optimize for some video cards.
	      		if (prevOverButton != null) {
	      			Rectangle r = prevOverButton.getShape().getBounds();
	      			redraw(r.x, r.y, r.width, r.height, true);
	      		}
	      		if (overButton != null) {
	      			Rectangle r = overButton.getShape().getBounds();
	      			redraw(r.x, r.y, r.width, r.height, true);
	      		}
			}
			
			if (mousePressed) {
				Device device = DeviceFactory.getDevice();
				org.microemu.device.impl.Rectangle rect = ((SwtDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();
				SwtInputMethod inputMethod = (SwtInputMethod) device.getInputMethod();
				boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
				if (rect.x <= e.x && (rect.x + rect.width) > e.x
						&& rect.y <= e.y && (rect.y + rect.height) > e.y) {
					if (device.hasPointerMotionEvents()) {
						if (!fullScreenMode) {
							if (initialPressedSoftButton != null) {
								org.microemu.device.impl.Rectangle pb = initialPressedSoftButton.getPaintable();
								if (pb != null) {
									if (pb.contains(e.x - rect.x, e.y - rect.y)) {
										if (!initialPressedSoftButton.isPressed()) {
											initialPressedSoftButton.setPressed(true);
											dc.repaint(pb.x, pb.y, pb.width, pb.height);
										}
									} else {
										if (initialPressedSoftButton.isPressed()) {
											initialPressedSoftButton.setPressed(false);
											dc.repaint(pb.x, pb.y, pb.width, pb.height);
										}
									}
								}
							}
						}
						if (fullScreenMode) {
							inputMethod.pointerDragged(e.x - rect.x, e.y - rect.y);
						} else {
							org.microemu.device.impl.Rectangle pb = ((SwtDeviceDisplay) device.getDeviceDisplay()).getDisplayPaintable();
							inputMethod.pointerDragged(e.x - rect.x - pb.x, e.y - rect.y - pb.y);
						}
					}
				}
			}
		}    
	};
  
  
	public SwtDeviceComponent(Composite parent) 
	{
		super(parent, SWT.NO_BACKGROUND);
		instance = this;
		mousePressed = false;
    
		dc = new SwtDisplayComponent(this);    
    
	    this.initialPressedSoftButton = null;

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
			Device device = DeviceFactory.getDevice();
			
			gc.drawImage(((SwtImmutableImage) device.getNormalImage()).getImage()
			        , 0, 0);
    
			org.microemu.device.impl.Rectangle displayRectangle = 
					((SwtDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();
			gc.translate(displayRectangle.x, displayRectangle.y);
			dc.paint(gc);
			gc.translate(-displayRectangle.x, -displayRectangle.y);

	    	if (prevOverButton != null) {
				org.microemu.device.impl.Shape shape = prevOverButton.getShape();
				if (shape != null) {
					drawImageInShape(
							gc, 
							((SwtImmutableImage) device.getNormalImage()).getImage(), 
							shape);
				}
				prevOverButton = null;
			}
			if (overButton != null) {
				org.microemu.device.impl.Shape shape = overButton.getShape();
				if (shape != null) {
					drawImageInShape(
							gc, 
							((SwtImmutableImage) device.getOverImage()).getImage(), 
							shape);
				}
			}
			if (pressedButton != null) {
				org.microemu.device.impl.Shape shape = pressedButton.getShape();
				if (shape != null) {
					drawImageInShape(
							gc, 
							((SwtImmutableImage) device.getPressedImage()).getImage(), 
							shape);
				}
			}

			org.microemu.device.impl.Rectangle rect;
			if (prevOverButton != null ) {				
				rect = prevOverButton.getShape().getBounds();
				if (rect != null) {
					gc.drawImage(((SwtImmutableImage) DeviceFactory.getDevice().getNormalImage()).getImage(), 
							rect.x, rect.y, rect.width, rect.height,
							rect.x, rect.y, rect.width, rect.height);
				}
				prevOverButton = null;
			}
			if (overButton != null) {
				rect = overButton.getShape().getBounds();
				if (rect != null) {
					gc.drawImage(((SwtImmutableImage) DeviceFactory.getDevice().getOverImage()).getImage(), 
							rect.x, rect.y, rect.width, rect.height,
							rect.x, rect.y, rect.width, rect.height);
				}
			}
			if (pressedButton != null) {
				rect = pressedButton.getShape().getBounds();
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


  	private void drawImageInShape(SwtGraphics g, Image image, org.microemu.device.impl.Shape shape) {
  		org.eclipse.swt.graphics.Rectangle clipSave = g.getClipping();
		if (shape instanceof org.microemu.device.impl.Polygon) {
			// TODO not implemented yet
//			g.setCliping(region);
		}
		org.microemu.device.impl.Rectangle r = shape.getBounds();
		g.drawImage(image, 
				r.x, r.y, r.width, r.height,
				r.x, r.y, r.width, r.height);
		g.setClipping(clipSave);
	}
  	
  	
	private SwtButton getButton(int x, int y)
	{
		for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e.hasMoreElements(); ) {
			SwtButton button = (SwtButton) e.nextElement();
			if (button.getShape() != null) {
				try {
					org.microemu.device.impl.Shape tmp = (org.microemu.device.impl.Shape) button.getShape().clone();
				      if (tmp.contains(x, y)) {
				    	  return button;
				      }
				} catch (CloneNotSupportedException ex) {
					ex.printStackTrace();
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
		private boolean antialiasing;
		private Font font;
		
		GetFontRunnable(String name, int size, int style, boolean antialiasing)
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
			gc.setAntialias(antialiasing);
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


	public static Font getFont(String name, int size, int style, boolean antialiasing) 
	{
		GetFontRunnable getFontRunnable = instance.new GetFontRunnable(name, size, style, antialiasing);
		
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
