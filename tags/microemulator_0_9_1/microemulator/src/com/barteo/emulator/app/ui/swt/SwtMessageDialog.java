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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class SwtMessageDialog extends SwtDialog 
{
	public final static int ERROR = 1;
	public final static int INFORMATION = 2;
	public final static int QUESTION = 3;
	
	private String title;
	private String message;
	private String[] buttonLabels;
	private int defaultIndex;


	public SwtMessageDialog(Shell parentShell, String title, String message, int imageType, String[] buttonLabels, int defaultIndex) 
	{
		super(parentShell);
		
		this.title = title;
		this.message = message;
		this.buttonLabels = buttonLabels;
		this.defaultIndex = defaultIndex;
	}


	public static void openError(Shell parent, String title, String message) 
	{
		SwtMessageDialog dialog = 
				new SwtMessageDialog(parent, title, message, ERROR, new String[] {"OK"}, 0);
		dialog.open();
	}
	
	
	public static void openInformation(Shell parent, String title, String message) 
	{
		SwtMessageDialog dialog = 
				new SwtMessageDialog(parent, title, message, INFORMATION, new String[] {"OK"}, 0);
		dialog.open();	
	}
	
	
	public static boolean openQuestion(Shell parent, String title, String message) 
	{
		SwtMessageDialog dialog = 
				new SwtMessageDialog(parent, title, message, QUESTION, new String[] {"Yes", "No"}, 0);
		return dialog.open() == 0;
	}
	


	protected void configureShell(Shell shell) 
	{
		super.configureShell(shell);
		
		if (title != null) {
			shell.setText(title);
		}
	}
	
	
	protected Control createDialogArea(Composite composite) 
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);

		Label lbMessage = new Label(composite, SWT.NONE);
		lbMessage.setText(message);
		lbMessage.setLayoutData(new GridData(GridData.FILL_BOTH));

		return composite;
	}


	protected Control createButtonBar(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout(buttonLabels.length, false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		composite.setFont(parent.getFont());

		for (int i = 0; i < buttonLabels.length; i++) {
			Button button = new Button(composite, SWT.PUSH);
			button.setText(buttonLabels[i]);
			button.setData(new Integer(i));
			button.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent event) 
				{
					buttonPressed(((Integer) event.widget.getData()).intValue());
				}
			});
			
			if (i == defaultIndex) {
				Shell shell = parent.getShell();
				if (shell != null) {
					shell.setDefaultButton(button);
				}
			}
		}

		return composite;
	}


	protected void buttonPressed(int buttonId) 
	{
		setReturnCode(buttonId);
		close();
	}
	

}
