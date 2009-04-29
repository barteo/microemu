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

package javax.microedition.lcdui;

import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.FormUI;
import org.microemu.device.ui.ImageStringItemUI;

public class StringItem extends Item {

	private StringComponent stringComponent;

	public StringItem(String label, String text) {
		this(label, text, PLAIN);
	}
	
	public StringItem(String label, String text, int appearanceMode) {
		super(label);
		super.setUI(DeviceFactory.getDevice().getUIFactory().createImageStringItemUI(this));
		
		stringComponent = new StringComponent();
		setText(text);
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
		if (ui.getClass().getName().equals("org.microemu.android.device.ui.AndroidImageStringItemUI")) {
			((ImageStringItemUI) ui).setText(text);
		}

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
