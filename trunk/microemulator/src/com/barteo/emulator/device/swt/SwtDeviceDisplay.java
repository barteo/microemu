/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@it.pl>
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

package com.barteo.emulator.device.swt;

import java.awt.Color;
import java.util.Enumeration;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

import com.barteo.emulator.MIDletAccess;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.app.ui.swt.SwtGraphics;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.InputMethod;
import com.barteo.emulator.device.MutableImage;
import com.barteo.emulator.device.impl.DeviceDisplayImpl;
import com.barteo.emulator.device.impl.PositionedImage;
import com.barteo.emulator.device.impl.Rectangle;

public class SwtDeviceDisplay implements DeviceDisplayImpl 
{
	Device device;

	org.eclipse.swt.graphics.Rectangle displayRectangle;
	org.eclipse.swt.graphics.Rectangle displayPaintable;

	boolean isColor;
	int numColors;

	Color backgroundColor;
	Color foregroundColor;

	PositionedImage upImage;
	PositionedImage downImage;

	PositionedImage mode123Image;
	PositionedImage modeAbcUpperImage;
	PositionedImage modeAbcLowerImage;

	boolean scrollUp = false;
	boolean scrollDown = false;


	SwtDeviceDisplay(Device device) 
	{
		this.device = device;
	}


	public MutableImage getDisplayImage()
	{
		return device.getEmulatorContext().getDisplayComponent().getDisplayImage();
	}


	public int getHeight() 
	{
		return displayPaintable.height;
	}

	public int getWidth() 
	{
		return displayPaintable.width;
	}


	public int getFullHeight() 
	{
		return displayRectangle.height;
	}


	public int getFullWidth() 
	{
		return displayRectangle.width;
	}


	public boolean isColor() 
	{
		return isColor;
	}


	public int numColors() 
	{
		return numColors;
	}


	public void paint(SwtGraphics g) 
	{
		Device device = DeviceFactory.getDevice();
		
		g.setBackground(g.getColor(new RGB(
				backgroundColor.getRed(), 
				backgroundColor.getGreen(), 
				backgroundColor.getBlue())));
		g.fillRectangle(0, 0, displayRectangle.width, displayPaintable.y);
		g.fillRectangle(0, displayPaintable.y,
				displayPaintable.x, displayPaintable.height);
		g.fillRectangle(displayPaintable.x + displayPaintable.width, displayPaintable.y,
				displayRectangle.width - displayPaintable.x - displayPaintable.width, displayPaintable.height);
		g.fillRectangle(0, displayPaintable.y + displayPaintable.height,
				displayRectangle.width, displayRectangle.height - displayPaintable.y - displayPaintable.height);

		g.setForeground(g.getColor(new RGB(
				foregroundColor.getRed(), 
				foregroundColor.getGreen(), 
				foregroundColor.getBlue())));
		for (Enumeration s = device.getSoftButtons().elements(); s.hasMoreElements();) {
			((SwtSoftButton) s.nextElement()).paint(g);
		}

		int inputMode = device.getInputMethod().getInputMode();
		if (inputMode == InputMethod.INPUT_123) {
			g.drawImage(((SwtImmutableImage) mode123Image.getImage()).getImage(), 
			        mode123Image.getRectangle().x, mode123Image.getRectangle().y);
		} else if (inputMode == InputMethod.INPUT_ABC_UPPER) {
			g.drawImage(((SwtImmutableImage) modeAbcUpperImage.getImage()).getImage(), 
			        modeAbcUpperImage.getRectangle().x, modeAbcUpperImage.getRectangle().y);
		} else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
			g.drawImage(((SwtImmutableImage) modeAbcLowerImage.getImage()).getImage(), 
			        modeAbcLowerImage.getRectangle().x, modeAbcLowerImage.getRectangle().y);
		}

		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma != null) {
			Displayable current = ma.getDisplayAccess().getCurrent();
			org.eclipse.swt.graphics.Rectangle oldclip = g.getClipping();
			if (!(current instanceof Canvas) 
					|| ((Canvas) current).getWidth() != displayRectangle.width
					|| ((Canvas) current).getHeight() != displayRectangle.height) {
				g.setClipping(displayPaintable);
				g.translate(displayPaintable.x, displayPaintable.y);
			}
			Font f = g.getFont();

			ma.getDisplayAccess().paint(new SwtDisplayGraphics(g, getDisplayImage()));

			g.setFont(f);
			
			if (!(current instanceof Canvas) 
					|| ((Canvas) current).getWidth() != displayRectangle.width
					|| ((Canvas) current).getHeight() != displayRectangle.height) {
				g.translate(-displayPaintable.x, -displayPaintable.y);
				g.setClipping(oldclip);
			}
		}

		if (scrollUp) {
			g.drawImage(((SwtImmutableImage) upImage.getImage()).getImage(), 
			        upImage.getRectangle().x, upImage.getRectangle().y);
		}
		if (scrollDown) {
			g.drawImage(((SwtImmutableImage) downImage.getImage()).getImage(), 
			        downImage.getRectangle().x, downImage.getRectangle().y);
		}
	}


	public void repaint() 
	{
		device.getEmulatorContext().getDisplayComponent().repaint();
	}


	public void setScrollDown(boolean state) 
	{
		scrollDown = state;
	}


	public void setScrollUp(boolean state) 
	{
		scrollUp = state;
	}


	public org.eclipse.swt.graphics.Rectangle getDisplayRectangle() 
	{
		return displayRectangle;
	}


	public Color getBackgroundColor() 
	{
		return backgroundColor;
	}


	public Color getForegroundColor() 
	{
		return foregroundColor;
	}


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setNumColors(int)
     */
    public void setNumColors(int i)
    {
        numColors = i;
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setIsColor(boolean)
     */
    public void setIsColor(boolean b)
    {
        isColor = b;
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setBackgroundColor(java.awt.Color)
     */
    public void setBackgroundColor(Color color)
    {
        backgroundColor = color;
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setForegroundColor(java.awt.Color)
     */
    public void setForegroundColor(Color color)
    {
        foregroundColor = color;
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setDisplayRectangle(java.awt.Rectangle)
     */
    public void setDisplayRectangle(Rectangle rectangle)
    {
        displayRectangle = new org.eclipse.swt.graphics.Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setDisplayPaintable(java.awt.Rectangle)
     */
    public void setDisplayPaintable(Rectangle rectangle)
    {
        displayPaintable = new org.eclipse.swt.graphics.Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setUpImage(com.barteo.emulator.device.impl.PositionedImage)
     */
    public void setUpImage(PositionedImage object)
    {
        upImage = object;
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setDownImage(com.barteo.emulator.device.impl.PositionedImage)
     */
    public void setDownImage(PositionedImage object)
    {
        downImage = object;
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setMode123Image(com.barteo.emulator.device.impl.PositionedImage)
     */
    public void setMode123Image(PositionedImage object)
    {
        mode123Image = object;
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setModeAbcLowerImage(com.barteo.emulator.device.impl.PositionedImage)
     */
    public void setModeAbcLowerImage(PositionedImage object)
    {
        modeAbcLowerImage = object;
    }


    /* (non-Javadoc)
     * @see com.barteo.emulator.device.impl.DeviceDisplayImpl#setModeAbcUpperImage(com.barteo.emulator.device.impl.PositionedImage)
     */
    public void setModeAbcUpperImage(PositionedImage object)
    {
        modeAbcUpperImage = object;
    }

}