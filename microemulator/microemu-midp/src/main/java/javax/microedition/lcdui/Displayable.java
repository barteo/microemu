/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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

import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.DisplayableUI;



public abstract class Displayable
{
	Device device;
	
	boolean sizeChangedDeferredRequest;
	
	Display currentDisplay = null;
    
	boolean fullScreenMode;

    Ticker ticker;
    
    // TODO make private
    int viewPortY;
    // TODO make private
    int viewPortHeight;
    
    protected DisplayableUI ui;
    
    private String title;
    
    /**
     * @associates Command 
     */
	private Vector commands = new Vector();
	
	private CommandListener listener = null;

    
    Displayable(String title) 
    {
        this.device = DeviceFactory.getDevice();
        this.sizeChangedDeferredRequest = false;        
        this.fullScreenMode = false;
        this.title = title;
    }
    
    
    void setUI(DisplayableUI ui) {
    	this.ui = ui;
    }
  

	public void addCommand(Command cmd) {
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
		
		ui.addCommand(cmd);

		if (isShown()) {
			currentDisplay.updateCommands();
		}
	}


	public void removeCommand(Command cmd)
	{
		commands.removeElement(cmd);
		
		ui.removeCommand(cmd);

		if (isShown()) {
			currentDisplay.updateCommands();
		}
	}
    
    
    public int getWidth()
    {
    	if (fullScreenMode) {
    		return device.getDeviceDisplay().getFullWidth();
    	} else {
    		return device.getDeviceDisplay().getWidth();
    	}
    }


    public int getHeight()
    {
    	if (fullScreenMode) {
    		return device.getDeviceDisplay().getFullHeight();
    	} else {
    		return device.getDeviceDisplay().getHeight();
    	}
    }


	public boolean isShown()
	{
		if (currentDisplay == null) {
			return false;
		}
		return currentDisplay.isShown(this);
	}

    
    public Ticker getTicker() 
    {
        return ticker;
    }

    
    public void setTicker(Ticker ticker) 
    {
        this.ticker = ticker;

        repaint();
    }

    
    public String getTitle() 
    {
        return title;
    }

    
    public void setTitle(String s) 
    {
        this.title = s;
        
        // TODO move to the native UI component
        ui.invalidate();
    }        
    

	public void setCommandListener(CommandListener l)
	{
		listener = l;
		
		ui.setCommandListener(l);
	}
	
	
	protected void sizeChanged(int w, int h)
	{		
	}


	CommandListener getCommandListener()
	{
		return listener;
	}


	Vector getCommands()
	{
		// in Form this is overriden to allow for the inclusion
		// of item contained commands 
		// Andres Navarro
		return commands;
	}


	void hideNotify()
	{
	}


	final void hideNotify(Display d)
	{		
		ui.hideNotify();

		hideNotify();
	}


	void keyPressed(int keyCode)
	{
	}


	void keyRepeated(int keyCode)
	{
	}


	void keyReleased(int keyCode)
	{
	}


	void pointerPressed(int x, int y) 
	{
	}

	
	void pointerReleased(int x, int y) 
	{
	}

	
	void pointerDragged(int x, int y) 
	{
	}

	
	abstract void paint(Graphics g);


	void repaint()
	{
		if (currentDisplay != null) {
			repaint(0, 0, getWidth(), getHeight());
		}
	}


	void repaint(int x, int y, int width, int height)
    {
		if (currentDisplay != null) {
			currentDisplay.repaint(this, x, y, width, height);
		}
    }
	
	
	void showNotify()
	{        
	}


	final void showNotify(Display d)
	{
		currentDisplay = d;
        viewPortY = 0;
        // TODO remove this StringComponent object when native UI is completed
        StringComponent title = new StringComponent(getTitle());
        viewPortHeight = getHeight() - title.getHeight() - 1;
        if (ticker != null) {
        		viewPortHeight -= this.ticker.getHeight();
        }
        
        if (sizeChangedDeferredRequest) {
        	sizeChanged(getWidth(), getHeight());
        	sizeChangedDeferredRequest = false;
        }
		
		showNotify();

		ui.showNotify();		
	}

}
