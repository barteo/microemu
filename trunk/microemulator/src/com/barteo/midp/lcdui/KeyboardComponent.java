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

import java.awt.*;
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

      com.barteo.emulator.Button tmp = (com.barteo.emulator.Button) compToButton.get(e.getSource());      
      if (tmp instanceof SoftButton) {
        Command cmd = ((SoftButton) tmp).getCommand();
        if (cmd != null) {
          CommandManager.getInstance().commandAction(cmd);
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
