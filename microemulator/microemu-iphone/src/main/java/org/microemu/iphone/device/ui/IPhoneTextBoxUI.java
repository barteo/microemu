package org.microemu.iphone.device.ui;

import javax.microedition.lcdui.TextBox;

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

public class IPhoneTextBoxUI extends AbstractUI implements TextBoxUI {

	private TextBox textBox;
	private UIView view;
	private UITextView textView;
	private UINavigationBar navigtionBar;

	public IPhoneTextBoxUI(MicroEmulator microEmulator, TextBox textBox) {
		super(microEmulator, textBox);
		this.textBox = textBox;
	}

	public int getCaretPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setString(String text) {
		// TODO Auto-generated method stub
	}

	public void hideNotify() {
		// TODO Auto-generated method stub

	}

	public void invalidate() {
		// TODO Auto-generated method stub

	}

	public void showNotify() {
		if (view == null) {
			view = new UIView().initWithFrame$(microEmulator.getWindow().bounds());

			navigtionBar = new UINavigationBar().initWithFrame$(new CGRect(0, 0,
					microEmulator.getWindow().bounds().size.width, TOOLBAR_HEIGHT));
			UINavigationItem title = new UINavigationItem().initWithTitle$(textBox.getTitle());
			title.setBackButtonTitle$("Done");
			navigtionBar.pushNavigationItem$(title);
			view.addSubview$(navigtionBar);
			navigtionBar.setDelegate$(new NSObject() {
				@SuppressWarnings("unused")
				@Message(name = "navigationBar:shouldPopItem:")
				public boolean navigationBar$shouldPopItem$(UINavigationBar bar, UINavigationItem item) {
					//Close Keyboard
					textView.resignFirstResponder();
					return true;
				}
			});

			textView = new UITextView() {
				@Override
				@Message(name = "becomeFirstResponder", types = "B8@0:4")
				public boolean becomeFirstResponder() {
					//Open Keybords and add a Done-Button
					UINavigationItem keyboardTitle = new UINavigationItem().initWithTitle$(textBox.getTitle());
					navigtionBar.pushNavigationItem$(keyboardTitle);
					return ((Byte) joc.Runtime.msgSend(this, incorrect_ ? null : UITextView.class,
							"becomeFirstResponder")) == Static.YES;
				}
			}.initWithFrame$(new CGRect(0, TOOLBAR_HEIGHT, microEmulator.getWindow().bounds().size.width, microEmulator
					.getWindow().bounds().size.height
					- TOOLBAR_HEIGHT - TOOLBAR_HEIGHT));
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
