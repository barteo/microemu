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
 *   3GLab
 *   Andres Navarro
 */

package javax.microedition.lcdui;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;

import com.barteo.emulator.CommandManager;
import com.barteo.emulator.DisplayAccess;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.device.DeviceFactory;
// Andres Navarro
import javax.microedition.lcdui.game.GameCanvas;
import com.barteo.emulator.GameCanvasKeyAccess;
// Andres Navarro

public class Display 
{
	private static EventDispatcher eventDispatcher = null;
	private static TickerPaint tickerPaint = null;
	private static GaugePaint gaugePaint = null;

	private Displayable current = null;

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
			// Andres Navarro
			if (cmd.isRegularCommand()) {
				if (current == null) {
					return;
				}
				CommandListener listener = current.getCommandListener();
				if (listener == null) {
					return;
				}
				listener.commandAction(cmd, current);
			} else {
				// item contained command
				Item item = cmd.getFocusedItem();
				
				ItemCommandListener listener = item.getItemCommandListener();
				if (listener == null) {
					return;
				}
				listener.commandAction(cmd.getOriginalCommand(), item);
			}
		}

		public Display getDisplay() 
		{
			return display;
		}

        // Andres Navarro
        private void processGameCanvasKeyEvent(GameCanvas c, int k, boolean press)
        {
            // TODO Game Canvas keys need more work
            // and better integration with the microemulator
            // maybe actualKeyState in GameCanvas should be 
            // global and should update even while no GameCanvas
            // is current
            GameCanvasKeyAccess access = MIDletBridge.getMIDletAccess().getGameCanvasKeyAccess();
            int gameCode = c.getGameAction(k);
            boolean suppress = false;
            if (gameCode != 0) {
                // valid game key
                if(press) 
                    access.recordKeyPressed(c, gameCode);
                else
                    access.recordKeyReleased(c, gameCode);
                suppress = access.suppressedKeyEvents(c);
            }
            if (!(suppress)) {
                if (press)
                    c.keyPressed(k);
                else
                    c.keyPressed(k);
            }
        }
        // TODO according to the specification this should be
        // only between show and hide notify...
        // check later
        // Andres Navarro

        public void keyPressed(int keyCode) 
		{
			if (current != null) {
                            // Andres Navarro
                            if (current instanceof GameCanvas)
                                    processGameCanvasKeyEvent((GameCanvas) current, keyCode, true);
                            else
                            // Andres Navarro
				current.keyPressed(keyCode);
			}
		}

		public void keyReleased(int keyCode) 
		{
			if (current != null) {
                            // Andres Navarro
                            if (current instanceof GameCanvas)
                                    processGameCanvasKeyEvent((GameCanvas) current, keyCode, false);
                            else
                            // Andres Navarro
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

		public Displayable getCurrent() 
		{
			return getDisplay().getCurrent();
		}
		
		public boolean isFullScreenMode()
		{
			Displayable current = getCurrent();
			
			if (current instanceof Canvas) {
				return ((Canvas) current).fullScreenMode;
			} else {
				return false;
			}
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

			Displayable d = current;
			if (d != null && d instanceof Alert) {
				Alert alert = (Alert) d;
				if (alert.time != Alert.FOREVER) {
					alert.getCommandListener().commandAction(
						(Command) alert.getCommands().get(0), alert);
				}
			}
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

	// Andres Navarro
	// class to automatically repaint CONTINUOUS_RUNNING gauges
	// XXX having a whole thread doing this is kinda ridiculous
	// specially since it is even running when no gauge is in
	// sight... 
	private class GaugePaint implements Runnable 
	{
		private Display currentDisplay = null;

		public void setCurrentDisplay(Display currentDisplay) 
		{
			this.currentDisplay = currentDisplay;
		}

		public void run() 
		{
			while (true) {
				if (currentDisplay != null && currentDisplay.current != null && 
						(currentDisplay.current instanceof Alert || 
							currentDisplay.current instanceof Form)) {
					if (currentDisplay.current instanceof Alert) {
						Gauge gauge = ((Alert) currentDisplay.current).indicator;
						if (gauge != null && gauge.hasIndefiniteRange() &&
								gauge.getValue() == Gauge.CONTINUOUS_RUNNING) {
							gauge.updateIndefiniteFrame();
						}
					} else if (currentDisplay.current instanceof Form) {
						Item [] items = ((Form)currentDisplay.current).items;
						for (int i = 0; i < items.length; i++) {
							Item it = items[i];
							if (it != null && it instanceof Gauge) {
								Gauge gauge = (Gauge) it;
								
								if (gauge.hasIndefiniteRange() &&
										gauge.getValue() == Gauge.CONTINUOUS_RUNNING) {
									gauge.updateIndefiniteFrame();								
								}
							}
						}
					}
				}
					
				try {
					Thread.sleep(Gauge.PAINT_TIMEOUT);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					gaugePaint = null;
				}
			}
		}
	}
	// Andres Navarro

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
		// Andres Navarro
		if (gaugePaint == null) {
			gaugePaint = new GaugePaint();
			new Thread(gaugePaint, "GaugePaint").start();
		}
		// Andres Navarro
	}

	
	public void callSerially(Runnable r) 
	{
		eventDispatcher.add(r);
	}

	
    public int numAlphaLevels()
    {
        return DeviceFactory.getDevice().getDeviceDisplay().numAlphaLevels();
    }

    
    public int numColors() 
	{
		return DeviceFactory.getDevice().getDeviceDisplay().numColors();
	}
    
    
    public boolean flashBacklight(int duration)
    {
        // TODO
        return false;
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
		gaugePaint.setCurrentDisplay(result);

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
            // Andres Navarro
			// TODO uncomment and test with JBenchmark2
			/*
            if (nextDisplayable instanceof GameCanvas) {
                // clear the keys of the GameCanvas
                MIDletBridge.getMIDletAccess().getGameCanvasKeyAccess().setActualKeyState(
                        (GameCanvas) nextDisplayable, 0);                            
            }
            */
            // Andres Navarro
			current.showNotify(this);
			setScrollUp(false);
			setScrollDown(false);
			updateCommands();

			current.repaint();
		}
	}

	
	public void setCurrent(Alert alert, Displayable nextDisplayable) 
	{
		// XXX check if nextDisplayble is
		// Alert
		Alert.nextDisplayable = nextDisplayable;

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
    
    
    public boolean vibrate(int duration) 
    { 
        return false; 
    }
	
	// Who call this?? (Andres Navarro)
	void clearAlert() 
	{
		setCurrent(Alert.nextDisplayable);
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
