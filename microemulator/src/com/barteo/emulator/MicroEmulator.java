/*
 * @(#)MicroEmulator.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.barteo.emulator;

import java.applet.Applet;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.barteo.emulator.device.Device;
import com.barteo.midp.lcdui.DisplayComponent;
import com.barteo.midp.lcdui.FontManager;
import com.barteo.midp.lcdui.KeyboardComponent;
import com.barteo.midp.lcdui.XYConstraints;
import com.barteo.midp.lcdui.XYLayout;


public class MicroEmulator extends Applet
{
  static MIDletAccess midletAccess;
	Class midletClass;
	MIDlet midlet = null;
	Display disp = null;

	DisplayComponent dc;
	KeyboardComponent kc;

	Image offscreen = null;

  Font defaultFont;
  

  public void init()
	{
		String midletName = getParameter("midlet");
		if (midletName == null) {
			System.out.println("There is no midlet parameter");
			return;
		}
		if (!setMidletClass(midletName)) {
			return;
		}

		initApp();
	}


  public boolean initApp()
  {
    defaultFont = new Font("SansSerif", Font.PLAIN, 11);
    setFont(defaultFont);
    FontManager.getInstance().setDefaultFontMetrics(getFontMetrics(defaultFont));

    if (!Device.getInstance().isInitialized()) {
      return false;
    }
      
    XYLayout xy = new XYLayout();
    setLayout(xy);

    dc = new DisplayComponent(this);
    xy.addLayoutComponent(dc, new XYConstraints(Device.screenRectangle));
    add(dc);

    kc = new KeyboardComponent();
    xy.addLayoutComponent(kc, new XYConstraints(Device.keyboardRectangle));
    add(kc);      
    
    try {
      midlet = (MIDlet) midletClass.newInstance();
    } catch (Exception ex) {
      System.out.println("Cannot initialize " + midletClass + " MIDlet class");
      System.out.println(ex);        
      return false;
    }

    disp = Display.getDisplay(midlet);

    resize(400,500);
    
    return true;
  }


  public void start()
	{
		if (midlet != null) {
      try {
        midletAccess.startApp();
			} catch (MIDletStateChangeException ex) {
				System.err.println(ex);
			}
		}
  }


	public void stop() 
  {
		if (midlet != null) {
			midletAccess.pauseApp();
		}
    
    removeAll();
  }


	public void destroy()
	{
		if (midlet != null) {
      try {
  			midletAccess.destroyApp(true);
			} catch (MIDletStateChangeException ex) {
				System.err.println(ex);
			}
		}
	}


	public boolean setMidletClass(String name)
	{
		try {
			midletClass = Class.forName(name);
		} catch (ClassNotFoundException ex) {
			System.out.println("Cannot find " + name + " MIDlet class");
			return false;
		}

		return true;
	}


  public static void setMIDletAccess(MIDletAccess ma)
  {
    midletAccess = ma;
  }


  public String getAppletInfo()
	{
    return "Title: MicroEmulator \nAuthor: Bartek Teodorczyk, 2001";
  }


  public String[][] getParameterInfo()
	{
    String[][] info = {
		    {"midlet", "MIDlet class name", "The MIDlet class name. This field is mandatory."},
    };

		return info;
  }


  static class MyAdapter extends WindowAdapter
	{
    public void windowClosing(WindowEvent e)
		{
      System.exit(0);
    }
  }


  public static void main(String args[])
  {
    if (args.length != 1) {
      System.out.println("Usage: java com.barteo.emulator.MicroEmulator [midlet class name]");
      System.exit(0);
    }

    Frame f = new Frame("MicroEmulator");
    MicroEmulator app = new MicroEmulator();

    if (!app.setMidletClass(args[0])) {
      System.exit(0);
    }

    if (!app.initApp()) {
      System.exit(0);
    }
    app.start();

    f.add("Center", app);
    f.pack();
    f.setSize(400, 500);
    f.show();

    f.addWindowListener(new MyAdapter());
  }

}
