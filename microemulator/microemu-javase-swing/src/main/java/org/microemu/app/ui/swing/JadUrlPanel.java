package org.microemu.app.ui.swing;

import javax.swing.JTextField;

import org.microemu.app.ui.swing.SwingDialogPanel;

public class JadUrlPanel extends SwingDialogPanel {
	
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
