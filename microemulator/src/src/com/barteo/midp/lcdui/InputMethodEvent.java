/*
 * @(#)InputMethodEvent.java  07/07/2001
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


public class InputMethodEvent
{

	public static final int CARET_POSITION_CHANGED = 1;
	public static final int INPUT_METHOD_TEXT_CHANGED = 2;
	
	int type;
	int caret;
	String text;


	public InputMethodEvent(int type, int caret, String text)
	{
		this.type = type;
		this.caret = caret;
		this.text = text;
	}


	public int getCaret()
	{
		return caret;
	}


	public String getText()
	{
		return text;
	}

}
