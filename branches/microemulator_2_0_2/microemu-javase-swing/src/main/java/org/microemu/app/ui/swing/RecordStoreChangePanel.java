/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
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

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.microemu.app.Common;
import org.microemu.app.util.FileRecordStoreManager;

public class RecordStoreChangePanel extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	private Common common;

	private JComboBox selectStoreCombo = new JComboBox(new String[] { "File record store", "Memory record store" });

	public RecordStoreChangePanel(Common common) {
		this.common = common;

		add(new JLabel("Record store type:"));
		add(selectStoreCombo);
	}

	protected void showNotify() {
		if (common.getRecordStoreManager() instanceof FileRecordStoreManager) {
			selectStoreCombo.setSelectedIndex(0);
		} else {
			selectStoreCombo.setSelectedIndex(1);
		}
	}

	public String getSelectedRecordStoreName() {
		return (String) selectStoreCombo.getSelectedItem();
	}

}
