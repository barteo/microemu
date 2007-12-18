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

package javax.microedition.lcdui;

import org.microemu.device.DeviceFactory;
import org.microemu.device.InputMethod;
import org.microemu.device.InputMethodEvent;
import org.microemu.device.InputMethodListener;

//TODO implement pointer events
public class TextBox extends Screen {

	TextField tf;

	InputMethodListener inputMethodListener = new InputMethodListener() {

		public void caretPositionChanged(InputMethodEvent event) {
			setCaretPosition(event.getCaret());
			tf.setCaretVisible(true);
			repaint();
		}

		public void inputMethodTextChanged(InputMethodEvent event) {
			tf.setCaretVisible(false);
			tf.setString(event.getText(), event.getCaret());
			repaint();
		}

		public int getCaretPosition() {
			return TextBox.this.getCaretPosition();
		}

		public String getText() {
			return TextBox.this.getString();
		}

		public int getConstraints() {
			return TextBox.this.getConstraints();
		}
	};

	public TextBox(String title, String text, int maxSize, int constraints) {
		super(title);
		tf = new TextField(null, text, maxSize, constraints);
	}

	public void delete(int offset, int length) {
		tf.delete(offset, length);
	}

	public int getCaretPosition() {
		return tf.getCaretPosition();
	}

	public int getChars(char[] data) {
		return tf.getChars(data);
	}

	public int getConstraints() {
		return tf.getConstraints();
	}

	public int getMaxSize() {
		return tf.getMaxSize();
	}

	public String getString() {
		return tf.getString();
	}

	public void insert(char[] data, int offset, int length, int position) {
		tf.insert(data, offset, length, position);
	}

	public void insert(String src, int position) {
		tf.insert(src, position);
	}

	public void setChars(char[] data, int offset, int length) {
		tf.setChars(data, offset, length);
	}

	public void setConstraints(int constraints) {
		tf.setConstraints(constraints);
	}

	public void setInitialInputMode(String characterSubset) {
		// TODO implement
	}

	public int setMaxSize(int maxSize) {
		return tf.setMaxSize(maxSize);
	}

	public void setString(String text) {
		tf.setString(text);
	}

	public void setTicker(Ticker ticker) {
		// TODO implement
	}

	public void setTitle(String s) {
		super.setTitle(s);
	}

	public int size() {
		return tf.size();
	}

	void hideNotify() {
		DeviceFactory.getDevice().getInputMethod().removeInputMethodListener(inputMethodListener);
		super.hideNotify();
	}

	int paintContent(Graphics g) {
		g.translate(0, viewPortY);
		g.drawRect(1, 1, getWidth() - 3, viewPortHeight - 3);
		g.setClip(3, 3, getWidth() - 6, viewPortHeight - 6);
		g.translate(3, 3);
		g.translate(0, -viewPortY);
		tf.paintContent(g);

		return tf.stringComponent.getHeight() + 6;
	}

	void setCaretPosition(int position) {
		tf.setCaretPosition(position);

		StringComponent tmp = tf.stringComponent;
		if (tmp.getCharPositionY(position) < viewPortY) {
			viewPortY = tmp.getCharPositionY(position);
		} else if (tmp.getCharPositionY(position) + tmp.getCharHeight() > viewPortY + viewPortHeight - 6) {
			viewPortY = tmp.getCharPositionY(position) + tmp.getCharHeight() - (viewPortHeight - 6);
		}
	}

	void showNotify() {
		super.showNotify();
		InputMethod inputMethod = DeviceFactory.getDevice().getInputMethod();
		inputMethod.setInputMethodListener(inputMethodListener);
		inputMethod.setMaxSize(getMaxSize());
		setCaretPosition(getString().length());
		tf.setCaretVisible(true);
	}

	int traverse(int gameKeyCode, int top, int bottom) {
		int traverse = tf.traverse(gameKeyCode, top, bottom, true);
		if (traverse == Item.OUTOFITEM) {
			return 0;
		} else {
			return traverse;
		}
	}

}
