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
import org.microemu.device.InputMethod;
import org.microemu.device.ui.CommandUI;
import org.microemu.device.ui.TextBoxUI;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class AndroidTextBoxUI extends AndroidDisplayableUI implements TextBoxUI {
	
	private EditText editView;
	
	public AndroidTextBoxUI(final MicroEmulatorActivity activity, final TextBox textBox) {		
		super(activity, textBox, true);		
		
		activity.post(new Runnable() {
			public void run() {
				editView = new EditText(activity) {

					@Override
					public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
						Configuration conf = Resources.getSystem().getConfiguration();
						if (conf.hardKeyboardHidden != Configuration.HARDKEYBOARDHIDDEN_NO) {
							InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(this, 0);
						}
						
						return super.onCreateInputConnection(outAttrs);
					}
					
				};
				editView.setText(textBox.getString());
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
				editView.addTextChangedListener(new TextWatcher() {

					private String previousText;
					
					public void afterTextChanged(Editable s) {
					}

					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						previousText = s.toString();
					}

					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (s.toString().length() <= textBox.getMaxSize()
								&& InputMethod.validate(s.toString(), textBox.getConstraints())) {
						} else {
							editView.setText(previousText);
							editView.setSelection(start);
						}
					}

				});
				((LinearLayout) view).addView(editView);
				
				invalidate();
			}
		});		
	}
	
	@Override
	public void hideNotify() {
		activity.post(new Runnable() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
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
