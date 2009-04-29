/**
 *  MicroEmulator
 *  Copyright (C) 2009 Bartek Teodorczyk <barteo@barteo.net>
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
 *  @version $Id: AndroidChoiceGroupUI.java 1918 2009-01-21 12:56:43Z barteo $
 */

package org.microemu.android.device.ui;

import java.util.ArrayList;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ItemStateListener;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.ChoiceGroupUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AndroidChoiceGroupUI extends LinearLayout implements ChoiceGroupUI {

	private MicroEmulatorActivity activity;
	
	private ChoiceGroup choiceGroup;
	
	private int choiceType;
	
	private TextView labelView;
	
	private AndroidListAdapter listAdapter;
	
	private LinearLayout listView;
	
//	private RadioGroup radioGroup;
	
	public AndroidChoiceGroupUI(final MicroEmulatorActivity activity, final ChoiceGroup choiceGroup, final int choiceType) {
		super(activity);
		
		this.activity = activity;
		this.choiceGroup = choiceGroup;
		this.choiceType = choiceType;

		activity.post(new Runnable() {
			public void run() {
				setOrientation(LinearLayout.VERTICAL);
//				setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				labelView = new TextView(activity);
				labelView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				TypedArray a = labelView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				labelView.setTextAppearance(labelView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
				addView(labelView);
				
				listAdapter = new AndroidListAdapter();
//				listView.setAdapter(listAdapter);
//				listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				
				if (choiceType == Choice.EXCLUSIVE) {
					listView = new RadioGroup(activity);
				} else {
					listView = new AndroidListView(activity, AndroidChoiceGroupUI.this);
				}
				listView.setOrientation(LinearLayout.VERTICAL);
				addView(listView);
				
				setLabel(choiceGroup.getLabel());
			}
		});
	}

	public void setLabel(final String label) {
		activity.post(new Runnable() {
			public void run() {
				labelView.setText(label);
			}
		});
	}
	
	private int appendTransfer;

	public int append(final String stringPart, final Image imagePart) {
		if (activity.isActivityThread()) {
			appendTransfer = appendInternal(stringPart, imagePart);
		} else {
			appendTransfer = Integer.MIN_VALUE;
			activity.post(new Runnable() {
				public void run() {
					synchronized (AndroidChoiceGroupUI.this) {
						appendTransfer = appendInternal(stringPart, imagePart);
						AndroidChoiceGroupUI.this.notify();
					}
				}
			});

			synchronized (AndroidChoiceGroupUI.this) {
				if (appendTransfer == Integer.MIN_VALUE) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return appendTransfer;
	}

	private View createView(String stringPart, Image imagePart) {
		if (choiceType == Choice.EXCLUSIVE) {
			final RadioButton radioButton = new RadioButton(activity);
			radioButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					AndroidFormUI.AndroidListView formList = (AndroidFormUI.AndroidListView) getParent();
					if (formList != null) {
						ItemStateListener listener = formList.getUI().getItemStateListener();
						if (listener != null) {
							listener.itemStateChanged(choiceGroup);
						}
					}
				}
				
			});
			radioButton.setText(stringPart);
			return radioButton;
		} else if (choiceType == Choice.MULTIPLE) {
			CheckBox checkBox = new CheckBox(activity);
			checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					AndroidFormUI.AndroidListView formList = (AndroidFormUI.AndroidListView) getParent();
					if (formList != null) {
						ItemStateListener listener = formList.getUI().getItemStateListener();
						if (listener != null) {
							listener.itemStateChanged(choiceGroup);
						}
					}
				}
				
			});
			checkBox.setText(stringPart);
			return checkBox;
		} else if (choiceType == Choice.IMPLICIT) {
			TextView textView = new TextView(activity);
			textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

			textView.setText(stringPart);
			return textView;
		} else { // Choice.POPUP
System.out.println("Choice.POPUP not implemented yet");
			return null;
		}
	}
	
	private int appendInternal(String stringPart, Image imagePart) {
		View view = createView(stringPart, imagePart);
		listView.addView(view);
		return listAdapter.append(view);
	}
	
	public void setSelectedIndex(int elementNum, boolean selected) {
System.out.println("AndroidChoiceGroupUI.setSelectedIndex() not synced");		
		Object element = listAdapter.getItem(elementNum);
		((CompoundButton) element).setChecked(selected);
	}

	public int getSelectedIndex() {
		switch (choiceType) {
			case Choice.EXCLUSIVE:
			case Choice.POPUP:
				for (int i = 0; i < listAdapter.getCount(); ++i) {
					if (((CompoundButton) listAdapter.getItem(i)).isChecked()) {
						return i;
					}
				}
				break;
			case Choice.IMPLICIT:
System.out.println("Choice.IMPLICIT not implemented yet");
				return -1;
		}
		return -1;
	}

	public boolean isSelected(int elementNum) {
System.out.println("AndroidChoiceGroupUI.isSelected(..) not synced");		
		return ((CompoundButton) listAdapter.getItem(elementNum)).isChecked();
	}
	
	public void setSelectedFlags(boolean[] selectedArray) {
System.out.println("AndroidChoiceGroupUI.setSelectedFlags(..) not synced");		
		for (int i = 0; i < listAdapter.getCount(); ++i) {
			setSelectedIndex(i, selectedArray[i]);
		}
	}

	public int getSelectedFlags(boolean[] selectedArray) {
System.out.println("AndroidChoiceGroupUI.getSelectedFlags(..) not synced");		
		int selectedItemsCount = 0;
		
		for (int i = 0; i < selectedArray.length; ++i) {
			selectedArray[i] = (i < listAdapter.getCount()) ? isSelected(i) : false;
			if (selectedArray[i]) {
				++selectedItemsCount;
			}
		}
		
		return selectedItemsCount;
	}
	
	public void set(int elementNum, String stringPart, Image imagePart) {
System.out.println("AndroidChoiceGroupUI.set(..) not synced");
		View view = createView(stringPart, imagePart);
		listView.addView(view, elementNum);
		listAdapter.set(elementNum, view);
	}

	private class AndroidListAdapter extends BaseAdapter {
		
		ArrayList<View> objects = new ArrayList<View>();
		
		public int append(View item) {
			objects.add(item);
			notifyDataSetChanged();
			
			return objects.lastIndexOf(item);
		}
		
		public void set(int position, View item) {
			objects.set(position, item);
			notifyDataSetChanged();
		}
		
		public void deleteAll() {
			objects.clear();
			notifyDataSetChanged();
		}


		public int getCount() {
			return objects.size();
		}

		public Object getItem(int position) {
			return objects.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return (View) getItem(position);
		}
		
	}
	
	class AndroidListView extends LinearLayout {
		
		private AndroidChoiceGroupUI ui;

		public AndroidListView(Context context, AndroidChoiceGroupUI ui) {
			super(context);
			
			this.ui = ui;
		}
	
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			// TODO implement pointer events
			return super.onTouchEvent(ev);
		}
		
		public AndroidChoiceGroupUI getUI() {
			return ui;
		}
		
	}

}
