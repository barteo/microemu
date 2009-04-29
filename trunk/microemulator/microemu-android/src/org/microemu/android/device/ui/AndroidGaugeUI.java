package org.microemu.android.device.ui;

import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.ItemStateListener;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.GaugeUI;

import android.content.res.TypedArray;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class AndroidGaugeUI extends LinearLayout implements GaugeUI {

	private MicroEmulatorActivity activity;
	
	private TextView labelView;

	private SeekBar seekBar;
	
	public AndroidGaugeUI(final MicroEmulatorActivity activity, final Gauge gauge) {
		super(activity);
		
		this.activity = activity;

		activity.post(new Runnable() {
			public void run() {
				setOrientation(LinearLayout.VERTICAL);
		//		setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				labelView = new TextView(activity);
				labelView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				TypedArray a = labelView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				labelView.setTextAppearance(labelView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
				addView(labelView);
				
				seekBar = new SeekBar(activity);
				seekBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
						AndroidFormUI.AndroidListView formList = (AndroidFormUI.AndroidListView) getParent();
						if (formList != null) {
							ItemStateListener listener = formList.getUI().getItemStateListener();
							if (listener != null) {
								listener.itemStateChanged(gauge);
							}
						}
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
					}
					
				});
				addView(seekBar);
				
				setLabel(gauge.getLabel());
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

	public void setValue(final int value) {
		activity.post(new Runnable() {
			public void run() {
				seekBar.setProgress(value);
			}
		});
	}
	
	private int getValueTransfer;

	public int getValue() {
		if (activity.isActivityThread()) {
			getValueTransfer = seekBar.getProgress();
		} else {
			getValueTransfer = Integer.MIN_VALUE;
			activity.post(new Runnable() {
				public void run() {
					synchronized (AndroidGaugeUI.this) {
						getValueTransfer = seekBar.getProgress();
						AndroidGaugeUI.this.notify();
					}
				}
			});

			synchronized (AndroidGaugeUI.this) {
				if (getValueTransfer == Integer.MIN_VALUE) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return getValueTransfer;
	}

	public void setMaxValue(final int maxValue) {
		activity.post(new Runnable() {
			public void run() {
				seekBar.setMax(maxValue);
			}
		});
	}

}
