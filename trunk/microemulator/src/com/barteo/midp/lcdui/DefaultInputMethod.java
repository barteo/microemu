/*
 * @(#)DefaultInputMethod.java  07/07/2001
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

import java.awt.event.KeyEvent;


public class DefaultInputMethod extends InputMethod implements Runnable
{

	int[] deviceKeys = {
			KeyEvent.VK_0,
			KeyEvent.VK_1,
			KeyEvent.VK_2,
			KeyEvent.VK_3,
			KeyEvent.VK_4,
			KeyEvent.VK_5,
			KeyEvent.VK_6,
			KeyEvent.VK_7,
			KeyEvent.VK_8,
			KeyEvent.VK_9,
      KeyEvent.VK_MULTIPLY
	};

	char[][] abcUpperKeys = {
			{ ' ', '0' },
			{ '.', ',', '?', '!', ':', ';', '-', '+', '#', '*', '1' },
			{ 'A', 'B', 'C', '2' },
			{ 'D', 'E', 'F', '3' },
			{ 'G', 'H', 'I', '4' },
			{ 'J', 'K', 'L', '5' },
			{ 'M', 'N', 'O', '6' },
			{ 'P', 'Q', 'R', 'S', '7' },
			{ 'T', 'U', 'V', '8' },
			{ 'W', 'X', 'Y', 'Z', '9' },
      { '*' }
	};

	char[][] abcLowerKeys = {
			{ ' ', '0' },
			{ '.', ',', '?', '!', ':', ';', '-', '+', '#', '*', '1' },
			{ 'a', 'b', 'c', '2' },
			{ 'd', 'e', 'f', '3' },
			{ 'g', 'h', 'i', '4' },
			{ 'j', 'k', 'k', '5' },
			{ 'm', 'n', 'o', '6' },
			{ 'p', 'q', 'r', 's', '7' },
			{ 't', 'u', 'v', '8' },
			{ 'w', 'x', 'y', 'z', '9' },
      { '*' }      
	};

	char[][] numericKeys = {
			{ '0' },
			{ '1' },
			{ '2' },
			{ '3' },
			{ '4' },
			{ '5' },
			{ '6' },
			{ '7' },
			{ '8' },
			{ '9' },
			{ '.' }
	};

	int lastKeyIndex = -1;
	int lastKeyCountIndex = -1;
	boolean resetKey;

	Thread t;


	DefaultInputMethod()
	{
		t = new Thread(this);
		t.start();
	}


	public void keyPressed(int keyCode)
	{
		String tmp;

		if (inputMethodListener == null) {
			DisplayBridge.keyPressed(keyCode);
			return;
		}

		if (keyCode == KeyEvent.VK_MODECHANGE) {
			if (getInputMode() == InputMethod.INPUT_123) {
				setInputMode(InputMethod.INPUT_ABC_UPPER);
			} else if (getInputMode() == InputMethod.INPUT_ABC_UPPER) {
				setInputMode(InputMethod.INPUT_ABC_LOWER);
			} else if (getInputMode() == InputMethod.INPUT_ABC_LOWER) {
				setInputMode(InputMethod.INPUT_123);
			}
			synchronized (this) {
				if (lastKeyIndex != -1) {
					caret++;
					lastKeyIndex = -1;
					lastKeyCountIndex = -1;
				}
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.caretPositionChanged(event);
			return;
		}

		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
			synchronized (this) {
				if (keyCode == KeyEvent.VK_LEFT && caret > 0) {
					caret--;
				}
				if (keyCode == KeyEvent.VK_RIGHT && caret < text.length()) {
					caret++;
				}
				lastKeyIndex = -1;
				lastKeyCountIndex = -1;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.caretPositionChanged(event);
			return;
		}

		if (keyCode == KeyEvent.VK_BACK_SPACE) {
			synchronized (this) {
				if (lastKeyIndex != -1) {
					caret++;
					lastKeyIndex = -1;
					lastKeyCountIndex = -1;
				}
				if (caret > 0) {
					caret--;
					tmp = "";
					if (caret > 0) {
						tmp += text.substring(0, caret);
					}
					if (caret < text.length() - 1) {
						tmp += text.substring(caret + 1);
					}
					text = tmp;
				}
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.inputMethodTextChanged(event);
			inputMethodListener.caretPositionChanged(event);
			return;
		}

		if (text.length() < maxSize) {
			char[][] transformKeys = null;
			if (getInputMode() == InputMethod.INPUT_123) {
				transformKeys = numericKeys;
			} else if (getInputMode() == InputMethod.INPUT_ABC_UPPER) {
				transformKeys = abcUpperKeys;
			} else if (getInputMode() == InputMethod.INPUT_ABC_LOWER) {
				transformKeys = abcLowerKeys;
			}
			for (int i = 0; i < deviceKeys.length; i++) {
				if (keyCode == deviceKeys[i]) {
					synchronized (this) {
						lastKeyCountIndex++;
						if (lastKeyCountIndex == transformKeys[i].length) {
							if (transformKeys[i].length == 1) {
								if (lastKeyIndex != -1) {
									caret++;
								}
								lastKeyIndex = -1;
							} else {
								lastKeyCountIndex = 0;
							}
						}
						if (lastKeyIndex != i) {
							if (lastKeyIndex != -1) {
								caret++;
							}
							tmp = "";
							if (caret > 0) {
								tmp += text.substring(0, caret);
							}
							tmp += transformKeys[i][0];
							if (caret < text.length()) {
								tmp += text.substring(caret);
							}
							text = tmp;
							lastKeyCountIndex = 0;
						} else {
							tmp = "";
							if (caret > 0) {
								tmp += text.substring(0, caret);
							}
							tmp += transformKeys[i][lastKeyCountIndex];
							if (caret < text.length() - 1) {
								tmp += text.substring(caret + 1);
							}
							text = tmp;
						}
						lastKeyIndex = i;
						resetKey = false;
						notify();
					}

					InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, text);
					inputMethodListener.inputMethodTextChanged(event);
					break;
				}
			}
		}
	}


	public void keyReleased(int keyCode)
	{
		DisplayBridge.keyReleased(keyCode);
  }


	public void run()
	{
		while (true) {
			try {
				resetKey = true;
				synchronized (this) {
					wait(1500);
				}
			} catch (InterruptedException ex) {}
			synchronized (this) {
				if (resetKey && lastKeyIndex != -1) {
					caret++;
					lastKeyIndex = -1;
					lastKeyCountIndex = -1;
					if (inputMethodListener != null) {
						InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
						inputMethodListener.caretPositionChanged(event);
					}
				}
			}
		}
	}

}
