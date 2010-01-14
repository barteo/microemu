/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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

import javax.microedition.lcdui.TextField;

import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.InputMethodListener;

public abstract class InputMethodImpl extends InputMethod implements Runnable {

	protected boolean resetKey;
	
	protected Button lastButton;
	
	protected int lastButtonCharIndex;

	private boolean cancel;
	
	private Thread t;

	public InputMethodImpl() {
		this.lastButton = null;
		this.lastButtonCharIndex = -1;
		
		this.cancel = false;
		this.t = new Thread(this, "InputMethodThread");
		this.t.setDaemon(true);
		this.t.start();
	}

	// TODO to be removed when event dispatcher will run input method task
	public void dispose() {
		cancel = true;
		synchronized (this) {
			notify();
		}
	}

	// Runnable
	public void run() 
	{
		while (!cancel) {
			try {
				resetKey = true;
				synchronized (this) {
					wait(1500);
				}
			} catch (InterruptedException ex) {
			}
			synchronized (this) {
				if (resetKey && lastButton != null && inputMethodListener != null) {
					int caret = inputMethodListener.getCaretPosition() + 1;
                    if (caret <= inputMethodListener.getText().length()) {
    					lastButton = null;
    					lastButtonCharIndex = -1;
						InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, inputMethodListener.getText());
						inputMethodListener.caretPositionChanged(event);
                    }
				}
			}
		}
	}
	
    public void setInputMethodListener(InputMethodListener l) {
        super.setInputMethodListener(l);

        lastButton = null;
        lastButtonCharIndex = -1;
    }
	
	public void pointerPressed(int x, int y) {		
		if (DeviceFactory.getDevice().hasPointerEvents()) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().pointerPressed(x, y);
		}
	}

	public void pointerReleased(int x, int y) {
		if (DeviceFactory.getDevice().hasPointerEvents()) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().pointerReleased(x, y);
		}
	}

	public void pointerDragged(int x, int y) {
		if (DeviceFactory.getDevice().hasPointerMotionEvents()) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().pointerDragged(x, y);
		}
	}
	
	protected void insertText(String str) {
		if (str.length() > 0) {
			int caret = inputMethodListener.getCaretPosition();
			String tmp = "";
			synchronized (this) {
				if (lastButton != null) {
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				if (caret > 0) {
					tmp += inputMethodListener.getText().substring(0, caret);
				}
				tmp += str;
				if (caret < inputMethodListener.getText().length()) {
					tmp += inputMethodListener.getText().substring(caret);
				}
				caret += str.length();
			}
            if (!validate(tmp, inputMethodListener.getConstraints())) {
                return;
            }
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, tmp);
			inputMethodListener.inputMethodTextChanged(event);
			event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, tmp);
			inputMethodListener.caretPositionChanged(event);
		}
	}

	protected char[] filterConstraints(char[] chars) {
		char[] result = new char[chars.length];
		int i, j;

		for (i = 0, j = 0; i < chars.length; i++) {
            switch (inputMethodListener.getConstraints() & TextField.CONSTRAINT_MASK) {
                case TextField.ANY :
                    result[j] = chars[i];
                    j++;
                    break;
                case TextField.EMAILADDR :
                    // TODO
                    break;
                case TextField.NUMERIC :
                    if (Character.isDigit(chars[i]) || chars[i] == '-') {
                        result[j] = chars[i];
                        j++;
                    }
                    break;
                case TextField.PHONENUMBER :
                    if (Character.isDigit(chars[i]) || chars[i] == '-' || chars[i] == '.' || chars[i] == ' ' || chars[i] == '+') {
                        result[j] = chars[i];
                        j++;
                    }
                    break;
                case TextField.URL :
                    if (chars[i] != '\n') {
                        result[j] = chars[i];
                        j++;
                    }
                    break;
                case TextField.DECIMAL :
                    if (Character.isDigit(chars[i]) || chars[i] == '-' || chars[i] == '.') {
                        result[j] = chars[i];
                        j++;
                    }
                    break;
            }
		}
		if (i != j) {
			char[] newresult = new char[j];
			System.arraycopy(result, 0, newresult, 0, j);
			result = newresult;
		}

		return result;
	}

	protected char[] filterInputMode(char[] chars) {
	    if (chars == null) {
	        return new char[0];
	    }
	    
		int inputMode = getInputMode();
		char[] result = new char[chars.length];
		int i, j;

		for (i = 0, j = 0; i < chars.length; i++) {
			if (inputMode == InputMethod.INPUT_ABC_UPPER) {
				result[j] = Character.toUpperCase(chars[i]);
				j++;
			} else if (inputMode == InputMethod.INPUT_ABC_LOWER) {
				result[j] = Character.toLowerCase(chars[i]);
				j++;
			} else if (inputMode == InputMethod.INPUT_123) {
				if (Character.isDigit(chars[i]) || chars[i] == '-' || chars[i] == '.' || chars[i] == ' ' || chars[i] == '+') {
					result[j] = chars[i];
					j++;
				}
			}
		}
		if (i != j) {
			char[] newresult = new char[j];
			System.arraycopy(result, 0, newresult, 0, j);
			result = newresult;
		}

		return result;
	}
	
}
