/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.microemu.CommandManager;
import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.MutableImage;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.device.j2se.J2SEMutableImage;


public class SwingDisplayComponent extends JComponent implements DisplayComponent 
{
	private static final long serialVersionUID = 1L;

	private SwingDeviceComponent deviceComponent;
	
	private J2SEMutableImage displayImage = null;
	
	private SoftButton initialPressedSoftButton;
	
	private DisplayRepaintListener displayRepaintListener;
	
	private MouseAdapter mouseListener = new MouseAdapter() {

		public void mousePressed(MouseEvent e) {
			requestFocus();

			if (MIDletBridge.getCurrentMIDlet() == null) {
				return;
			}

			if (SwingUtilities.isMiddleMouseButton(e)) {
				// fire
				KeyEvent event = new KeyEvent(deviceComponent, 0, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER,
						KeyEvent.CHAR_UNDEFINED);
				deviceComponent.keyPressed(event);
				deviceComponent.keyReleased(event);
				return;
			}

			Device device = DeviceFactory.getDevice();
			J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();
			// if the displayable is in full screen mode, we should not
			// invoke any associated commands, but send the raw key codes
			// instead
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();

			if (device.hasPointerEvents()) {
				if (!fullScreenMode) {
					Iterator it = device.getSoftButtons().iterator();
					while (it.hasNext()) {
						SoftButton button = (SoftButton) it.next();
						if (button.isVisible()) {
							org.microemu.device.impl.Rectangle pb = button.getPaintable();
							if (pb != null && pb.contains(e.getX(), e.getY())) {
								initialPressedSoftButton = button;
								button.setPressed(true);
								repaintRequest(pb.x, pb.y, pb.width, pb.height);
								break;
							}
						}
					}
				}
				if (fullScreenMode) {
					inputMethod.pointerPressed(e.getX(), e.getY());
				} else {
					org.microemu.device.impl.Rectangle pb = ((J2SEDeviceDisplay) device.getDeviceDisplay())
							.getDisplayPaintable();
					inputMethod.pointerPressed(e.getX() - pb.x, e.getY() - pb.y);
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (MIDletBridge.getCurrentMIDlet() == null) {
				return;
			}

			Device device = DeviceFactory.getDevice();
			J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
			if (device.hasPointerEvents()) {
				if (!fullScreenMode) {
					if (initialPressedSoftButton != null && initialPressedSoftButton.isPressed()) {
						initialPressedSoftButton.setPressed(false);
						org.microemu.device.impl.Rectangle pb = initialPressedSoftButton.getPaintable();
						if (pb != null) {
							repaintRequest(pb.x, pb.y, pb.width, pb.height);
							if (pb.contains(e.getX(), e.getY())) {
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
					inputMethod.pointerReleased(e.getX(), e.getY());
				} else {
					org.microemu.device.impl.Rectangle pb = ((J2SEDeviceDisplay) device.getDeviceDisplay())
							.getDisplayPaintable();
					inputMethod.pointerReleased(e.getX() - pb.x, e.getY() - pb.y);
				}
			}
		}

	};
	
	private MouseMotionListener mouseMotionListener = new MouseMotionListener() {

		public void mouseDragged(MouseEvent e) {
			Device device = DeviceFactory.getDevice();
			InputMethodImpl inputMethod = (InputMethodImpl) device.getInputMethod();
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();
			if (device.hasPointerMotionEvents()) {
				if (!fullScreenMode) {
					if (initialPressedSoftButton != null) {
						org.microemu.device.impl.Rectangle pb = initialPressedSoftButton.getPaintable();
						if (pb != null) {
							if (pb.contains(e.getX(), e.getY())) {
								if (!initialPressedSoftButton.isPressed()) {
									initialPressedSoftButton.setPressed(true);
									repaintRequest(pb.x, pb.y, pb.width, pb.height);
								}
							} else {
								if (initialPressedSoftButton.isPressed()) {
									initialPressedSoftButton.setPressed(false);
									repaintRequest(pb.x, pb.y, pb.width, pb.height);
								}
							}
						}
					}
				}
				if (fullScreenMode) {
					inputMethod.pointerDragged(e.getX(), e.getY());
				} else {
					org.microemu.device.impl.Rectangle pb = ((J2SEDeviceDisplay) device.getDeviceDisplay())
							.getDisplayPaintable();
					inputMethod.pointerDragged(e.getX() - pb.x, e.getY() - pb.y);
				}
			}
		}

		public void mouseMoved(MouseEvent e) {
		}

	};
	
    private MouseWheelListener mouseWheelListener = new MouseWheelListener() {

		public void mouseWheelMoved(MouseWheelEvent ev) {
			if (ev.getWheelRotation() > 0) {
				// down
				KeyEvent event = new KeyEvent(deviceComponent, 0,
						System.currentTimeMillis(), 0, KeyEvent.VK_DOWN,
						KeyEvent.CHAR_UNDEFINED);
				deviceComponent.keyPressed(event);
				deviceComponent.keyReleased(event);
			} else {
				// up
				KeyEvent event = new KeyEvent(deviceComponent, 0,
						System.currentTimeMillis(), 0, KeyEvent.VK_UP,
						KeyEvent.CHAR_UNDEFINED);
				deviceComponent.keyPressed(event);
				deviceComponent.keyReleased(event);
			}
		}

	};


	SwingDisplayComponent(SwingDeviceComponent deviceComponent)
	{
		this.deviceComponent = deviceComponent;

		setFocusable(false);
    	
    	addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
        addMouseWheelListener(mouseWheelListener);
	}
	
	
	public void init()
	{
	    displayImage = null;
	    initialPressedSoftButton = null;
	}


	public void addDisplayRepaintListener(DisplayRepaintListener l) 
	{
		displayRepaintListener = l;
	}


	public void removeDisplayRepaintListener(DisplayRepaintListener l) 
	{
		if (displayRepaintListener == l) {
			displayRepaintListener = null;
		}
	}


	public MutableImage getDisplayImage()
	{
		return displayImage;
	}

	
	public Dimension getPreferredSize() {
  		Device device = DeviceFactory.getDevice();
  		if (device == null) {
  			return new Dimension(0, 0);
  		}  		
  		
  		return new Dimension(
  				device.getDeviceDisplay().getFullWidth(),
  				device.getDeviceDisplay().getFullHeight());
	}


	protected void paintComponent(Graphics g) 
	{
		if (displayImage != null) {
			synchronized (displayImage) {
				g.drawImage(displayImage.getImage(), 0, 0, null);
			}
		}
	}


	public void repaintRequest(int x, int y, int width, int height) 
	{
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}
		Displayable current = da.getCurrent();
		if (current == null) {
			return;
		}

		Device device = DeviceFactory.getDevice();

		if (device != null) {
			if (displayImage == null) {
				displayImage = new J2SEMutableImage(
						device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
			}
			
			synchronized (displayImage) {
				Graphics gc = displayImage.getImage().getGraphics();

				J2SEDeviceDisplay deviceDisplay = (J2SEDeviceDisplay) device.getDeviceDisplay();
				deviceDisplay.paintDisplayable(gc, x, y, width, height);
				if (!deviceDisplay.isFullScreenMode()) {
					deviceDisplay.paintControls(gc);
				}
			}
				
			fireDisplayRepaint(displayImage);
		}	

		repaint();
	}


	private void fireDisplayRepaint(MutableImage image)
	{
		if (displayRepaintListener != null) {
			displayRepaintListener.repaintInvoked(image);
		}
	}

}
