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
 
package com.barteo.emulator;

import com.barteo.emulator.DisplayAccess;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


public abstract class MIDletAccess
{
  public MIDlet midlet;
  DisplayAccess displayAccess;
    
    
  public MIDletAccess(MIDlet amidlet)
  {
    midlet = amidlet;
  }
  
  
  public DisplayAccess getDisplayAccess()
  {
    return displayAccess;
  }
  
  
  public void setDisplayAccess(DisplayAccess adisplayAccess)
  {
    displayAccess = adisplayAccess;
  }
    

	public abstract void startApp()
  		throws MIDletStateChangeException;

	public abstract void pauseApp();

	public abstract void destroyApp(boolean unconditional)
  		throws MIDletStateChangeException;

}