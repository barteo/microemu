/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.swing;

import javax.swing.JTextField;

import org.microemu.app.ui.swing.SwingDialogPanel;

public class JadUrlPanel extends SwingDialogPanel {
	
    private static final long serialVersionUID = 1L;
    
    private JTextField jadUrlField = new JTextField(50);

	public JadUrlPanel() {		
		add(jadUrlField);
	}
	
	public String getText() {
		return jadUrlField.getText();
	}

	protected void showNotify() {
		jadUrlField.setText("");
	}
	
}
