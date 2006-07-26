package org.microemu.device.impl;

import javax.microedition.lcdui.TextField;

import org.microemu.MIDletBridge;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;

public abstract class InputMethodImpl extends InputMethod implements Runnable {

	protected boolean resetKey;
	
	protected Button lastButton;
	
	protected int lastButtonCharIndex;

	private boolean hasPointerEvents;

	private boolean hasPointerMotionEvents;

	// TODO not implemented yet
	private boolean hasRepeatEvents;

	private boolean cancel;
	
	private Thread t;

	public InputMethodImpl() {
		this.hasPointerEvents = false;
		this.hasPointerMotionEvents = false;
		this.hasRepeatEvents = false;

		this.lastButton = null;
		this.lastButtonCharIndex = -1;
		
		this.cancel = false;
		this.t = new Thread(this, "InputMethodThread");
		this.t.start();
	}

	// InputMethod
	public boolean hasPointerEvents() {
		return hasPointerEvents;
	}

	public boolean hasPointerMotionEvents() {
		return hasPointerMotionEvents;
	}

	public boolean hasRepeatEvents() {
		return hasRepeatEvents;
	}

	public void setHasPointerEvents(boolean state) {
		this.hasPointerEvents = state;
	}

	public void setHasPointerMotionEvents(boolean state) {
		this.hasPointerMotionEvents = state;
	}

	public void setHasRepeatEvents(boolean state) {
		this.hasRepeatEvents = state;
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
				if (resetKey && lastButton != null) {
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
					if (inputMethodListener != null) {
						InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
						inputMethodListener.caretPositionChanged(event);
					}
				}
			}
		}
	}
	
	public void pointerPressed(int x, int y) {
		if (hasPointerEvents) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().pointerPressed(x, y);
		}
	}

	public void pointerReleased(int x, int y) {
		if (hasPointerEvents) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().pointerReleased(x, y);
		}
	}

	public void pointerDragged(int x, int y) {
		if (hasPointerMotionEvents) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().pointerDragged(x, y);
		}
	}

	protected char[] filterConstraints(char[] chars) {
		char[] result = new char[chars.length];
		int i, j;

		for (i = 0, j = 0; i < chars.length; i++) {
			if ((constraints & TextField.NUMERIC) != 0) {
				if (Character.isDigit(chars[i]) || chars[i] == '.') {
					result[j] = chars[i];
					j++;
				}
			} else if ((constraints & TextField.URL) != 0) {
				if (chars[i] != '\n') {
					result[j] = chars[i];
					j++;
				}
			} else {
				result[j] = chars[i];
				j++;
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
				if (Character.isDigit(chars[i])) {
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
