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

import java.lang.reflect.Field;

import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import joc.Message;
import joc.Static;
import obc.CGRect;
import obc.NSObject;
import obc.UINavigationBar;
import obc.UINavigationItem;
import obc.UITextView;
import obc.UIToolbar;
import obc.UIView;

import org.microemu.device.ui.TextBoxUI;
import org.microemu.iphone.MicroEmulator;

public class IPhoneTextBoxUI extends AbstractUI<TextBox> implements TextBoxUI {

	private final class TextBoxField extends TextField {
		public TextBoxField(TextField textField) {
			super(textField.getLabel(), textField.getString(), textField.getMaxSize(), textField.getConstraints());
		}
		
		@Override
		public void setString(String text) {
			super.setString(text);
			if(textView!=null)
				textView.setText$(text);
		}
		
		@Override
		public int getCaretPosition() {
			if(textView!=null)
				return textView.selectedRange().location;
			else
				return 0;
		}

		public void setStringSilent(String text) {
			super.setString(text);
		}
	}

	private TextBoxField textField;
	private UIView view;
	private UITextView textView;
	private UINavigationBar navigtionBar;

	public IPhoneTextBoxUI(MicroEmulator microEmulator, TextBox textBox) {
		super(microEmulator, textBox);

		try {
			Field tf = TextBox.class.getDeclaredField("tf");
			tf.setAccessible(true);
			textField = new TextBoxField((TextField) tf.get(textBox));
			tf.set(textBox, textField);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public int getCaretPosition() {
		return textField.getCaretPosition();
	}

	public String getString() {
		return textField.getString();
	}

	public void setString(String text) {
		textField.setString(text);
	}

	public void hideNotify() {
		// TODO Auto-generated method stub

	}

	public void invalidate() {
		// TODO Auto-generated method stub

	}

	public void showNotify() {
		System.out.println("IPhoneTextBoxUI.showNotify()");
		if (view == null) {
			view = new UIView().initWithFrame$(microEmulator.getWindow().bounds());

			navigtionBar = new UINavigationBar().initWithFrame$(new CGRect(0, 0,
					microEmulator.getWindow().bounds().size.width, NAVIGATION_HEIGHT));
			UINavigationItem title = new UINavigationItem().initWithTitle$(displayable.getTitle());
			title.setBackButtonTitle$("Done");
			navigtionBar.pushNavigationItem$(title);
			view.addSubview$(navigtionBar);
			navigtionBar.setDelegate$(new NSObject() {
				@SuppressWarnings("unused")
				@Message(name = "navigationBar:shouldPopItem:")
				public boolean navigationBar$shouldPopItem$(UINavigationBar bar, UINavigationItem item) {
					// Close Keyboard
					textView.resignFirstResponder();
					return true;
				}
			});

			textView = new UITextView() {
				@Override
				@Message(name = "becomeFirstResponder", types = "B8@0:4")
				public boolean becomeFirstResponder() {
					// Open Keybords and add a Done-Button
					UINavigationItem keyboardTitle = new UINavigationItem().initWithTitle$(displayable.getTitle());
					navigtionBar.pushNavigationItem$(keyboardTitle);
					return ((Byte) joc.Runtime.msgSend(this, incorrect_ ? null : UITextView.class,
							"becomeFirstResponder")) == Static.YES;
				}
			}.initWithFrame$(new CGRect(0, NAVIGATION_HEIGHT, microEmulator.getWindow().bounds().size.width, microEmulator
					.getWindow().bounds().size.height
					- NAVIGATION_HEIGHT - TOOLBAR_HEIGHT));
			textView.setText$(textField.getString());
			textView.setDelegate$(new NSObject(){
				@SuppressWarnings("unused")
				@Message(name="textViewDidChange:")
				public void textViewDidChange$(UITextView textView) {
				textField.setStringSilent(textView.text().toString());

			}});
			view.addSubview$(textView);

			toolbar = (UIToolbar) new UIToolbar().initWithFrame$(new CGRect(0,
					microEmulator.getWindow().bounds().size.height - TOOLBAR_HEIGHT,
					microEmulator.getWindow().bounds().size.width, TOOLBAR_HEIGHT));
			view.addSubview$(toolbar);
			updateToolbar();
		}

		view.retain();
		microEmulator.getWindow().addSubview$(view);
	}

}
