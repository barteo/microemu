/*
 * @(#)FontManager.java  07/07/2001
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

package com.barteo.midp.lcdui;

import java.awt.FontMetrics;

import javax.microedition.lcdui.Font;


public class FontManager
{

  static FontManager instance = new FontManager();

	java.awt.FontMetrics defaultFontMetrics;


  public static FontManager getInstance()
  {
    return instance;
  }


	public int charWidth(javax.microedition.lcdui.Font f, char ch)
	{
		return defaultFontMetrics.charWidth(ch);
	}


  public int charsWidth(javax.microedition.lcdui.Font f, char[] ch, int offset, int length)
  {
    return defaultFontMetrics.charsWidth(ch, offset, length);
  }


  public int getBaselinePosition(javax.microedition.lcdui.Font f)
  {
    return defaultFontMetrics.getAscent();
  }


	public int getHeight(javax.microedition.lcdui.Font f)
	{
		return defaultFontMetrics.getHeight();
	}


	public void setDefaultFontMetrics(FontMetrics fm)
	{
		defaultFontMetrics = fm;
	}


	public int stringWidth(javax.microedition.lcdui.Font f, String str)
	{
		return defaultFontMetrics.stringWidth(str);
	}

}
