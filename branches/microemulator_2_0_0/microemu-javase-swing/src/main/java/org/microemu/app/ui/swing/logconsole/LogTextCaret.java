/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Michael Lifshits 
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
package org.microemu.app.ui.swing.logconsole;

import java.awt.Rectangle;

import javax.swing.text.DefaultCaret;

/**
 * @author Michael Lifshits
 *
 */
public class LogTextCaret extends DefaultCaret{

	private static final long serialVersionUID = 1L;
	
	private boolean visibilityAdjustmentEnabled = true;
	
    protected void adjustVisibility(Rectangle nloc) {
    	if (visibilityAdjustmentEnabled) {
    		super.adjustVisibility(nloc);
    	}
    }

	public void setVisibilityAdjustment(boolean flag) {
		visibilityAdjustmentEnabled = flag;
	}
    
}
