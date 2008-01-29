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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.TextField;

import org.microemu.CommandManager;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.impl.ButtonDetaultDeviceKeyCodes;
import org.microemu.device.impl.ButtonName;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.SoftButton;
import org.microemu.util.ThreadUtils;

public class J2SEInputMethod extends InputMethodImpl {

	private boolean eventAlreadyConsumed;

	private Timer keyReleasedDelayTimer;

	private List repeatModeKeyCodes = new Vector();

	private class KeyReleasedDelayTask extends TimerTask {

		private int repeatModeKeyCode;

		KeyReleasedDelayTask(int repeatModeKeyCode) {
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
		keyReleasedDelayTimer = ThreadUtils.createTimer("InputKeyReleasedDelayTimer");
	}

	/**
	 * Gets the game action associated with the given key code of the device.
	 * 
	 * @return the game action corresponding to this key, or <code>0</code> if
	 *         none
	 */
	public int getGameAction(int keyCode) {
		for (Iterator it = DeviceFactory.getDevice().getButtons().iterator(); it.hasNext();) {
			J2SEButton button = (J2SEButton) it.next();
			if (button.getKeyCode() == keyCode) {
				return ButtonDetaultDeviceKeyCodes.getGameAction(button.getFunctionalName());
			}
		}
		return 0;
	}

	/**
	 * 
	 * 
	 * @return a key code corresponding to this game action
	 * @throws IllegalArgumentException
	 *             if <code>gameAction</code> is not a valid game action
	 */
	public int getKeyCode(int gameAction) {
		ButtonName name = ButtonDetaultDeviceKeyCodes.getButtonNameByGameAction(gameAction);
		return J2SEDeviceButtonsHelper.getButton(name).getKeyCode();
	}

	/**
	 * @return a string name for the key
	 * @throws IllegalArgumentException
	 *             if <code>keyCode</code> is not a valid key code
	 */
	public String getKeyName(int keyCode) throws IllegalArgumentException {
		for (Iterator it = DeviceFactory.getDevice().getButtons().iterator(); it.hasNext();) {
			J2SEButton button = (J2SEButton) it.next();
			if (button.getKeyCode() == keyCode) {
				return button.getName();
			}
		}
		return Character.toString((char) keyCode);
	}

	protected boolean fireInputMethodListener(J2SEButton button, char keyChar) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return false;
		}

		int keyCode = keyChar;
		if (keyChar == '\0') {
			keyCode = button.getKeyCode();
		}

		if (inputMethodListener == null) {
			da.keyPressed(keyCode);
			return true;
		}
		
		if (button == null) {
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
			StringBuffer editText = new StringBuffer(inputMethodListener.getText());
			synchronized (this) {
				lastButtonCharIndex++;
				char[] buttonChars = filterConstraints(filterInputMode(button.getChars(getInputMode())));
				if (keyChar != '\0') {
					// Pass through letters and characters typed on keyboard but
					// not numbers that are buttons keys (presumably).
					editText.append(keyChar);
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
				} else if (buttonChars.length > 0) {
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
						if (editText.length() < caret) {
							editText.append(buttonChars[0]);
						} else {
							editText.insert(caret, buttonChars[0]);
						}
						lastButton = button;
						lastButtonCharIndex = 0;
					} else {
						editText.setCharAt(caret, buttonChars[lastButtonCharIndex]);
						lastButton = button;
					}
				} else {
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				resetKey = false;
				notify();
			}
			if (!validate(editText.toString(), inputMethodListener.getConstraints())) {
				return false;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, editText
					.toString());
			inputMethodListener.inputMethodTextChanged(event);
		}
		return false;
	}

	public void buttonTyped(J2SEButton button) {
		if (eventAlreadyConsumed) {
			return;
		}
	}

	public void clipboardPaste(String str) {
		if (inputMethodListener != null && inputMethodListener.getText() != null
				&& ((inputMethodListener.getText().length() + str.length()) <= maxSize)) {
			insertText(str);
		}
		eventAlreadyConsumed = true;
	}

	public void buttonPressed(J2SEButton button, char keyChar) {
		int keyCode = keyChar;
		if (keyChar == '\0') {
			keyCode = button.getKeyCode();
		}
		eventAlreadyConsumed = false;
		if (DeviceFactory.getDevice().hasRepeatEvents() && inputMethodListener == null) {
			if (repeatModeKeyCodes.contains(new Integer(keyCode))) {
				MIDletAccess ma = MIDletBridge.getMIDletAccess();
				if (ma == null) {
					return;
				}
				DisplayAccess da = ma.getDisplayAccess();
				if (da == null) {
					return;
				}
				da.keyRepeated(keyCode);
				eventAlreadyConsumed = true;
				return;
			} else {
				repeatModeKeyCodes.add(new Integer(keyCode));
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

		if (fireInputMethodListener(button, keyChar)) {
			eventAlreadyConsumed = true;
			return;
		}
	}

	public void buttonReleased(J2SEButton button, char keyChar) {
		int keyCode = keyChar;
		if (keyChar == '\0') {
			keyCode = button.getKeyCode();
		}
		if (DeviceFactory.getDevice().hasRepeatEvents() && inputMethodListener == null) {
			repeatModeKeyCodes.remove(new Integer(keyCode));
			keyReleasedDelayTimer.schedule(new KeyReleasedDelayTask(keyCode), 50);
		} else {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}

			DisplayAccess da = ma.getDisplayAccess();
			if (da == null) {
				return;
			}

			da.keyReleased(keyCode);
			eventAlreadyConsumed = false;
		}
	}

	public J2SEButton getButton(KeyEvent ev) {
		J2SEButton button = J2SEDeviceButtonsHelper.getButton(ev);
		if (button != null) {
			return button;
		}
		if (getInputMode() != INPUT_123) {
			for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e.hasMoreElements();) {
				button = (J2SEButton) e.nextElement();
				if (button.isChar(ev.getKeyChar(), getInputMode())) {
					return button;
				}
			}
		}
		return null;
	}
}
