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

import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.TextBoxUI;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
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
				editView.setGravity(Gravity.TOP);
				editView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				int constraints = textBox.getConstraints();
				if ((constraints & TextField.CONSTRAINT_MASK) == TextField.URL) {
					editView.setSingleLine(true);
				} else if ((constraints & TextField.CONSTRAINT_MASK) == TextField.NUMERIC) {
					editView.setSingleLine(true);
					editView.setInputType(InputType.TYPE_CLASS_NUMBER);
				} else if ((constraints & TextField.CONSTRAINT_MASK) == TextField.DECIMAL) {
					editView.setSingleLine(true);
					editView.setInputType(
							InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                } else if ((constraints & TextField.CONSTRAINT_MASK) == TextField.PHONENUMBER) {
                    editView.setSingleLine(true);
                    editView.setInputType(InputType.TYPE_CLASS_PHONE);
				}
				if ((constraints & TextField.PASSWORD) != 0) {
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
	
	private String getStringTransfer;

	public String getString() {
		if (activity.isActivityThread()) {
			getStringTransfer = editView.getText().toString();
		} else {
			getStringTransfer = null;
			activity.post(new Runnable() {
				public void run() {
					synchronized (AndroidTextBoxUI.this) {
						getStringTransfer = editView.getText().toString();
						AndroidTextBoxUI.this.notify();
					}
				}
			});

			synchronized (AndroidTextBoxUI.this) {
				if (getStringTransfer == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return getStringTransfer;
	}

	public void setString(final String text) {
		activity.post(new Runnable() {
			public void run() {
				editView.setText(text);
				editView.setSelection(text.length());
			}
		});
	}

	public void insert(final String text, final int position) {
		activity.post(new Runnable() {
			public void run() {
				String newtext = text;
				if (position > 0) {
					newtext += getString().substring(position + 1);
				}
				editView.setText(newtext);
				editView.setSelection(newtext.length());
			}
		});
	}

}
