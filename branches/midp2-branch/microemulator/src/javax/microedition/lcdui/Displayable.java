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

import java.util.Vector;

import com.barteo.emulator.device.DeviceFactory;


public abstract class Displayable
{
// Move more code from Screen (eg. paint)		
	StringComponent title;
	Ticker ticker;
	int viewPortY;
	int viewPortHeight;

	Display currentDisplay = null;

	private Vector commands = new Vector();
	CommandListener listener = null;


	Displayable(String title)
	{
		this.title = new StringComponent(title);
		viewPortY = 0;
		viewPortHeight = DeviceFactory.getDevice().getDeviceDisplay().getHeight() - this.title.getHeight() - 1;
	}
	

	public void addCommand(Command cmd)
	{
    // Check that its not the same command
    for (int i = 0; i < commands.size(); i++) {
      if (cmd == (Command) commands.elementAt(i)) {
        // Its the same just return
				return;
			}
		}

    // Now insert it in order
    boolean inserted = false;
    for (int i = 0; i < commands.size(); i++) {
      if (cmd.getPriority() < ((Command) commands.elementAt(i)).getPriority()) {
        commands.insertElementAt(cmd, i);
        inserted = true;
        break;
      }
    }
    if (inserted == false) {
      // Not inserted just place it at the end
      commands.addElement(cmd);
    }

		if (isShown()) {
			currentDisplay.updateCommands();
		}
	}


	public int getHeight()
	{
System.out.println("TODO");
		return 0;
	}
	

	public int getWidth()
	{
System.out.println("TODO");
		return 0;
	}
	
		
	public Ticker getTicker()
	{
		return ticker;
	}


	public void setTicker(Ticker ticker)
	{
		if (this.ticker != null) {
			viewPortHeight += this.ticker.getHeight();
		}
		this.ticker = ticker;
		if (this.ticker != null) {
			viewPortHeight -= this.ticker.getHeight();
		}
		repaint();
	}


	public String getTitle()
	{
		return title.getText();
	}


	public void setTitle(String s)
	{
		title.setText(s);
	}


	public void removeCommand(Command cmd)
	{
		commands.removeElement(cmd);

		if (isShown()) {
			currentDisplay.updateCommands();
		}
	}


	public boolean isShown()
	{
		if (currentDisplay == null) {
			return false;
		}
		return currentDisplay.isShown(this);
	}


	public void setCommandListener(CommandListener l)
	{
		listener = l;
	}


	protected void sizeChanged(int w, int h)
	{
System.out.println("TODO");
	}
	
	
	CommandListener getCommandListener()
	{
		return listener;
	}


	Vector getCommands()
	{
		return commands;
	}


	void hideNotify()
	{
	}


	final void hideNotify(Display d)
	{
		hideNotify();
	}


	void keyPressed(int keyCode)
	{
	}


	void keyReleased(int keyCode)
	{
	}


	abstract void paint(Graphics g);


	void repaint()
	{
		if (currentDisplay != null) {
			currentDisplay.repaint(this);
		}
	}


	void showNotify()
	{
	}


	final void showNotify(Display d)
	{
		currentDisplay = d;
		showNotify();
	}

}
