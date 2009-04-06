/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu.midp.examples.simpledemo;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;

public class KeyCanvasPanel extends BaseExamplesCanvas {

	static Hashtable actionNames = new Hashtable();
	
	static Hashtable shortNames = new Hashtable();
	
	int lastKeyCode = 0;

	int sameKeyCount = 0;
	
	int lastKeyRepeatedKeyCode = 0;
	
	int keyRepeatedCount = 0;
	
	long keyRepeatedTime = 0;
	
	long keyRepeatedInitialDellay = 0;
	
	String lastKeyEvent = null;
	
	Vector keysHistory = new Vector();
	
	Vector keysPressed = new Vector();
	
	boolean debug = true;
	
	static { 		
		initActionNames();
	}
	
	public KeyCanvasPanel() {
		super("KeyCanvas");
	}

	protected void paint(Graphics g) {
		int width = getWidth();
        int height = getHeight();

		g.setGrayScale(255);
		g.fillRect(0, 0, width, height);
		
		g.setColor(0);
		int line = 0;
		writeln(g, line++, "Key Canvas - Press any key!");
		if (fullScreenMode) {
			writeln(g, line++, "Back - same key 3 times");
		}
		if (sameKeyCount > 0) {
			writeln(g, line++, "KeyCode: " + lastKeyCode); 
			writeln(g, line++, "As char: " +  (char) lastKeyCode);
			writeln(g, line++, "GameAction: " + gameActionName(getGameAction(lastKeyCode)));
			writeln(g, line++, "KeyName: " + getKeyName(lastKeyCode));
			StringBuffer pressed = new StringBuffer();
			for(Enumeration en = keysPressed.elements(); en.hasMoreElements(); ) {
			    pressed.append(en.nextElement());
			}
			writeln(g, line++, "Pressed: " + pressed.toString());
			writeln(g, line++, "Event: " + lastKeyEvent);
		}
		if (keysHistory.size() > 0) {
			writeln(g, line++, "- history -");
			for (int i = keysHistory.size() - 1; i >= 0; i--) {
				if (writeln(g, line++, (String) keysHistory.elementAt(i)) > height) {
					break;
				}
			}
		}
	}
	
	public String getKeyName(int keyCode) {
		try {
			return super.getKeyName(keyCode);
		} catch (IllegalArgumentException e) {
			return "not valid key code";
		}
	}
	
	protected void keyPressed(int keyCode) {
		if (lastKeyCode == keyCode) {
			sameKeyCount ++;
			if ((fullScreenMode) && (sameKeyCount >= 3)) {
				setFullScreenMode(false);
				SimpleDemoMIDlet.showMenu();
			}
		} else {
			sameKeyCount = 1;
			logEvent(String.valueOf(keyCode) + " " + getKeyName(keyCode));
		}
		keyRepeatedTime = System.currentTimeMillis();
		lastKeyCode = keyCode;
		lastKeyEvent = "keyPressed";
		keysPressed.addElement(shortName(keyCode));
		if (debug) {
		    System.out.println(lastKeyEvent + " " + keyCode);
		}
		repaint();
	}
	
	public void keyReleased(int keyCode) {
		lastKeyEvent = "keyReleased";
		lastKeyCode = keyCode;
		if (debug) {
            System.out.println(lastKeyEvent + " " + keyCode);
        }
		keysPressed.removeElement(shortName(keyCode));
		keyRepeatedCount = 1;
        keyRepeatedTime = 0;
        keyRepeatedInitialDellay = 0;
		repaint();
	}

	public void keyRepeated(int keyCode) {
	    long keyRepeatedDellay = 0;
		if (lastKeyRepeatedKeyCode == keyCode) {
			keyRepeatedCount ++;
		} else {
			keyRepeatedCount = 1;
		}
		keyRepeatedDellay = System.currentTimeMillis() - keyRepeatedTime; 
        if (keyRepeatedInitialDellay == 0) {
            keyRepeatedInitialDellay = keyRepeatedDellay;
        }
        keyRepeatedTime = System.currentTimeMillis();
        
		lastKeyEvent = "keyRepeated (" + Utils.d00(keyRepeatedCount) + ")";
		if (keyRepeatedDellay != 0) {
		    lastKeyEvent += " " + keyRepeatedInitialDellay + "/" + keyRepeatedDellay + " ms";
		}
		lastKeyCode = keyCode;
		lastKeyRepeatedKeyCode = keyCode;
		if (debug) {
            System.out.println(lastKeyEvent + " " + keyCode);
        }
		repaint();
	}

	private void logEvent(String e) {
		StringBuffer sb = new StringBuffer();
        sb.append(Utils.when());
        sb.append("   ").append(e);
		keysHistory.addElement(sb.toString());
	}

	
	static String gameActionName(int gameAction) {
		return (String)actionNames.get(new Integer(gameAction));
	}

	String shortName(int keyCode) {
	    int gameAction = getGameAction(keyCode);
        String n = (String)shortNames.get(new Integer(gameAction));
        if (n != null) {
            return n;
        } else {
            return ""+(char) keyCode;
        }
    }
	
	private static void actionName(int gameAction, String name, String shortName) {
		actionNames.put(new Integer(gameAction), name);
		shortNames.put(new Integer(gameAction), shortName);
	}
	
	private static void initActionNames() {
		actionName(UP, "UP", "u");
		actionName(DOWN, "DOWN", "d");
		actionName(LEFT, "LEFT", "l");
		actionName(RIGHT, "RIGHT", "r");
		actionName(FIRE, "FIRE", "f");

		actionName(GAME_A, "GAME_A", "a");
		actionName(GAME_B, "GAME_B", "b");
		actionName(GAME_C, "GAME_C", "c");
		actionName(GAME_D, "GAME_D", "d");

		actionName(KEY_NUM0, "KEY_NUM0", "0");
		actionName(KEY_NUM1, "KEY_NUM1", "1");
		actionName(KEY_NUM2, "KEY_NUM2", "2");
		actionName(KEY_NUM3, "KEY_NUM3", "3");
		actionName(KEY_NUM4, "KEY_NUM4", "4");
		actionName(KEY_NUM5, "KEY_NUM5", "5");
		actionName(KEY_NUM6, "KEY_NUM6", "6");
		actionName(KEY_NUM7, "KEY_NUM7", "7");
		actionName(KEY_NUM8, "KEY_NUM8", "8");
		actionName(KEY_NUM9, "KEY_NUM9", "9");
		actionName(KEY_STAR, "KEY_STAR", "*");
		actionName(KEY_POUND, "KEY_POUND", "#");
	}

}
