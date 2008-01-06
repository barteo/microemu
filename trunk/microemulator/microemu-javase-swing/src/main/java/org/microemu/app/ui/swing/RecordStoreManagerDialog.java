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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotOpenException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.microemu.RecordStoreManager;
import org.microemu.app.Common;
import org.microemu.app.Config;
import org.microemu.app.util.FileRecordStoreManager;
import org.microemu.log.Logger;
import org.microemu.util.MemoryRecordStoreManager;

public class RecordStoreManagerDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	private Common common;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

	private JLabel recordStoreTypeLabel = new JLabel();

	private JLabel suiteNameLabel = new JLabel();

	private RecordStoreChangePanel recordStoreChangePanel = null;

	private DefaultTableModel modelTable = new DefaultTableModel();

	private JTable logTable = new JTable(modelTable);

	private JScrollPane logScrollPane = new JScrollPane(logTable);

	private ActionListener recordStoreTypeChangeListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			if (recordStoreChangePanel == null) {
				recordStoreChangePanel = new RecordStoreChangePanel(common);
			}
			if (SwingDialogWindow.show(RecordStoreManagerDialog.this, "Change Record Store...", recordStoreChangePanel,
					true)) {
				String recordStoreName = recordStoreChangePanel.getSelectedRecordStoreName();
				if (!recordStoreName.equals(common.getRecordStoreManager().getName())) {
					if (JOptionPane.showConfirmDialog(RecordStoreManagerDialog.this,
							"Changing record store type requires MIDlet restart. \n"
									+ "Do you want to proceed? All MIDlet data will be lost.", "Question?",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
						for (int i = modelTable.getRowCount() - 1; i >= 0; i--) {
							modelTable.removeRow(i);
						}
						RecordStoreManager manager;
						if (recordStoreName.equals("File record store")) {
							manager = new FileRecordStoreManager();
						} else {
							manager = new MemoryRecordStoreManager();
						}
						common.setRecordStoreManager(manager);
						Config.setRecordStoreManagerClassName(manager.getClass().getName());
						refresh();
						try {
							common.initMIDlet(true);
						} catch (Exception ex) {
							Logger.error(ex);
						}
					}
				}
			}
		}

	};

	public RecordStoreManagerDialog(Frame owner, Common common) {
		super("Record Store Manager");

		this.common = common;

		setIconImage(owner.getIconImage());
		setFocusableWindowState(false);

		setLayout(new BorderLayout());

		refresh();

		JButton recordStoreTypeChangeButton = new JButton("Change...");
		recordStoreTypeChangeButton.addActionListener(recordStoreTypeChangeListener);

		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		headerPanel.add(new JLabel("Record store type:"), c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		headerPanel.add(recordStoreTypeLabel, c);

		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		headerPanel.add(recordStoreTypeChangeButton, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		headerPanel.add(new JLabel("Suite name:"), c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		headerPanel.add(suiteNameLabel, c);

		modelTable.addColumn("Timestamp");
		modelTable.addColumn("Action type");
		modelTable.addColumn("Record store name");
		modelTable.addColumn("Details");
		logTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

			private Color SUPER_LIGHT_GRAY = new Color(240, 240, 240);

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				if ((row % 2) == 0) {
					setBackground(Color.WHITE);
				} else {
					setBackground(SUPER_LIGHT_GRAY);
				}

				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}

			private static final long serialVersionUID = 1L;

		});
		logTable.setShowGrid(false);
		logScrollPane.setAutoscrolls(true);

		JTabbedPane viewPanel = new JTabbedPane();
		// viewPanel.addTab("Records view", new JLabel("Records view"));
		viewPanel.addTab("Log view", logScrollPane);

		getContentPane().add(headerPanel, BorderLayout.NORTH);
		getContentPane().add(viewPanel, BorderLayout.CENTER);
	}

	public void refresh() {
		recordStoreTypeLabel.setText(common.getRecordStoreManager().getName());

		suiteNameLabel.setText(common.getLauncher().getSuiteName());

		common.getRecordStoreManager().setRecordListener(new RecordListener() {

			public void recordEvent(int type, long timestamp, RecordStore recordStore, int recordId) {
				String eventMessageType = null;

				switch (type) {
				case RecordListener.RECORD_ADD:
					eventMessageType = "added";
					break;
				case RecordListener.RECORD_READ:
					eventMessageType = "read";
					break;
				case RecordListener.RECORD_CHANGE:
					eventMessageType = "changed";
					break;
				case RecordListener.RECORD_DELETE:
					eventMessageType = "deleted";
					break;
				}

				try {
					modelTable.addRow(new Object[] { dateFormat.format(new Date(timestamp)),
							"record " + eventMessageType, recordStore.getName(), "recordId = " + recordId });
				} catch (RecordStoreNotOpenException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logTable.scrollRectToVisible(logTable.getCellRect(modelTable.getRowCount() - 1, 0, true));
			}

			public void recordStoreEvent(int type, long timestamp, String recordStoreName) {
				String eventMessageType = null;

				switch (type) {
				case RecordListener.RECORDSTORE_OPEN:
					eventMessageType = "opened";
					break;
				case RecordListener.RECORDSTORE_CLOSE:
					eventMessageType = "closed";
					break;
				case RecordListener.RECORDSTORE_DELETE:
					eventMessageType = "deleted";
					break;
				}

				modelTable.addRow(new Object[] { dateFormat.format(new Date(timestamp)), "store " + eventMessageType,
						recordStoreName, null });
				logTable.scrollRectToVisible(logTable.getCellRect(modelTable.getRowCount() - 1, 0, true));
			}

		});
	}
}