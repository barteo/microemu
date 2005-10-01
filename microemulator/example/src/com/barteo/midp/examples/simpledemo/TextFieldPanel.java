/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 
package com.barteo.midp.examples.simpledemo;

import javax.microedition.lcdui.*;


public class TextFieldPanel extends Form implements ScreenPanel, CommandListener
{

  static String NAME = "TextField";

  TextField textBoxes[] = {
      new TextField("Any character", null, 128, TextField.ANY),
      new TextField("Email", null, 128, TextField.EMAILADDR),
      new TextField("Numeric", null, 128, TextField.NUMERIC),
      new TextField("Phone number", null, 128, TextField.PHONENUMBER),
      new TextField("URL", null, 128, TextField.URL),
      new TextField("Password", null, 128, TextField.PASSWORD),
  };  
  
  Command backCommand = new Command("Back", Command.BACK, 1);
  
  
  public TextFieldPanel()
  {
    super(NAME);
    
    append("Ala ma kota");
    for (int i = 0; i < textBoxes.length; i++) {
//      textBoxes[i].addCommand(backCommand);
      //textBoxes[i].setCommandListener(this);      
      append(textBoxes[i]);
    }
    
    addCommand(backCommand);
    setCommandListener(this);
  }
    
    
  public String getName()
  {
    return NAME;
  }
  
  
  public void commandAction(Command c, Displayable d)
  {
    if (d == this) {
      if (c == List.SELECT_COMMAND) {
//        Display.getDisplay(SimpleDemo.getInstance()).setCurrent(textBoxes[getSelectedIndex()]);
      } else if (c == backCommand) {
        Display.getDisplay(SimpleDemo.getInstance()).setCurrent(SimpleDemo.getInstance().menuList);
      }
    } else if (c == backCommand) {
/*      for (int i = 0; i < textBoxes.length; i++) {
        if (d == textBoxes[i]) {
          Display.getDisplay(SimpleDemo.getInstance()).setCurrent(this);
        }
      }*/
    }
  }

}
