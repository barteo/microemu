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

// Andres Navarro
import javax.microedition.lcdui.game.GameCanvas;

import org.microemu.CommandManager;
import org.microemu.DisplayAccess;
import org.microemu.GameCanvasKeyAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
// Andres Navarro

public class Display 
{
	private PaintThread paintThread = null;
	private EventDispatcher eventDispatcher = null;
	private TickerPaint tickerPaint = null;
	private GaugePaint gaugePaint = null;
	
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
			if (cmd.equals(CommandManager.CMD_SCREEN_UP)) {
				if (current != null && current instanceof Screen) {
					((Screen) current).scroll(Canvas.UP);
				}
			} else if (cmd.equals(CommandManager.CMD_SCREEN_DOWN)) {
				if (current != null && current instanceof Screen) {
					((Screen) current).scroll(Canvas.DOWN);
				}
			} else if (cmd.isRegularCommand()) {
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
                if (current instanceof GameCanvas) {
                	processGameCanvasKeyEvent((GameCanvas) current, keyCode, true);
                } else {
	                // Andres Navarro
					current.keyPressed(keyCode);
                }
			}
		}

		public void keyReleased(int keyCode) 
		{
			if (current != null) {
                // Andres Navarro
                if (current instanceof GameCanvas) {
                	processGameCanvasKeyEvent((GameCanvas) current, keyCode, false);
                } else {
	                // Andres Navarro
					current.keyReleased(keyCode);
                }
			}
		}
		
		public void pointerPressed(int x, int y)
		{
			if (current != null) {
				current.pointerPressed(x, y);
			}
		}

		public void pointerReleased(int x, int y)
		{
			if (current != null) {
				current.pointerReleased(x, y);
			}
		}

		public void pointerDragged(int x, int y)
		{
			if (current != null) {
				current.pointerDragged(x, y);
			}
		}

		public void paint(Graphics g) 
		{
			if (current != null) {
				current.paint(g);
				g.translate(-g.getTranslateX(), -g.getTranslateY());
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
		
		public boolean isRepaintPending()
		{
			return repaintPending;
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
			tickerPaint.cancel();
			gaugePaint.cancel();
			eventDispatcher.cancel();
			paintThread.cancel();
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
		private boolean canceled;
		
		private Display currentDisplay = null;

		public void setCurrentDisplay(Display currentDisplay) 
		{
			this.currentDisplay = currentDisplay;
		}
		
		public void cancel()
		{
			canceled = true;
		}

		public void run() 
		{
			canceled = false;
			
			while (!canceled) {
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
						currentDisplay.repaint(current, 0, 0, current.getWidth(), current.getHeight());
					}
				}
				try {
					Thread.sleep(Ticker.PAINT_TIMEOUT);
				} catch (InterruptedException ex) {
					tickerPaint = null;
					return;
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
		private boolean canceled;
		
		private Display currentDisplay = null;

		public void setCurrentDisplay(Display currentDisplay) 
		{
			this.currentDisplay = currentDisplay;
		}
		
		public void cancel()
		{
			canceled = true;
		}

		public void run() 		
		{
			canceled = false;
			
			while (!canceled) {
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
					gaugePaint = null;
					return;
				}
			}
		}
	}
	// Andres Navarro
	
	private class PaintThread implements Runnable
	{
		private boolean canceled = false;
		
		private Object serviceRepaintsLock = new Object();
		
		private int x = -1;
		private int y = -1;
		private int width = -1;
		private int height = -1;			
		
		public void repaint(int x, int y, int width, int height)
		{
			synchronized (paintLock) {
				repaintPending = true;				
				if (this.x == -1 && this.y == -1 && this.width == -1 && this.height == -1) {
					this.x = x;
					this.y = y;
					this.width = width;
					this.height = height;
				} else {
					// TODO analyze and update clipping, currently repaints the whole displayable
					this.x = 0;
					this.y = 0;
					this.width = current.getWidth();
					this.height = current.getHeight();
				}
				paintLock.notify();
			}
		}

		public void serviceRepaints()
		{
			synchronized (paintLock) {
				if (!repaintPending) {
					return;
				}			
			}
			
			synchronized (serviceRepaintsLock) {
				try {
					serviceRepaintsLock.wait();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}

		public void cancel()
		{
			canceled = true;
			synchronized (paintLock) {
				paintLock.notify();
			}
		}

		public void run() 
		{
			while (!canceled) {
				if (repaintPending) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException ex) {
					}
					int repaintX, repaintY, repaintWidth, repaintHeight;
					synchronized (paintLock) {
						repaintPending = false;
						repaintX = x;
						repaintY = y;
						repaintWidth = width;
						repaintHeight = height;
						x = -1;
						y = -1;
						width = -1;
						height = -1;
					}					
					DeviceFactory.getDevice().getDeviceDisplay().repaint(repaintX, repaintY, repaintWidth, repaintHeight);
					if (!repaintPending) {
						synchronized (serviceRepaintsLock) {
							serviceRepaintsLock.notify();
						}
					}
				}
				
				synchronized (paintLock) {
					if (repaintPending) {
						continue;
					}
					try {
						// TODO remove timeout, currently there is somotimes problem with notify on cancel
						paintLock.wait(1000);
					} catch (InterruptedException ex) {
						paintThread = null;
						return;
					}
				}					
			}
		}		
	}

	private class EventDispatcher implements Runnable 
	{		
		private boolean canceled = false;
		
		private Object dispatcherLock = new Object();
		
		private Vector events = new Vector();		

		public void add(Runnable r) 
		{
			synchronized (paintLock) {
				events.addElement(r);
				synchronized (dispatcherLock) {
					dispatcherLock.notify();
				}
			}
		}
		
		public void cancel()
		{
			canceled = true;
			synchronized (dispatcherLock) {
				dispatcherLock.notify();
			}
		}
		
		public void run() 
		{
			Vector jobs;
			
			while (!canceled) {
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

				synchronized (dispatcherLock) {
					if (events.size() > 0 || repaintPending) {
						continue;
					}
					try {
						// TODO remove timeout, currently there is somotimes problem with notify on cancel
						dispatcherLock.wait(1000);
					} catch (InterruptedException ex) {
						eventDispatcher = null;
						return;
					}
				}					
			}
		}
	}

	
	Display() 
	{
		accessor = new DisplayAccessor(this);
		
		if (paintThread == null) {
			paintThread = new PaintThread();
			new Thread(paintThread, "PaintThread").start();
		}
		if (eventDispatcher == null) {
			eventDispatcher = new EventDispatcher();
			new Thread(eventDispatcher, "EventDispatcher").start();
		}
		if (tickerPaint == null) {
			tickerPaint = new TickerPaint();
			// TODO refactor into eventDispatcher
			//new Thread(tickerPaint, "TickerPaint").start();
		}
		tickerPaint.setCurrentDisplay(this);
		// Andres Navarro
		if (gaugePaint == null) {
			gaugePaint = new GaugePaint();
			// TODO refactor into eventDispatcher
			//new Thread(gaugePaint, "GaugePaint").start();
		}
		gaugePaint.setCurrentDisplay(this);
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

	
	void repaint(Displayable d, int x, int y, int width, int height)
    {
		if (current == d) {
			paintThread.repaint(x, y, width, height);
		}
    }

		
	void serviceRepaints()
	{
		paintThread.serviceRepaints();
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
		repaint(current, 0, 0, current.getWidth(), current.getHeight());
	}

}
