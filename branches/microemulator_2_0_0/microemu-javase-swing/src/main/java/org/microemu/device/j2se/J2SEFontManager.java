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
 
package org.microemu.device.j2se;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Font;

import org.microemu.device.impl.FontManagerImpl;

public class J2SEFontManager implements FontManagerImpl
{
	private static String FACE_SYSTEM_NAME = "SansSerif";
	private static String FACE_MONOSPACE_NAME = "Monospaced";
	private static String FACE_PROPORTIONAL_NAME = "SansSerif";

	private static int SIZE_SMALL = 9;
	private static int SIZE_MEDIUM = 11;
	private static int SIZE_LARGE = 13;

	private Hashtable fonts = new Hashtable();

	private boolean antialiasing;
  

	org.microemu.device.impl.Font getFont(Font meFont)
	{
    	int key = 0;
    	key |= meFont.getFace();
    	key |= meFont.getStyle();
    	key |= meFont.getSize();
    	
    	org.microemu.device.impl.Font result = (org.microemu.device.impl.Font) fonts.get(new Integer(key));
	    
	    if (result == null) {
	    	String name = null;
	    	if (meFont.getFace() == Font.FACE_SYSTEM) {
	    		name = FACE_SYSTEM_NAME;
	    	} else if (meFont.getFace() == Font.FACE_MONOSPACE) {
	    		name = FACE_MONOSPACE_NAME;
	    	} else if (meFont.getFace() == Font.FACE_PROPORTIONAL) {
	    		name = FACE_PROPORTIONAL_NAME;
	    	}
	    	String style = ",";
	    	if ((meFont.getStyle() & Font.STYLE_PLAIN) != 0) {
	    		style += "plain,";
	    	}
	    	if ((meFont.getStyle() & Font.STYLE_BOLD) != 0) {
	    		style += "bold,";
	    	}
	    	if ((meFont.getStyle() & Font.STYLE_ITALIC) != 0) {
	    		style += "italic,";
	    	}
	    	if ((meFont.getStyle() & Font.STYLE_ITALIC) != 0) {
	    		style += "underlined,";
	    	}
	    	style = style.substring(0, style.length() - 1);
	    	int size = 0;
	    	if (meFont.getSize() == Font.SIZE_SMALL) {
	    		size = SIZE_SMALL;
	    	} else if (meFont.getSize() == Font.SIZE_MEDIUM) {
	    		size = SIZE_MEDIUM;
	    	} else if (meFont.getSize() == Font.SIZE_LARGE) {
	    		size = SIZE_LARGE;
	    	}
	    	result = new J2SESystemFont(name, style, size, antialiasing);
	    	fonts.put(new Integer(key), result);
	    }
	    
	    return result;
	}
	
	
	public void init()
	{
		fonts.clear();
	}
  

	public int charWidth(Font f, char ch)
	{
		return getFont(f).charWidth(ch);
	}


	public int charsWidth(Font f, char[] ch, int offset, int length) 
	{
		return getFont(f).charsWidth(ch, offset, length);
	}

	public int getBaselinePosition(Font f) 
	{
		return getFont(f).getBaselinePosition();
	}


	public int getHeight(Font f)
	{
		return getFont(f).getHeight();
	}


	public int stringWidth(Font f, String str)
	{
		return getFont(f).stringWidth(str);
	}
	
	
	public boolean getAntialiasing() {
		return antialiasing;
	}


	public void setAntialiasing(boolean antialiasing) 
	{
		this.antialiasing = antialiasing;
		
		Enumeration en = fonts.elements();
		while (en.hasMoreElements()) {
			J2SEFont font = (J2SEFont) en.nextElement();
			font.setAntialiasing(antialiasing);
		}
	}


	public void setFont(String face, String style, String size, org.microemu.device.impl.Font font) {
		int key = 0;
		
		if (face.equalsIgnoreCase("system")) {
			key |= Font.FACE_SYSTEM;
		} else if (face.equalsIgnoreCase("monospace")) {
			key |= Font.FACE_MONOSPACE;
		} else if (face.equalsIgnoreCase("proportional")) {
			key |= Font.FACE_PROPORTIONAL;
		}
		
		String testStyle = style.toLowerCase();
		if (testStyle.indexOf("plain") != -1) {
			key |= Font.STYLE_PLAIN;
		} 
		if (testStyle.indexOf("bold") != -1) {
			key |= Font.STYLE_BOLD;
		} 
		if (testStyle.indexOf("italic") != -1) {
			key |= Font.STYLE_ITALIC;
		} 
		if (testStyle.indexOf("underlined") != -1) {
			key |= Font.STYLE_UNDERLINED;
		}
		
		if (size.equalsIgnoreCase("small")) {
			key |= Font.SIZE_SMALL;
		} else if (size.equalsIgnoreCase("medium")) {
			key |= Font.SIZE_MEDIUM;
		} else if (size.equalsIgnoreCase("large")) {
			key |= Font.SIZE_LARGE;
		}
		
		fonts.put(new Integer(key), font);
	}


	public org.microemu.device.impl.Font createSystemFont(String defName, String defStyle, int defSize, boolean antialiasing) {
		return new J2SESystemFont(defName, defStyle, defSize, antialiasing);
	}

	public org.microemu.device.impl.Font createTrueTypeFont(URL defUrl, String defStyle, int defSize, boolean antialiasing) {
		return new J2SETrueTypeFont(defUrl, defStyle, defSize, antialiasing);
	}

}
