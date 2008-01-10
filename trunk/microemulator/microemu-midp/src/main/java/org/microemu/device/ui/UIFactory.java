package org.microemu.device.ui;

public interface UIFactory {
	
	DisplayableUI createAlertUI();

	CanvasUI createCanvasUI();
	
	DisplayableUI createFormUI();

	DisplayableUI createListUI();
	
	TextBoxUI createTextBoxUI();

}
