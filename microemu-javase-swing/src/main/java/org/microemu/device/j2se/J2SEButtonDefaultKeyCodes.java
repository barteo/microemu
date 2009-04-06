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
package org.microemu.device.j2se;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import org.microemu.device.impl.ButtonName;

/**
 * This class defines default computer keyboard key and char codes for buttons.
 * 
 * Use 'device.xml' to redefine codes for your device if required.
 * 
 * CharCodes do not depends on InputMode. This is computer keyboard codes when
 * it is impossible to map to VK keys.
 * 
 * @author vlads
 * 
 */
public class J2SEButtonDefaultKeyCodes {

	private static Map codes = new HashMap();

	private static Map backwardCompatibleNames = new HashMap();

	private static class KeyInformation {

		int[] keyCodes;

		String charCodes = "";

	}

	public static int[] getKeyCodes(ButtonName name) {
		KeyInformation info = (KeyInformation) codes.get(name);
		if (info == null) {
			return new int[0];
		}
		return info.keyCodes;
	}

	public static String getCharCodes(ButtonName name) {
		KeyInformation info = (KeyInformation) codes.get(name);
		if (info == null) {
			return "";
		}
		return info.charCodes;
	}

	static {
		code(ButtonName.SOFT1, KeyEvent.VK_F1);
		code(ButtonName.SOFT2, KeyEvent.VK_F2);
		code(ButtonName.SELECT, KeyEvent.VK_ENTER);
		code(ButtonName.UP, KeyEvent.VK_UP, KeyEvent.VK_KP_UP);
		code(ButtonName.DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_KP_DOWN);
		code(ButtonName.LEFT, KeyEvent.VK_LEFT, KeyEvent.VK_KP_LEFT);
		code(ButtonName.RIGHT, KeyEvent.VK_RIGHT, KeyEvent.VK_KP_RIGHT);

		// code(ButtonName.BACK, KeyEvent.VK_HOME);
		code(ButtonName.BACK_SPACE, KeyEvent.VK_BACK_SPACE);
		code(ButtonName.DELETE, KeyEvent.VK_CLEAR, KeyEvent.VK_DELETE);

		code(ButtonName.KEY_NUM0, KeyEvent.VK_0, KeyEvent.VK_NUMPAD0).charCodes = "0";
		code(ButtonName.KEY_NUM1, KeyEvent.VK_1, KeyEvent.VK_NUMPAD1).charCodes = "1";
		code(ButtonName.KEY_NUM2, KeyEvent.VK_2, KeyEvent.VK_NUMPAD2).charCodes = "2";
		code(ButtonName.KEY_NUM3, KeyEvent.VK_3, KeyEvent.VK_NUMPAD3).charCodes = "3";
		code(ButtonName.KEY_NUM4, KeyEvent.VK_4, KeyEvent.VK_NUMPAD4).charCodes = "4";
		code(ButtonName.KEY_NUM5, KeyEvent.VK_5, KeyEvent.VK_NUMPAD5).charCodes = "5";
		code(ButtonName.KEY_NUM6, KeyEvent.VK_6, KeyEvent.VK_NUMPAD6).charCodes = "6";
		code(ButtonName.KEY_NUM7, KeyEvent.VK_7, KeyEvent.VK_NUMPAD7).charCodes = "7";
		code(ButtonName.KEY_NUM8, KeyEvent.VK_8, KeyEvent.VK_NUMPAD8).charCodes = "8";
		code(ButtonName.KEY_NUM9, KeyEvent.VK_9, KeyEvent.VK_NUMPAD9).charCodes = "9";
		code(ButtonName.KEY_STAR, KeyEvent.VK_MULTIPLY, KeyEvent.VK_ASTERISK).charCodes = "*";
		code(ButtonName.KEY_POUND, KeyEvent.VK_MODECHANGE, KeyEvent.VK_SUBTRACT).charCodes = "#";
	}

	private static KeyInformation code(ButtonName name, int code) {
		KeyInformation info = new KeyInformation();
		info.keyCodes = new int[] { code };
		codes.put(name, info);
		backwardCompatibleNames.put(new Integer(code), name);
		return info;
	}

	private static KeyInformation code(ButtonName name, int code1, int code2) {
		KeyInformation info = new KeyInformation();
		info.keyCodes = new int[] { code1, code2 };
		codes.put(name, info);
		backwardCompatibleNames.put(new Integer(code1), name);
		return info;
	}

	public static ButtonName getBackwardCompatibleName(int keyboardKey) {
		return (ButtonName) backwardCompatibleNames.get(new Integer(keyboardKey));
	}
}
