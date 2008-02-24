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
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.microemu.app.util.MRUListListener;

/**
 * @author vlads
 * 
 */
public class JMRUMenu extends JMenu implements MRUListListener {

	private static final long serialVersionUID = 1L;

	public static class MRUActionEvent extends ActionEvent {

		private static final long serialVersionUID = 1L;

		Object sourceMRU;

		public MRUActionEvent(Object sourceMRU, ActionEvent orig) {
			super(orig.getSource(), orig.getID(), orig.getActionCommand(), orig.getWhen(), orig.getModifiers());
			this.sourceMRU = sourceMRU;
		}

		public Object getSourceMRU() {
			return sourceMRU;
		}

	}

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

			Object sourceMRU = item;

			public void actionPerformed(ActionEvent e) {
				JMRUMenu.this.fireActionPerformed(new MRUActionEvent(sourceMRU, e));
			}
		};

		JMenuItem menu = new JMenuItem(a);
		this.insert(menu, 0);
	}

	/**
	 * Do not create new Event
	 */
	protected void fireActionPerformed(ActionEvent event) {
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				((ActionListener) listeners[i + 1]).actionPerformed(event);
			}
		}
	}

}
