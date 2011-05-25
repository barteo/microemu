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

import java.util.HashMap;

import javax.microedition.lcdui.Font;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.FontManager;

import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class AndroidFontManager implements FontManager
{
	private static HashMap<Font, AndroidFont> fonts = new HashMap<Font, AndroidFont>();

    private static DisplayMetrics metrics;
    
    public AndroidFontManager(DisplayMetrics metrics) {
        AndroidFontManager.metrics = metrics;
    }

	static AndroidFont getFont(Font meFont)
	{
    	AndroidFont result = fonts.get(meFont);
	    
	    if (result == null) {
	    	Typeface family = Typeface.SANS_SERIF;
	    	if (meFont.getFace() == Font.FACE_SYSTEM) {
	    		family = Typeface.SANS_SERIF;
	    	} else if (meFont.getFace() == Font.FACE_MONOSPACE) {
	    		family = Typeface.MONOSPACE;
	    	} else if (meFont.getFace() == Font.FACE_PROPORTIONAL) {
	    		family = Typeface.SANS_SERIF;
	    	}
	    	int style = 0;
	    	if ((meFont.getStyle() & Font.STYLE_PLAIN) != 0) {
	    		style |= Typeface.NORMAL;
	    	}
	    	if ((meFont.getStyle() & Font.STYLE_BOLD) != 0) {
	    		style |= Typeface.BOLD;
	    	}
	    	if ((meFont.getStyle() & Font.STYLE_ITALIC) != 0) {
	    		style |= Typeface.ITALIC;
	    	}
	    	boolean underlined = false;
	    	if ((meFont.getStyle() & Font.STYLE_UNDERLINED) != 0) {
	    		underlined = true;
	    	}
	    	int size = 0;
	    	if (meFont.getSize() == Font.SIZE_SMALL) {
	    		size = MicroEmulatorActivity.config.FONT_SIZE_SMALL;
	    	} else if (meFont.getSize() == Font.SIZE_MEDIUM) {
	    		size = MicroEmulatorActivity.config.FONT_SIZE_MEDIUM;
	    	} else if (meFont.getSize() == Font.SIZE_LARGE) {
	    		size = MicroEmulatorActivity.config.FONT_SIZE_LARGE;
	    	}
	    	result = new AndroidFont(Typeface.create(family, style), TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP,
                            size,
                            metrics), underlined);
	    	fonts.put(meFont, result);
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

	public int substringWidth(Font f, String str, int offset, int len)
	{
		return getFont(f).substringWidth(str, offset, len);
	}

}
