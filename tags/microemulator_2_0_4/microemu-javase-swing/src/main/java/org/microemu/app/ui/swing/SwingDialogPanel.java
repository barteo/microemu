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

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Bazowa klasa panelu wyswietlanego w oknie dialogowym
 */

public class SwingDialogPanel extends JPanel
{

  public JButton btOk = new JButton("OK");
  public JButton btCancel = new JButton("Cancel");

  boolean state;
  
  boolean extra;

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


  protected void hideNotify()
  {
  }

  
  protected void showNotify()
  {
  }
  
  protected JButton getExtraButton()
  {
	return null;  
  }
  
  public boolean isExtraButtonPressed() {
	  return extra;
  }

}