package org.microemu.device.j2se;

import java.awt.Font;

public interface J2SEFont extends org.microemu.device.impl.Font {

	Font getFont();
	
	void setAntialiasing(boolean antialiasing);
	
}
