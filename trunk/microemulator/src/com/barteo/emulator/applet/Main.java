/*
 * @(#)Main.java  10/12/2001
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

package com.barteo.emulator.applet;

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
import com.barteo.emulator.MicroEmulator;
import com.barteo.emulator.MIDletBridge;
import com.barteo.midp.lcdui.FontManager;
import com.barteo.midp.lcdui.KeyboardComponent;
import com.barteo.midp.lcdui.XYConstraints;
import com.barteo.midp.lcdui.XYLayout;


public class Main extends Applet implements MicroEmulator
{
  MIDlet midlet;

	AWTDisplayComponent dc;
	KeyboardComponent kc;

  Font defaultFont;
  

  public void init()
  {
    defaultFont = new Font("SansSerif", Font.PLAIN, 11);
    setFont(defaultFont);
    FontManager.getInstance().setDefaultFontMetrics(getFontMetrics(defaultFont));
    
    MIDletBridge.setMicroEmulator(this);

    if (!Device.getInstance().isInitialized()) {
      return;
    }
      
    XYLayout xy = new XYLayout();
    setLayout(xy);

    dc = new AWTDisplayComponent(this);
    xy.addLayoutComponent(dc, new XYConstraints(Device.screenRectangle));
    add(dc);

    kc = new KeyboardComponent();
    xy.addLayoutComponent(kc, new XYConstraints(Device.keyboardRectangle));
    add(kc);      
    
		String midletName = getParameter("midlet");
		if (midletName == null) {
			System.out.println("There is no midlet parameter");
			return;
		}

    Class midletClass;
		try {
			midletClass = Class.forName(midletName);
		} catch (ClassNotFoundException ex) {
			System.out.println("Cannot find " + midletName + " MIDlet class");
			return;
		}

    try {
      midlet = (MIDlet) midletClass.newInstance();
    } catch (Exception ex) {
      System.out.println("Cannot initialize " + midletClass + " MIDlet class");
      System.out.println(ex);        
      return;
    }

    resize(Device.deviceRectangle.getSize());
    
    return;
  }


  public void start()
	{
    try {
      MIDletBridge.getAccess(midlet).startApp();
		} catch (MIDletStateChangeException ex) {
      System.err.println(ex);
		}
  }


	public void stop() 
  {
    MIDletBridge.getAccess(midlet).pauseApp();
  }


	public void destroy()
	{
    try {
			MIDletBridge.getAccess(midlet).destroyApp(true);
		} catch (MIDletStateChangeException ex) {
  		System.err.println(ex);
		}
	}


  public void notifyDestroyed()
  {
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

}
