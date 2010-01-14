package org.microemu.android.device.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidImmutableImage;
import org.microemu.device.ui.CommandUI;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class AndroidCommandUI implements CommandUI {

	private MicroEmulatorActivity activity;
	
	private Command command;
	
	private Drawable drawable;

	public AndroidCommandUI(MicroEmulatorActivity activity, Command command) {
		this.activity = activity;
		this.command = command;
	}

	public Command getCommand() {
		return command;
	}

	public void setImage(Image image) {
		drawable = new BitmapDrawable(((AndroidImmutableImage) image).getBitmap());
	}
	
	public Drawable getDrawable() {
		return drawable;
	}

}
