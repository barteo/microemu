/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.device.swt;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.SoftButton;

public class SwtInputMethod extends InputMethodImpl {

	private Timer keyRepeatTimer;

	private int repeatModeKeyCode;

	private boolean clearRepeatFlag;

	private class KeyRepeatTask extends TimerTask {
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

				if (clearRepeatFlag) {
					da.keyReleased(repeatModeKeyCode);
					repeatModeKeyCode = Integer.MIN_VALUE;
				}
			}
		}
	};

	public SwtInputMethod() {
		super();

		// TODO When InputMethod will be removed from EmulatorContext add:
		// if (DeviceFactory.getDevice().hasRepeatEvents()) {
		keyRepeatTimer = new Timer();
		repeatModeKeyCode = Integer.MIN_VALUE;
		clearRepeatFlag = false;
	}

	public int getGameAction(int keyCode) {
		// TODO fix KeyEvent
		switch (keyCode) {
		case SWT.ARROW_UP:
			return Canvas.UP;

		case SWT.ARROW_DOWN:
			return Canvas.DOWN;

		case SWT.ARROW_LEFT:
			return Canvas.LEFT;

		case SWT.ARROW_RIGHT:
			return Canvas.RIGHT;

		case SWT.CR:
			return Canvas.FIRE;

			/*
			 * case KeyEvent.VK_1: case KeyEvent.VK_A: return Canvas.GAME_A;
			 * 
			 * case KeyEvent.VK_3: case KeyEvent.VK_B: return Canvas.GAME_B;
			 * 
			 * case KeyEvent.VK_7: case KeyEvent.VK_C: return Canvas.GAME_C;
			 * 
			 * case KeyEvent.VK_9: case KeyEvent.VK_D: return Canvas.GAME_D;
			 */

		default:
			return 0;
		}
	}

	public int getKeyCode(int gameAction) {
		// TODO fix KeyEvent
		switch (gameAction) {
		case Canvas.UP:
			return SWT.ARROW_UP;

		case Canvas.DOWN:
			return SWT.ARROW_DOWN;

		case Canvas.LEFT:
			return SWT.ARROW_LEFT;

		case Canvas.RIGHT:
			return SWT.ARROW_RIGHT;

		case Canvas.FIRE:
			return SWT.CR;

			/*
			 * case Canvas.GAME_A: return KeyEvent.VK_1;
			 * 
			 * case Canvas.GAME_B: return KeyEvent.VK_3;
			 * 
			 * case Canvas.GAME_C: return KeyEvent.VK_7;
			 * 
			 * case Canvas.GAME_D: return KeyEvent.VK_9;
			 */

		default:
			throw new IllegalArgumentException();
		}
	}

	public String getKeyName(int keyCode) throws IllegalArgumentException {
		for (Iterator it = DeviceFactory.getDevice().getButtons().iterator(); it.hasNext();) {
			SwtButton button = (SwtButton) it.next();
			if (button.getKeyCode() == keyCode) {
				return button.getName();
			}
		}

		for (Iterator it = DeviceFactory.getDevice().getButtons().iterator(); it.hasNext();) {
			SwtButton button = (SwtButton) it.next();
			if (button.getKeyboardKey() == keyCode) {
				return button.getName();
			}
		}

		throw new IllegalArgumentException();
	}

	private boolean commonKeyPressed(KeyEvent ev) {
		int keyCode = ev.keyCode;
		if (inputMethodListener == null) {
			int midpKeyCode;
			switch (ev.keyCode) {
			case SWT.BS:
				return true;
			default:
				midpKeyCode = keyCode;
			}
			switch (ev.character) {
			case '*':
				midpKeyCode = Canvas.KEY_STAR;
				break;
			case '#':
				midpKeyCode = Canvas.KEY_POUND;
				break;
			default:
				midpKeyCode = keyCode;
			}
			MIDletBridge.getMIDletAccess().getDisplayAccess().keyPressed(midpKeyCode);
			return true;
		}

		if (getGameAction(keyCode) == Canvas.UP || getGameAction(keyCode) == Canvas.DOWN) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().keyPressed(keyCode);
			return true;
		}

		// TODO fix KeyEvent
		/*
		 * if (keyCode == KeyEvent.VK_MODECHANGE) { if (getInputMode() ==
		 * InputMethod.INPUT_123) { setInputMode(InputMethod.INPUT_ABC_UPPER); }
		 * else if (getInputMode() == InputMethod.INPUT_ABC_UPPER) {
		 * setInputMode(InputMethod.INPUT_ABC_LOWER); } else if (getInputMode() ==
		 * InputMethod.INPUT_ABC_LOWER) { setInputMode(InputMethod.INPUT_123); }
		 * synchronized (this) { if (lastButton != null) { caret++; lastButton =
		 * null; lastButtonCharIndex = -1; } } InputMethodEvent event = new
		 * InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret,
		 * text); inputMethodListener.caretPositionChanged(event); return true;
		 */

		int caret = inputMethodListener.getCaretPosition();

		if (getGameAction(keyCode) == Canvas.LEFT || getGameAction(keyCode) == Canvas.RIGHT) {
			synchronized (this) {
				if (getGameAction(keyCode) == Canvas.LEFT && caret > 0) {
					caret--;
				}
				if (getGameAction(keyCode) == Canvas.RIGHT && caret < inputMethodListener.getText().length()) {
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

		if (keyCode == SWT.BS) {
			String tmp = inputMethodListener.getText();
			synchronized (this) {
				if (lastButton != null) {
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				if (caret > 0) {
					caret--;
					tmp = "";
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

		if (keyCode == SWT.DEL) {
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

		if (keyCode == SWT.SHIFT || keyCode == SWT.CTRL || keyCode == SWT.ALT) {
			return true;
		}

		return false;
	}

	public void keyPressed(KeyEvent ev) {
		if (DeviceFactory.getDevice().hasRepeatEvents() && inputMethodListener == null) {
			clearRepeatFlag = false;
			if (repeatModeKeyCode == ev.keyCode) {
				MIDletAccess ma = MIDletBridge.getMIDletAccess();
				if (ma == null) {
					return;
				}

				DisplayAccess da = ma.getDisplayAccess();
				if (da == null) {
					return;
				}

				da.keyRepeated(ev.keyCode);

				return;
			}

			repeatModeKeyCode = ev.keyCode;
		}

		// invoke any associated commands, but send the raw key codes instead
		boolean rawSoftKeys = DeviceFactory.getDevice().getDeviceDisplay().isFullScreenMode();
		SwtButton pressedButton = getButton(ev);
		if (pressedButton != null) {
			if (pressedButton instanceof SoftButton && !rawSoftKeys) {
				Command cmd = ((SoftButton) pressedButton).getCommand();
				if (cmd != null) {
					MIDletAccess ma = MIDletBridge.getMIDletAccess();
					if (ma == null) {
						return;
					}
					DisplayAccess da = ma.getDisplayAccess();
					if (da == null) {
						return;
					}
					da.commandAction(cmd, da.getCurrent());
					return;
				}
			}
		}

		if (commonKeyPressed(ev)) {
			return;
		}

		if (inputMethodListener.getText().length() < maxSize && (ev.keyCode & SWT.EMBEDDED) == 0) {
			insertText(new Character(ev.character).toString());
		}
	}

	public void keyReleased(KeyEvent ev) {
		if (DeviceFactory.getDevice().hasRepeatEvents() && inputMethodListener == null) {
			clearRepeatFlag = true;
			keyRepeatTimer.schedule(new KeyRepeatTask(), 50);
		} else {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}

			DisplayAccess da = ma.getDisplayAccess();
			if (da == null) {
				return;
			}

			da.keyReleased(ev.keyCode);
		}
	}

	public void mousePressed(KeyEvent ev) {
		if (commonKeyPressed(ev)) {
			return;
		}

		if (inputMethodListener.getText().length() < maxSize) {
			for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e.hasMoreElements();) {
				SwtButton button = (SwtButton) e.nextElement();
				if (ev.keyCode == button.getKeyCode()) {
					int caret = inputMethodListener.getCaretPosition();
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
						return;
					}
					InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret,
							tmp);
					inputMethodListener.inputMethodTextChanged(event);
					break;
				}
			}
		}
	}

	public void mouseReleased(int keyCode) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}

		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}

		da.keyReleased(keyCode);
	}

	public SwtButton getButton(KeyEvent ev) {
		for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e.hasMoreElements();) {
			SwtButton button = (SwtButton) e.nextElement();
			if (ev.keyCode == button.getKeyCode()) {
				return button;
			}
			if (button.isChar(ev.character, getInputMode())) {
				return button;
			}
		}

		return null;
	}

}
