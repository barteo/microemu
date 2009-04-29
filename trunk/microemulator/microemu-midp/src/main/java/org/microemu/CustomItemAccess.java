package org.microemu;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;

public interface CustomItemAccess {

	CustomItem getCustomItem();
	
	int getPrefContentHeight(int width);

	int getPrefContentWidth(int height);

	void paint(Graphics g, int w, int h);

}
