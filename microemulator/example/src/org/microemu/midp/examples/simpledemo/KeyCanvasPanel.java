/*
 *  MicroEmulator
 *  Copyright (C) 2001-2007 MicroEmulator Team.
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
 *
 *  @version $Id$
 */
package org.microemu.midp.examples.simpledemo;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;

public class KeyCanvasPanel extends BaseExamplesCanvas {

	static Hashtable actionNames = new Hashtable();
	
	int lastKeyCode = 0;

	int sameKeyCount = 0;
	
	String lastKeyEvent = null;
	
	Vector keysHistory = new Vector();
	
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
		lastKeyCode = keyCode;
		lastKeyEvent = "keyPressed";
		repaint();
	}
	
	public void keyReleased(int keyCode) {
		lastKeyEvent = "keyReleased";
		lastKeyCode = keyCode;
		repaint();
	}

	public void keyRepeated(int keyCode) {
		lastKeyEvent = "keyRepeated";
		lastKeyCode = keyCode;
		repaint();
	}

	private String d00(int i) {
        if (i > 9) {
            return String.valueOf(i);
        } else {
            return "0" + String.valueOf(i);
        }
    }
	
	private void logEvent(String e) {
		StringBuffer sb = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        sb.append(d00(calendar.get(Calendar.HOUR_OF_DAY))).append(":");
        sb.append(d00(calendar.get(Calendar.MINUTE))).append(":");
        sb.append(d00(calendar.get(Calendar.SECOND)));
        sb.append("   ").append(e);
		keysHistory.addElement(sb.toString());
	}

	
	static String gameActionName(int gameAction) {
		return (String)actionNames.get(new Integer(gameAction));
	}

	private static void actionName(int keyCode, String name) {
		actionNames.put(new Integer(keyCode), name);
	}
	
	private static void initActionNames() {
		actionName(UP, "UP");
		actionName(DOWN, "DOWN");
		actionName(LEFT, "LEFT");
		actionName(RIGHT, "RIGHT");
		actionName(FIRE, "FIRE");

		actionName(GAME_A, "GAME_A");
		actionName(GAME_B, "GAME_B");
		actionName(GAME_C, "GAME_C");
		actionName(GAME_D, "GAME_D");

		actionName(KEY_NUM0, "KEY_NUM0");
		actionName(KEY_NUM1, "KEY_NUM1");
		actionName(KEY_NUM2, "KEY_NUM2");
		actionName(KEY_NUM3, "KEY_NUM3");
		actionName(KEY_NUM4, "KEY_NUM4");
		actionName(KEY_NUM5, "KEY_NUM5");
		actionName(KEY_NUM6, "KEY_NUM6");
		actionName(KEY_NUM7, "KEY_NUM7");
		actionName(KEY_NUM8, "KEY_NUM8");
		actionName(KEY_NUM9, "KEY_NUM9");
		actionName(KEY_STAR, "KEY_STAR");
		actionName(KEY_POUND, "KEY_POUND");
	}

}
