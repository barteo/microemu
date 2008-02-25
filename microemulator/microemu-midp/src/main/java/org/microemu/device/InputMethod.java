/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 
package org.microemu.device;

import javax.microedition.lcdui.TextField;


public abstract class InputMethod
{

	public static final int INPUT_NONE = 0;
	public static final int INPUT_123 = 1;
	public static final int INPUT_ABC_UPPER = 2;
	public static final int INPUT_ABC_LOWER = 3;

	static InputMethod inputMethod = null;
	int inputMode = INPUT_NONE;

	protected InputMethodListener inputMethodListener = null;
	protected int maxSize;


	// TODO to be removed when event dispatcher will run input method task
	public abstract void dispose();
	
	public abstract int getGameAction(int keyCode);

    public abstract int getKeyCode(int gameAction);

    public abstract String getKeyName(int keyCode) throws IllegalArgumentException;


	public void removeInputMethodListener(InputMethodListener l)
	{
		if (l == inputMethodListener) {
			inputMethodListener = null;
			setInputMode(INPUT_NONE);
		}
	}


	public void setInputMethodListener(InputMethodListener l)
	{
		inputMethodListener = l;
        switch (l.getConstraints() & TextField.CONSTRAINT_MASK) {
	        case TextField.ANY :
	        case TextField.EMAILADDR :
	        case TextField.URL :
	            setInputMode(INPUT_ABC_LOWER);
	            break;
	        case TextField.NUMERIC :
	        case TextField.PHONENUMBER :
	        case TextField.DECIMAL :
	            setInputMode(INPUT_123);
	            break;
	    }
	}
  
  
	public int getInputMode()
	{
		return inputMode;
	}


	public void setInputMode(int mode)
	{
		inputMode = mode;
	}


    public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}
    
    public static boolean validate(String text, int constraints) 
    {
        switch (constraints & TextField.CONSTRAINT_MASK) {
            case TextField.ANY :
                break;
            case TextField.EMAILADDR :
                // TODO validate email
                break;
            case TextField.NUMERIC :
                if (text != null && text.length() > 0 && !text.equals("-")) {
                    try { 
                        Integer.parseInt(text); 
                    } catch (NumberFormatException e) { 
                        return false;
                    }
                }
                break;
            case TextField.PHONENUMBER :
                // TODO validate email
                break;
            case TextField.URL :
                // TODO validate url
                break;
            case TextField.DECIMAL :
                if (text != null && text.length() > 0 && !text.equals("-")) {
                    try {
                        Double.valueOf(text);
                    } catch (NumberFormatException e) { 
                        return false;
                    }
                }
                break;
        }
        
        return true;
    }

}
