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

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Bazowa klasa panelu wyswietlanego w oknie dialogowym
 */

public class DialogPanel extends JPanel
{

  public JButton btOk = new JButton("OK");
  public JButton btCancel = new JButton("Cancel");


  /**
   * Walidacja panelu
   *
   * @param state czy wyswietlac komunikaty bledow
   * @return true jesli wszysko jest ok
   */
  public boolean check(boolean state)
  {
    return true;
  }


  /**
   * Metoda wywolywana w momencie wyswietlania okna dialogowego
   */
  protected void showNotify()
  {
  }

}