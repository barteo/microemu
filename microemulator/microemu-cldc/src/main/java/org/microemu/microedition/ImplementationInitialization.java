/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *  @version $Id$
 */
package org.microemu.microedition;

import java.util.Map;

/**
 * @author vlads
 * 
 * Optional JSR implementation can be plugged to Emulator using this interfaces.
 * See module microemu-jsr-75 as example
 * 
 * Relevant MicroEmulator command line option
 * 
 * <pre>
 *  --impl JSR_implementation_class_name Initialize and register optional JSR implementation class.
 * </pre>
 * 
 */
public interface ImplementationInitialization {

	public static final String PARAM_EMULATOR_ID = "emulatorID";

	/**
	 * 
	 * Call implementation initialization inside secure context.
	 * 
	 * @param parameters
	 *            Map of configuration options
	 */
	public void registerImplementation(Map parameters);

	/**
	 * Called when MIDlet started
	 */
	public void notifyMIDletStart();

	/**
	 * Called when MIDlet exits or destroyed
	 */
	public void notifyMIDletDestroyed();
}
