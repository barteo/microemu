/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id$
 */

package org.microemu.android.device;

import java.util.Hashtable;

import javax.microedition.android.lcdui.Font;

import org.microemu.device.FontManager;

public class AndroidFontManager implements FontManager
{
	private static String FACE_SYSTEM_NAME = "SansSerif";
	private static String FACE_MONOSPACE_NAME = "Monospaced";
	private static String FACE_PROPORTIONAL_NAME = "SansSerif";

	private static int SIZE_SMALL = 9;
	private static int SIZE_MEDIUM = 11;
	private static int SIZE_LARGE = 13;

	private Hashtable fonts = new Hashtable();


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
	    	result = new AndroidFont(name, style, size);
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
	
}
