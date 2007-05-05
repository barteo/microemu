/*
 *  MicroEmulator
 *  Copyright (C) 2005 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import org.microemu.device.DeviceDisplay;


public interface DeviceDisplayImpl extends DeviceDisplay
{
	
	Image createSystemImage(URL url) throws IOException;  

	/**
	 * @param name
	 * @param shape
	 * @param keyCode - Integer.MIN_VALUE when unspecified
	 * @param keyName
	 * @param chars
	 * @return
	 */
	Button createButton(
	        String name, Shape shape, int keyCode, String keyName, Hashtable inputToChars);
	
	/**
	 * @param name
	 * @param rectangle
	 * @param keyCode - Integer.MIN_VALUE when unspecified
	 * @param keyName
	 * @param paintable
	 * @param alignmentName
	 * @param commands
	 * @param font
	 * @return
	 */
	SoftButton createSoftButton(
	        String name, Shape shape, int keyCode, String keyName, Rectangle paintable, String alignmentName, Vector commands, Font font);

	SoftButton createSoftButton(
			String name, Rectangle paintable, Image normalImage, Image pressedImage);

    /**
     * @param i
     */
    void setNumColors(int i);

    /**
     * @param b
     */
    void setIsColor(boolean b);
    
    
    void setNumAlphaLevels(int i); 

    /**
     * @param color
     */
    void setBackgroundColor(Color color);

    /**
     * @param color
     */
    void setForegroundColor(Color color);

    /**
     * @param rectangle
     */
    void setDisplayRectangle(Rectangle rectangle);

    /**
     * @param rectangle
     */
    void setDisplayPaintable(Rectangle rectangle);

    /**
     * @param object
     */
    void setMode123Image(PositionedImage object);

    /**
     * @param object
     */
    void setModeAbcLowerImage(PositionedImage object);

    /**
     * @param object
     */
    void setModeAbcUpperImage(PositionedImage object);

}
