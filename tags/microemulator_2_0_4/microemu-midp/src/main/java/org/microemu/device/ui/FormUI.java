package org.microemu.device.ui;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;

public interface FormUI extends DisplayableUI {

	int append(Image img);
	 
	int append(Item item);
	 
	int append(String str);
	 
	void delete(int itemNum);
	 
	void deleteAll();
	 
	void insert(int itemNum, Item item);

	void set(int itemNum, Item item);

}
