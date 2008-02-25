/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *  
 *  @version $Id$
 */

package org.microemu.app.ui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
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

import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.Common;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.MutableImage;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.device.j2se.J2SEMutableImage;

public class SwingDisplayComponent extends JComponent implements DisplayComponent {
	private static final long serialVersionUID = 1L;

	private SwingDeviceComponent deviceComponent;

	private J2SEMutableImage displayImage = null;

	private SoftButton initialPressedSoftButton;

	private DisplayRepaintListener displayRepaintListener;

	private boolean showMouseCoordinates = false;

	private Point pressedPoint = new Point();

	private MouseAdapter mouseListener = new MouseAdapter() {

		public void mousePressed(MouseEvent e) {
			deviceComponent.requestFocus();
			pressedPoint = e.getPoint();

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
				Point p = deviceCoordinate(device.getDeviceDisplay(), e.getPoint());
				inputMethod.pointerPressed(p.x, p.y);
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
									MIDletAccess ma = MIDletBridge.getMIDletAccess();
									if (ma == null) {
										return;
									}
									DisplayAccess da = ma.getDisplayAccess();
									if (da == null) {
										return;
									}
									da.commandAction(cmd, da.getCurrent());
								}
							}
						}
					}
					initialPressedSoftButton = null;
				}
				Point p = deviceCoordinate(device.getDeviceDisplay(), e.getPoint());
				inputMethod.pointerReleased(p.x, p.y);
			}
		}

	};

	private MouseMotionListener mouseMotionListener = new MouseMotionListener() {

		public void mouseDragged(MouseEvent e) {
			if (showMouseCoordinates) {
				StringBuffer buf = new StringBuffer();
				int width = e.getX() - pressedPoint.x;
				int height = e.getY() - pressedPoint.y;
				Point p = deviceCoordinate(DeviceFactory.getDevice().getDeviceDisplay(), pressedPoint);
				buf.append(p.x).append(",").append(p.y).append(" ").append(width).append("x").append(height);
				Common.setStatusBar(buf.toString());
			}

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
				Point p = deviceCoordinate(device.getDeviceDisplay(), e.getPoint());
				inputMethod.pointerDragged(p.x, p.y);
			}
		}

		public void mouseMoved(MouseEvent e) {
			if (showMouseCoordinates) {
				StringBuffer buf = new StringBuffer();
				Point p = deviceCoordinate(DeviceFactory.getDevice().getDeviceDisplay(), e.getPoint());
				buf.append(p.x).append(",").append(p.y);
				Common.setStatusBar(buf.toString());
			}
		}

	};

	private MouseWheelListener mouseWheelListener = new MouseWheelListener() {

		public void mouseWheelMoved(MouseWheelEvent ev) {
			if (ev.getWheelRotation() > 0) {
				// down
				KeyEvent event = new KeyEvent(deviceComponent, 0, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN,
						KeyEvent.CHAR_UNDEFINED);
				deviceComponent.keyPressed(event);
				deviceComponent.keyReleased(event);
			} else {
				// up
				KeyEvent event = new KeyEvent(deviceComponent, 0, System.currentTimeMillis(), 0, KeyEvent.VK_UP,
						KeyEvent.CHAR_UNDEFINED);
				deviceComponent.keyPressed(event);
				deviceComponent.keyReleased(event);
			}
		}

	};

	SwingDisplayComponent(SwingDeviceComponent deviceComponent) {
		this.deviceComponent = deviceComponent;

		setFocusable(false);

		addMouseListener(mouseListener);
		addMouseMotionListener(mouseMotionListener);
		addMouseWheelListener(mouseWheelListener);
	}

	public void init() {
		synchronized (this) {
			displayImage = null;
			initialPressedSoftButton = null;
		}
	}

	public void addDisplayRepaintListener(DisplayRepaintListener l) {
		displayRepaintListener = l;
	}

	public void removeDisplayRepaintListener(DisplayRepaintListener l) {
		if (displayRepaintListener == l) {
			displayRepaintListener = null;
		}
	}

	public MutableImage getDisplayImage() {
		return displayImage;
	}

	public Dimension getPreferredSize() {
		Device device = DeviceFactory.getDevice();
		if (device == null) {
			return new Dimension(0, 0);
		}

		return new Dimension(device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
	}

	protected void paintComponent(Graphics g) {
		if (displayImage != null) {
			synchronized (displayImage) {
				g.drawImage(displayImage.getImage(), 0, 0, null);
			}
		}
	}

	public void repaintRequest(int x, int y, int width, int height) {
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
			synchronized (this) {
				if (displayImage == null) {
					displayImage = new J2SEMutableImage(device.getDeviceDisplay().getFullWidth(), device
							.getDeviceDisplay().getFullHeight());
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
		}

		repaint();
	}

	private void fireDisplayRepaint(MutableImage image) {
		if (displayRepaintListener != null) {
			displayRepaintListener.repaintInvoked(image);
		}
	}

	Point deviceCoordinate(DeviceDisplay deviceDisplay, Point p) {
		if (deviceDisplay.isFullScreenMode()) {
			return p;
		} else {
			org.microemu.device.impl.Rectangle pb = ((J2SEDeviceDisplay) deviceDisplay).getDisplayPaintable();
			return new Point(p.x - pb.x, p.y - pb.y);
		}
	}

	void switchShowMouseCoordinates() {
		showMouseCoordinates = !showMouseCoordinates;
	}

}
