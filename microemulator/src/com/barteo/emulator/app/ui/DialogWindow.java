/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@it.pl>
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

package com.barteo.emulator.app.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Uniwersalna klasa sluzaca do wyswietlania okienek dialogowych
 */

public class DialogWindow
{

  /**
   * Metoda wywolujaca modalne okienko dialogowe
   *
   * @param title tytul okienka
   * @param panel wnetrze okienka dialogowego
   * @return true jesli zamkniecie okna zostalo wywolane przez przycisk OK
   */
  public static boolean show(String title, final DialogPanel panel)
  {
    final JDialog dialog = new JDialog(new JFrame(), title, true);
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

    CloseListener closeListener = new CloseListener(panel.btOk)
    {

      public void actionPerformed(ActionEvent event)
      {
        if (event.getSource() == btOk) {
          if (panel.check(true)) {
            state = true;
            dialog.setVisible(false);
          }
        } else {
          dialog.setVisible(false);
        }
      }
    };


    panel.btOk.addActionListener(closeListener);
    panel.btCancel.addActionListener(closeListener);
    panel.showNotify();
    dialog.setVisible(true);
    panel.btOk.removeActionListener(closeListener);
    panel.btCancel.removeActionListener(closeListener);

    return closeListener.getState();
  }

}


abstract class CloseListener implements ActionListener
{
  boolean state = false;
  JButton btOk;


  CloseListener(JButton btOk)
  {
    this.btOk = btOk;
  }


  public boolean getState()
  {
    return state;
  }

}
