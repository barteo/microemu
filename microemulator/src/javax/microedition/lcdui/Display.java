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
 */
 
package javax.microedition.lcdui;

import javax.microedition.midlet.*;

import com.barteo.emulator.MIDletBridge;
import com.barteo.midp.lcdui.*;


public class Display
{

	Displayable current = null;
	Displayable nextScreen = null;

	DisplayAccessor accessor = null;


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


	public int numColors()
	{
		return 256;
	}


	public static Display getDisplay(MIDlet m)
	{
    Display result;
    
    if (MIDletBridge.getAccess(m).getDisplayAccess() == null) {
      result = new Display();
      MIDletBridge.getAccess(m).setDisplayAccess(result.accessor);
    } else {
      result = MIDletBridge.getAccess(m).getDisplayAccess().getDisplay();
    }

    return result;
	}


	public Displayable getCurrent()
	{
		return current;
	}


	public boolean isColor()
	{
		return false;
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
		DisplayBridge.updateCommands(current.getCommands());
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
		return DisplayBridge.getGameAction(keyCode);
	}


	static int getKeyCode(int gameAction)
	{
		return DisplayBridge.getKeyCode(gameAction);
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
			DisplayBridge.repaint();
    }
  }
    
    
  void repaint(Displayable d)
	{
		if (current == d) {
			DisplayBridge.repaint();
		}
	}
  
  
  void setScrollDown(boolean state)
  {
    DisplayBridge.setScrollDown(state);
  }


  void setScrollUp(boolean state)
  {
    DisplayBridge.setScrollUp(state);
  }


	void updateCommands()
	{
    if (current == null) {
      DisplayBridge.updateCommands(null);
    } else {
      DisplayBridge.updateCommands(current.getCommands());
    }
	}

}
