/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package javax.microedition.lcdui;

import com.barteo.emulator.device.Device;


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
	static final Command backCommand = new Command("Back", Command.BACK, 0);

	CommandListener textBoxListener = new CommandListener()
	{

		public void commandAction(Command cmd, Displayable d)
		{
			if (cmd == backCommand) {
				setString(tb.getString());
        getOwner().currentDisplay.setCurrent(owner);
			} 
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
		stringComponent.setWidth(Device.screenPaintable.width - 8);
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
		if (text.length() > maxSize) {
			throw new IllegalArgumentException("text.length(): " + text.length() + " > maxsize: " + maxSize);
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
		repaint();
	}


	public int getChars(char[] data)
	{
		if (data.length > field.length()) {
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
		if ((constraints & TextField.CONSTRAINT_MASK) < 0 ||
				(constraints & TextField.CONSTRAINT_MASK) > 4) {
			throw new IllegalArgumentException();
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
		if (isFocus()) {
			g.drawRect(1, 1, Device.screenPaintable.width - 3, stringComponent.getHeight() + 4);
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
			tb.addCommand(backCommand);
			tb.setCommandListener(textBoxListener);
		} else {
			tb.setString(getString());
		}
    getOwner().currentDisplay.setCurrent(tb);

		return true;
	}


	int traverse(int gameKeyCode, int top, int bottom, boolean action)
	{
		Font f = Font.getDefaultFont();

		if (gameKeyCode == 1) {
			if (top > 0) {
				return -top;
			} else {
				return Item.OUTOFITEM;
			}
		}
		if (gameKeyCode == 6) {
			if (getHeight() > bottom) {
				return getHeight() - bottom;
			} else {
				return Item.OUTOFITEM;
			}
		}

		return 0;
	}


	void validate(String text)
	{
		// text is illegal for the specified constraints so IllegalArgumentException
	}

}
