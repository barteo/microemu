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
 *  @version $Id: DropTransferHandler.java 1104 2007-03-10 17:30:40Z vlads $
 */
package org.microemu.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class SwingLogConsoleDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JTextArea logArea = new JTextArea();

	public SwingLogConsoleDialog(Frame owner) {
		super(owner, "Log console");

		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		
		JPanel north = new JPanel();
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logArea.setText("");
			}
		});
		north.add(clear);		
		pane.add(north, BorderLayout.NORTH);
		
		pane.add(logArea, BorderLayout.CENTER);
	}

}
