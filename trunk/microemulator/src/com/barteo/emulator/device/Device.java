package com.barteo.emulator.device;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;

import com.barteo.emulator.SoftButton;
import nanoxml.XMLElement;


public class Device {

  static Device instance = new Device();

  Vector softButtons = new Vector();

  public static Color backgroundColor;
  public static Color foregroundColor;

  public static int screenRectangleX;
  public static int screenRectangleY;
  public static int screenRectangleWidth;
  public static int screenRectangleHeight;

  public static int screenPaintableX;
  public static int screenPaintableY;
  public static int screenPaintableWidth;
  public static int screenPaintableHeight;
  
  class Rectangle
  {
    int x;
    int y;
    int width;
    int height;
  }


  Device()
  {
    SoftButton soft;

    soft = new SoftButton(1, 112, 38, 16, SoftButton.LEFT);
    soft.addCommandType(Command.BACK);
    soft.addCommandType(Command.EXIT);
    soft.addCommandType(Command.CANCEL);
    soft.addCommandType(Command.STOP);
    softButtons.addElement(soft);

    soft = new SoftButton(58, 112, 38, 16, SoftButton.RIGHT);
    soft.setMenuActivate(true);
    soft.addCommandType(Command.OK);
    soft.addCommandType(Command.SCREEN);
    soft.addCommandType(Command.ITEM);
    soft.addCommandType(Command.HELP);
    softButtons.addElement(soft);
  }
  
  
  public static Device getInstance()
  {
    return instance;
  }


  public boolean init()
  {
    String xml = "";
    DataInputStream dis = new DataInputStream(
        instance.getClass().getResourceAsStream("/com/barteo/emulator/device/device.xml"));
    try {
      while (dis.available() > 0) {
        byte[] b = new byte[dis.available()];
        dis.read(b);
        xml += new String(b);
      }
    } catch (Exception ex) {
      System.out.println("Cannot find com.barteo.emulator.device.device.xml definition file");
      return false;
    }

    XMLElement doc = new XMLElement();
    doc.parseString(xml);

    for (Enumeration e = doc.enumerateChildren(); e.hasMoreElements(); ) {
      XMLElement tmp = (XMLElement) e.nextElement();
      if (tmp.getName().equals("screen")) {
        for (Enumeration e_screen = tmp.enumerateChildren(); e_screen.hasMoreElements(); ) {
          XMLElement tmp_screen = (XMLElement) e_screen.nextElement();
          if (tmp_screen.getName().equals("background")) {
            backgroundColor = new Color(Integer.parseInt(tmp_screen.getContents(), 16));
          }
          if (tmp_screen.getName().equals("foreground")) {
            foregroundColor = new Color(Integer.parseInt(tmp_screen.getContents(), 16));
          }
          if (tmp_screen.getName().equals("rectangle")) {
            Rectangle tmpRect = getRectangle(tmp_screen);
            screenRectangleX = tmpRect.x;
            screenRectangleY = tmpRect.y;
            screenRectangleWidth = tmpRect.width;
            screenRectangleHeight = tmpRect.height;
          }
          if (tmp_screen.getName().equals("paintable")) {
            Rectangle tmpRect = getRectangle(tmp_screen);
            screenPaintableX = tmpRect.x;
            screenPaintableY = tmpRect.y;
            screenPaintableWidth = tmpRect.width;
            screenPaintableHeight = tmpRect.height;
          }
        }
      }
      if (tmp.getName().equals("keyboard")) {
        for (Enumeration e_keyboard = tmp.enumerateChildren(); e_keyboard.hasMoreElements(); ) {
          XMLElement tmp_keyboard = (XMLElement) e_keyboard.nextElement();
          if (tmp_keyboard.getName().equals("button")) {
//            System.out.println(getRectangle(tmp_keyboard));
          }
        }
      }
    }
    
    return true;
  }


  public static Vector getSoftButtons() {
   return instance.softButtons;
  }
  
  
  Rectangle getRectangle(XMLElement source)
  {
    Rectangle rect = new Rectangle();
    
    for (Enumeration e_rectangle = source.enumerateChildren(); e_rectangle.hasMoreElements(); ) {
      XMLElement tmp_rectangle = (XMLElement) e_rectangle.nextElement();
      if (tmp_rectangle.getName().equals("x")) {
        rect.x = Integer.parseInt(tmp_rectangle.getContents());
      }
      if (tmp_rectangle.getName().equals("y")) {
        rect.y = Integer.parseInt(tmp_rectangle.getContents());
      }
      if (tmp_rectangle.getName().equals("width")) {
        rect.width = Integer.parseInt(tmp_rectangle.getContents());
      }
      if (tmp_rectangle.getName().equals("height")) {
        rect.height = Integer.parseInt(tmp_rectangle.getContents());
      }
    }
    
    return rect;
  }

}
