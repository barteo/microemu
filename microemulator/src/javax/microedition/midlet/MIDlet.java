/*
 * @(#)MIDlet.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package javax.microedition.midlet;

import javax.microedition.lcdui.*;

import com.barteo.emulator.MicroEmulator;
import com.barteo.emulator.MIDletAccess;
import com.barteo.emulator.MIDletBridge;
import com.barteo.midp.lcdui.DisplayBridge;


public abstract class MIDlet
{

  MIDlet midlet;

	class MIDletAccessor implements MIDletAccess
	{

	  public void startApp()
  		  throws MIDletStateChangeException
    {
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
    midlet = this;
    MIDletBridge.setAccess(this, new MIDletAccessor());
	}


	protected abstract void startApp()
  		throws MIDletStateChangeException;


	protected abstract void pauseApp();


	protected abstract void destroyApp(boolean unconditional)
  		throws MIDletStateChangeException;


  public final String getAppProperty(String key)
  {
    return null;
  }


	public final void notifyDestroyed()
	{
    MIDletBridge.notifyDestroyed();
	}

}
