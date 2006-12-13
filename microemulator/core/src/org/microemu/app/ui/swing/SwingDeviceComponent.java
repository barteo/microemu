/*
 *  MicroEmulator
 *  Copyright (C) 2001,2002 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.microedition.lcdui.Command;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.microemu.CommandManager;
import org.microemu.DisplayComponent;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.Device;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.j2se.J2SEButton;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEImmutableImage;
import org.microemu.device.j2se.J2SEInputMethod;


public class SwingDeviceComponent extends JPanel implements KeyListener
{
  SwingDeviceComponent instance;
	SwingDisplayComponent dc;

  J2SEButton prevOverButton;
  J2SEButton overButton;
  J2SEButton pressedButton;
  
  private SoftButton initialPressedSoftButton;
  
	Image offi;
	Graphics offg;
      
  private MouseAdapter mouseListener = new MouseAdapter() 
  {
    
	  	public void mousePressed(MouseEvent e) {
			requestFocus();

			if (MIDletBridge.getCurrentMIDlet() == null) {
				return;
			}

			Device device = DeviceFactory.getDevice();
			org.microemu.device.impl.Rectangle rect = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();
			J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();
			// if the displayable is in full screen mode, we should not
			// invoke any associated commands, but send the raw key codes
			// instead
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();

			if (rect.x <= e.getX() && (rect.x + rect.width) > e.getX()
					&& rect.y <= e.getY() && (rect.y + rect.height) > e.getY()) {
				if (device.hasPointerEvents()) {
					if (!fullScreenMode) {
						Iterator it = device.getSoftButtons().iterator();
						while (it.hasNext()) {
							SoftButton button = (SoftButton) it.next();
							if (button.isVisible()) {
								org.microemu.device.impl.Rectangle pb = button.getPaintable();
								if (pb != null && pb.contains(e.getX() - rect.x, e.getY() - rect.y)) {
									initialPressedSoftButton = button;
									button.setPressed(true);
									dc.repaint(pb.x, pb.y, pb.width, pb.height);
									break;
								}
							}
						}
					}
					if (fullScreenMode) {
						inputMethod.pointerPressed(e.getX() - rect.x, e.getY() - rect.y);
					} else {
						org.microemu.device.impl.Rectangle pb = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayPaintable();
						inputMethod.pointerPressed(e.getX() - rect.x - pb.x, e.getY() - rect.y - pb.y);
					}
				}
			} else {
				pressedButton = getButton(e.getX(), e.getY());
				if (pressedButton != null) {
					if (pressedButton instanceof SoftButton && !fullScreenMode) {
						Command cmd = ((SoftButton) pressedButton).getCommand();
						if (cmd != null) {
							CommandManager.getInstance().commandAction(cmd);
						}
					} else {
						int key = pressedButton.getKey();
						KeyEvent ev = new KeyEvent(instance, 0, 0, 0, key,
								KeyEvent.CHAR_UNDEFINED);
						inputMethod.mousePressed(ev);
					}
					// optimize for some video cards.
					Rectangle r = pressedButton.getShape().getBounds();
	      			repaint(r.x, r.y, r.width, r.height);
				}
			}
		}


	public void mouseReleased(MouseEvent e) {
			if (MIDletBridge.getCurrentMIDlet() == null) {
				return;
			}

			Device device = DeviceFactory.getDevice();
			org.microemu.device.impl.Rectangle rect = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();
			J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
			if (rect.x <= e.getX() && (rect.x + rect.width) > e.getX()
					&& rect.y <= e.getY() && (rect.y + rect.height) > e.getY()) {
				if (device.hasPointerEvents()) {
					if (!fullScreenMode) {
						if (initialPressedSoftButton != null && initialPressedSoftButton.isPressed()) {
							initialPressedSoftButton.setPressed(false);
							org.microemu.device.impl.Rectangle pb = initialPressedSoftButton.getPaintable();
							if (pb != null) {
								dc.repaint(pb.x, pb.y, pb.width, pb.height);
								if (pb.contains(e.getX() - rect.x, e.getY() - rect.y)) {
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
						inputMethod.pointerReleased(e.getX() - rect.x, e.getY() - rect.y);
					} else {
						org.microemu.device.impl.Rectangle pb = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayPaintable();
						inputMethod.pointerReleased(e.getX() - rect.x - pb.x, e.getY() - rect.y - pb.y);
					}
				}
			} else {
				J2SEButton prevOverButton = getButton(e.getX(), e.getY());
				if (prevOverButton != null) {
					int key = prevOverButton.getKey();
					KeyEvent ev = new KeyEvent(instance, 0, 0, 0, key,
							KeyEvent.CHAR_UNDEFINED);

					inputMethod.mouseReleased(ev.getKeyCode());
				}
				pressedButton = null;
				//	optimize for some video cards.
				if (prevOverButton != null) {
					Rectangle r = prevOverButton.getShape().getBounds();
	      			repaint(r.x, r.y, r.width, r.height);
	      		} else {
	      			repaint();
	      		}
			}
		}

  };
  

  private MouseMotionListener mouseMotionListener = new MouseMotionListener() 
  {

    public void mouseDragged(MouseEvent e)
    {
    	overButton = getButton(e.getX(), e.getY());
    	
		Device device = DeviceFactory.getDevice();
		org.microemu.device.impl.Rectangle rect = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();
		InputMethodImpl inputMethod = (InputMethodImpl) device.getInputMethod();
		boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
		if (rect.x <= e.getX() && (rect.x + rect.width) > e.getX()
				&& rect.y <= e.getY() && (rect.y + rect.height) > e.getY()) {
			if (device.hasPointerMotionEvents()) {
				if (!fullScreenMode) {
					if (initialPressedSoftButton != null) {
						org.microemu.device.impl.Rectangle pb = initialPressedSoftButton.getPaintable();
						if (pb != null) {
							if (pb.contains(e.getX() - rect.x, e.getY() - rect.y)) {
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
					inputMethod.pointerDragged(e.getX() - rect.x, e.getY() - rect.y);
				} else {
					org.microemu.device.impl.Rectangle pb = ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayPaintable();
					inputMethod.pointerDragged(e.getX() - rect.x - pb.x, e.getY() - rect.y - pb.y);
				}
			}
		}
    }

    
    public void mouseMoved(MouseEvent e)
    {
    	prevOverButton = overButton;
    	overButton = getButton(e.getX(), e.getY());
      	if (overButton != prevOverButton) {
      		// optimize for some video cards.
      		if (prevOverButton != null) {
      			Rectangle r = prevOverButton.getShape().getBounds();
      			repaint(r.x, r.y, r.width, r.height);
      		}
      		if (overButton != null) {
      			Rectangle r = overButton.getShape().getBounds();
      			repaint(r.x, r.y, r.width, r.height);
      		}
      	}
    }
    
  };
  
  
  public SwingDeviceComponent() 
  {
    instance = this;
    
    dc = new SwingDisplayComponent(this);    
    
    this.initialPressedSoftButton = null;
    
    addMouseListener(mouseListener);
    addMouseMotionListener(mouseMotionListener);
  }
  
  
  public DisplayComponent getDisplayComponent()
  {
    return dc;
  }
  
  
  public void init()
  {
      dc.init();
      
      revalidate();
  }
  
  
  	public void keyTyped(KeyEvent ev) 
  	{
    	if (MIDletBridge.getCurrentMIDlet() == null) {
    		return;
    	}

    	((J2SEInputMethod) DeviceFactory.getDevice().getInputMethod()).keyTyped(ev);
	}
  
  
  	public void keyPressed(KeyEvent ev)
  	{
    	if (MIDletBridge.getCurrentMIDlet() == null) {
    		return;
    	}
    	
    	J2SEInputMethod inputMethod = (J2SEInputMethod) DeviceFactory.getDevice().getInputMethod();
		
		if (ev.getKeyCode() == KeyEvent.VK_V && (ev.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			try {
				Object data = clipboard.getData(DataFlavor.stringFlavor);
				if (data instanceof String) {
					inputMethod.clipboardPaste((String) data);
				}
			} catch (UnsupportedFlavorException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return;
		}
		
		inputMethod.keyPressed(ev);
		
		pressedButton = inputMethod.getButton(ev);
		repaint();
  	}
   
  
  	public void keyReleased(KeyEvent ev) 
  	{
    	if (MIDletBridge.getCurrentMIDlet() == null) {
    		return;
    	}

		((J2SEInputMethod) DeviceFactory.getDevice().getInputMethod())
				.keyReleased(ev);

		prevOverButton = pressedButton;
		pressedButton = null;
		repaint();
	}
  	
  	
  	public MouseListener getDefaultMouseListener()
  	{
  		return mouseListener;
  	}
  	
  	
  	public MouseMotionListener getDefaultMouseMotionListener()
  	{
  		return mouseMotionListener;
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
    Device device = DeviceFactory.getDevice();
    if (device == null) 
    {
        g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        return;
    }
    offg.drawImage(((J2SEImmutableImage) device.getNormalImage()).getImage(),
              0, 0, this);
    
    org.microemu.device.impl.Rectangle displayRectangle = 
        ((J2SEDeviceDisplay) device.getDeviceDisplay()).getDisplayRectangle();
    offg.translate(displayRectangle.x, displayRectangle.y);
    dc.paint(offg);
    offg.translate(-displayRectangle.x, -displayRectangle.y);

    	if (prevOverButton != null) {
			org.microemu.device.impl.Shape shape = prevOverButton.getShape();
			if (shape != null) {
				drawImageInShape(
						offg, 
						((J2SEImmutableImage) device.getNormalImage()).getImage(), 
						shape);
			}
			prevOverButton = null;
		}
		if (overButton != null) {
			org.microemu.device.impl.Shape shape = overButton.getShape();
			if (shape != null) {
				drawImageInShape(
						offg, 
						((J2SEImmutableImage) device.getOverImage()).getImage(), 
						shape);
			}
		}
		if (pressedButton != null) {
			org.microemu.device.impl.Shape shape = pressedButton.getShape();
			if (shape != null) {
				drawImageInShape(
						offg, 
						((J2SEImmutableImage) device.getPressedImage()).getImage(), 
						shape);
			}
		}
    
 		g.drawImage(offi, 0, 0, null);
  	}
  
  	
  	private void drawImageInShape(Graphics g, Image image, org.microemu.device.impl.Shape shape) {
  		Shape clipSave = g.getClip();
		if (shape instanceof org.microemu.device.impl.Polygon) {
			Polygon poly = new Polygon(
					((org.microemu.device.impl.Polygon) shape).xpoints,
					((org.microemu.device.impl.Polygon) shape).ypoints,
					((org.microemu.device.impl.Polygon) shape).npoints);
			g.setClip(poly);
		}
		org.microemu.device.impl.Rectangle r = shape.getBounds();
		g.drawImage(image, r.x, r.y, r.x + r.width, r.y + r.height, r.x,
				r.y, r.x + r.width, r.y + r.height, null);
		g.setClip(clipSave);
	}


	public void update(Graphics g)
	{
		paint(g);
	}
 
  
  private J2SEButton getButton(int x, int y)
  {
    for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e.hasMoreElements(); ) {
      J2SEButton button = (J2SEButton) e.nextElement();
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
  
  	public Dimension getPreferredSize() {
  		javax.microedition.lcdui.Image img = DeviceFactory.getDevice().getNormalImage();
  		
  		return new Dimension(img.getWidth(), img.getHeight());
	}

}
