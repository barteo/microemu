/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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
                    // TODO
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
				if (Character.isDigit(chars[i]) || chars[i] == '-' || chars[i] == '.') {
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
