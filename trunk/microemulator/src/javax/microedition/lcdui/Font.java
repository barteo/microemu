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
 
package javax.microedition.lcdui;

import com.barteo.midp.lcdui.FontManager;


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
		this.face = face;
		this.style = style;
		this.size = size;
	}


	public static Font getFont(int face, int style, int size)
	{
		return DEFAULT_FONT;
	}


	public static Font getDefaultFont()
	{
		return DEFAULT_FONT;
	}


  public int getFace()
  {
    return face;
  }


  public int getHeight()
  {
    return FontManager.getInstance().getHeight(this);
  }


  public int getSize()
  {
    return size;
  }


  public int getStyle()
  {
    return style;
  }


  public int charWidth(char ch)
  {
    return FontManager.getInstance().charWidth(this, ch);
  }


  public int charsWidth(char[] ch, int offset, int length)
  {
    return FontManager.getInstance().charsWidth(this, ch, offset, length);
  }


  public int getBaselinePosition()
  {
    return FontManager.getInstance().getBaselinePosition(this);
  }


  public boolean isBold()
  {
    if (style == STYLE_BOLD) {
      return true;
    } else {
      return false;
    }
  }


  public boolean isItalic()
  {
    if (style == STYLE_ITALIC) {
      return true;
    } else {
      return false;
    }
  }


  public boolean isPlain()
  {
    if (style == STYLE_PLAIN) {
      return true;
    } else {
      return false;
    }
  }


  public boolean isUnderlined()
  {
    if (style == STYLE_UNDERLINED) {
      return true;
    } else {
      return false;
    }
  }


	public int stringWidth(String str)
	{
		return FontManager.getInstance().stringWidth(this, str);
	}


	public int substringWidth(String str, int offset, int len)
	{
		return stringWidth(str.substring(offset, offset + len));
	}

}
