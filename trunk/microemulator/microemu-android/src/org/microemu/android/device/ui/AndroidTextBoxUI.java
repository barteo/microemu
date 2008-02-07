/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
 *
 *  @version $Id$
 */

package org.microemu.android.device.ui;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.TextBoxUI;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidTextBoxUI extends AndroidDisplayableUI implements TextBoxUI {
	
	private MicroEmulatorActivity activity;
	
	private LinearLayout view;
	
	private TextView titleView;
	
	private EditText editView;
	
	public AndroidTextBoxUI(final MicroEmulatorActivity activity) {
		this.activity = activity;		
		
		activity.post(new Runnable() {
			public void run() {
				AndroidTextBoxUI.this.view = new LinearLayout(activity);
				AndroidTextBoxUI.this.view.setOrientation(LinearLayout.VERTICAL);
				AndroidTextBoxUI.this.view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				titleView = new TextView(activity);
				titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				AndroidTextBoxUI.this.view.addView(titleView);
				
				editView = new EditText(activity);
				editView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				AndroidTextBoxUI.this.view.addView(editView);
			}
		});		
	}

	//
	// DisplayableUI
	//
	
	@Override
	public void setTitle(final String title) {
		super.setTitle(title);
		
		// TODO improve method that waits for for view being initialized
		while (titleView == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		titleView.setText(title);		
	}

	public void hideNotify() {
	}

	public void showNotify() {
		activity.post(new Runnable() {
			public void run() {
				activity.setContentView(view);
				editView.requestFocus();
			}
		});
	}
	
	//
	// TextBoxUI
	//
	
	public int getCaretPosition() {
		return editView.getSelectionStart();
	}
	
	public String getString() {
		return editView.getText().toString();
	}

	public void setString(String text) {
		editView.setText(text);
		editView.setSelection(text.length());
	}

}
