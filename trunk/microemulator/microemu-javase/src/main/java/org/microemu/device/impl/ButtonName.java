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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * This class defines Buttons available on Device
 * 
 * Implementation is Java 5 enum in java 1.4
 * 
 * @author vlads
 * 
 */
public final class ButtonName {

	private static Map altNames = new HashMap();

	public static final ButtonName SOFT1 = new ButtonName();

	public static final ButtonName SOFT2 = new ButtonName();

	public static final ButtonName SOFT3 = new ButtonName();

	public static final ButtonName SELECT = new ButtonName("SEL");

	public static final ButtonName UP = new ButtonName("U");

	public static final ButtonName DOWN = new ButtonName("D");

	public static final ButtonName LEFT = new ButtonName("L");

	public static final ButtonName RIGHT = new ButtonName("R");

	// public static final ButtonName BACK = new ButtonName();

	public static final ButtonName BACK_SPACE = new ButtonName();

	public static final ButtonName DELETE = new ButtonName();

	// public static final ButtonName CLEAR = DELETE;

	public static final ButtonName KEY_NUM0 = new ButtonName("0");

	public static final ButtonName KEY_NUM1 = new ButtonName("1");

	public static final ButtonName KEY_NUM2 = new ButtonName("2");

	public static final ButtonName KEY_NUM3 = new ButtonName("3");

	public static final ButtonName KEY_NUM4 = new ButtonName("4");

	public static final ButtonName KEY_NUM5 = new ButtonName("5");

	public static final ButtonName KEY_NUM6 = new ButtonName("6");

	public static final ButtonName KEY_NUM7 = new ButtonName("7");

	public static final ButtonName KEY_NUM8 = new ButtonName("8");

	public static final ButtonName KEY_NUM9 = new ButtonName("9");

	public static final ButtonName KEY_STAR = new ButtonName(new String[] { "*", "STAR", "ASTERISK" });

	public static final ButtonName KEY_POUND = new ButtonName(new String[] { "#", "POUND" });

	private String name = "n/a";

	public static ButtonName getButtonName(String functionName) {
		String name = functionName.toUpperCase();
		try {
			Field field = ButtonName.class.getField(name);
			if (field.getType() == ButtonName.class) {
				return (ButtonName) field.get(null);
			}
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
		ButtonName btn = (ButtonName) altNames.get(name);
		if (btn == null) {
			// User defined button
			btn = new ButtonName();
			btn.name = functionName;
		}

		return btn;
	}

	static {
		// Name all the Buttons by filed name
		Field[] fields = ButtonName.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getType() == ButtonName.class) {
				try {
					((ButtonName) fields[i].get(null)).name = fields[i].getName();
				} catch (IllegalAccessException e) {

				}
			}
		}
	}

	private ButtonName() {

	}

	private ButtonName(String name) {
		altNames.put(name, this);
	}

	private ButtonName(String[] names) {
		for (int i = 0; i < names.length; i++) {
			altNames.put(names[i], this);
		}
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name;
	}

}
