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

import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.InputMethod;
import com.barteo.emulator.device.InputMethodEvent;
import com.barteo.emulator.device.InputMethodListener;


public class TextBox extends Screen
{

	TextField tf;

	InputMethodListener inputMethodListener = new InputMethodListener()
	{

		public void caretPositionChanged(InputMethodEvent event)
		{
			tf.setCaretPosition(event.getCaret());
			tf.setCaretVisible(true);
			repaint();
		}

		public void inputMethodTextChanged(InputMethodEvent event)
		{
			tf.setCaretVisible(false);
			tf.setString(event.getText());
			repaint();
		}
	};


	public TextBox(String title, String text, int maxSize, int constraints)
	{
		super(title);
		tf = new TextField(null, text, maxSize, constraints);
	}


	public String getString()
	{
		return tf.getString();
	}


	public void setString(String text)
	{
		tf.setString(text);
	}


	public int getChars(char[] data)
	{
		return tf.getChars(data);
	}


	public void setChars(char[] data, int offset, int length)
	{
		tf.setChars(data, offset, length);
	}


	public void insert(String src, int position)
	{
		tf.insert(src, position);
	}


	public void insert(char[] data, int offset, int length, int position)
	{
		tf.insert(data, offset, length, position);
	}


	public void delete(int offset, int length)
	{
		tf.delete(offset, length);
	}


	public int getMaxSize()
	{
		return tf.getMaxSize();
	}


	public int setMaxSize(int maxSize)
	{
		return tf.setMaxSize(maxSize);
	}


	public int size()
	{
		return tf.size();
	}


	public int getCaretPosition()
	{
		return tf.getCaretPosition();
	}


	public void setConstraints(int constraints)
	{
		tf.setConstraints(constraints);
	}


	public int getConstraints()
	{
		return tf.getConstraints();
	}


	void hideNotify()
	{
		DeviceFactory.getDevice().getInputMethod().removeInputMethodListener(inputMethodListener);
		super.hideNotify();
	}


	int paintContent(Graphics g)
	{
		g.drawRect(1, 1, 
        DeviceFactory.getDevice().getDeviceDisplay().getWidth() - 3, viewPortHeight - 3);
		g.setClip(3, 3, 
        DeviceFactory.getDevice().getDeviceDisplay().getWidth() - 6, viewPortHeight - 6);
		g.translate(3, 3);
		tf.paintContent(g);

		return tf.getHeight() + 6;
	}


	void setCaretPosition(int position)
	{
		tf.setCaretPosition(position);
	}


	int traverse(int gameKeyCode, int top, int bottom)
	{
		int traverse = tf.traverse(gameKeyCode, top, bottom, true);
		if (traverse == Item.OUTOFITEM) {
			return 0;
		} else {
	    return traverse;
		}
	}


	void showNotify()
	{
		super.showNotify();
    InputMethod inputMethod = DeviceFactory.getDevice().getInputMethod();
		inputMethod.setInputMethodListener(inputMethodListener);
    inputMethod.setConstraints(getConstraints());
		inputMethod.setText(getString());
		inputMethod.setMaxSize(getMaxSize());
		setCaretPosition(getString().length());
		tf.setCaretVisible(true);
	}

}
