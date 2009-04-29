package org.microemu.device.ui;

import java.util.Date;

public interface DateFieldUI extends ItemUI {

	void setInputMode(int mode);

	void setDate(Date date);

	Date getDate();

}
