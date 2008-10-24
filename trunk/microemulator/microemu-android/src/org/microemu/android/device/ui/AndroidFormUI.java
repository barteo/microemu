package org.microemu.android.device.ui;

import java.util.ArrayList;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.FormUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidFormUI extends AndroidDisplayableUI implements FormUI {

	private AndroidListAdapter listAdapter;
	
	private AndroidListView listView;
	
	public AndroidFormUI(final MicroEmulatorActivity activity, Form form) {
		super(activity, form);
		
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
		
				listAdapter = new AndroidListAdapter();
				listView = new AndroidListView(activity);
				listView.setAdapter(listAdapter);
				listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				((LinearLayout) AndroidFormUI.this.view).addView(listView);		

				invalidate();
			}
		});		
	}
	
	//
	// FormUI
	//
	
	public int append(Image img) {
		System.out.println("append(Image img)");
		return 0;
	}
	 
	public int append(Item item) {
		System.out.println("append(Item item)");
		return 0;
	}
	 
	public int append(String str) {
		System.out.println("append(String str)");
		return 0;
	}
	 
	public void delete(int itemNum) {
		System.out.println("delete(int itemNum)");
	}
	 
	public void deleteAll() {
		System.out.println("deleteAll()");
	}
	 
	public void insert(int itemNum, Item item) {
		System.out.println("insert(int itemNum, Item item)");
	}

	public void set(int itemNum, Item item) {
		System.out.println("set(int itemNum, Item item)");
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
		public boolean onTouchEvent(MotionEvent ev) {
			// TODO implement pointer events
			return super.onTouchEvent(ev);
		}
		
	}
	
}
