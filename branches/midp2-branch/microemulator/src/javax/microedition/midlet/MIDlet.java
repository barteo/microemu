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
 
package javax.microedition.midlet;

import javax.microedition.lcdui.*;

import com.barteo.emulator.MIDletAccess;
import com.barteo.emulator.MIDletBridge;


public abstract class MIDlet
{

	class MIDletAccessor extends MIDletAccess
	{

    public MIDletAccessor(MIDlet amidlet)
    {
      super(amidlet);
    }
    

	  public void startApp()
  		  throws MIDletStateChangeException
    {
      MIDletBridge.setCurrentMIDlet(midlet);
      getDisplayAccess().updateCommands();
      midlet.startApp();
    }


  	public void pauseApp()
    {
      midlet.pauseApp();
    }


  	public void destroyApp(boolean unconditional)
    		throws MIDletStateChangeException
    {
      midlet.destroyApp(unconditional);
    }

  }


	protected MIDlet()
	{
    MIDletBridge.setAccess(this, new MIDletAccessor(this));
        
    // initialize display
    Display.getDisplay(this);
	}


	protected abstract void startApp()
  		throws MIDletStateChangeException;


	protected abstract void pauseApp();


	protected abstract void destroyApp(boolean unconditional)
  		throws MIDletStateChangeException;


  public final String getAppProperty(String key)
  {
    return MIDletBridge.getAppProperty(key);
  }


	public final void notifyDestroyed()
	{
    MIDletBridge.notifyDestroyed();
	}

}
