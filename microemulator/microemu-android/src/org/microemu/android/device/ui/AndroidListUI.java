package org.microemu.android.device.ui;

import java.util.ArrayList;

import javax.microedition.android.lcdui.Image;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.ListUI;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidListUI extends AndroidDisplayableUI implements ListUI {

	private MicroEmulatorActivity activity;
	
	private LinearLayout view;
	
	private TextView titleView;
	
	private ListUIAdapter listAdapter;
	
	private ListView listView;
	
	public AndroidListUI(final MicroEmulatorActivity activity) {
		this.activity = activity;	
			
		activity.post(new Runnable() {
			public void run() {
				AndroidListUI.this.view = new LinearLayout(activity);
				AndroidListUI.this.view.setOrientation(LinearLayout.VERTICAL);
				AndroidListUI.this.view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				AndroidListUI.this.titleView = new TextView(activity);
				AndroidListUI.this.titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				AndroidListUI.this.view.addView(titleView);				
		
				AndroidListUI.this.listAdapter = new ListUIAdapter();
				AndroidListUI.this.listView = new ListView(activity);
				AndroidListUI.this.listView.setAdapter(AndroidListUI.this.listAdapter);
				AndroidListUI.this.listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				AndroidListUI.this.view.addView(AndroidListUI.this.listView);				
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
				listView.requestFocus();
			}
		});
	}

	//
	// ListUI
	//
	
	public int append(String stringPart, Image imagePart) {
		return listAdapter.append(stringPart);
	}
	
	class ListUIAdapter extends BaseAdapter {
		
		ArrayList<String> objects = new ArrayList<String>();
		
		public int append(String stringPart) {
			objects.add(stringPart);
			notifyChange();
			
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
	
}
