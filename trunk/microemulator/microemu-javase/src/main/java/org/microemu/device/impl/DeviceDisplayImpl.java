/*
 *  MicroEmulator
 *  Copyright (C) 2005 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package org.microemu.device.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import org.microemu.device.DeviceDisplay;

public interface DeviceDisplayImpl extends DeviceDisplay {

	Image createSystemImage(URL url) throws IOException;

	/**
	 * @param name
	 * @param shape
	 * @param keyCode -
	 *            Integer.MIN_VALUE when unspecified
	 * @param keyboardKeys
	 * @param keyboardChars
	 * @param chars
	 * @param modeChange
	 * @return
	 */
	Button createButton(int skinVersion, String name, Shape shape, int keyCode, String keyboardKeys,
			String keyboardChars, Hashtable inputToChars, boolean modeChange);

	/**
	 * @param name
	 * @param rectangle
	 * @param keyCode -
	 *            Integer.MIN_VALUE when unspecified
	 * @param keyName
	 * @param paintable
	 * @param alignmentName
	 * @param commands
	 * @param font
	 * @return
	 */
	SoftButton createSoftButton(int skinVersion, String name, Shape shape, int keyCode, String keyName,
			Rectangle paintable, String alignmentName, Vector commands, Font font);

	SoftButton createSoftButton(int skinVersion, String name, Rectangle paintable, Image normalImage, Image pressedImage);

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

	boolean isResizable();

	void setResizable(boolean state);

}
