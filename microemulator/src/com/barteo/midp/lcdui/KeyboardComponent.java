/*
 *  @(#)KeyboardComponent.java  07/07/2001
 *
 *  Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.barteo.midp.lcdui;

import java.awt.Button;
import java.awt.Panel;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Command;
import com.barteo.emulator.Button;
import com.barteo.emulator.SoftButton;
import com.barteo.emulator.device.Device;


public class KeyboardComponent extends Panel 
{

  Hashtable compToButton = new Hashtable();

  KeyboardActionListener kb_listener = new KeyboardActionListener();


  public KeyboardComponent() 
  {
    XYLayout xy = new XYLayout();
    setLayout(xy);

    for (Enumeration e = Device.getDeviceButtons().elements(); e.hasMoreElements(); ) {
      com.barteo.emulator.Button button = (com.barteo.emulator.Button) e.nextElement();
      button.getRectangle().x -= Device.keyboardRectangle.x;
      button.getRectangle().y -= Device.keyboardRectangle.y;
      java.awt.Button comp = new java.awt.Button(button.getName());
      comp.addMouseListener(kb_listener);
      xy.addLayoutComponent(comp, new XYConstraints(button.getRectangle()));
      add(comp);
      compToButton.put(comp, button);
    }        
  }


  class KeyboardActionListener extends MouseAdapter 
  {

    public void mousePressed(MouseEvent e) 
    {
      int key = getKey(e);

      if (key != 0) {
        InputMethod.getInputMethod().keyPressed(key);
        return;
      }

      java.awt.Button tmp = (java.awt.Button) e.getSource();      
      if (compToButton.get(tmp) instanceof SoftButton) {
        Command cmd;
        int i = 0;
        for (Enumeration s = Device.getDeviceButtons().elements(); s.hasMoreElements(); ) {
          com.barteo.emulator.Button button = (com.barteo.emulator.Button) s.nextElement();
          if (button instanceof SoftButton) {
            cmd = ((SoftButton) button).getCommand();
            /* previous code                        
            if (cmd != null && i == 0 && tmp == soft1) {
              CommandManager.getInstance().commandAction(cmd);
            }
            if (cmd != null && i == 1 && tmp == soft2) {
              CommandManager.getInstance().commandAction(cmd);
            }*/            
            if (cmd != null && i == 0) {
              CommandManager.getInstance().commandAction(cmd);
            }
            if (cmd != null && i == 1) {
              CommandManager.getInstance().commandAction(cmd);
            }
            i++;
          }
        }
      }
    }


    public void mouseReleased(MouseEvent e) 
    {
      int key = getKey(e);

      if (key != 0) {
        InputMethod.getInputMethod().keyReleased(key);
        return;
      }
    }


    int getKey(MouseEvent e) 
    {
      int key = 0;
      java.awt.Button tmp = (java.awt.Button) e.getSource();

      com.barteo.emulator.Button button = (com.barteo.emulator.Button) compToButton.get(tmp);
      if (button != null && !(button instanceof SoftButton)) {
        key = button.getKey();
      }

      return key;
    }

  }

}
