/*
 * MicroEmulator
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.barteo.emulator;

import javax.microedition.midlet.MIDletStateChangeException;

/**
 * MIDletAccess
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
public interface MIDletAccess
{

	public void startApp()
  		throws MIDletStateChangeException;

	public void pauseApp();

	public void destroyApp(boolean unconditional)
  		throws MIDletStateChangeException;

}