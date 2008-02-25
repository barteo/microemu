/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

package org.microemu.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Uniwersalna klasa sluzaca do wyswietlania okienek dialogowych
 */

public class SwingDialogWindow
{

  /**
   * Open modal dialog window
   *
   * @param title dialog title
   * @param panel content
   * @param hasCancel has Cancel button 
   * @return true if user pressed OK button
   */
  public static boolean show(Frame parent, String title, final SwingDialogPanel panel, boolean hasCancel)
  {
    final JDialog dialog = new JDialog(parent, title, true);
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);

    JPanel actionPanel = new JPanel();
    actionPanel.add(panel.btOk);
    if (hasCancel) {
    	actionPanel.add(panel.btCancel);
    }
    final JButton extraButton = panel.getExtraButton();
    if (extraButton != null) {
    	actionPanel.add(extraButton);
    }
    dialog.getContentPane().add(actionPanel, BorderLayout.SOUTH);
    dialog.pack();
    
    Dimension frameSize = dialog.getSize();
    int x = parent.getLocation().x + ((parent.getWidth() - frameSize.width) / 2);
    if (x < 0) {
    	x = 0;
    }
    int y = parent.getLocation().y + ((parent.getHeight() - frameSize.height) / 2);
    if (y < 0) {
    	y = 0;
    }
    dialog.setLocation(x, y);

    ActionListener closeListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			panel.extra = false;
			if (source == panel.btOk || source == extraButton) {
				if (panel.check(true)) {
					if (source == extraButton) {
						panel.extra = true;
					}
					panel.state = true;
					dialog.setVisible(false);
					panel.hideNotify();
				}
			} else {
				panel.state = false;
				dialog.setVisible(false);
				panel.hideNotify();
			}
		}
	};
    
    WindowAdapter windowAdapter = new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        panel.state = false;
        panel.hideNotify();
      }
    };

    dialog.addWindowListener(windowAdapter);
    panel.btOk.addActionListener(closeListener);
    panel.btCancel.addActionListener(closeListener);
    if (extraButton != null) {
    	extraButton.addActionListener(closeListener);
    }
    panel.showNotify();
    dialog.setVisible(true);
    panel.btOk.removeActionListener(closeListener);
    panel.btCancel.removeActionListener(closeListener);
    if (extraButton != null) {
    	extraButton.removeActionListener(closeListener);
    }

    return panel.state;
  }

}

