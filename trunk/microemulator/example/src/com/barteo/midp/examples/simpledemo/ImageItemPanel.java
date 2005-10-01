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

import java.io.*;

import javax.microedition.lcdui.*;


public class ImageItemPanel extends Form implements ScreenPanel, CommandListener
{
  
  static String NAME = "ImageItem";
  
  Command backCommand = new Command("Back", Command.BACK, 1);
  
  
  public ImageItemPanel()
  {
    super(NAME);
    
    try {
      Image image = Image.createImage("/com/barteo/midp/examples/simpledemo/image.png");
      
      append(new ImageItem("Default Layout", image, ImageItem.LAYOUT_DEFAULT, null));
      append(new ImageItem("Left Layout", image, ImageItem.LAYOUT_LEFT, null));
      append(new ImageItem("Center Layout", image, ImageItem.LAYOUT_CENTER, null));
      append(new ImageItem("Right Layout", image, ImageItem.LAYOUT_RIGHT, null));      
    } catch (IOException ex) {
      append("Cannot load images");
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
      if (c == backCommand) {
        Display.getDisplay(SimpleDemo.getInstance()).setCurrent(SimpleDemo.getInstance().menuList);
      }
    }
  }
  
}
