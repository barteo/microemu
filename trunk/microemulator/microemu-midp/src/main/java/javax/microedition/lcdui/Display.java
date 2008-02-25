/**
 * MicroEmulator 
 * Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 * Copyright (C) 2007 Rushabh Doshi <radoshi@cs.stanford.edu> Pelago, Inc
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

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

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

    // private PaintThread paintThread = null;
    // private EventDispatcher eventDispatcher = null;

    private Displayable current = null;

    private DisplayAccessor accessor = null;

    private static final String EVENT_DISPATCHER_NAME = "event-thread";

    private final EventDispatcher eventDispatcher = new EventDispatcher();

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
     * Simple method that eats interrupted exception since in most cases we
     * can't do anything about these anyway
     * 
     * @param event
     */
    private void putInQueue(Runnable runnable) {
        try {
            eventQueue.put(runnable);
        } catch (InterruptedException exception) {
            // nothing to do
            exception.printStackTrace();
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
    private final class KeyEvent implements Runnable {

        static final short KEY_PRESSED = 0;

        static final short KEY_RELEASED = 1;

        static final short KEY_REPEATED = 2;

        private short type;

        private int keyCode;

        KeyEvent(short type, int keyCode) {
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
    
    private final class PointerEvent implements Runnable {
    	
    	static final short POINTER_PRESSED = 0;
    	
    	static final short POINTER_RELEASED = 1;
    	
    	static final short POINTER_DRAGGED = 2;
    	
    	private short type;
    	
    	private int x;
    	
    	private int y;
    	
    	PointerEvent(short type, int x, int y) {
    		this.type = type;
    		this.x = x;
    		this.y = y;
    	}
    	
        public void run() {
            switch (type) {
            case POINTER_PRESSED:
                if (current != null) {
                    current.pointerPressed(x, y);
                }
            	break;
            case POINTER_RELEASED:
                if (current != null) {
                    current.pointerReleased(x, y);
                }
            	break;
            case POINTER_DRAGGED:
                if (current != null) {
                    current.pointerDragged(x, y);
                }
            	break;
            }
        }   	
    }

    private final class ShowHideNotifyEvent implements Runnable {
    	
    	static final short SHOW_NOTIFY = 0;
    	
    	static final short HIDE_NOTIFY = 1;
    	
    	private short type;
    	
    	private Displayable current;
    	
    	private Displayable nextDisplayable;
    	
    	ShowHideNotifyEvent(short type, Displayable current, Displayable nextDisplayable) {
    		this.type = type;
    		this.current = current;
    		this.nextDisplayable = nextDisplayable;
    	}
    	
        public void run() {
            switch (type) {
            case SHOW_NOTIFY:
                if (current != null) {
                	putInQueue(new ShowHideNotifyEvent(ShowHideNotifyEvent.HIDE_NOTIFY, current, nextDisplayable));
                }

                if (nextDisplayable instanceof Alert) {
                    setCurrent((Alert) nextDisplayable, current);
                    return;
                }

                // Andres Navarro
                // TODO uncomment and test with JBenchmark2
                /*
                 * if (nextDisplayable instanceof GameCanvas) { // clear the keys of
                 * the GameCanvas
                 * MIDletBridge.getMIDletAccess().getGameCanvasKeyAccess().setActualKeyState(
                 * (GameCanvas) nextDisplayable, 0); }
                 */
                // Andres Navarro
                nextDisplayable.showNotify(Display.this);
                Display.this.current = nextDisplayable;                
                
                setScrollUp(false);
                setScrollDown(false);
                updateCommands();
                nextDisplayable.repaint();
                
                break;
            case HIDE_NOTIFY:
            	current.hideNotify(Display.this);
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
        	if (c.equals(CommandManager.CMD_MENU)) {
        		CommandManager.getInstance().commandAction(c);
        	} else if (c.equals(CMD_SCREEN_UP)) {
                if (d != null && d instanceof Screen) {
                    ((Screen) d).scroll(Canvas.UP);
                }
            } else if (c.equals(CMD_SCREEN_DOWN)) {
                if (d != null && d instanceof Screen) {
                    ((Screen) d).scroll(Canvas.DOWN);
                }
            } else if (c.isRegularCommand()) {
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
            GameCanvasKeyAccess access = MIDletBridge.getMIDletAccess().getGameCanvasKeyAccess();
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
                    putInQueue(new KeyEvent(KeyEvent.KEY_PRESSED, k));
                } else {
                    putInQueue(new KeyEvent(KeyEvent.KEY_RELEASED, k));
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
                putInQueue(new KeyEvent(KeyEvent.KEY_PRESSED, keyCode));
            }
        }

        public void keyRepeated(int keyCode) {
            putInQueue(new KeyEvent(KeyEvent.KEY_REPEATED, keyCode));
        }

        public void keyReleased(int keyCode) {
            // Andres Navarro
            if (current != null && current instanceof GameCanvas) {
                processGameCanvasKeyEvent((GameCanvas) current, keyCode, false);
            } else {
                putInQueue(new KeyEvent(KeyEvent.KEY_RELEASED, keyCode));
            }
        }

        public void pointerPressed(int x, int y) {
            if (current != null) {
            	putInQueue(new PointerEvent(PointerEvent.POINTER_PRESSED, x, y));
            }
        }

        public void pointerReleased(int x, int y) {
            if (current != null) {
            	putInQueue(new PointerEvent(PointerEvent.POINTER_RELEASED, x, y));
            }
        }

        public void pointerDragged(int x, int y) {

            if (current != null) {
            	putInQueue(new PointerEvent(PointerEvent.POINTER_DRAGGED, x, y));
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

        public void sizeChanged(int width, int height) {
            if (current != null) {
                current.sizeChanged(width, height);
                updateCommands();
            }
        }

        public void updateCommands() {
            getDisplay().updateCommands();
        }

        public void clean() {
            if (current != null) {
                current.hideNotify();
            }
            eventDispatcher.cancel();
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
                    alert.getCommandListener().commandAction((Command) alert.getCommands().get(0), alert);
                }
            }
        }
    }

    private final Semaphore serviceRepaintSemaphore = new Semaphore(0);

    private final class PaintTask implements Runnable {

        private int x = -1, y = -1, width = -1, height = -1;

        PaintTask(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void run() {
            DeviceFactory.getDevice().getDeviceDisplay().repaint(x, y, width, height);
        }

        /**
         * Do a 2-D merge of the paint areas
         * 
         * @param task
         */
        public final void merge(PaintTask task) {
            int xMax = x + width;
            int yMax = y + height;

            this.x = Math.min(this.x, task.x);
            xMax = Math.max(xMax, task.x + task.width);

            this.y = Math.min(this.y, task.y);
            yMax = Math.max(yMax, task.y + task.height);

            this.width = xMax - x;
            this.height = yMax - y;
        }

    }

    /**
     * TODO: User a priority queue instead The problem is that key events should
     * propogate to the head of the queue, even if inserted at the tail. A
     * priority queue is probably a more appropriate structure to use
     * 
     * @author radoshi
     */
    private final BlockingQueue eventQueue = new LinkedBlockingQueue();

    /**
     * Management of all events including paints, keyboard events and serial
     * runners
     * 
     * @author radoshi
     * 
     */
    private final class EventDispatcher implements Runnable {

        private volatile boolean cancelled = false;

        public void run() {

            while (!cancelled) {
                try {
                    Runnable runnable = (Runnable) eventQueue.take();

                    if (runnable instanceof PaintTask) {

                        PaintTask paint = (PaintTask) runnable;

                        while (eventQueue.peek() != null && eventQueue.peek() instanceof PaintTask) {
                            paint.merge((PaintTask) eventQueue.take());
                        }

                    }
                    runnable.run();

                } catch (InterruptedException exception) {
                    // nothing to do really, just keep retrying
                }
            }
        }

        /**
         * Do not service any more events
         */
        public final void cancel() {
            this.cancelled = true;
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
            putInQueue(runnable);
        }

    }

    Display() {

        accessor = new DisplayAccessor(this);

        startEventDispatcher();

        timer.scheduleAtFixedRate(new RunnableWrapper(new TickerPaintTask()), 0, Ticker.PAINT_TIMEOUT);

        timer.scheduleAtFixedRate(new RunnableWrapper(new GaugePaintTask()), 0, Ticker.PAINT_TIMEOUT);
    }

    private final void startEventDispatcher() {
        Thread thread = new Thread(eventDispatcher, EVENT_DISPATCHER_NAME);
        thread.setDaemon(true);
        thread.start();
    }

    public void callSerially(Runnable runnable) {
        putInQueue(runnable);
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

    public void setCurrent(Displayable nextDisplayable) {
        if (nextDisplayable != null) {
        	putInQueue(new ShowHideNotifyEvent(ShowHideNotifyEvent.SHOW_NOTIFY, current, nextDisplayable));
        }
    }

    public void setCurrent(Alert alert, Displayable nextDisplayable) {
        // TODO check if nextDisplayble is Alert
    	// TODO change to putInQueue implementation
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
            putInQueue(new PaintTask(x, y, width, height));
        }
    }

    void serviceRepaints() {
        //
        // If service repaints is being called from the event thread, then we
        // just execute an immediate repaint and call it a day. If it is being
        // called from another thread, then we setup a repaint barrier and wait
        // for that barrier to execute
        //
        if (EVENT_DISPATCHER_NAME.equals(Thread.currentThread().getName())) {
            DeviceFactory.getDevice().getDeviceDisplay().repaint(0, 0, current.getWidth(), current.getHeight());
            return;
        }

        // put in a repaint task and block until that task is completed
        putInQueue(new Runnable() {

            public void run() {
                serviceRepaintSemaphore.release();
            }

        });

        try {
            serviceRepaintSemaphore.acquire();
        } catch (InterruptedException exception) {
            // nothing to do - fall out of service repaints
            exception.printStackTrace();
        }
    }

    void setScrollDown(boolean state) {
        DeviceFactory.getDevice().getDeviceDisplay().setScrollDown(state);
    }

    void setScrollUp(boolean state) {
        DeviceFactory.getDevice().getDeviceDisplay().setScrollUp(state);
    }

    void updateCommands() {
        if (current == null) {
            CommandManager.getInstance().updateCommands(null);
        } else {
            CommandManager.getInstance().updateCommands(current.getCommands());
        }
        repaint(current, 0, 0, current.getWidth(), current.getHeight());
    }

}
