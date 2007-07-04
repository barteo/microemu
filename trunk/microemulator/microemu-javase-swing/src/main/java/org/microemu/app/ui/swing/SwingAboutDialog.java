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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.microemu.app.Main;
import org.microemu.app.util.BuildVersion;

/**
 * @author vlads
 *
 */
public class SwingAboutDialog extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	private JLabel iconLabel;
	
	private JLabel textLabel;
	
	public SwingAboutDialog() {
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.ipadx = 10;
		c.ipady = 10;
		c.gridx = 0;
		c.gridy = 0;
		iconLabel = new JLabel();
		add(iconLabel, c);
		
		iconLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/org/microemu/icon.png"))));
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		textLabel = new JLabel("MicroEmulator");
		textLabel.setFont(new Font("Default", Font.BOLD, 18));
		add(textLabel, c);
		
		c.gridy = 1;
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.WEST;
		add(new JLabel("version: " + BuildVersion.getVersion()), c);
		
		c.gridy = 2;
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(new JLabel("Copyright (C) 2001-2007 Bartek Teodorczyk & co"), c);

	}
}
