/*
 *  MicroEmulator
 *  Copyright (C) 2001,2002 Bartek Teodorczyk <barteo@barteo.net>
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Command;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.Common;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.impl.ui.CommandManager;
import org.microemu.device.j2se.J2SEButton;
import org.microemu.device.j2se.J2SEDeviceButtonsHelper;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEImmutableImage;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.device.j2se.J2SEMutableImage;
import org.microemu.log.Logger;

public class SwingDeviceComponent extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;

	SwingDisplayComponent dc;

	J2SEButton prevOverButton;

	J2SEButton overButton;

	J2SEButton pressedButton;

	private boolean mouseButtonDown = false;

	Image offi;

	Graphics offg;

	private boolean showMouseCoordinates = false;

	private int pressedX;

	private int pressedY;

	private static class MouseRepeatedTimerTask extends TimerTask {

		private static final int DELAY = 100;

		Timer timer;

		Component source;

		J2SEButton button;

		J2SEInputMethod inputMethod;

		static MouseRepeatedTimerTask task;

		static void schedule(Component source, J2SEButton button, J2SEInputMethod inputMethod) {
			if (task != null) {
				task.cancel();
			}
			task = new MouseRepeatedTimerTask();
			task.source = source;
			task.button = button;
			task.inputMethod = inputMethod;
			task.timer = new Timer();
			task.timer.scheduleAtFixedRate(task, 5 * DELAY, DELAY);
		}

		static void stop() {
			if (task != null) {
				task.inputMethod = null;
				if (task.timer != null) {
					task.timer.cancel();
				}
				task.cancel();
				task = null;
			}
		}

		public static void mouseReleased() {
			if ((task != null) && (task.inputMethod != null)) {
				task.inputMethod.buttonReleased(task.button, '\0');
				stop();
			}

		}

		public void run() {
			if (inputMethod != null) {
				inputMethod.buttonPressed(button, '\0');
			}
		}

	}

	private MouseAdapter mouseListener = new MouseAdapter() {

		public void mousePressed(MouseEvent e) {
			requestFocus();
			mouseButtonDown = true;
			pressedX = e.getX();
			pressedY = e.getY();

			MouseRepeatedTimerTask.stop();
			if (MIDletBridge.getCurrentMIDlet() == null) {
				return;
			}

			Device device = DeviceFactory.getDevice();
			J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();
			// if the displayable is in full screen mode, we should not
			// invoke any associated commands, but send the raw key codes
			// instead
			boolean fullScreenMode = device.getDeviceDisplay().isFullScreenMode();

			pressedButton = J2SEDeviceButtonsHelper.getSkinButton(e);
			if (pressedButton != null) {
				if (pressedButton instanceof SoftButton && !fullScreenMode) {
					Command cmd = ((SoftButton) pressedButton).getCommand();
					if (cmd != null) {
						MIDletAccess ma = MIDletBridge.getMIDletAccess();
						if (ma == null) {
							return;
						}
						DisplayAccess da = ma.getDisplayAccess();
						if (da == null) {
							return;
						}
						if (cmd.equals(CommandManager.CMD_MENU)) {
							CommandManager.getInstance().commandAction(cmd);
						} else {
							da.commandAction(cmd, da.getCurrent());
						}
					}
				} else {
					inputMethod.buttonPressed(pressedButton, '\0');
					MouseRepeatedTimerTask.schedule(SwingDeviceComponent.this, pressedButton, inputMethod);
				}
				// optimize for some video cards.
				repaint(pressedButton.getShape().getBounds());
			}
		}

		public void mouseReleased(MouseEvent e) {
			mouseButtonDown = false;
			MouseRepeatedTimerTask.stop();

			if (pressedButton == null) {
				return;
			}

			if (MIDletBridge.getCurrentMIDlet() == null) {
				return;
			}

			Device device = DeviceFactory.getDevice();
			J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();
			J2SEButton prevOverButton = J2SEDeviceButtonsHelper.getSkinButton(e);
			if (prevOverButton != null) {
				inputMethod.buttonReleased(prevOverButton, '\0');
			}
			pressedButton = null;
			// optimize for some video cards.
			if (prevOverButton != null) {
				repaint(prevOverButton.getShape().getBounds());
			} else {
				repaint();
			}
		}

	};

	private MouseMotionListener mouseMotionListener = new MouseMotionListener() {

		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}

		public void mouseMoved(MouseEvent e) {
			if (showMouseCoordinates) {
				StringBuffer buf = new StringBuffer();
				if (mouseButtonDown) {
					int width = e.getX() - pressedX;
					int height = e.getY() - pressedY;
					buf.append(pressedX).append(",").append(pressedY).append(" ").append(width).append("x").append(
							height);
				} else {
					buf.append(e.getX()).append(",").append(e.getY());
				}
				Common.setStatusBar(buf.toString());
			}

			if (mouseButtonDown && pressedButton == null) {
				return;
			}

			prevOverButton = overButton;
			overButton = J2SEDeviceButtonsHelper.getSkinButton(e);
			if (overButton != prevOverButton) {
				// optimize for some video cards.
				if (prevOverButton != null) {
					MouseRepeatedTimerTask.mouseReleased();
					pressedButton = null;
					repaint(prevOverButton.getShape().getBounds());
				}
				if (overButton != null) {
					repaint(overButton.getShape().getBounds());
				}
			} else if (overButton == null) {
				MouseRepeatedTimerTask.mouseReleased();
				pressedButton = null;
				if (prevOverButton != null) {
					repaint(prevOverButton.getShape().getBounds());
				}
			}
		}

	};

	public SwingDeviceComponent() {
		dc = new SwingDisplayComponent(this);
		setLayout(new XYLayout());

		addMouseListener(mouseListener);
		addMouseMotionListener(mouseMotionListener);
	}

	public DisplayComponent getDisplayComponent() {
		return dc;
	}

	public void init() {
		dc.init();

		remove(dc);

		Rectangle r = ((J2SEDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay()).getDisplayRectangle();
		add(dc, new XYConstraints(r.x, r.y, -1, -1));

		revalidate();
	}

	private void repaint(Rectangle r) {
		repaint(r.x, r.y, r.width, r.height);
	}

	public void switchShowMouseCoordinates() {
		// TODO skin editing mode.
		// showMouseCoordinates = !showMouseCoordinates;
		dc.switchShowMouseCoordinates();
	}

	public void keyTyped(KeyEvent ev) {
		if (MIDletBridge.getCurrentMIDlet() == null) {
			return;
		}

		J2SEInputMethod inputMethod = ((J2SEInputMethod) DeviceFactory.getDevice().getInputMethod());
		J2SEButton button = inputMethod.getButton(ev);
		if (button != null) {
			inputMethod.buttonTyped(button);
		}
	}

	public void keyPressed(KeyEvent ev) {
		if (MIDletBridge.getCurrentMIDlet() == null) {
			return;
		}

		Device device = DeviceFactory.getDevice();
		J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();

		if (ev.getKeyCode() == KeyEvent.VK_V && (ev.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable transferable = clipboard.getContents(null);
			if (transferable != null) {
				try {
					Object data = transferable.getTransferData(DataFlavor.stringFlavor);
					if (data instanceof String) {
						inputMethod.clipboardPaste((String) data);
					}
				} catch (UnsupportedFlavorException ex) {
					Logger.error(ex);
				} catch (IOException ex) {
					Logger.error(ex);
				}
			}
			return;
		}

		switch (ev.getKeyCode()) {
		case KeyEvent.VK_ALT:
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_SHIFT:
			return;
		case 0:
			// Don't know what is the case was intended for but this may be
			// national keyboard letter, so let it work
			if (ev.getKeyChar() == '\0') {
				return;
			}
		}

		char keyChar = '\0';
		if (ev.getKeyChar() >= 32 && ev.getKeyChar() != 65535) {
			keyChar = ev.getKeyChar();
		}
		J2SEButton button = inputMethod.getButton(ev);
		if (button != null) {
			pressedButton = button;
			// numeric keypad functions as hot keys for buttons only
			if ((ev.getKeyCode() >= KeyEvent.VK_NUMPAD0) && (ev.getKeyCode() <= KeyEvent.VK_NUMPAD9)) {
				keyChar = '\0';
			}
			// soft buttons
			if ((ev.getKeyCode() >= KeyEvent.VK_F1) && (ev.getKeyCode() <= KeyEvent.VK_F12)) {
				keyChar = '\0';
			}
			org.microemu.device.impl.Shape shape = button.getShape();
			if (shape != null) {
				repaint(shape.getBounds());
			}
		} else {
			// Logger.debug0x("no button for KeyCode", ev.getKeyCode());
		}
		inputMethod.buttonPressed(button, keyChar);
	}

	public void keyReleased(KeyEvent ev) {
		if (MIDletBridge.getCurrentMIDlet() == null) {
			return;
		}

		switch (ev.getKeyCode()) {
		case KeyEvent.VK_ALT:
		case KeyEvent.VK_CONTROL:
		case KeyEvent.VK_SHIFT:
			return;
		case 0:
			// Don't know what is the case was intended for but this may be
			// national keyboard letter, so let it work
			if (ev.getKeyChar() == '\0') {
				return;
			}
		}

		Device device = DeviceFactory.getDevice();
		J2SEInputMethod inputMethod = (J2SEInputMethod) device.getInputMethod();

		char keyChar = '\0';
		if (ev.getKeyChar() >= 32 && ev.getKeyChar() != 65535) {
			keyChar = ev.getKeyChar();
		}
		// numeric keypad functions as hot keys for buttons only
		if ((ev.getKeyCode() >= KeyEvent.VK_NUMPAD0) && (ev.getKeyCode() <= KeyEvent.VK_NUMPAD9)) {
			keyChar = '\0';
		}
		// soft buttons
		if ((ev.getKeyCode() >= KeyEvent.VK_F1) && (ev.getKeyCode() <= KeyEvent.VK_F12)) {
			keyChar = '\0';
		}
		// Logger.debug0x("keyReleased [" + keyChar + "]", keyChar);
		inputMethod.buttonReleased(inputMethod.getButton(ev), keyChar);

		prevOverButton = pressedButton;
		pressedButton = null;
		if (prevOverButton != null) {
			org.microemu.device.impl.Shape shape = prevOverButton.getShape();
			if (shape != null) {
				repaint(shape.getBounds());
			}
		}
	}

	public MouseListener getDefaultMouseListener() {
		return mouseListener;
	}

	public MouseMotionListener getDefaultMouseMotionListener() {
		return mouseMotionListener;
	}

	protected void paintComponent(Graphics g) {
		if (offg == null || offi.getWidth(null) != getSize().width || offi.getHeight(null) != getSize().height) {
			offi = new J2SEMutableImage(getSize().width, getSize().height, false, 0x000000).getImage();
			offg = offi.getGraphics();
		}

		Dimension size = getSize();
		offg.setColor(UIManager.getColor("text"));
		try {
			offg.fillRect(0, 0, size.width, size.height);
		} catch (NullPointerException ex) {
			// Fix for NPE in sun.java2d.pipe.SpanShapeRenderer.renderRect(..) on Mac platform
		}
		Device device = DeviceFactory.getDevice();
		if (device == null) {
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			return;
		}
		if (((DeviceDisplayImpl) device.getDeviceDisplay()).isResizable()) {
			return;
		}

		offg.drawImage(((J2SEImmutableImage) device.getNormalImage()).getImage(), 0, 0, this);

		if (prevOverButton != null) {
			org.microemu.device.impl.Shape shape = prevOverButton.getShape();
			if (shape != null) {
				drawImageInShape(offg, ((J2SEImmutableImage) device.getNormalImage()).getImage(), shape);
			}
			prevOverButton = null;
		}
		if (overButton != null) {
			org.microemu.device.impl.Shape shape = overButton.getShape();
			if (shape != null) {
				drawImageInShape(offg, ((J2SEImmutableImage) device.getOverImage()).getImage(), shape);
			}
		}
		if (pressedButton != null) {
			org.microemu.device.impl.Shape shape = pressedButton.getShape();
			if (shape != null) {
				drawImageInShape(offg, ((J2SEImmutableImage) device.getPressedImage()).getImage(), shape);
			}
		}

		g.drawImage(offi, 0, 0, null);
	}

	private void drawImageInShape(Graphics g, Image image, org.microemu.device.impl.Shape shape) {
		Shape clipSave = g.getClip();
		if (shape instanceof org.microemu.device.impl.Polygon) {
			Polygon poly = new Polygon(((org.microemu.device.impl.Polygon) shape).xpoints,
					((org.microemu.device.impl.Polygon) shape).ypoints,
					((org.microemu.device.impl.Polygon) shape).npoints);
			g.setClip(poly);
		}
		org.microemu.device.impl.Rectangle r = shape.getBounds();
		g.drawImage(image, r.x, r.y, r.x + r.width, r.y + r.height, r.x, r.y, r.x + r.width, r.y + r.height, null);
		g.setClip(clipSave);
	}

	public Dimension getPreferredSize() {
		Device device = DeviceFactory.getDevice();
		if (device == null) {
			return new Dimension(0, 0);
		}

		DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay();
		if (deviceDisplay.isResizable()) {
			return new Dimension(deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
		} else {
			javax.microedition.lcdui.Image img = device.getNormalImage();
			return new Dimension(img.getWidth(), img.getHeight());
		}
	}

}
