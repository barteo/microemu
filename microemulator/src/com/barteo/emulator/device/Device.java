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

  static Device device = new Device();

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


  public static boolean init()
  {
    String xml = "";
    DataInputStream dis = new DataInputStream(
        device.getClass().getResourceAsStream("/com/barteo/emulator/device/device.xml"));
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
      if (tmp.getTagName().equals("screen")) {
        for (Enumeration e_screen = tmp.enumerateChildren(); e_screen.hasMoreElements(); ) {
          XMLElement tmp_screen = (XMLElement) e_screen.nextElement();
          if (tmp_screen.getTagName().equals("background")) {
            backgroundColor = new Color(Integer.parseInt(tmp_screen.getContents(), 16));
          }
          if (tmp_screen.getTagName().equals("foreground")) {
            foregroundColor = new Color(Integer.parseInt(tmp_screen.getContents(), 16));
          }
          if (tmp_screen.getTagName().equals("rectangle")) {
            for (Enumeration e_rectangle = tmp_screen.enumerateChildren(); e_rectangle.hasMoreElements(); ) {
              XMLElement tmp_rectangle = (XMLElement) e_rectangle.nextElement();
              if (tmp_rectangle.getTagName().equals("x")) {
                screenRectangleX = Integer.parseInt(tmp_rectangle.getContents());
              }
              if (tmp_rectangle.getTagName().equals("y")) {
                screenRectangleY = Integer.parseInt(tmp_rectangle.getContents());
              }
              if (tmp_rectangle.getTagName().equals("width")) {
                screenRectangleWidth = Integer.parseInt(tmp_rectangle.getContents());
              }
              if (tmp_rectangle.getTagName().equals("height")) {
                screenRectangleHeight = Integer.parseInt(tmp_rectangle.getContents());
              }
            }
          }
          if (tmp_screen.getTagName().equals("paintable")) {
            for (Enumeration e_paintable = tmp_screen.enumerateChildren(); e_paintable.hasMoreElements(); ) {
              XMLElement tmp_paintable = (XMLElement) e_paintable.nextElement();
              if (tmp_paintable.getTagName().equals("x")) {
                screenPaintableX = Integer.parseInt(tmp_paintable.getContents());
              }
              if (tmp_paintable.getTagName().equals("y")) {
                screenPaintableY = Integer.parseInt(tmp_paintable.getContents());
              }
              if (tmp_paintable.getTagName().equals("width")) {
                screenPaintableWidth = Integer.parseInt(tmp_paintable.getContents());
              }
              if (tmp_paintable.getTagName().equals("height")) {
                screenPaintableHeight = Integer.parseInt(tmp_paintable.getContents());
              }
            }
          }
        }
        break;
      }
      if (tmp.getTagName().equals("keyboard")) {
      }
    }
    
    return true;
  }


  public static Vector getSoftButtons() {
   return device.softButtons;
  }

}
