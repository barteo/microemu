/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 * Contributor(s): 
 *   3GLab
 *   Robert Helmer
 */

package javax.microedition.lcdui;

import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.InputMethod;
import com.barteo.emulator.device.InputMethodListener;
import com.barteo.emulator.device.InputMethodEvent;

public class TextField extends Item 
{
	public static final int ANY = 0;
	public static final int EMAILADDR = 1;
	public static final int NUMERIC = 2;
	public static final int PHONENUMBER = 3;
	public static final int URL = 4;

	public static final int PASSWORD = 0x10000;
	public static final int CONSTRAINT_MASK = 0xffff;

	StringComponent stringComponent;
	String field;
	int caret;
	boolean caretVisible;
	int maxSize;
	int constraints;

	TextBox tb = null;
	static final Command doneCommand = new Command("Done", Command.OK, 0);
	static final Command cancelCommand = new Command("Cancel", Command.CANCEL, 0);

	CommandListener textBoxListener = new CommandListener() 
	{
		public void commandAction(Command cmd, Displayable d) 
		{
			if (cmd == doneCommand) {
				setString(tb.getString());
				getOwner().currentDisplay.setCurrent(owner);
			} else if (cmd == cancelCommand) {
				getOwner().currentDisplay.setCurrent(owner);
			}
		}

	};

	InputMethodListener inputMethodListener = new InputMethodListener() 
	{
		public void caretPositionChanged(InputMethodEvent event) 
		{
			setCaretPosition(event.getCaret());
			setCaretVisible(true);
		}

		public void inputMethodTextChanged(InputMethodEvent event) 
		{
			setCaretVisible(false);
			setString(event.getText());
		}
	};

	
	public TextField(String label, String text, int maxSize, int constraints) 
	{
		super(label);
		if (maxSize <= 0) {
			throw new IllegalArgumentException();
		}
		setConstraints(constraints);
		this.maxSize = maxSize;
		stringComponent = new StringComponent();
		if (text != null) {
			setString(text);
		} else {
			setString("");
		}
		stringComponent.setWidth(DeviceFactory.getDevice().getDeviceDisplay().getWidth() - 8);
		caret = 0;
		caretVisible = false;
	}

	
	public String getString() 
	{
		return field;
	}

	
	public void setString(String text) 
	{
		validate(text);
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
			validate(newtext);
			setString(newtext);
		}
		repaint();
	}

	
	public void insert(String src, int position) 
	{
		validate(src);
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
		if (field.length() > maxSize) {
			setString(getString().substring(0, maxSize));
		}
		this.maxSize = maxSize;
		return maxSize;
	}

	
	public int size() 
	{
		return field.length();
	}

	
	public int getCaretPosition() 
	{
		return caret;
	}

	
	public void setConstraints(int constraints) 
	{
		if ((constraints & TextField.CONSTRAINT_MASK) < ANY
			|| (constraints & TextField.CONSTRAINT_MASK) > URL) {
			throw new IllegalArgumentException("constraints is an illegal value");
		}
		this.constraints = constraints;
	}

	
	public int getConstraints() 
	{
		return constraints;
	}

	
	public void setLabel(String label) 
	{
		super.setLabel(label);
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
		if (hasFocus()) {
			g.drawRect(
					1, 1, 
					DeviceFactory.getDevice().getDeviceDisplay().getWidth() - 3, stringComponent.getHeight() + 4);
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

	
	boolean select() 
	{
		if (tb == null) {
			tb = new TextBox(getLabel(), getString(), maxSize, constraints);
			tb.addCommand(doneCommand);
			tb.addCommand(cancelCommand);
			tb.setCommandListener(textBoxListener);
		} else {
			tb.setString(getString());
		}
		getOwner().currentDisplay.setCurrent(tb);

		return true;
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
			inputMethod.setConstraints(getConstraints());
			inputMethod.setText(getString());
			inputMethod.setMaxSize(getMaxSize());
			setCaretPosition(getString().length());
			setCaretVisible(true);
		} else {
			// unregister input listener
			DeviceFactory.getDevice().getInputMethod().removeInputMethodListener(inputMethodListener);
		}
	}

	
	void validate(String text) 
	{
		// text is illegal for the specified constraints so IllegalArgumentException
		if ((constraints & CONSTRAINT_MASK) == ANY) {
			return;
		}
		/*
		 * if ((constraints & CONSTRAINT_MASK) == NUMERIC) { 
		 *   try { 
		 *     int tmp = Integer.parseInt(text); 
		 *   } catch (NumberFormatException e) { 
		 *     throw new IllegalArgumentException(
		 *         "TextField limited to numeric values: text = " + text); 
		 *   } 
		 * }
		 */

		/* @todo add more constraints checking */
	}

}
