/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.android.device.ui;

import java.util.List;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.TextBoxUI;

import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidTextBoxUI extends AndroidDisplayableUI implements TextBoxUI {
	
	private EditText editView;
	
	public AndroidTextBoxUI(final MicroEmulatorActivity activity, final TextBox textBox) {		
		super(activity, textBox);		
		
		activity.post(new Runnable() {
			public void run() {
				view = new LinearLayout(activity);
				((LinearLayout) view).setOrientation(LinearLayout.VERTICAL);
				view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				titleView = new TextView(activity);
				titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				TypedArray a = titleView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				titleView.setTextAppearance(titleView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
				((LinearLayout) view).addView(titleView);
				
				editView = new EditText(activity);
				editView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				if ((textBox.getConstraints() & TextField.URL) != 0) {
					editView.setSingleLine(true);
					editView.setOnClickListener(new View.OnClickListener() {
	
						public void onClick(View v) {
							List<AndroidCommandUI> commands = getCommandsUI();
							for (int i = 0; i < commands.size(); i++) {
								CommandUI cmd = commands.get(i);
								if (cmd.getCommand().getCommandType() == Command.OK) {
									CommandListener l = getCommandListener();
									l.commandAction(cmd.getCommand(), displayable);
									break;
								}
							}			
						}
						
					});
				}
				if ((textBox.getConstraints() & TextField.PASSWORD) != 0) {
					editView.setTransformationMethod(PasswordTransformationMethod.getInstance());
					editView.setTypeface(Typeface.MONOSPACE);
				}
				((LinearLayout) view).addView(editView);
				
				invalidate();
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
