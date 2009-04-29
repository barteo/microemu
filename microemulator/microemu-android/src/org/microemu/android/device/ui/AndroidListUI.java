package org.microemu.android.device.ui;

import java.util.ArrayList;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Command;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.ListUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidListUI extends AndroidDisplayableUI implements ListUI {

	private Command selectCommand;
	
	private AndroidListAdapter listAdapter;
	
	private AndroidListView listView;
	
	public AndroidListUI(final MicroEmulatorActivity activity, List list) {
		super(activity, list, true);
		
		this.selectCommand = List.SELECT_COMMAND;
			
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
	
	public int append(String stringPart, Image imagePart) {
		// TODO improve method that waits for for listAdapter being initialized
		while (listAdapter == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
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
			notifyDataSetChanged();
			
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
				TypedArray a = convertView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				((TextView) convertView).setTextAppearance(convertView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
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
			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
				if (getCommandListener() != null) {
					getCommandListener().commandAction(selectCommand, displayable);
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
