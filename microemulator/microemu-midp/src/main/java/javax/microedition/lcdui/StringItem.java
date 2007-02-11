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

public class StringItem extends Item {

	StringComponent stringComponent;

	public StringItem(String label, String text) {
		this(label, text, PLAIN);
	}
	
	public StringItem(String label, String text, int appearanceMode) {
		super(label);
		stringComponent = new StringComponent(text);
		// TODO apperanceMode
	}
	
	public int getAppearanceMode() {
    	// TODO implement
		return Item.PLAIN;
	}
	
	public Font getFont() {
    	// TODO implement
		return Font.getDefaultFont();
	}
	
	public void setFont(Font font) {
    	// TODO implement
	}

	public void setPreferredSize(int width, int height) {
    	// TODO implement
	}
	
	public String getText() {
		return stringComponent.getText();
	}

	public void setText(String text) {
		stringComponent.setText(text);
		repaint();
	}

	int getHeight() {
		return super.getHeight() + stringComponent.getHeight();
	}

	int paint(Graphics g) {
		super.paintContent(g);

		g.translate(0, super.getHeight());
		stringComponent.paint(g);
		g.translate(0, -super.getHeight());

		return getHeight();
	}

	int traverse(int gameKeyCode, int top, int bottom, boolean action) {
		Font f = Font.getDefaultFont();

		if (gameKeyCode == Canvas.UP) {
			if (top > 0) {
				if ((top % f.getHeight()) == 0) {
					return -f.getHeight();
				} else {
					return -(top % f.getHeight());
				}
			} else {
				return Item.OUTOFITEM;
			}
		}
		if (gameKeyCode == Canvas.DOWN) {
			if (bottom < getHeight()) {
				if (getHeight() - bottom < f.getHeight()) {
					return getHeight() - bottom;
				} else {
					return f.getHeight();
				}
			} else {
				return Item.OUTOFITEM;
			}
		}

		return 0;
	}

}
