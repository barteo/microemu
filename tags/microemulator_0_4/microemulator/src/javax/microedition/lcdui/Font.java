/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
 *  Copyright (C) 2002 3GLab http://www.3glab.com
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

import com.barteo.emulator.device.DeviceFactory;


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
    checkFace(face);
    checkStyle(style);
    checkSize(size);

    this.face = face;
		this.style = style;
		this.size = size;
	}


	public static Font getFont(int face, int style, int size)
      throws IllegalArgumentException
	{
		return new Font(face, style, size);
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
    return DeviceFactory.getDevice().getFontManager().getHeight(this);
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
    return DeviceFactory.getDevice().getFontManager().charWidth(this, ch);
  }


  public int charsWidth(char[] ch, int offset, int length)
  {
    return DeviceFactory.getDevice().getFontManager().charsWidth(this, ch, offset, length);
  }


  public int getBaselinePosition()
  {
    return DeviceFactory.getDevice().getFontManager().getBaselinePosition(this);
  }


  public boolean isBold()
  {
    if ((style & STYLE_BOLD) == STYLE_BOLD) {
      return true;
    } else {
      return false;
    }
  }


  public boolean isItalic()
  {
    if ((style & STYLE_ITALIC) == STYLE_ITALIC) {
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
    if ((style & STYLE_UNDERLINED) == STYLE_UNDERLINED) {
      return true;
    } else {
      return false;
    }
  }


	public int stringWidth(String str)
	{
		return DeviceFactory.getDevice().getFontManager().stringWidth(this, str);
	}


	public int substringWidth(String str, int offset, int len)
	{
		return stringWidth(str.substring(offset, offset + len));
	}
	
	
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof Font)) {
			return false;
		}
		if (((Font) obj).face != face) {
			return false;
		}
		if (((Font) obj).style != style) {
			return false;
		}
		if (((Font) obj).size != size) {
			return false;
		}
		
		return true;
	}


	public int hashCode()
	{
		return face | style | size;
	}


  private void checkFace(int face)
      throws IllegalArgumentException 
  {
    if ((face != FACE_SYSTEM) &&
        (face != FACE_MONOSPACE) &&
        (face != FACE_PROPORTIONAL) ) {
      throw new IllegalArgumentException("Font face is not a known type");
    }
  }

  
  private void checkStyle(int style)
      throws IllegalArgumentException 
  {
    if (style == STYLE_PLAIN) {
      return;
    }

    int allstyles = STYLE_ITALIC|STYLE_BOLD|STYLE_UNDERLINED;
    if ((style < 0) || (style > allstyles) || ((style&allstyles) == 0)) {
        throw new IllegalArgumentException("Font style is not a known type");
    }
  }

  
  private void checkSize(int size)
      throws IllegalArgumentException 
  {
    if ((size != SIZE_SMALL) &&
        (size != SIZE_MEDIUM) &&
        (size != SIZE_LARGE) ) {
      throw new IllegalArgumentException("Font size is not a known type");
    }
  }
  
}
