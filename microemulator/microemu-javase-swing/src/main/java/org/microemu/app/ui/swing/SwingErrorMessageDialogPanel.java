/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu.app.ui.swing;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.microemu.app.ui.MessageListener;

/**
 * @author vlads
 *
 */
public class SwingErrorMessageDialogPanel implements MessageListener {

	private static final long serialVersionUID = 1L;

	private Component parent;

	/**
	 * @param parent
	 */
	public SwingErrorMessageDialogPanel(Component parent) {
		this.parent = parent;
	}
	
	/* (non-Javadoc)
	 * @see org.microemu.app.ui.MessageListener#showMessage(int, java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public void showMessage(int level, String title, String text, Throwable throwable) {
		// TODO Add option to show throwable
		JOptionPane.showMessageDialog(parent, text, title, JOptionPane.INFORMATION_MESSAGE);
	}


}
