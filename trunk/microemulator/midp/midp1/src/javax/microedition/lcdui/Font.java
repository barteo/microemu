/*
 *  MicroEmulator
 *  Copyright (C) 2001-2005 Bartek Teodorczyk <barteo@barteo.net>
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

package javax.microedition.lcdui;

import org.microemu.device.DeviceFactory;


public final class Font 
{
	public static final int STYLE_PLAIN = 0;
	public static final int STYLE_BOLD = 1;
	public static final int STYLE_ITALIC = 2;
	public static final int STYLE_UNDERLINED = 4;
	
	public static final int SIZE_SMALL = 8;
	public static final int SIZE_MEDIUM = 0;
	public static final int SIZE_LARGE = 16;
	
	public static final int FACE_SYSTEM = 0;
	public static final int FACE_MONOSPACE = 32;
	public static final int FACE_PROPORTIONAL = 64;
	
	private static final Font DEFAULT_FONT = new Font(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	
	private int face;
	private int style;
	private int size;

	
	private Font(int face, int style, int size)
	{
		if ((face != FACE_SYSTEM) && (face != FACE_MONOSPACE) && (face != FACE_PROPORTIONAL)) {
			throw new IllegalArgumentException();
		}
		if ((style != STYLE_PLAIN) && (style != STYLE_BOLD) && (style != STYLE_ITALIC) && (style != STYLE_UNDERLINED)) {
			throw new IllegalArgumentException();
		}
		if ((size != SIZE_SMALL) && (size != SIZE_MEDIUM) && (size != SIZE_LARGE)) {
			throw new IllegalArgumentException();
		}
		
		this.face = face;
		this.style = style;
		this.size = size;
	}


	public static Font getDefaultFont()
	{
		return DEFAULT_FONT;
	}
	
	
	public static Font getFont(int face, int style, int size)
	{
		return new Font(face, style, size);
	}

	
	public int getStyle()
	{
		return style;
	}


	public int getSize()
	{
		return size;
	}

	
	public int getFace()
	{
		return face;
	}


	public boolean isPlain()
	{
		if (style == STYLE_PLAIN) {
			return true;
		} else {
			return false;
		}
	}
	

	public boolean isBold()
	{
		if ((style & STYLE_BOLD) != 0) {
			return true;
		} else {
			return false;
		}
	}


	public boolean isItalic()
	{
		if ((style & STYLE_ITALIC) != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean isUnderlined()
	{
		if ((style & STYLE_UNDERLINED) != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public int getHeight()
	{
		return DeviceFactory.getDevice().getFontManager().getHeight(this);
	}

	
	public int getBaselinePosition()
	{
		return DeviceFactory.getDevice().getFontManager().getBaselinePosition(this);
	}

	
	public int charWidth(char ch)
	{
		return DeviceFactory.getDevice().getFontManager().charWidth(this, ch);
	}

	
	public int charsWidth(char[] ch, int offset, int length)
	{
		return DeviceFactory.getDevice().getFontManager().charsWidth(this, ch, offset, length);
	}


	public int stringWidth(String str)
	{
		return DeviceFactory.getDevice().getFontManager().stringWidth(this, str);
	}

	
	public int substringWidth(String str, int offset, int len)
	{
		return stringWidth(str.substring(offset, offset + len));
	}	
	
}
