package org.microemu.android.device.ui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ItemStateListener;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.FormUI;
import org.microemu.device.ui.ItemUI;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class AndroidFormUI extends AndroidDisplayableUI implements FormUI {

	private AndroidListView listView;
	
	private ItemStateListener itemStateListener;
	
	public AndroidFormUI(final MicroEmulatorActivity activity, Form form) {
		super(activity, form, true);
		
		activity.post(new Runnable() {
			public void run() {
				ScrollView scrollView = new ScrollView(activity);
				((LinearLayout) AndroidFormUI.this.view).addView(scrollView);
				listView = new AndroidListView(activity, AndroidFormUI.this);
				listView.setOrientation(LinearLayout.VERTICAL);
				listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				scrollView.addView(listView);

				invalidate();
			}
		});		
	}
	
	//
	// FormUI
	//
	
	private int appendTransfer;

	public int append(final ItemUI item) {
		if (activity.isActivityThread()) {
			listView.addView((View) item);
			appendTransfer = listView.getChildCount() - 1;
		} else {
			appendTransfer = Integer.MIN_VALUE;
			activity.post(new Runnable() {
				public void run() {
					synchronized (AndroidFormUI.this) {
						listView.addView((View) item);						
						appendTransfer = listView.getChildCount() - 1;
						AndroidFormUI.this.notify();
					}
				}
			});

			synchronized (AndroidFormUI.this) {
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
	 
	public void delete(int itemNum) {
		System.out.println("delete(int itemNum)");
	}
	 
	public void deleteAll() {
		activity.post(new Runnable() {
			public void run() {
				listView.removeAllViews();				
			}
		});
	}
	 
	public void insert(int itemNum, ItemUI item) {
		System.out.println("insert(int itemNum, Item item)");
	}

	public void set(int itemNum, ItemUI item) {
		System.out.println("set(int itemNum, Item item)");
	}
	
	public void setItemStateListener(ItemStateListener itemStateListener) {
		this.itemStateListener = itemStateListener;
	}
	
	public ItemStateListener getItemStateListener() {
		return itemStateListener;
	}
	
	class AndroidListView extends LinearLayout {
		
		private AndroidFormUI ui;

		public AndroidListView(Context context, AndroidFormUI ui) {
			super(context);
			
			this.ui = ui;
		}
	
		public AndroidFormUI getUI() {
			return ui;
		}
		
	}

}
