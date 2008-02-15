package org.microemu.android.device.ui;

import java.util.ArrayList;

import javax.microedition.android.lcdui.Image;
import javax.microedition.android.lcdui.List;
import javax.microedition.android.lcdui.Command;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.ListUI;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidListUI extends AndroidDisplayableUI implements ListUI {

	private MicroEmulatorActivity activity;
	
	private List list;
	
	private Command selectCommand;
	
	private LinearLayout view;
	
	private TextView titleView;
	
	private AndroidListAdapter listAdapter;
	
	private AndroidListView listView;
	
	public AndroidListUI(final MicroEmulatorActivity activity, List list) {
		this.activity = activity;
		this.list = list;
		
		this.selectCommand = List.SELECT_COMMAND;
			
		activity.post(new Runnable() {
			public void run() {
				AndroidListUI.this.view = new LinearLayout(activity);
				AndroidListUI.this.view.setOrientation(LinearLayout.VERTICAL);
				AndroidListUI.this.view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				AndroidListUI.this.titleView = new TextView(activity);
				AndroidListUI.this.titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				AndroidListUI.this.view.addView(titleView);				
		
				AndroidListUI.this.listAdapter = new AndroidListAdapter();
				AndroidListUI.this.listView = new AndroidListView(activity);
				AndroidListUI.this.listView.setAdapter(AndroidListUI.this.listAdapter);
				AndroidListUI.this.listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));				
				AndroidListUI.this.view.addView(AndroidListUI.this.listView);		

				AndroidListUI.this.invalidate();
			}
		});		
	}
	
	//
	// DisplayableUI
	//
	
	public void hideNotify() {
	}

	public void showNotify() {
		activity.post(new Runnable() {
			public void run() {
				activity.setContentView(view);
				listView.requestFocus();
			}
		});
	}
	
	public void invalidate() {
		titleView.setText(list.getTitle());		
	}	

	//
	// ListUI
	//
	
	public int append(String stringPart, Image imagePart) {
		return listAdapter.append(stringPart);
	}
	
	public int getSelectedIndex() {
		return listView.getSelectedItemPosition();
	}

	public String getString(int elementNum) {
		return (String) listAdapter.getItem(elementNum);
	}

	public void setSelectCommand(Command command) {
		this.selectCommand = command;		
	}

	private class AndroidListAdapter extends BaseAdapter {
		
		ArrayList<String> objects = new ArrayList<String>();
		
		public int append(String stringPart) {
			objects.add(stringPart);
			// TODO API changed notifyChange();
			
			return objects.lastIndexOf(stringPart);
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
			if (convertView == null) {
				convertView = new TextView(activity);
			}
			
			((TextView) convertView).setText((String) getItem(position));
			
			return convertView;
		}
		
	}
	
	private class AndroidListView extends ListView {

		public AndroidListView(Context context) {
			super(context);
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				if (getCommandListener() != null) {
					getCommandListener().commandAction(selectCommand, list);
					return true;
				} else {				
					return false;
				}
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
	
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			// TODO implement pointer events
			return super.onTouchEvent(ev);
		}
		
	}
	
}
