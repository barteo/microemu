package com.barteo.emulator.app.ui.awt;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

/**
 * @author barteo
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileChooser extends FileDialog 
{

	public FileChooser(Frame parent, String title, int mode)
	{
		super(parent, title, mode);
	}
	
	
	public File getSelectedFile()
	{
		return new File(getDirectory(), getFile());
	}
	
}
