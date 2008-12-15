package org.microemu.device.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

public interface CommandUI {

	Command getCommand();

	void setImage(Image image);

}
