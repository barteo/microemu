/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
	protected int constraints = TextField.ANY;
	protected String text;
	protected int caret;
	protected int maxSize;


	// TODO to be removed when event dispatcher will run input method task
	public abstract void dispose();
	
	public abstract int getGameAction(int keyCode);

    public abstract int getKeyCode(int gameAction);

    public abstract String getKeyName(int keyCode);

    public abstract boolean hasPointerMotionEvents();

    public abstract boolean hasPointerEvents();

    public abstract boolean hasRepeatEvents();
        

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
		setInputMode(INPUT_ABC_UPPER);
		text = "";
		caret = 0;
	}
  
  
	public void setConstraints(int aconstraints)
  	{
	    constraints = aconstraints;
  	}


	public int getInputMode()
	{
		return inputMode;
	}


	public void setInputMode(int mode)
	{
		inputMode = mode;
	}


	public void setText(String text)
	{
		this.text = text;
		caret = text.length();
	}
    
    public String getText()
    {
        return text;
    }


    public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}

}
