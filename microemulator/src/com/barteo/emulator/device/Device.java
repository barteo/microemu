/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.device;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;

import com.barteo.emulator.Button;
import com.barteo.emulator.Resource;
import com.barteo.emulator.SoftButton;
import nanoxml.XMLElement;
import nanoxml.XMLParseException;


public class Device {

  static Device instance = new Device();

  boolean initialized = false;
  Vector deviceButtons = new Vector();
  
  public static Image normalImage;
  public static Image overImage;
  public static Image pressedImage;

  public static Image upImage;
  public static Rectangle upImagePaintable;
  public static Image downImage;
  public static Rectangle downImagePaintable;
  
  public static Image mode123Image;  
  public static Rectangle mode123ImagePaintable;
  public static Image modeAbcUpperImage;  
  public static Rectangle modeAbcUpperImagePaintable;
  public static Image modeAbcLowerImage;  
  public static Rectangle modeAbcLowerImagePaintable;

  public static Color backgroundColor;
  public static Color foregroundColor;

  public static Rectangle deviceRectangle;

  public static Rectangle screenRectangle;
  public static Rectangle screenPaintable;
  
  
  private Device()
  {
    String xml = "";
    // Here should be device.xml but Netscape security manager doesn't accept this extension
    InputStream dis = new BufferedInputStream(
        getClass().getResourceAsStream("/com/barteo/emulator/device/device.txt"));
    try {
      while (dis.available() > 0) {
        byte[] b = new byte[dis.available()];
        dis.read(b);
        xml += new String(b);
      }
    } catch (Exception ex) {
      System.out.println("Cannot find com.barteo.emulator.device.device.txt definition file");
      return;
    }

    XMLElement doc = new XMLElement();
    try {
      doc.parseString(xml);
    } catch (XMLParseException ex) {
      System.err.println(ex);
      return;
    }

    for (Enumeration e = doc.enumerateChildren(); e.hasMoreElements(); ) {
      XMLElement tmp = (XMLElement) e.nextElement();
      if (tmp.getName().equals("rectangle")) {
        deviceRectangle = getRectangle(tmp);
      } else if (tmp.getName().equals("img")) {
        try {
          if (tmp.getStringAttribute("name").equals("normal")) {
            normalImage = Resource.getInstance().getSystemImage(tmp.getStringAttribute("src"));
          } else if (tmp.getStringAttribute("name").equals("over")) {
            overImage = Resource.getInstance().getSystemImage(tmp.getStringAttribute("src"));
          } else if (tmp.getStringAttribute("name").equals("pressed")) {
            pressedImage = Resource.getInstance().getSystemImage(tmp.getStringAttribute("src"));
          }
        } catch (IOException ex) {
          System.out.println("Cannot load " + tmp.getStringAttribute("src"));
          return;
        }
      } else if (tmp.getName().equals("screen")) {
        for (Enumeration e_screen = tmp.enumerateChildren(); e_screen.hasMoreElements(); ) {
          XMLElement tmp_screen = (XMLElement) e_screen.nextElement();
          if (tmp_screen.getName().equals("background")) {
            backgroundColor = new Color(Integer.parseInt(tmp_screen.getContent(), 16));
          } else if (tmp_screen.getName().equals("foreground")) {
            foregroundColor = new Color(Integer.parseInt(tmp_screen.getContent(), 16));
          } else if (tmp_screen.getName().equals("rectangle")) {
            screenRectangle = getRectangle(tmp_screen);
          } else if (tmp_screen.getName().equals("paintable")) {
            screenPaintable = getRectangle(tmp_screen);
          } else if (tmp_screen.getName().equals("img")) {
            try {
              if (tmp_screen.getStringAttribute("name").equals("up")) {
                upImage = Resource.getInstance().getImage(tmp_screen.getStringAttribute("src"));
                upImagePaintable = getRectangle(getElement(tmp_screen, "paintable"));
              } else if (tmp_screen.getStringAttribute("name").equals("down")) {
                downImage = Resource.getInstance().getImage(tmp_screen.getStringAttribute("src"));
                downImagePaintable = getRectangle(getElement(tmp_screen, "paintable"));
              } else if (tmp_screen.getStringAttribute("name").equals("mode")) {
                if (tmp_screen.getStringAttribute("type").equals("123")) {
                  mode123Image = Resource.getInstance().getImage(tmp_screen.getStringAttribute("src"));
                  mode123ImagePaintable = getRectangle(getElement(tmp_screen, "paintable"));
                } else if (tmp_screen.getStringAttribute("type").equals("abc")) {
                  modeAbcLowerImage = Resource.getInstance().getImage(tmp_screen.getStringAttribute("src"));
                  modeAbcLowerImagePaintable = getRectangle(getElement(tmp_screen, "paintable"));
                } else if (tmp_screen.getStringAttribute("type").equals("ABC")) {
                  modeAbcUpperImage = Resource.getInstance().getImage(tmp_screen.getStringAttribute("src"));
                  modeAbcUpperImagePaintable = getRectangle(getElement(tmp_screen, "paintable"));
                }
              }
            } catch (IOException ex) {
              System.out.println("Cannot load " + tmp_screen.getStringAttribute("src"));
              return;
            }
          }
        }
      } else if (tmp.getName().equals("keyboard")) {
        for (Enumeration e_keyboard = tmp.enumerateChildren(); e_keyboard.hasMoreElements(); ) {
          XMLElement tmp_keyboard = (XMLElement) e_keyboard.nextElement();
          if (tmp_keyboard.getName().equals("button")) {
            Rectangle rectangle = null;
            Vector stringArray = new Vector();
            for (Enumeration e_button = tmp_keyboard.enumerateChildren(); e_button.hasMoreElements(); ) {
              XMLElement tmp_button = (XMLElement) e_button.nextElement();
              if (tmp_button.getName().equals("chars")) {
                for (Enumeration e_chars = tmp_button.enumerateChildren(); e_chars.hasMoreElements(); ) {
                  XMLElement tmp_chars = (XMLElement) e_chars.nextElement();
                  if (tmp_chars.getName().equals("char")) {                 
                    stringArray.addElement(tmp_chars.getContent());                    
                  }
                }
              } else if (tmp_button.getName().equals("rectangle")) {
                rectangle = getRectangle(tmp_button);
              }
            }
            char[] charArray = new char[stringArray.size()];
            for (int i = 0; i < stringArray.size(); i++) {
              String str = (String) stringArray.elementAt(i);
              if (str.length() > 0) {
                charArray[i] = str.charAt(0);
              } else {
                charArray[i] = ' ';
              }
            }
            deviceButtons.addElement(new Button(tmp_keyboard.getStringAttribute("name"), 
                rectangle, tmp_keyboard.getStringAttribute("key"), charArray));
          } else if (tmp_keyboard.getName().equals("softbutton")) {
            Vector commands = new Vector();
            Rectangle rectangle = null, paintable = null;
            for (Enumeration e_button = tmp_keyboard.enumerateChildren(); e_button.hasMoreElements(); ) {
              XMLElement tmp_button = (XMLElement) e_button.nextElement();
              if (tmp_button.getName().equals("rectangle")) {
                rectangle = getRectangle(tmp_button);
              } else if (tmp_button.getName().equals("paintable")) {
                paintable = getRectangle(tmp_button);
              } else if (tmp_button.getName().equals("command")) {
                commands.addElement(tmp_button.getContent());
              }
            }
            String tmp_str = tmp_keyboard.getStringAttribute("menuactivate");
            boolean menuactivate = false;
            if (tmp_str != null && tmp_str.equals("true")) {
              menuactivate = true;
            }
            deviceButtons.addElement(new SoftButton(tmp_keyboard.getStringAttribute("name"),
                rectangle, tmp_keyboard.getStringAttribute("key"), paintable, 
                tmp_keyboard.getStringAttribute("alignment"), commands, menuactivate));
          }
        }
      }
    }
    
    initialized = true;
  }
  
  
  public static Device getInstance()
  {
    return instance;
  }


  public boolean isInitialized()
  {    
    return initialized;
  }


  public static Vector getDeviceButtons() 
  {
    return instance.deviceButtons;
  }
  
  
  XMLElement getElement(XMLElement source, String name)
  {
    for (Enumeration e_content = source.enumerateChildren(); e_content.hasMoreElements(); ) {
      XMLElement tmp_content = (XMLElement) e_content.nextElement();
      if (tmp_content.getName().equals(name)) {
        return tmp_content;
      }
    }

    return null;
  }
  
  
  Rectangle getRectangle(XMLElement source)
  {
    Rectangle rect = new Rectangle();
    
    for (Enumeration e_rectangle = source.enumerateChildren(); e_rectangle.hasMoreElements(); ) {
      XMLElement tmp_rectangle = (XMLElement) e_rectangle.nextElement();
      if (tmp_rectangle.getName().equals("x")) {
        rect.x = Integer.parseInt(tmp_rectangle.getContent());
      } else if (tmp_rectangle.getName().equals("y")) {
        rect.y = Integer.parseInt(tmp_rectangle.getContent());
      } else if (tmp_rectangle.getName().equals("width")) {
        rect.width = Integer.parseInt(tmp_rectangle.getContent());
      } else if (tmp_rectangle.getName().equals("height")) {
        rect.height = Integer.parseInt(tmp_rectangle.getContent());
      }
    }
    
    return rect;
  }

}
