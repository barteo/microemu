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
 * 
 * Contributor(s): 
 *   3GLab
 *   Robert Helmer
 */

package javax.microedition.lcdui;

import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.InputMethodListener;
import org.microemu.device.ui.TextFieldUI;

public class TextField extends Item 
{
	public static final int ANY = 0;
	public static final int EMAILADDR = 1;
	public static final int NUMERIC = 2;
	public static final int PHONENUMBER = 3;
	public static final int URL = 4;	
	public static final int DECIMAL = 5;

	public static final int PASSWORD = 0x10000;
	public static final int UNEDITABLE = 0x20000;
	public static final int SENSITIVE = 0x40000;
	public static final int NON_PREDICTIVE = 0x80000;
	public static final int INITIAL_CAPS_WORD = 0x100000;
	public static final int INITIAL_CAPS_SENTENCE = 0x200000;
	
	public static final int CONSTRAINT_MASK = 0xffff;

	StringComponent stringComponent;
	
	private String field;
	private int caret;
	private boolean caretVisible;
	private int maxSize;
	private int constraints;

	private InputMethodListener inputMethodListener = new InputMethodListener() 
	{
		public void caretPositionChanged(InputMethodEvent event) 
		{
			setCaretPosition(event.getCaret());
			setCaretVisible(true);
			repaint();
		}

		public void inputMethodTextChanged(InputMethodEvent event) 
		{
			setCaretVisible(false);
			setString(event.getText(), event.getCaret());
			repaint();
            
            if (owner instanceof Form) {
                ((Form) owner).fireItemStateListener();
            }
		}

		public int getCaretPosition()
		{
			return TextField.this.getCaretPosition();
		}

		public String getText()
		{
			return TextField.this.getString();
		}

		public int getConstraints()
        {
            return TextField.this.getConstraints();
        }
	};

	
	public TextField(String label, String text, int maxSize, int constraints) 
	{
		super(label);
		super.setUI(DeviceFactory.getDevice().getUIFactory().createTextFieldUI(this));
		
		if (maxSize <= 0) {
			throw new IllegalArgumentException();
		}
		setConstraints(constraints);
        if (!InputMethod.validate(text, constraints)) {
            throw new IllegalArgumentException();
        }
		stringComponent = new StringComponent();
		if (text != null) {
			setString(text);
		} else {
			setString("");
		}
		setMaxSize(maxSize);
		stringComponent.setWidthDecreaser(8);
	}

	
	public String getString() 
	{
		if (ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidTextFieldUI")) {
			return ((TextFieldUI) ui).getString();
		}

		return field;
	}

	
	public void setString(String text) 
	{
		if (ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidTextFieldUI")) {
			((TextFieldUI) ui).setString(text);
		}
		
        setString(text, text.length());
	}
    
    
    void setString(String text, int caret)
    {
        if (!InputMethod.validate(text, constraints)) {
            throw new IllegalArgumentException();
        }
        if (text == null) {
            field = "";
            stringComponent.setText("");
        } else {
            if (text.length() > maxSize) {
                throw new IllegalArgumentException();
            }
            field = text;
            if ((constraints & PASSWORD) == 0) {
                stringComponent.setText(text);
            } else {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < text.length(); i++) {
                    sb.append('*');
                }
                stringComponent.setText(sb.toString());
            }
        }
        setCaretPosition(caret);
        setCaretVisible(false);
        repaint();
    }

	
	public int getChars(char[] data) 
	{
		if (data.length < field.length()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		getString().getChars(0, field.length(), data, 0);

		return field.length();
	}

	
	public void setChars(char[] data, int offset, int length) 
	{
		if (data == null) {
			setString("");
		} else {
			if (length > maxSize) {
				throw new IllegalArgumentException();
			}
			String newtext = new String(data, offset, length);
			if (!InputMethod.validate(newtext, constraints)) {
                throw new IllegalArgumentException();
            }
			setString(newtext);
		}
		repaint();
	}

	
	public void insert(String src, int position) 
	{
		if (!InputMethod.validate(src, constraints)) {
            throw new IllegalArgumentException();
        }
		if (field.length() + src.length() > maxSize) {
			throw new IllegalArgumentException();
		}
		String newtext = "";
		if (position > 0) {
			newtext = getString().substring(0, position);
		}
		newtext += src;
		if (position < field.length()) {
			newtext += getString().substring(position + 1);
		}
		setString(newtext);
		repaint();
	}

	
	public void insert(char[] data, int offset, int length, int position) 
	{
		if (offset + length > data.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		insert(new String(data, offset, length), position);
	}

	
	public void delete(int offset, int length) 
	{
		if (offset + length > field.length()) {
			throw new StringIndexOutOfBoundsException();
		}
		String newtext = "";
		if (offset > 0) {
			newtext = getString().substring(0, offset);
		}
		if (offset + length < field.length()) {
			newtext += getString().substring(offset + length);
		}
		setString(newtext);
		repaint();
	}

	
	public int getMaxSize() 
	{
		return maxSize;
	}

	
	public int setMaxSize(int maxSize) 
	{
		if (maxSize <= 0) {
			throw new IllegalArgumentException();
		}
		if (getString().length() > maxSize) {
			setString(getString().substring(0, maxSize));
		}
		this.maxSize = maxSize;
		return maxSize;
	}

	
	public int size() 
	{
		return getString().length();
	}

	
	public int getCaretPosition() 
	{
		return caret;
	}

	
	public void setConstraints(int constraints) 
	{
		if ((constraints & TextField.CONSTRAINT_MASK) < ANY
			|| (constraints & TextField.CONSTRAINT_MASK) > DECIMAL) {
			throw new IllegalArgumentException("constraints " + constraints + " is an illegal value");
		}
		this.constraints = constraints;
        if (!InputMethod.validate(getString(), constraints)) {
            setString("");
        }
        ((TextFieldUI) ui).setConstraints(constraints);
	}

	
	public int getConstraints() 
	{
		return constraints;
	}
	
	
	public void setInitialInputMode(String characterSubset)
	{
		// TODO implement
	}

	
	boolean isFocusable() 
	{
		return true;
	}

	
	int getHeight() 
	{
		return super.getHeight() + stringComponent.getHeight() + 8;
	}

	
	int paint(Graphics g) 
	{
		super.paintContent(g);

		g.translate(0, super.getHeight());
		int savedColor = g.getColor();
		if (!hasFocus()) {
		    g.setGrayScale(127);
		}
		g.drawRect(
		        1, 1, 
				owner.getWidth() - 3, stringComponent.getHeight() + 4);
		if (!hasFocus()) {
		    g.setColor(savedColor);
		}
		g.translate(3, 3);
		paintContent(g);
		g.translate(-3, -3);
		g.translate(0, -super.getHeight());

		return getHeight();
	}

	
	void paintContent(Graphics g) 
	{
		stringComponent.paint(g);
		if (caretVisible) {
			int x_pos = stringComponent.getCharPositionX(caret);
			int y_pos = stringComponent.getCharPositionY(caret);
			g.drawLine(x_pos, y_pos, x_pos, y_pos + Font.getDefaultFont().getHeight());
		}
	}

	
	void setCaretPosition(int position) 
	{
		caret = position;
	}

	
	void setCaretVisible(boolean state) 
	{
		caretVisible = state;
	}

	
	int traverse(int gameKeyCode, int top, int bottom, boolean action) 
	{
		if (gameKeyCode == Canvas.UP) {
			if (top > 0) {
				return -top;
			} else {
				return Item.OUTOFITEM;
			}
		}
		if (gameKeyCode == Canvas.DOWN) {
			if (getHeight() > bottom) {
				return getHeight() - bottom;
			} else {
				return Item.OUTOFITEM;
			}
		}

		return 0;
	}

	
	void setFocus(boolean hasFocus) 
	{
		super.setFocus(hasFocus);
		if (hasFocus) {
			// register input listener
			InputMethod inputMethod = DeviceFactory.getDevice().getInputMethod();
			inputMethod.setInputMethodListener(inputMethodListener);
			inputMethod.setMaxSize(getMaxSize());
			setCaretVisible(true);
		} else {
			// unregister input listener
			DeviceFactory.getDevice().getInputMethod().removeInputMethodListener(inputMethodListener);
			setCaretVisible(false);
		}
	}	

}
