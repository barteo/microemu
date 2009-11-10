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

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ItemStateListener;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.ChoiceGroupUI;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AndroidChoiceGroupUI extends LinearLayout implements ChoiceGroupUI {

	private MicroEmulatorActivity activity;
	
	private ChoiceGroup choiceGroup;
	
	private int choiceType;
	
	private TextView labelView;
	
	private AndroidListAdapterEx listAdapter;
	
	private ViewGroup listView;
	
	public AndroidChoiceGroupUI(final MicroEmulatorActivity activity, final ChoiceGroup choiceGroup, final int choiceType) {
		super(activity);
		
		this.activity = activity;
		this.choiceGroup = choiceGroup;
		this.choiceType = choiceType;
		
		this.listAdapter = new AndroidListAdapterEx(activity);

		activity.post(new Runnable() {
			public void run() {
				setOrientation(LinearLayout.VERTICAL);
//				setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				labelView = new TextView(activity);
				labelView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				labelView.setTextAppearance(labelView.getContext(), android.R.style.TextAppearance_Large);
				addView(labelView);
				
//				listView.setAdapter(listAdapter);
//				listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				
				if (choiceType == Choice.EXCLUSIVE) {
					listView = new RadioGroup(activity);
					((LinearLayout) listView).setOrientation(LinearLayout.VERTICAL);
				} else if (choiceType == Choice.POPUP) {
					listView = new AndroidPopupView(activity, AndroidChoiceGroupUI.this);
					((Spinner) listView).setAdapter(listAdapter);
				} else {
					listView = new AndroidListView(activity, AndroidChoiceGroupUI.this);
					((LinearLayout) listView).setOrientation(LinearLayout.VERTICAL);
				}
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
			TextView textView = new TextView(activity) {

				@Override
				public String toString() {
					return (String) getText();
				}

			};
			textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

			textView.setText(stringPart);
			return textView;
		}
	}
	
	public void delete(int elementNum) {
System.out.println("AndroidChoiceGroupUI.delete(..) not synced");
		if (listView instanceof LinearLayout) {
			listView.removeViewAt(elementNum);
		}
		listAdapter.remove(elementNum);
	}
	
	public void deleteAll() {
System.out.println("AndroidChoiceGroupUI.deleteAll() not synced");
				if (listView instanceof LinearLayout) {
					listView.removeAllViews();
				}
				listAdapter.clear();
			}

	public void setSelectedIndex(final int elementNum, final boolean selected) {
		activity.post(new Runnable() {
			public void run() {
				if (choiceType == Choice.POPUP) {
					if (selected) {
						((AdapterView) listView).setSelection(elementNum);
					}
				} else {
					Object element = listAdapter.getItem(elementNum);
					((CompoundButton) element).setChecked(selected);
				}
			}
		});
	}

	public int getSelectedIndex() {
		switch (choiceType) {
			case Choice.EXCLUSIVE:
				for (int i = 0; i < listAdapter.getCount(); ++i) {
					if (((CompoundButton) listAdapter.getItem(i)).isChecked()) {
						return i;
					}
				}
				break;
			case Choice.POPUP:
				return ((AdapterView) listView).getSelectedItemPosition();
			case Choice.IMPLICIT:
System.out.println("Choice.IMPLICIT not implemented yet");
				return -1;
		}
		return -1;
	}

	public void insert(final int elementNum, final String stringPart, final Image imagePart) {
		activity.post(new Runnable() {
			public void run() {
				synchronized (AndroidChoiceGroupUI.this) {
					View view = createView(stringPart, imagePart);
					if (listView instanceof LinearLayout) {
						listView.addView(view, elementNum);
					}
					listAdapter.insert(elementNum, view);
				}
			}
		});
	}
	
	public boolean isSelected(int elementNum) {
System.out.println("AndroidChoiceGroupUI.isSelected(..) not synced");
		if (choiceType == Choice.POPUP) {
			return elementNum == ((AdapterView) listView).getSelectedItemPosition();
		} else {
			return ((CompoundButton) listAdapter.getItem(elementNum)).isChecked();
		}
	}
	
	public void setSelectedFlags(boolean[] selectedArray) {
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
	
	public String getString(int elementNum) {
		if (choiceType == Choice.POPUP) {
			return listAdapter.getItem(elementNum).toString();
		} else {
			return (String) ((CompoundButton) listAdapter.getItem(elementNum)).getText();
		}
  }
	
	public void set(int elementNum, String stringPart, Image imagePart) {
System.out.println("AndroidChoiceGroupUI.set(..) not synced");
		View view = createView(stringPart, imagePart);
		if (listView instanceof LinearLayout) {
			listView.removeViewAt(elementNum);
			listView.addView(view, elementNum);
		}
		listAdapter.set(elementNum, view);
	}
	
	private int sizeTransfer;

	public int size() {
		sizeTransfer = Integer.MIN_VALUE;
		activity.post(new Runnable() {
			public void run() {
				synchronized (AndroidChoiceGroupUI.this) {
					sizeTransfer = listAdapter.getCount();
					AndroidChoiceGroupUI.this.notify();
				}
			}
		});

		synchronized (AndroidChoiceGroupUI.this) {
			if (sizeTransfer == Integer.MIN_VALUE) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		return sizeTransfer;
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

	private class AndroidListAdapterEx extends ArrayAdapter<View> {

		public AndroidListAdapterEx(Context context) {
			super(context, android.R.layout.simple_spinner_item);
			setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		}

		public void insert(int position, View item) {
			super.insert(item, position);
			notifyDataSetChanged();
		}

		public void remove(int position) {
			super.remove(super.getItem(position));
			notifyDataSetChanged();
		}

		public void set(int position, View item) {
			remove(position);
			insert(position, item);
			notifyDataSetChanged();
		}

	}

	class AndroidPopupView extends Spinner {

		private AndroidChoiceGroupUI ui;

		public AndroidPopupView(Context context, AndroidChoiceGroupUI ui) {
			super(context);

			this.ui = ui;
		}

		public AndroidChoiceGroupUI getUI() {
			return ui;
		}

	}

}
