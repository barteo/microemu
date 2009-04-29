/*
 *  MicroEmulator
 *  Copyright (C) 2001-2006 Bartek Teodorczyk <barteo@barteo.net>
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

import org.microemu.CustomItemAccess;
import org.microemu.device.DeviceFactory;

public class Spacer extends Item {
	
	private int minWidth;
	
	private int minHeight;
	
	private SpacerCustomItem customItem;
	
	public Spacer(int minWidth, int minHeight) {
		super(null);
		this.customItem = new SpacerCustomItem();
		super.setUI(DeviceFactory.getDevice().getUIFactory().createCustomItemUI(new CustomItemAccess() {

			public CustomItem getCustomItem() {
				return customItem;
			}

			public int getPrefContentWidth(int height) {
				return customItem.getPrefContentWidth(height);
			}
			
			public int getPrefContentHeight(int width) {
				return customItem.getPrefContentHeight(width);
			}

			public void paint(Graphics g, int w, int h) {
				customItem.paint(g, w, h);
			}

		}));
		setMinimumSize(minWidth, minHeight);
	}

	public void setLabel(String label) {
		throw new IllegalStateException("Spacer items can't have labels");
	}

	public void addCommand(Command cmd) {
		throw new IllegalStateException("Spacer items can't have commands");
	}
	
	public void setDefaultCommand(Command cmd) {
		throw new IllegalStateException("Spacer items can't have commands");
	}
	
	public void setMinimumSize(int minWidth, int minHeight) {
		if (minWidth < 0 || minHeight < 0) {
			throw new IllegalArgumentException();
		}
		
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}
	
	// Item methods
	int paint(Graphics g) {
		return 0;
	}
	
	private class SpacerCustomItem extends CustomItem {

		protected SpacerCustomItem() {
			super(null);
		}

		protected int getMinContentWidth() {
			return minWidth;
		}

		protected int getMinContentHeight() {
			return minHeight;
		}

		protected int getPrefContentWidth(int height) {
			return minWidth;
		}

		protected int getPrefContentHeight(int width) {
			return minHeight;
		}

		protected void paint(Graphics g, int w, int h) {
		}
		
	}

}
