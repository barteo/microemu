package org.microemu.device.swt;

import org.eclipse.swt.graphics.Font;

public interface SwtFont extends org.microemu.device.impl.Font {
	
	Font getFont();

	void setAntialiasing(boolean antialiasing);
	
}
