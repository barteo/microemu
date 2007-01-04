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
 */

package org.microemu.device.j2se;

import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;

import org.microemu.CommandManager;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.impl.InputMethodImpl;
import org.microemu.device.impl.SoftButton;


public class J2SEInputMethod extends InputMethodImpl 
{
	private boolean eventAlreadyConsumed;


	public int getGameAction(int keyCode)
    {
        switch (keyCode) {
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

	
    public int getKeyCode(int gameAction)
    {
        switch (gameAction) {
            case Canvas.UP:
                return KeyEvent.VK_UP;

            case Canvas.DOWN:
                return KeyEvent.VK_DOWN;

            case Canvas.LEFT:
                return KeyEvent.VK_LEFT;

            case Canvas.RIGHT:
                return KeyEvent.VK_RIGHT;

            case Canvas.FIRE:
                return KeyEvent.VK_ENTER;

            case Canvas.GAME_A:
                return KeyEvent.VK_1;

            case Canvas.GAME_B:
                return KeyEvent.VK_3;

            case Canvas.GAME_C:
                return KeyEvent.VK_7;

            case Canvas.GAME_D:
                return KeyEvent.VK_9;

            default:
                throw new IllegalArgumentException();
        }
    }
    
    
	public String getKeyName(int keyCode)
    {
	      if (keyCode == KeyEvent.VK_F1) {
	    	  return "SOFT1";
	      } else if (keyCode == KeyEvent.VK_F2) {
	    	  return "SOFT2";
	      } else if (keyCode == KeyEvent.VK_ENTER) {
	    	  return "SELECT";
	      }
	          
          return Integer.toString(keyCode);
    }

    
	protected boolean commonKeyPressed(KeyEvent ev) 
	{
		String tmp;

		int keyCode = ev.getKeyCode();
		if (inputMethodListener == null) {
			int midpKeyCode;
			switch (keyCode) {
				case KeyEvent.VK_BACK_SPACE :
					return true;
				case KeyEvent.VK_MULTIPLY :
					midpKeyCode = Canvas.KEY_STAR;
					break;
				case KeyEvent.VK_MODECHANGE :
					midpKeyCode = Canvas.KEY_POUND;
					break;
				default :
					midpKeyCode = keyCode;
			}
			switch (ev.getKeyChar()) {
				case '*' :
					midpKeyCode = Canvas.KEY_STAR;
					break;
				case '#' :
					midpKeyCode = Canvas.KEY_POUND;
					break;
			}
			MIDletBridge.getMIDletAccess().getDisplayAccess().keyPressed(midpKeyCode);
			return true;
		}

		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) {
			MIDletBridge.getMIDletAccess().getDisplayAccess().keyPressed(keyCode);
			return true;
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
				if (lastButton != null) {
					caret++;
					lastButton = null;
					lastButtonCharIndex = -1;
				}
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
			synchronized (this) {
				if (keyCode == KeyEvent.VK_LEFT && caret > 0) {
					caret--;
				}
				if (keyCode == KeyEvent.VK_RIGHT && caret < text.length()) {
					caret++;
				}
				lastButton = null;
				lastButtonCharIndex = -1;
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		if (keyCode == KeyEvent.VK_BACK_SPACE) {
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
						tmp += text.substring(0, caret);
					}
					if (caret < text.length() - 1) {
						tmp += text.substring(caret + 1);
					}
					text = tmp;
				}
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, text);
			inputMethodListener.inputMethodTextChanged(event);
			event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}
		
		if (keyCode == KeyEvent.VK_DELETE) {
			synchronized (this) {
				if (lastButton != null) {
					lastButton = null;
					lastButtonCharIndex = -1;
				}
				if (caret != text.length()) {
					text = text.substring(0, caret) + text.substring(caret + 1);
				}
			}
			InputMethodEvent event = new InputMethodEvent(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, caret, text);
			inputMethodListener.inputMethodTextChanged(event);
			event = new InputMethodEvent(InputMethodEvent.CARET_POSITION_CHANGED, caret, text);
			inputMethodListener.caretPositionChanged(event);
			return true;
		}

		return false;
	}
	
	
	public void keyTyped(KeyEvent ev)
	{
		if (eventAlreadyConsumed) {
			return;
		}

		char c = ev.getKeyChar();
		if (c == '\b') {
			return;
		}

		if (inputMethodListener != null && text != null && text.length() < maxSize) {
			insertText(new Character(c).toString());
		}
	}

	
	public void clipboardPaste(String str) 
	{
		if (inputMethodListener != null && text != null
				&& ((text.length() + str.length()) <= maxSize)) {
			insertText(str);
		}

		eventAlreadyConsumed = true;
	}
	
	
	public void keyPressed(KeyEvent ev) 
	{		
		eventAlreadyConsumed = false;
		
		// invoke any associated commands, but send the raw key codes instead
		boolean rawSoftKeys = DeviceFactory.getDevice().getDeviceDisplay().isFullScreenMode();
		J2SEButton pressedButton = getButton(ev);
		if (pressedButton != null) {
		    if (pressedButton instanceof SoftButton && !rawSoftKeys) {
			    Command cmd = ((SoftButton) pressedButton).getCommand();
			    if (cmd != null) {
					CommandManager.getInstance().commandAction(cmd);
					eventAlreadyConsumed = true;
					return;
			    }
			}
		}
		
		if (commonKeyPressed(ev)) {
			eventAlreadyConsumed = true;
			return;
		}
	}


	
	public void mousePressed(KeyEvent ev) 
	{
		String tmp;

		if (commonKeyPressed(ev)) {
			return;
		}

		if (text.length() < maxSize) {
			for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e.hasMoreElements();) {
				J2SEButton button = (J2SEButton) e.nextElement();
				if (ev.getKeyCode() == button.getKey()) {
					synchronized (this) {
						lastButtonCharIndex++;
						char[] buttonChars = filterConstraints(filterInputMode(button.getChars()));
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
									tmp += text.substring(0, caret);
								}
								tmp += buttonChars[0];
								if (caret < text.length()) {
									tmp += text.substring(caret);
								}
								text = tmp;
								lastButton = button;
								lastButtonCharIndex = 0;
							} else {
								tmp = "";
								if (caret > 0) {
									tmp += text.substring(0, caret);
								}
								tmp += buttonChars[lastButtonCharIndex];
								if (caret < text.length() - 1) {
									tmp += text.substring(caret + 1);
								}
								text = tmp;
								lastButton = button;
							}
						} else {
							lastButton = null;
							lastButtonCharIndex = -1;
						}
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

	
	public void keyReleased(KeyEvent ev) 
	{
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}
		
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}
		
		da.keyReleased(ev.getKeyCode());
		
		eventAlreadyConsumed = false;
	}

	
	public void mouseReleased(int keyCode) 
	{
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

	
    public J2SEButton getButton(KeyEvent ev) {
		for (Enumeration e = DeviceFactory.getDevice().getButtons().elements(); e
				.hasMoreElements();) {
			J2SEButton button = (J2SEButton) e.nextElement();
			if (ev.getKeyCode() == button.getKey()) {
				return button;
			}
			if (button.isChar(ev.getKeyChar())) {
				return button;
			}
		}
		return null;
	}

}
