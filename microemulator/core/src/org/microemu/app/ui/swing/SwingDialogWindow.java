/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package org.microemu.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Uniwersalna klasa sluzaca do wyswietlania okienek dialogowych
 */

public class SwingDialogWindow
{

  /**
   * Metoda wywolujaca modalne okienko dialogowe
   *
   * @param title tytul okienka
   * @param panel wnetrze okienka dialogowego
   * @return true jesli zamkniecie okna zostalo wywolane przez przycisk OK
   */
  public static boolean show(JFrame parent, String title, final SwingDialogPanel panel)
  {
    final JDialog dialog = new JDialog(parent, title, true);
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);

    JPanel actionPanel = new JPanel();
    actionPanel.add(panel.btOk);
    actionPanel.add(panel.btCancel);
    dialog.getContentPane().add(actionPanel, BorderLayout.SOUTH);

    dialog.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = dialog.getSize();
    dialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    ActionListener closeListener = new ActionListener()
    {
      public void actionPerformed(ActionEvent event)
      {
        if (event.getSource() == panel.btOk) {
          if (panel.check(true)) {
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
    panel.showNotify();
    dialog.setVisible(true);
    panel.btOk.removeActionListener(closeListener);
    panel.btCancel.removeActionListener(closeListener);

    return panel.state;
  }

}

