package org.microemu.device.ui;

import javax.microedition.lcdui.Image;

public interface ChoiceGroupUI extends ItemUI {

	int append(String stringPart, Image imagePart);

	void setSelectedIndex(int elementNum, boolean selected);

	int getSelectedIndex();

	boolean isSelected(int elementNum);

	void setSelectedFlags(boolean[] selectedArray);

	int getSelectedFlags(boolean[] selectedArray);

	void set(int elementNum, String stringPart, Image imagePart);

}
