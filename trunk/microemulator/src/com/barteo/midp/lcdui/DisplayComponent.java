/*
 * @(#)DisplayComponent.java  07/07/2001
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

import java.awt.*;
import java.io.IOException;
import java.util.Enumeration;

import com.barteo.emulator.Device;
import com.barteo.emulator.Resource;
import com.barteo.emulator.SoftButton;


public class DisplayComponent extends Canvas
{

	static int screenPaintableRegionX = 0;
	static int screenPaintableRegionY = 10;
	static int screenPaintableRegionWidth = 96;
	static int screenPaintableRegionHeight = 100;

	Container parent;
	
	Image up_arrow, down_arrow;
	Image input_123, input_abc_upper, input_abc_lower;
	boolean scrollUp = false;
	boolean scrollDown = false;
	

	public DisplayComponent(Container a_parent)
	{
		parent = a_parent;
		DisplayBridge.setComponent(this);
		setBackground(Device.getBackgroundColor());
		
		try {
			up_arrow = Resource.getInstance().getImage("/com/barteo/emulator/resources/up.png");
			down_arrow = Resource.getInstance().getImage("/com/barteo/emulator/resources/down.png");
			input_123 = Resource.getInstance().getImage("/com/barteo/emulator/resources/123.png");
			input_abc_upper = Resource.getInstance().getImage("/com/barteo/emulator/resources/abc_upper.png");
			input_abc_lower = Resource.getInstance().getImage("/com/barteo/emulator/resources/abc_lower.png");
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}


	public void paint(Graphics g)
	{
		Graphics graphics = getGraphics();
		
		for (Enumeration s = Device.getSoftButtons().elements();  s.hasMoreElements() ;) {
			((SoftButton) s.nextElement()).paint(graphics);
		}
		
		graphics.setColor(Device.getBackgroundColor());
		graphics.fillRect(0, 0, input_abc_upper.getWidth(this), input_abc_upper.getHeight(this));
		graphics.setColor(Device.getForegroundColor());
		int inputMode = InputMethod.getInputMethod().getInputMode();
		if (inputMode == InputMethod.INPUT_123) {
			graphics.drawImage(input_123, 0, 0, this);
		} else if (inputMode == InputMethod.INPUT_ABC_UPPER) {
			graphics.drawImage(input_abc_upper, 0, 0, this);
		} else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
			graphics.drawImage(input_abc_lower, 0, 0, this);
		}

		Shape oldclip = graphics.getClip();
		graphics.setClip(screenPaintableRegionX, screenPaintableRegionY, screenPaintableRegionWidth, screenPaintableRegionHeight);
		graphics.translate(screenPaintableRegionX, screenPaintableRegionY);
		DisplayBridge.paint(graphics);		
		graphics.translate(-screenPaintableRegionX, -screenPaintableRegionY);
		graphics.setClip(oldclip);

		graphics.setColor(Device.getBackgroundColor());
		graphics.fillRect(40, 120, up_arrow.getWidth(this), up_arrow.getHeight(this));
		graphics.fillRect(48, 120, down_arrow.getWidth(this), down_arrow.getHeight(this));
		if (scrollUp) {
			graphics.setColor(Device.getForegroundColor());
			graphics.drawImage(up_arrow, 40, 120, this);
		}
		if (scrollDown) {		
			graphics.setColor(Device.getForegroundColor());
			graphics.drawImage(down_arrow, 48, 120, this);
		}
	}
	
	
	public void repaint()
	{
		parent.repaint();
	}
	
	
	public void setScrollDown(boolean state)
	{
		scrollDown = state;
	}


	public void setScrollUp(boolean state)
	{
		scrollUp = state;
	}

}
