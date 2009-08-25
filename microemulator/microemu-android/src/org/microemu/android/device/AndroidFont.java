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

import org.microemu.device.impl.Font;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetricsInt;

public class AndroidFont implements Font {

	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	FontMetricsInt metrics;
	
	public AndroidFont(Typeface typeface, int size, boolean underlined) {
		paint.setTypeface(typeface);
		paint.setTextSize(size);
		paint.setUnderlineText(underlined);
		metrics = paint.getFontMetricsInt();
	}

	public int charWidth(char ch) {
		return (int) paint.measureText(new char[] { ch }, 0, 1);
	}

	public int charsWidth(char[] ch, int offset, int length) {
		return (int) paint.measureText(ch, offset, length);
	}

	public int getBaselinePosition() {
		return -metrics.ascent;
	}

	public int getHeight() {
		return paint.getFontMetricsInt(metrics);
	}

	public int stringWidth(String str) {
		return (int) paint.measureText(str);
	}

	public int substringWidth(String str, int offset, int len) {
		return (int) paint.measureText(str, offset, offset + len);
	}

}
