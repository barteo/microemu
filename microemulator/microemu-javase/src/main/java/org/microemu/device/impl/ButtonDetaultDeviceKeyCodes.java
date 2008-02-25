/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
package org.microemu.device.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.microedition.lcdui.Canvas;

/**
 * 
 * This class defines default device key codes and game actions for buttons.
 * 
 * Key code is reported to MIDP application by Canvas.keyPressed()
 * 
 * Game action is reported to MIDP application by Canvas.getGameAction()
 * 
 * Use 'device.xml' to redefine codes for your device if required.
 * 
 * @author vlads
 * 
 */
public abstract class ButtonDetaultDeviceKeyCodes {

	private static Map codes = new HashMap();

	private static Map gameActions = new HashMap();

	public static int getKeyCode(ButtonName name) {
		Integer code = (Integer) codes.get(name);
		if (code != null) {
			return code.intValue();
		}
		return 0;
	}

	public static int getGameAction(ButtonName name) {
		Integer code = (Integer) gameActions.get(name);
		if (code != null) {
			return code.intValue();
		}
		return 0;
	}

	public static ButtonName getButtonNameByGameAction(int gameAction) {
		Integer value = new Integer(gameAction);
		if (gameActions.containsValue(value)) {
			for (Iterator iterator = gameActions.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry v = (Map.Entry) iterator.next();
				if (v.getValue().equals(value)) {
					return (ButtonName) v.getKey();
				}
			}
		}
		throw new IllegalArgumentException("Illegal action " + gameAction);
	}

	static {
		code(ButtonName.SOFT1, -6);
		code(ButtonName.SOFT2, -7);
		code(ButtonName.SELECT, -5, Canvas.FIRE);
		code(ButtonName.UP, -1, Canvas.UP);
		code(ButtonName.DOWN, -2, Canvas.DOWN);
		code(ButtonName.LEFT, -3, Canvas.LEFT);
		code(ButtonName.RIGHT, -4, Canvas.RIGHT);

		code(ButtonName.KEY_NUM0, Canvas.KEY_NUM0);
		code(ButtonName.KEY_NUM1, Canvas.KEY_NUM1, Canvas.GAME_A);
		code(ButtonName.KEY_NUM2, Canvas.KEY_NUM2);
		code(ButtonName.KEY_NUM3, Canvas.KEY_NUM3, Canvas.GAME_B);
		code(ButtonName.KEY_NUM4, Canvas.KEY_NUM4);
		code(ButtonName.KEY_NUM5, Canvas.KEY_NUM5);
		code(ButtonName.KEY_NUM6, Canvas.KEY_NUM6);
		code(ButtonName.KEY_NUM7, Canvas.KEY_NUM7, Canvas.GAME_C);
		code(ButtonName.KEY_NUM8, Canvas.KEY_NUM8);
		code(ButtonName.KEY_NUM9, Canvas.KEY_NUM9, Canvas.GAME_D);
		code(ButtonName.KEY_STAR, Canvas.KEY_STAR);
		code(ButtonName.KEY_POUND, Canvas.KEY_POUND);
	}

	private static void code(ButtonName name, int code) {
		codes.put(name, new Integer(code));
	}

	private static void code(ButtonName name, int code, int gameAction) {
		code(name, code);
		gameActions.put(name, new Integer(gameAction));
	}
}
