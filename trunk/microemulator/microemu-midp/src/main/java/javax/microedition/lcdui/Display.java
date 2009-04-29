/**
 * MicroEmulator 
 * Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 * Copyright (C) 2007 Rushabh Doshi <radoshi@cs.stanford.edu> Pelago, Inc
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
 * Contributor(s): 
 *   3GLab
 *   Andres Navarro
 *   
 *  @version $Id$
 */
package javax.microedition.lcdui;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

import org.microemu.DisplayAccess;
import org.microemu.GameCanvasKeyAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.DisplayableUI;
import org.microemu.device.ui.EventDispatcher;

public class Display {

	public static final int LIST_ELEMENT = 1;

	public static final int CHOICE_GROUP_ELEMENT = 2;

	public static final int ALERT = 3;

	public static final int COLOR_BACKGROUND = 0;

	public static final int COLOR_FOREGROUND = 1;

	public static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;

	public static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;

	public static final int COLOR_BORDER = 4;

	public static final int COLOR_HIGHLIGHTED_BORDER = 5;

	private Displayable current = null;

	private DisplayAccessor accessor = null;

	private EventDispatcher eventDispatcher;

	private final class GaugePaintTask implements Runnable {

		public void run() {
			if (current != null) {
				if (current instanceof Alert) {
					Gauge gauge = ((Alert) current).indicator;
					if (gauge != null && gauge.hasIndefiniteRange() && gauge.getValue() == Gauge.CONTINUOUS_RUNNING) {
						gauge.updateIndefiniteFrame();
					}
				} else if (current instanceof Form) {
					Item[] items = ((Form) current).items;
					for (int i = 0; i < items.length; i++) {
						Item it = items[i];
						if (it != null && it instanceof Gauge) {
							Gauge gauge = (Gauge) it;

							if (gauge.hasIndefiniteRange() && gauge.getValue() == Gauge.CONTINUOUS_RUNNING) {
								gauge.updateIndefiniteFrame();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @author radoshi
	 * 
	 */
	private final class TickerPaintTask implements Runnable {

		public void run() {
			if (current != null) {
				Ticker ticker = current.getTicker();
				if (ticker != null) {
					synchronized (ticker) {
						if (ticker.resetTextPosTo != -1) {
							ticker.textPos = ticker.resetTextPosTo;
							ticker.resetTextPosTo = -1;
						}
						ticker.textPos -= Ticker.PAINT_MOVE;
					}
					repaint(current, 0, 0, current.getWidth(), current.getHeight());
				}
			}
		}
	}

	/**
	 * Wrap a key event as a runnable so it can be thrown into the event
	 * processing queue. Note that this may be a bit buggy, since events are
	 * supposed to propogate to the head of the queue and not get tied behind
	 * other repaints or serial calls in the queue.
	 * 
	 * @author radoshi
	 * 
	 */
	private final class KeyEvent extends EventDispatcher.Event {

		static final short KEY_PRESSED = 0;

		static final short KEY_RELEASED = 1;

		static final short KEY_REPEATED = 2;

		private short type;

		private int keyCode;

		KeyEvent(short type, int keyCode) {
			eventDispatcher.super();
			this.type = type;
			this.keyCode = keyCode;
		}

		public void run() {
			switch (type) {
			case KEY_PRESSED:
				if (current != null) {
					current.keyPressed(keyCode);
				}
				break;

			case KEY_RELEASED:
				if (current != null) {
					current.keyReleased(keyCode);
				}
				break;

			case KEY_REPEATED:
				if (current != null) {
					current.keyRepeated(keyCode);
				}
				break;
			}
		}
	}

	private class DisplayAccessor implements DisplayAccess {

		Display display;

		DisplayAccessor(Display d) {

			display = d;
		}

		public void commandAction(Command c, Displayable d) {
			if (c.isRegularCommand()) {
				if (d == null) {
					return;
				}
				CommandListener listener = d.getCommandListener();
				if (listener == null) {
					return;
				}
				listener.commandAction(c, d);
			} else {
				// item contained command
				Item item = c.getFocusedItem();

				ItemCommandListener listener = item.getItemCommandListener();
				if (listener == null) {
					return;
				}
				listener.commandAction(c.getOriginalCommand(), item);
			}
		}

		public Display getDisplay() {
			return display;
		}

		// Andres Navarro
		private void processGameCanvasKeyEvent(GameCanvas c, int k, boolean press) {
			// TODO Game Canvas keys need more work
			// and better integration with the microemulator
			// maybe actualKeyState in GameCanvas should be
			// global and should update even while no GameCanvas
			// is current
			GameCanvasKeyAccess access = MIDletBridge.getGameCanvasKeyAccess(c);
			int gameCode = c.getGameAction(k);
			boolean suppress = false;
			if (gameCode != 0) {
				// valid game key
				if (press)
					access.recordKeyPressed(c, gameCode);
				else
					access.recordKeyReleased(c, gameCode);
				suppress = access.suppressedKeyEvents(c);
			}
			if (!suppress) {
				if (press) {
					eventDispatcher.put(new KeyEvent(KeyEvent.KEY_PRESSED, k));
				} else {
					eventDispatcher.put(new KeyEvent(KeyEvent.KEY_RELEASED, k));
				}
			}
		}

		// TODO according to the specification this should be
		// only between show and hide notify...
		// check later
		// Andres Navarro
		public void keyPressed(int keyCode) {
			// Andres Navarro
			if (current != null && current instanceof GameCanvas) {
				processGameCanvasKeyEvent((GameCanvas) current, keyCode, true);
			} else {
				eventDispatcher.put(new KeyEvent(KeyEvent.KEY_PRESSED, keyCode));
			}
		}

		public void keyRepeated(int keyCode) {
			eventDispatcher.put(new KeyEvent(KeyEvent.KEY_REPEATED, keyCode));
		}

		public void keyReleased(int keyCode) {
			// Andres Navarro
			if (current != null && current instanceof GameCanvas) {
				processGameCanvasKeyEvent((GameCanvas) current, keyCode, false);
			} else {
				eventDispatcher.put(new KeyEvent(KeyEvent.KEY_RELEASED, keyCode));
			}
		}

		public void pointerPressed(final int x, final int y) {
			if (current != null) {
				eventDispatcher.put(eventDispatcher.new PointerEvent(new Runnable() {
					
					public void run() {
						current.pointerPressed(x, y);
						
					}
					
				}, EventDispatcher.PointerEvent.POINTER_PRESSED, x, y));
			}
		}

		public void pointerReleased(final int x, final int y) {
			if (current != null) {
				eventDispatcher.put(eventDispatcher.new PointerEvent(new Runnable() {
					
					public void run() {
						current.pointerReleased(x, y);
						
					}
					
				}, EventDispatcher.PointerEvent.POINTER_RELEASED, x, y));
			}
		}

		public void pointerDragged(final int x, final int y) {
			if (current != null) {
				eventDispatcher.put(eventDispatcher.new PointerEvent(new Runnable() {
					
					public void run() {
						current.pointerDragged(x, y);
						
					}
					
				}, EventDispatcher.PointerEvent.POINTER_DRAGGED, x, y));
			}
		}

		public void paint(Graphics g) {
			// TODO consider removal of DisplayAccess::paint(..)
			if (current != null) {
				try {
					current.paint(g);
				} catch (Throwable th) {
					th.printStackTrace();
				}
				g.translate(-g.getTranslateX(), -g.getTranslateY());
			}
		}

		public Displayable getCurrent() {
			return getDisplay().getCurrent();
		}

		public DisplayableUI getCurrentUI() {
			Displayable current = getCurrent();
			if (current == null) {
				return null;
			} else {
				return current.ui;
			}
		}

		public boolean isFullScreenMode() {
			Displayable current = getCurrent();

			if (current instanceof Canvas) {
				return ((Canvas) current).fullScreenMode;
			} else {
				return false;
			}
		}

		public void serviceRepaints() {
			getDisplay().serviceRepaints();
		}

		public void setCurrent(Displayable d) {
			getDisplay().setCurrent(d);
		}

		public void sizeChanged() {
			if (current != null) {
	    		if (current instanceof GameCanvas) {
	    			current.width = -1;
	    			current.height = -1;
	    		    GameCanvasKeyAccess access = MIDletBridge.getGameCanvasKeyAccess((GameCanvas) current);
	    		    access.initBuffer();
	    		}
				current.sizeChanged(Display.this);
			}
		}

		public void repaint() {
			Displayable d = getCurrent();
			if (d != null) {
				getDisplay().repaint(d, 0, 0, d.getWidth(), d.getHeight());
			}
		}

		public void clean() {
			if (current != null) {
				current.hideNotify();
			}
			eventDispatcher.cancel();
			timer.cancel();
		}
	}

	private class AlertTimeout implements Runnable {

		int time;

		AlertTimeout(int time) {
			this.time = time;
		}

		public void run() {
			try {
				Thread.sleep(time);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

			Displayable d = current;
			if (d != null && d instanceof Alert) {
				Alert alert = (Alert) d;
				if (alert.time != Alert.FOREVER) {
//					alert.getCommandListener().commandAction((Command) alert.getCommands().get(0), alert);
				}
			}
		}
	}

	private final Timer timer = new Timer();

	/**
	 * Wrap any runnable as a timertask so that when the timer gets fired, the
	 * runnable gets run
	 * 
	 * @author radoshi
	 * 
	 */
	private final class RunnableWrapper extends TimerTask {

		private final Runnable runnable;

		RunnableWrapper(Runnable runnable) {
			this.runnable = runnable;
		}

		public void run() {
			eventDispatcher.put(runnable);
		}

	}

	Display() {
		accessor = new DisplayAccessor(this);

		eventDispatcher = DeviceFactory.getDevice().getUIFactory().createEventDispatcher(this);

		timer.scheduleAtFixedRate(new RunnableWrapper(new TickerPaintTask()), 0, Ticker.PAINT_TIMEOUT);
		timer.scheduleAtFixedRate(new RunnableWrapper(new GaugePaintTask()), 0, Ticker.PAINT_TIMEOUT);
	}

	public void callSerially(Runnable runnable) {
		eventDispatcher.put(runnable);
	}

	public int numAlphaLevels() {
		return DeviceFactory.getDevice().getDeviceDisplay().numAlphaLevels();
	}

	public int numColors() {
		return DeviceFactory.getDevice().getDeviceDisplay().numColors();
	}

	public boolean flashBacklight(int duration) {
		// TODO
		return false;
	}

	public static Display getDisplay(MIDlet m) {
		Display result;

		if (MIDletBridge.getMIDletAccess(m).getDisplayAccess() == null) {
			result = new Display();
			MIDletBridge.getMIDletAccess(m).setDisplayAccess(result.accessor);
		} else {
			result = MIDletBridge.getMIDletAccess(m).getDisplayAccess().getDisplay();
		}

		return result;
	}

	public int getColor(int colorSpecifier) {
		// TODO implement better
		switch (colorSpecifier) {
		case COLOR_BACKGROUND:
		case COLOR_HIGHLIGHTED_FOREGROUND:
		case COLOR_HIGHLIGHTED_BORDER:
			return 0xFFFFFF;
		default:
			return 0x000000;
		}
	}

	public int getBorderStyle(boolean highlighted) {
		// TODO implement better
		return highlighted ? Graphics.DOTTED : Graphics.SOLID;
	}

	public int getBestImageWidth(int imageType) {
		// TODO implement
		return 0;
	}

	public int getBestImageHeight(int imageType) {

		// TODO implement
		return 0;
	}

	public Displayable getCurrent() {
		return current;
	}

	public boolean isColor() {
		return DeviceFactory.getDevice().getDeviceDisplay().isColor();
	}

	public void setCurrent(final Displayable nextDisplayable) {
		if (nextDisplayable != null) {
			eventDispatcher.put(eventDispatcher.new ShowNotifyEvent(new Runnable() {

				public void run() {
					if (current != null) {
						eventDispatcher.put(eventDispatcher.new HideNotifyEvent(new Runnable() {
							
							private Displayable displayable = current;

							public void run() {
								displayable.hideNotify(Display.this);
							}
							
						}));
					}

					if (nextDisplayable instanceof Alert) {
						setCurrent((Alert) nextDisplayable, current);
						return;
					}

					// Andres Navarro
					// TODO uncomment and test with JBenchmark2
					/*
					 * if (nextDisplayable instanceof GameCanvas) { // clear the
					 * keys of the GameCanvas
					 * MIDletBridge.getMIDletAccess().getGameCanvasKeyAccess().setActualKeyState(
					 * (GameCanvas) nextDisplayable, 0); }
					 */
					// Andres Navarro
					nextDisplayable.showNotify(Display.this);
					Display.this.current = nextDisplayable;

					setScrollUp(false);
					setScrollDown(false);
					nextDisplayable.repaint();
				}
												
			}));
		}
	}

	public void setCurrent(Alert alert, Displayable nextDisplayable) {
		// TODO check if nextDisplayble is Alert
		// TODO change to putInQueue implementation
		Alert.nextDisplayable = nextDisplayable;

		current = alert;

		current.showNotify(this);
		current.repaint();

		if (alert.getTimeout() != Alert.FOREVER) {
			AlertTimeout at = new AlertTimeout(alert.getTimeout());
			Thread t = new Thread(at);
			t.start();
		}
	}

	public void setCurrentItem(Item item) {
		// TODO implement
	}

	public boolean vibrate(int duration) {
		return DeviceFactory.getDevice().vibrate(duration);
	}

	// Who call this?? (Andres Navarro)
	void clearAlert() {
		setCurrent(Alert.nextDisplayable);
	}

	static int getGameAction(int keyCode) {
		return DeviceFactory.getDevice().getInputMethod().getGameAction(keyCode);
	}

	static int getKeyCode(int gameAction) {
		return DeviceFactory.getDevice().getInputMethod().getKeyCode(gameAction);
	}

	static String getKeyName(int keyCode) throws IllegalArgumentException {
		return DeviceFactory.getDevice().getInputMethod().getKeyName(keyCode);
	}

	boolean isShown(Displayable d) {
		if (current == null || current != d) {
			return false;
		} else {
			return true;
		}
	}

	void repaint(Displayable d, int x, int y, int width, int height) {
		if (current == d) {
			eventDispatcher.put(eventDispatcher.new PaintEvent(x, y, width, height));
		}
	}

	void serviceRepaints() {
		//
		// If service repaints is being called from the event thread, then we
		// just execute an immediate repaint and call it a day. If it is being
		// called from another thread, then we setup a repaint barrier and wait
		// for that barrier to execute
		//
		if (EventDispatcher.EVENT_DISPATCHER_NAME.equals(Thread.currentThread().getName())) {
			DeviceFactory.getDevice().getDeviceDisplay().repaint(0, 0, current.getWidth(), current.getHeight());
			return;
		}

		eventDispatcher.serviceRepaints();
	}

	void setScrollDown(boolean state) {
		DeviceFactory.getDevice().getDeviceDisplay().setScrollDown(state);
	}

	void setScrollUp(boolean state) {
		DeviceFactory.getDevice().getDeviceDisplay().setScrollUp(state);
	}

}
