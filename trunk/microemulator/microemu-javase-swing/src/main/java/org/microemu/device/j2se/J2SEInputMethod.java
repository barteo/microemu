/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * @version $Id$
 */

package org.microemu.device.j2se;

import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.TextField;

import org.microemu.CommandManager;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.impl.ButtonName;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.SoftButton;
import org.microemu.util.ThreadUtils;

public class J2SEInputMethod extends InputMethodImpl {

	private boolean eventAlreadyConsumed;

	private Timer keyRepeatTimer;

	private List repeatModeKeyCodes = new Vector();

	private class KeyRepeatTask extends TimerTask {

		private int repeatModeKeyCode;

		KeyRepeatTask(int repeatModeKeyCode) {
			this.repeatModeKeyCode = repeatModeKeyCode;

		}

		public void run() {
			if (repeatModeKeyCode != Integer.MIN_VALUE) {
				MIDletAccess ma = MIDletBridge.getMIDletAccess();
				if (ma == null) {
					return;
				}

				DisplayAccess da = ma.getDisplayAccess();
				if (da == null) {
					return;
				}

				da.keyReleased(repeatModeKeyCode);
				eventAlreadyConsumed = false;
				repeatModeKeyCode = Integer.MIN_VALUE;
			}
		}
	};

	public J2SEInputMethod() {
		super();

		// TODO When InputMethod will be removed from EmulatorContext add:
		// if (DeviceFactory.getDevice().hasRepeatEvents()) {
		keyRepeatTimer = ThreadUtils.createTimer("InputKeyRepeatTimer");
	}

	public int getGameAction(int keyCode) {
		int key = keyCode;

		for (Iterator it = DeviceFactory.getDevice().getButtons().iterator(); it.hasNext();) {
			J2SEButton button = (J2SEButton) it.next();
			if (button.getKeyCode() == keyCode) {
				key = button.getKeyboardKey();
				break;
			}
		}

		switch (key) {
		case KeyEvent.VK_UP:
			return Canvas.UP;

		case KeyEvent.VK_DOWN:
			return Canvas.DOWN;

		case KeyEvent.VK_LEFT:
			return Canvas.LEFT;

		case KeyEvent.VK_RIGHT:
			return Canvas.RIGHT;

		case KeyEvent.VK_ENTER:
			return Canvas.FIRE;

		case KeyEvent.VK_1:
		case KeyEvent.VK_A:
			return Canvas.GAME_A;

		case KeyEvent.VK_3:
		case KeyEvent.VK_B:
			return Canvas.GAME_B;

		case KeyEvent.VK_7:
		case KeyEvent.VK_C:
			return Canvas.GAME_C;

		case KeyEvent.VK_9:
		case KeyEvent.VK_D:
			return Canvas.GAME_D;

		default:
			return 0;
		}
	}

	public int getKeyCode(int gameAction) {
		int keyCode;

		switch (gameAction) {
		case Canvas.UP:
			keyCode = KeyEvent.VK_UP;
			break;
		case Canvas.DOWN:
			keyCode = KeyEvent.VK_DOWN;
			break;
		case Canvas.LEFT:
			keyCode = KeyEvent.VK_LEFT;
			break;
		case Canvas.RIGHT:
			keyCode = KeyEvent.VK_RIGHT;
			break;
		case Canvas.FIRE:
			keyCode = KeyEvent.VK_ENTER;
			break;
		case Canvas.GAME_A:
			keyCode = KeyEvent.VK_1;
			break;
		case Canvas.GAME_B:
			keyCode = KeyEvent.VK_3;
			break;
		case Canvas.GAME_C:
			keyCode = KeyEvent.VK_7;
			break;
		case Canvas.GAME_D:
			keyCode = KeyEvent.VK_9;
			break;
		default:
			throw new IllegalArgumentException();
		}

		for (Iterator it = DeviceFactory.getDevice().getButtons().iterator(); it.hasNext();) {
			J2SEButton button = (J2SEButton) it.next();
			if (button.getKeyboardKey() == keyCode) {
				keyCode = button.getKeyCode();
				break;
			}
		}

		return keyCode;
	}

	public String getKeyName(int keyCode) throws IllegalArgumentException {
		for (Iterator it = DeviceFactory.getDevice().getButtons().iterator(); it.hasNext();) {
			J2SEButton button = (J2SEButton) it.next();
			if (button.getKeyCode() == keyCode) {
				return button.getName();
			}
		}

		for (Iterator it = DeviceFactory.getDevice().getButtons().iterator(); it.hasNext();) {
			J2SEButton button = (J2SEButton) it.next();
			if (button.getKeyboardKey() == keyCode) {
				return button.getName();
			}
		}

		throw new IllegalArgumentException();
	}

	protected boolean fireInputMethodListener(J2SEButton button) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return false;
		}

		if (inputMethodListener == null) {
			da.keyPressed(button.getKeyCode());
			return true;
		}
		ButtonName functionalName = button.getFunctionalName();

		if (functionalName == ButtonName.UP || functionalName == ButtonName.DOWN) {
			da.keyPressed(button.getKeyCode());
			return true;
		}

		int caret = inputMethodListener.getCaretPosition();

		if (button.isModeChange()) {
			switch (inputMethodListener.getConstraints() & TextField.CONSTRAINT_MASK) {
			case TextField.ANY:
			case TextField.EMAILADDR:
			case TextField.URL:
				if (getInputMode() == InputMethod.INPUT_123) {
					setInputMode(InputMethod.INPUT_ABC_UPPER);
				} else if (getInputMode() == InputMethod.INPUT_ABC_UPPER) {
					setInputMode(InputMethod.INPUT_ABC_LOWER);
				} else if (getInputMode() == InputMethod.INPUT_ABC_LOWER) {
					setInputMode(InputMethod.INPUT_123);
				}
				synchronized (this) {
					if (lastButton != null) {
						caret++;
						lastButton = null;
						lastButtonCharIndex = -1;
					}
				}
				InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret,
						inputMethodListener.getText());
				inputMethodListener.caretPositionChanged(event);
				break;
			}
			return true;
		}

		if (functionalName == ButtonName.LEFT || functionalName == ButtonName.RIGHT) {
			synchronized (this) {
				if ((functionalName == ButtonName.LEFT) && caret > 0) {
					caret--;
				} else if ((functionalName == ButtonName.RIGHT) && caret < inputMethodListener.getText().length()) {
					caret++;
				}
				lastButton = null;
				lastButtonCharIndex = -1;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret,
					inputMethodListener.getText());
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (functionalName == ButtonName.BACK_SPACE) {
			String tmp = "";
			synchronized (this) {
				if (lastButton != null) {
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				if (caret > 0) {
					caret--;
					if (caret > 0) {
						tmp += inputMethodListener.getText().substring(0, caret);
					}
					if (caret < inputMethodListener.getText().length() - 1) {
						tmp += inputMethodListener.getText().substring(caret + 1);
					}
				}
			}
			if (!validate(tmp, inputMethodListener.getConstraints())) {
				return true;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, tmp);
			inputMethodListener.inputMethodTextChanged(event);
			event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, tmp);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (functionalName == ButtonName.DELETE) {
			String tmp = inputMethodListener.getText();
			synchronized (this) {
				if (lastButton != null) {
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				if (caret != inputMethodListener.getText().length()) {
					tmp = inputMethodListener.getText().substring(0, caret)
							+ inputMethodListener.getText().substring(caret + 1);
				}
			}
			if (!validate(tmp, inputMethodListener.getConstraints())) {
				return true;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, tmp);
			inputMethodListener.inputMethodTextChanged(event);
			event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, tmp);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (inputMethodListener.getText().length() < maxSize) {
			String tmp = inputMethodListener.getText();
			synchronized (this) {
				lastButtonCharIndex++;
				char[] buttonChars = filterConstraints(filterInputMode(button.getChars(getInputMode())));
				if (buttonChars.length > 0) {
					if (lastButtonCharIndex == buttonChars.length) {
						if (buttonChars.length == 1) {
							if (lastButton != null) {
								caret++;
							}
							lastButton = null;
						} else {
							lastButtonCharIndex = 0;
						}
					}
					if (lastButton != button) {
						if (lastButton != null) {
							caret++;
						}
						tmp = "";
						if (caret > 0) {
							tmp += inputMethodListener.getText().substring(0, caret);
						}
						tmp += buttonChars[0];
						if (caret < inputMethodListener.getText().length()) {
							tmp += inputMethodListener.getText().substring(caret);
						}
						lastButton = button;
						lastButtonCharIndex = 0;
					} else {
						tmp = "";
						if (caret > 0) {
							tmp += inputMethodListener.getText().substring(0, caret);
						}
						tmp += buttonChars[lastButtonCharIndex];
						if (caret < inputMethodListener.getText().length() - 1) {
							tmp += inputMethodListener.getText().substring(caret + 1);
						}
						lastButton = button;
					}
				} else {
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				resetKey = false;
				notify();
			}
			if (!validate(tmp, inputMethodListener.getConstraints())) {
				return false;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, tmp);
			inputMethodListener.inputMethodTextChanged(event);
		}
		return false;
	}

	public void buttonTyped(J2SEButton button) {
		if (eventAlreadyConsumed) {
			return;
		}
		// TODO verify not required
		// char c = ev.getKeyChar();
		// if (c == '\b') {
		// return;
		// }
		//
		// if (inputMethodListener != null && inputMethodListener.getText() !=
		// null
		// && inputMethodListener.getText().length() < maxSize) {
		// insertText(new Character(c).toString());
		// }
	}

	public void clipboardPaste(String str) {
		if (inputMethodListener != null && inputMethodListener.getText() != null
				&& ((inputMethodListener.getText().length() + str.length()) <= maxSize)) {
			insertText(str);
		}

		eventAlreadyConsumed = true;
	}

	public void buttonPressed(J2SEButton button) {
		eventAlreadyConsumed = false;
		if (DeviceFactory.getDevice().hasRepeatEvents() && inputMethodListener == null) {
			if (repeatModeKeyCodes.contains(new Integer(button.getKeyCode()))) {
				MIDletAccess ma = MIDletBridge.getMIDletAccess();
				if (ma == null) {
					return;
				}
				DisplayAccess da = ma.getDisplayAccess();
				if (da == null) {
					return;
				}
				da.keyRepeated(button.getKeyCode());
				eventAlreadyConsumed = true;
				return;
			} else {
				repeatModeKeyCodes.add(new Integer(button.getKeyCode()));
			}
		}

		// invoke any associated commands, but send the raw key codes instead
		boolean rawSoftKeys = DeviceFactory.getDevice().getDeviceDisplay().isFullScreenMode();
		if (button instanceof SoftButton && !rawSoftKeys) {
			Command cmd = ((SoftButton) button).getCommand();
			if (cmd != null) {
				CommandManager.getInstance().commandAction(cmd);
				eventAlreadyConsumed = true;
				return;
			}
		}

		if (fireInputMethodListener(button)) {
			eventAlreadyConsumed = true;
			return;
		}
	}

	public void buttonReleased(J2SEButton button) {
		if (DeviceFactory.getDevice().hasRepeatEvents() && inputMethodListener == null) {
			repeatModeKeyCodes.remove(new Integer(button.getKeyCode()));
			keyRepeatTimer.schedule(new KeyRepeatTask(button.getKeyCode()), 50);
		} else {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}

			DisplayAccess da = ma.getDisplayAccess();
			if (da == null) {
				return;
			}

			da.keyReleased(button.getKeyCode());
			eventAlreadyConsumed = false;
		}
	}

	public J2SEButton getButton(KeyEvent ev) {
		J2SEButton button = J2SEDeviceButtonsHelper.getButton(ev);
		if (button != null) {
			return button;
		}
		for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e.hasMoreElements();) {
			button = (J2SEButton) e.nextElement();
			if (button.isChar(ev.getKeyChar(), getInputMode())) {
				return button;
			}
		}
		return null;
	}

}
