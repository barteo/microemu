/*
 * @(#)Screen.java  07/07/2001
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

package javax.microedition.lcdui;

import com.barteo.emulator.device.Device;
import com.barteo.midp.lcdui.DisplayBridge;


public abstract class Screen extends Displayable
{

	StringComponent title;
  Ticker ticker;
	int viewPortY;
	int viewPortHeight;


	Screen(String title)
	{
		this.title = new StringComponent(title);
		viewPortY = 0;
		viewPortHeight = Device.screenPaintableHeight - this.title.getHeight() - 1;
	}


  public Ticker getTicker()
  {
    return ticker;
  }


  public String getTitle()
  {
    return title.getText();
  }


  public void setTitle(String s)
  {
    title.setText(s);
  }


  public void setTicker(Ticker ticker)
  {
    this.ticker = ticker;
  }


	abstract int traverse(int gameKeyCode, int top, int bottom);


  void keyPressed(int keyCode)
	{
    int gameKeyCode = Display.getGameAction(keyCode);

    if (gameKeyCode == 1 || gameKeyCode == 6) {
			viewPortY += traverse(gameKeyCode, viewPortY, viewPortY + viewPortHeight);
			repaint();
    }
	}


	void hideNotify()
	{
		super.hideNotify();
	}


	void keyRepeated(int keyCode)
	{
    keyPressed(keyCode);
	}


	final void paint(Graphics g)
	{
    int contentHeight = 0;
		int translatedY;

		if (viewPortY == 0) {
			currentDisplay.dispBridge.setScrollUp(false);
		} else {
			currentDisplay.dispBridge.setScrollUp(true);
		}

		g.setGrayScale(255);
		g.fillRect(0, 0, Device.screenPaintableWidth, Device.screenPaintableHeight);

		g.setGrayScale(0);

    if (ticker != null) {
      contentHeight += 20; // ticker height
    }

    g.translate(0, contentHeight);
		translatedY = contentHeight;

		contentHeight += title.paint(g);

    g.drawLine(0, contentHeight, Device.screenPaintableWidth, contentHeight);
		contentHeight += 1;

    g.translate(0, contentHeight - translatedY);
		translatedY = contentHeight;

		g.clipRect(0, 0, Device.screenPaintableWidth, Device.screenPaintableHeight - contentHeight);
    g.translate(0, -viewPortY);
    contentHeight += paintContent(g);
    g.translate(0, viewPortY);

		if (contentHeight - viewPortY > Device.screenPaintableHeight) {
			currentDisplay.dispBridge.setScrollDown(true);
		} else {
			currentDisplay.dispBridge.setScrollDown(false);
		}
		g.translate(0, -translatedY);
	}


  abstract int paintContent(Graphics g);


  void repaint()
  {
    super.repaint();
  }


	void showNotify()
	{
		super.showNotify();
	}

}
