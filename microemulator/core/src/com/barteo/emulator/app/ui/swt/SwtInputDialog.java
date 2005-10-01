/*
 *  MicroEmulator
 *  Copyright (C) 2002-2003 Bartek Teodorczyk <barteo@barteo.net>
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class SwtInputDialog extends SwtDialog 
{
	private String title;
	private String message;
	private String value;
	

	public SwtInputDialog(Shell parentShell, String title, String message)
	{
		super(parentShell);
		
		this.title = title;
		this.message = message;
	}


	public String getValue() 
	{
		return value;
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
		
		final Text txInput = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gridData = 
				new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		gridData.widthHint = txInput.getLineHeight() * 15;
		txInput.setLayoutData(gridData);
		txInput.addModifyListener(new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				value = txInput.getText();
			}
		});
		
		return composite;
	}
	
}
