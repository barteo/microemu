/**
 *  MicroEmulator
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
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
 *  @version $Id$
 */
package org.microemu.iphone.device.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;

import org.microemu.device.ui.CanvasUI;
import org.microemu.device.ui.FormUI;
import org.microemu.iphone.MicroEmulator;

public class IPhoneFormUI extends AbstractUI<Form> implements FormUI {

	private IPhoneCanvasUI canvasUI;

	public IPhoneFormUI(MicroEmulator microEmulator, Form form) {
		super(microEmulator, form);

		canvasUI = new IPhoneCanvasUI(microEmulator, new Canvas() {
			
			@Override
			protected void paint(Graphics g) {
				try {
					Method formPaint = Form.class.getMethod("paint", Graphics.class);
					formPaint.setAccessible(true);
					formPaint.invoke(displayable, g);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}){@Override
		protected void updateToolbar() {
			IPhoneFormUI.this.toolbar=this.toolbar;
			IPhoneFormUI.this.updateToolbar();
		}};
	}

	public int append(Image img) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int append(Item item) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int append(String str) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void delete(int itemNum) {
		// TODO Auto-generated method stub

	}

	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	public void insert(int itemNum, Item item) {
		// TODO Auto-generated method stub

	}

	public void set(int itemNum, Item item) {
		// TODO Auto-generated method stub

	}

	public void hideNotify() {
		canvasUI.hideNotify();
	}

	public void invalidate() {
		canvasUI.invalidate();
	}

	public void showNotify() {
		canvasUI.showNotify();
	}

}
