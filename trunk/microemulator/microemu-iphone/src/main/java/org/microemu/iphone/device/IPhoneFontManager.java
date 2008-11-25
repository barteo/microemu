package org.microemu.iphone.device;

import javax.microedition.lcdui.Font;

import obc.NSString;
import obc.UIFont;

import org.microemu.device.FontManager;

public class IPhoneFontManager implements FontManager {

	public UIFont getUIFont(Font font){
		return (UIFont)UIFont.$systemFontOfSize$(9.f);
	}
	
	public int charWidth(Font f, char ch) {
		return stringWidth(f, String.valueOf(ch));
	}

	public int charsWidth(Font f, char[] ch, int offset, int length) {
		return stringWidth(f, String.valueOf(ch, offset, length));
	}

	public int getBaselinePosition(Font f) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHeight(Font f) {
		return (int)getUIFont(f).capHeight();
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public int stringWidth(Font f, String str) {
		return (int)new NSString().initWithString$(str).sizeWithFont$(getUIFont(f)).width;
	}

}
