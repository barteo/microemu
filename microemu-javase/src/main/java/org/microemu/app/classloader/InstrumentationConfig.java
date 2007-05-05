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
package org.microemu.app.classloader;

/**
 * @author vlads
 */
public class InstrumentationConfig {

	private boolean enhanceThreadCreation = false;
	
	private boolean enhanceCatchBlock = false;

	public boolean isEnhanceCatchBlock() {
		return this.enhanceCatchBlock;
	}

	public void setEnhanceCatchBlock(boolean enhanceCatchBlock) {
		this.enhanceCatchBlock = enhanceCatchBlock;
	}

	public boolean isEnhanceThreadCreation() {
		return this.enhanceThreadCreation;
	}

	public void setEnhanceThreadCreation(boolean enhanceThreadCreation) {
		this.enhanceThreadCreation = enhanceThreadCreation;
	}
	
}
