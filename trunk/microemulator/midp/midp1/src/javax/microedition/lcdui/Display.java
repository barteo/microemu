/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contributor(s): 
 * 3GLab
 */

package javax.microedition.lcdui;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;

import com.barteo.emulator.CommandManager;
import com.barteo.emulator.DisplayAccess;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.device.DeviceFactory;

public class Display 
{
	private static EventDispatcher eventDispatcher = null;
	private static TickerPaint tickerPaint = null;

	private Displayable current = null;
	private Displayable nextScreen = null;

	private DisplayAccessor accessor = null;

	private Object paintLock = new Object();
	private boolean repaintPending = false;

	private class DisplayAccessor implements DisplayAccess 
	{
		Display display;

		DisplayAccessor(Display d) 
		{
			display = d;
		}

		public void commandAction(Command cmd) 
		{
			if (current == null) {
				return;
			}
			CommandListener listener = current.getCommandListener();
			if (listener == null) {
				return;
			}
			listener.commandAction(cmd, current);
		}

		public Display getDisplay() 
		{
			return display;
		}

		public void keyPressed(int keyCode) 
		{
			if (current != null) {
				current.keyPressed(keyCode);
			}
		}

		public void keyReleased(int keyCode) 
		{
			if (current != null) {
				current.keyReleased(keyCode);
			}
		}

		public void paint(Graphics g) 
		{
			if (current != null) {
				current.paint(g);
				g.translate(-g.getTranslateX(), -g.getTranslateY());
				synchronized (paintLock) {
					repaintPending = false;
					paintLock.notify();
				}
			}
		}

		public boolean isFullScreenMode()
		{
			return false;
		}

		public Displayable getCurrent() 
		{
			return getDisplay().getCurrent();
		}

		public void setCurrent(Displayable d) 
		{
			getDisplay().setCurrent(d);
		}

		public void updateCommands() 
		{
			getDisplay().updateCommands();
		}

		public void clean() 
		{
			if (current != null) {
				current.hideNotify();
			}
		}
	}

	private class AlertTimeout implements Runnable 
	{
		int time;

		AlertTimeout(int time) 
		{
			this.time = time;
		}

		public void run() 
		{
			try {
				Thread.sleep(time);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

			setCurrent(nextScreen);
		}
	}

	private class TickerPaint implements Runnable 
	{
		private Display currentDisplay = null;

		public void setCurrentDisplay(Display currentDisplay) 
		{
			this.currentDisplay = currentDisplay;
		}

		public void run() 
		{
			while (true) {
				if (currentDisplay != null && currentDisplay.current != null && currentDisplay.current instanceof Screen) {
					Ticker ticker = ((Screen) currentDisplay.current).getTicker();
					if (ticker != null) {
						synchronized (ticker) {
							if (ticker.resetTextPosTo != -1) {
								ticker.textPos = ticker.resetTextPosTo;
								ticker.resetTextPosTo = -1;
							}
							ticker.textPos -= Ticker.PAINT_MOVE;
						}
						currentDisplay.repaint();
					}
				}
				try {
					Thread.sleep(Ticker.PAINT_TIMEOUT);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					tickerPaint = null;
				}
			}
		}
	}

	private class EventDispatcher implements Runnable 
	{
		private Vector events = new Vector();

		public void add(Runnable r) 
		{
			synchronized (paintLock) {
				events.addElement(r);
				paintLock.notify();
			}
		}

		public void run() 
		{
			Vector jobs;

			while (true) {
				jobs = null;
				synchronized (paintLock) {
					if (!repaintPending) {
						if (events.size() > 0) {
							jobs = (Vector) events.clone();
							events.removeAllElements();
						}
					}
				}

				if (jobs != null) {
					for (Enumeration en = jobs.elements(); en.hasMoreElements();) {
						((Runnable) en.nextElement()).run();
					}
				}

				try {
					synchronized (paintLock) {
						if (events.size() == 0) {
							paintLock.wait();
						}
					}
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					eventDispatcher = null;
				}
			}
		}
	}

	
	Display() 
	{
		accessor = new DisplayAccessor(this);

		if (eventDispatcher == null) {
			eventDispatcher = new EventDispatcher();
			new Thread(eventDispatcher, "EventDispatcher").start();
		}
		if (tickerPaint == null) {
			tickerPaint = new TickerPaint();
			new Thread(tickerPaint, "TickerPaint").start();
		}
	}

	
	public void callSerially(Runnable r) 
	{
		eventDispatcher.add(r);
	}

	
	public int numColors() 
	{
		return DeviceFactory.getDevice().getDeviceDisplay().numColors();
	}

	
	public static Display getDisplay(MIDlet m) 
	{
		Display result;

		if (MIDletBridge.getMIDletAccess(m).getDisplayAccess() == null) {
			result = new Display();
			MIDletBridge.getMIDletAccess(m).setDisplayAccess(result.accessor);
		} else {
			result = MIDletBridge.getMIDletAccess(m).getDisplayAccess().getDisplay();
		}

		tickerPaint.setCurrentDisplay(result);

		return result;
	}

	
	public Displayable getCurrent() 
	{
		return current;
	}

	
	public boolean isColor() 
	{
		return DeviceFactory.getDevice().getDeviceDisplay().isColor();
	}

	
	public void setCurrent(Displayable nextDisplayable) 
	{
		if (nextDisplayable != null) {
			if (current != null) {
				current.hideNotify(this);
			}

			if (nextDisplayable instanceof Alert) {
				setCurrent((Alert) nextDisplayable, current);
				return;
			}

			current = nextDisplayable;
			current.showNotify(this);
			setScrollUp(false);
			setScrollDown(false);
			updateCommands();

			current.repaint();
		}
	}

	
	public void setCurrent(Alert alert, Displayable nextDisplayable) 
	{
		nextScreen = nextDisplayable;

		current = alert;

		current.showNotify(this);
		updateCommands();
		current.repaint();

		if (alert.getTimeout() != Alert.FOREVER) {
			AlertTimeout at = new AlertTimeout(alert.getTimeout());
			Thread t = new Thread(at);
			t.start();
		}
	}

	
	void clearAlert() 
	{
		setCurrent(nextScreen);
	}

	
	static int getGameAction(int keyCode) 
	{
		return DeviceFactory.getDevice().getInputMethod().getGameAction(keyCode);
	}

	
	static int getKeyCode(int gameAction) 
	{
		return DeviceFactory.getDevice().getInputMethod().getKeyCode(gameAction);
	}


	static String getKeyName(int keyCode) 
	{
		return DeviceFactory.getDevice().getInputMethod().getKeyName(keyCode);
	}

	
	boolean isShown(Displayable d) 
	{
		if (current == null || current != d) {
			return false;
		} else {
			return true;
		}
	}

	
	void repaint(Displayable d) 
	{
		if (current == d) {
			synchronized (paintLock) {
				repaintPending = true;
			}
			DeviceFactory.getDevice().getDeviceDisplay().repaint();
		}
	}

	
	void setScrollDown(boolean state) 
	{
		DeviceFactory.getDevice().getDeviceDisplay().setScrollDown(state);
	}

	
	void setScrollUp(boolean state) 
	{
		DeviceFactory.getDevice().getDeviceDisplay().setScrollUp(state);
	}

	
	void updateCommands() 
	{
		if (current == null) {
			CommandManager.getInstance().updateCommands(null);
		} else {
			CommandManager.getInstance().updateCommands(current.getCommands());
		}
		/**
		 * updateCommands has changed the softkey labels tell the outside world
		 * it has happened.
		 */
		MIDletBridge.notifySoftkeyLabelsChanged();
		repaint();
	}

	
	private void repaint() 
	{
		if (current != null) {
			repaint(current);
		}
	}

}
