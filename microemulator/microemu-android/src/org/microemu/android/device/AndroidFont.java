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
