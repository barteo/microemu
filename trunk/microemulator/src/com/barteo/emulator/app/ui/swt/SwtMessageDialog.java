/*
 *  MicroEmulator
 *  Copyright (C) 2002-2003 Bartek Teodorczyk <barteo@it.pl>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
package com.barteo.emulator.app.ui.swt;

import org.eclipse.swt.widgets.Shell;


public class SwtMessageDialog extends SwtDialog 
{
	public final static int ERROR = 1;
	public final static int INFORMATION = 2;
	
	private String title;


	public SwtMessageDialog(Shell parentShell, String title, String message, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) 
	{
		super(parentShell);
		
		this.title = title;
		// TODO Auto-generated constructor stub
	}


	public static void openError(Shell parent, String title, String message) 
	{
		SwtMessageDialog dialog = 
				new SwtMessageDialog(parent, title, message, ERROR, new String[] {"OK"}, 0);
System.out.println(message);				
		dialog.open();
	}
	
	
	public static void openInformation(Shell parent, String title, String message) 
	{
		SwtMessageDialog dialog = 
				new SwtMessageDialog(parent, title, message, INFORMATION, new String[] {"OK"}, 0);
System.out.println(message);				
		dialog.open();
	
	}


	protected void configureShell(Shell shell) 
	{
		super.configureShell(shell);
		
		if (title != null) {
			shell.setText(title);
		}
	}

}
