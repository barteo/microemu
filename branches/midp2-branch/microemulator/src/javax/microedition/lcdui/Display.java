/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 *
 *  Contributor(s):
 *    3GLab
 */
 
package javax.microedition.lcdui;

import javax.microedition.midlet.MIDlet;

import com.barteo.emulator.CommandManager;
import com.barteo.emulator.DisplayAccess;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.device.DeviceFactory;


public class Display
{
	static final int LIST_ELEMENT = 1;
	static final int CHOICE_GROUP_ELEMENT = 2;
	static final int ALERT = 3;
	
	static final int COLOR_BACKGROUND = 0;
	static final int COLOR_FOREGROUND = 1;
	static final int COLOR_HIGHLIGHTED_BACKGROUND = 2;
	static final int COLOR_HIGHLIGHTED_FOREGROUND = 3;
	static final int COLOR_BORDER = 4;
	static final int COLOR_HIGHLIGHTED_BORDER = 5;
		
	private Displayable current = null;
	private Displayable nextScreen = null;

	private DisplayAccessor accessor = null;


	class DisplayAccessor implements DisplayAccess
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
			}
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

	}


	class AlertTimeout implements Runnable
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
			} catch (InterruptedException ex) {}
			setCurrent(nextScreen);
		}
	}
  
  class TickerPaint implements Runnable
  {

    public void run()
		{
      while (true) {
        if (current != null && current instanceof Screen) {
          Ticker ticker = ((Screen) current).getTicker();
          if (ticker != null) {
            synchronized (ticker) {
              if (ticker.resetTextPosTo != -1) {
                ticker.textPos = ticker.resetTextPosTo;
                ticker.resetTextPosTo = -1;
              }
              ticker.textPos -= Ticker.PAINT_MOVE;
            }
            repaint();        
          }
        }
    		try {
    			Thread.sleep(Ticker.PAINT_TIMEOUT);
    		} catch (InterruptedException ex) {}
      }
		}
  
  }


	Display()
	{
		accessor = new DisplayAccessor(this);
    
    new Thread(new TickerPaint()).start();
	}


  public void callSerially(Runnable r)
  {
    // Not implemented
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


	public boolean flashBacklight(int duration)
	{
System.out.println("TODO");
		return false;
	}
	

	public int getBestImageHeight(int imageType)
	{
System.out.println("TODO");
		return 0;
	}
	

	public int getBestImageWidth(int imageType)
	{
System.out.println("TODO");
		return 0;
	}
	

	public int getBorderStyle(boolean highlighted)
	{
System.out.println("TODO");
		return 0;
	}
	

	public int getColor(int colorSpecifier)
	{
System.out.println("TODO");
		return 0;
	}
	
					
	public Displayable getCurrent()
	{
		return current;
	}


	public boolean isColor()
	{
		return DeviceFactory.getDevice().getDeviceDisplay().isColor();
	}


	public int numAlphaLevels()
	{
System.out.println("TODO");
		return 0;
	}
	
	
	public int numColors()
	{
		return DeviceFactory.getDevice().getDeviceDisplay().numColors();
	}


	public void setCurrent(Displayable nextDisplayable)
	{
		if (nextDisplayable != null) {
      if (current != null) {
        current.hideNotify(this);
      }

			if (nextDisplayable instanceof Alert)
			{
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


	public void setCurrentItem(Item item)
	{
System.out.println("TODO");
	}
	
	
	public boolean vibrate(int duration)
	{
System.out.println("TODO");
		return false;
	}
	
	void clearAlert()
	{
		setCurrent(nextScreen);
	}


	static int getGameAction(int keyCode)
	{
		return DeviceFactory.getDevice().getGameAction(keyCode);
	}


	static int getKeyCode(int gameAction)
	{
		return DeviceFactory.getDevice().getKeyCode(gameAction);
	}


	boolean isShown(Displayable d)
	{
		if (current == null || current != d) {
			return false;
		} else {
			return true;
		}
	}


	void repaint()
	{
    if (current != null) {
      repaint(current);
    }
  }
    
    
  void repaint(Displayable d)
	{
		if (current == d) {
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
     * updateCommands has changed the softkey labels
     * tell the outside world it has happened.
     */
    MIDletBridge.notifySoftkeyLabelsChanged();
    repaint();
	}

}
