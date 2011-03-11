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
 *  @version $Id$
 */

package org.microemu.android.device.ui;

import java.util.ArrayList;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Command;

import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.ListUI;
import org.microemu.android.device.AndroidImmutableImage;
import org.microemu.android.device.AndroidMutableImage;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ImageView;

public class AndroidListUI extends AndroidDisplayableUI implements ListUI {

	private Command selectCommand;
	
	private AndroidListAdapter listAdapter;
	
	private AndroidListView listView;

	private int selectedPosition;
	
	public AndroidListUI(final MicroEmulatorActivity activity, List list) {
		super(activity, list, true);
		
		this.selectCommand = List.SELECT_COMMAND;
		this.selectedPosition = AdapterView.INVALID_POSITION;
			
		activity.post(new Runnable() {
			public void run() {
				listAdapter = new AndroidListAdapter();
				listView = new AndroidListView(activity);
				listView.setAdapter(listAdapter);
				listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				((LinearLayout) AndroidListUI.this.view).addView(listView);		

				invalidate();
			}
		});		
	}
	
	//
	// ListUI
	//
	
	private int appendTransfer;
	
	public int append(final String stringPart, final Image imagePart) {
		if (activity.isActivityThread()) {
			appendTransfer = listAdapter.append(stringPart, imagePart);
		} else {
			appendTransfer = Integer.MIN_VALUE;
			activity.post(new Runnable() {
				public void run() {
					synchronized (AndroidListUI.this) {
						appendTransfer = listAdapter.append(stringPart, imagePart);
						AndroidListUI.this.notify();
					}
				}
			});

			synchronized (AndroidListUI.this) {
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

	public int getSelectedIndex() {
		int index = selectedPosition;
		if (index == AdapterView.INVALID_POSITION) {
			index = listView.getSelectedItemPosition();
			if (index == AdapterView.INVALID_POSITION) {
				index = -1;
			}
		}
		return index;
	}

	public String getString(int elementNum) {
		return listAdapter.getItem(elementNum).text;
	}

	public void setSelectCommand(Command command) {
		this.selectCommand = command;		
	}
	
	private String deleteException;

	public void delete(final int elementNum) {
		if (activity.isActivityThread()) {
			listAdapter.delete(elementNum);
		} else {
			deleteException = null;
			activity.post(new Runnable() {
				public void run() {
					synchronized (AndroidListUI.this) {
						try {
							listAdapter.delete(elementNum);
							deleteException = "";
						} catch (IndexOutOfBoundsException e) {
							String message = e.getMessage();
							if (message == null) {
								deleteException = e.toString();
							} else {
								deleteException = message;
							}
						}
						AndroidListUI.this.notify();
					}
				}
			});

			synchronized (AndroidListUI.this) {
				if (deleteException == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (deleteException.length() > 0) {
				throw new IndexOutOfBoundsException(deleteException);
			}
		}
	}

	public void deleteAll() {
		activity.post(new Runnable() {
			public void run() {
				listAdapter.deleteAll();
			}
		});
	}

	public void setSelectedIndex(final int elementNum, boolean selected) {
		if (selected) { // TODO if not???
			activity.post(new Runnable() {
				public void run() {
					listView.setSelection(elementNum);
				}
			});
		}
	}
	
	public void set(final int elementNum, final String stringPart, final Image imagePart) {
		activity.post(new Runnable() {
			public void run() {
				listAdapter.set(elementNum, stringPart, imagePart);
			}
		});
	}

	public int size() {
		return listAdapter.getCount();
	}

	private class AndroidListAdapter extends BaseAdapter {

		private class ViewHolder
		{
			Object image;
			String text;
		}

		private ArrayList<ViewHolder> objects = new ArrayList<ViewHolder>();

		public int append(String stringPart, Object image) {
			ViewHolder vh = new ViewHolder();
			vh.image = image;
			vh.text = stringPart;
			objects.add(vh);
			
			activity.post(new Runnable() {
				public void run() {
					notifyDataSetChanged();
				}
			});
			
			return objects.lastIndexOf(vh);
		}
		
		public void set(int elementNum, String stringPart, Image imagePart) {
			ViewHolder vh = getItem(elementNum);
			vh.image = imagePart;
			vh.text = stringPart;
		}

		public int getCount() {
			return objects.size();
		}

		public ViewHolder getItem(int position) {
			return objects.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				//TODO figure out a better layout - see example of different image and text sizes.
				LinearLayout layout = new LinearLayout(activity);
				layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.FILL_HORIZONTAL);

				ImageView iv = new ImageView(activity);
				TextView tv = new TextView(activity);

				layout.addView(iv);
				layout.addView(tv);

				convertView = layout;

				tv.setTextAppearance(convertView.getContext(), android.R.style.TextAppearance_Large);
			}

			LinearLayout ll = (LinearLayout)convertView;

			ViewHolder vh = (ViewHolder) getItem(position);

			TextView tv = (TextView) ll.getChildAt(1);
			tv.setText(vh.text);
			
			if (vh.image != null) {
				ImageView iv = (ImageView) ll.getChildAt(0);

				Image img = (Image) vh.image;
				if (img.isMutable())
					iv.setImageBitmap(((AndroidMutableImage) img).getBitmap());
				else
					iv.setImageBitmap(((AndroidImmutableImage) img).getBitmap());
			}

			return convertView;
		}
		
		public void delete(int elementNum) {
			objects.remove(elementNum);		
			
			activity.post(new Runnable() {
				public void run() {
					notifyDataSetChanged();
				}
			});
		}

		public void deleteAll() {
			objects.clear();
			
			activity.post(new Runnable() {
				public void run() {
					notifyDataSetChanged();
				}
			});
		}
	}
	
	private class AndroidListView extends ListView
			implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener  {

		public AndroidListView(Context context) {
			super(context);
			super.setClickable(true);
			super.setOnItemClickListener(this);
			super.setOnItemSelectedListener(this);
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
				if (getCommandListener() != null) {
					MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(selectCommand, displayable);
					return true;
				} else {				
					return false;
				}
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectedPosition = position;
			MIDletBridge.getMIDletAccess().getDisplayAccess().commandAction(selectCommand, displayable);
		}

		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			selectedPosition = position;
		}

		public void onNothingSelected(AdapterView<?> parent) {
			selectedPosition = AdapterView.INVALID_POSITION;
		}
	}
	
}
