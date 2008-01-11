/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
 *
 *  @version $Id$
 */

package org.microemu.android.device;

import org.microemu.device.impl.Font;

import android.graphics.Paint;

public class AndroidFont implements Font {

	static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public AndroidFont(String name, String style, int size) {
		// TODO Auto-generated constructor stub
	}

	public int charWidth(char ch) {
		return (int) paint.measureText(new char[] { ch }, 0, 1);
	}

	public int charsWidth(char[] ch, int offset, int length) {
		return (int) paint.measureText(ch, offset, length);
	}

	public int getBaselinePosition() {
		return -paint.getFontMetricsInt().ascent;
	}

	public int getHeight() {
		return paint.getFontMetricsInt(paint.getFontMetricsInt());
	}

	public int stringWidth(String str) {
		return (int) paint.measureText(str);
	}

}
