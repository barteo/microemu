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
package org.microemu.app.ui.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.microemu.app.util.MRUListListener;

/**
 * @author vlads
 * 
 */
public abstract class JMRUMenu extends JMenu implements MRUListListener {

	private static final long serialVersionUID = 1L;

	public JMRUMenu(String s) {
		super(s);
	}
	

	public void listItemChanged(final Object item) {
		String label = item.toString();
		for (int i = 0; i < getItemCount(); i++) {
			if (getItem(i).getText().equals(label)) {
				remove(i);
				break;
			}
		}
		AbstractAction a = new AbstractAction(label) {
			
			private static final long serialVersionUID = 1L;

			Object source = item; 
			
			public void actionPerformed(ActionEvent e) {
				fireMRUActionPerformed(source);
			}
		};
		
		JMenuItem menu = new JMenuItem(a);
		this.insert(menu, 0);
	}

	/**
	 * TODO Refactor the code to be natural swing
	 * @param source
	 */
	public abstract void fireMRUActionPerformed(Object source);

}
