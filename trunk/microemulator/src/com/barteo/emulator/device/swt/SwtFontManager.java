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
 
package com.barteo.emulator.device.swt;

import java.util.Hashtable;

import org.eclipse.swt.SWT;

import com.barteo.emulator.app.ui.swt.SwtDeviceComponent;
import com.barteo.emulator.device.FontManager;


public class SwtFontManager implements FontManager
{
  
  static String FACE_SYSTEM_NAME = "SansSerif";
  static String FACE_MONOSPACE_NAME = "Monospaced";
  static String FACE_PROPORTIONAL_NAME = "SansSerif";
  
  static int SIZE_SMALL = 6;
  static int SIZE_MEDIUM = 8;
  static int SIZE_LARGE = 10;
  
  Hashtable fonts = new Hashtable();
  Hashtable fontsMetrics = new Hashtable();
  

  org.eclipse.swt.graphics.Font getFont(javax.microedition.lcdui.Font meFont)
  {
		org.eclipse.swt.graphics.Font tmp = (org.eclipse.swt.graphics.Font) fonts.get(meFont);
    
		if (tmp == null) {
		  String name = null;
		  if (meFont.getFace() == javax.microedition.lcdui.Font.FACE_SYSTEM) {
			name = FACE_SYSTEM_NAME;
		  } else if (meFont.getFace() == javax.microedition.lcdui.Font.FACE_MONOSPACE) {
			name = FACE_MONOSPACE_NAME;
		  } else if (meFont.getFace() == javax.microedition.lcdui.Font.FACE_PROPORTIONAL) {
			name = FACE_PROPORTIONAL_NAME;
		  }
		  int style = 0;
		  if ((meFont.getStyle() & javax.microedition.lcdui.Font.STYLE_PLAIN) != 0) {
			style |= SWT.NORMAL;
		  }
		  if ((meFont.getStyle() & javax.microedition.lcdui.Font.STYLE_BOLD) != 0) {
			style |= SWT.BOLD;
		  }
		  if ((meFont.getStyle() & javax.microedition.lcdui.Font.STYLE_ITALIC) != 0) {
			style |= SWT.ITALIC;
		  }
		  int size = 0;
		  if (meFont.getSize() == javax.microedition.lcdui.Font.SIZE_SMALL) {
			size = SIZE_SMALL;
		  } else if (meFont.getSize() == javax.microedition.lcdui.Font.SIZE_MEDIUM) {
			size = SIZE_MEDIUM;
		  } else if (meFont.getSize() == javax.microedition.lcdui.Font.SIZE_LARGE) {
			size = SIZE_LARGE;
		  }
	
		  tmp =	SwtDeviceComponent.getFont(name, size, style);
		  fonts.put(meFont, tmp);
		}
	    
		return tmp;
  }
  

  org.eclipse.swt.graphics.FontMetrics getFontMetrics(javax.microedition.lcdui.Font meFont)
  {
		org.eclipse.swt.graphics.FontMetrics tmp = (org.eclipse.swt.graphics.FontMetrics) fontsMetrics.get(meFont);
    
    if (tmp == null) {
      String name = null;
      if (meFont.getFace() == javax.microedition.lcdui.Font.FACE_SYSTEM) {
        name = FACE_SYSTEM_NAME;
      } else if (meFont.getFace() == javax.microedition.lcdui.Font.FACE_MONOSPACE) {
        name = FACE_MONOSPACE_NAME;
      } else if (meFont.getFace() == javax.microedition.lcdui.Font.FACE_PROPORTIONAL) {
        name = FACE_PROPORTIONAL_NAME;
      }
      int style = 0;
      if ((meFont.getStyle() & javax.microedition.lcdui.Font.STYLE_PLAIN) != 0) {
        style |= java.awt.Font.PLAIN;
      }
      if ((meFont.getStyle() & javax.microedition.lcdui.Font.STYLE_BOLD) != 0) {
        style |= java.awt.Font.BOLD;
      }
      if ((meFont.getStyle() & javax.microedition.lcdui.Font.STYLE_ITALIC) != 0) {
        style |= java.awt.Font.ITALIC;
      }
      int size = 0;
      if (meFont.getSize() == javax.microedition.lcdui.Font.SIZE_SMALL) {
        size = SIZE_SMALL;
      } else if (meFont.getSize() == javax.microedition.lcdui.Font.SIZE_MEDIUM) {
        size = SIZE_MEDIUM;
      } else if (meFont.getSize() == javax.microedition.lcdui.Font.SIZE_LARGE) {
        size = SIZE_LARGE;
      }

      tmp =	SwtDeviceComponent.getFontMetrics(name, size, style);
      fontsMetrics.put(meFont, tmp);
    }
    
    return tmp;
  }
  

	public int charWidth(javax.microedition.lcdui.Font f, char ch)
	{
		return charsWidth(f, new char[] {ch}, 0, 1);
	}


  public int charsWidth(javax.microedition.lcdui.Font f, char[] ch, int offset, int length)
  {
		return SwtDeviceComponent.stringWidth(getFont(f), new String(ch, offset, length));
  }


  public int getBaselinePosition(javax.microedition.lcdui.Font f)
  {
    return getFontMetrics(f).getAscent();
  }


	public int getHeight(javax.microedition.lcdui.Font f)
	{
		return getFontMetrics(f).getHeight();
	}


	public int stringWidth(javax.microedition.lcdui.Font f, String str)
	{
		return SwtDeviceComponent.stringWidth(getFont(f), str);
	}

}
