/*
 *  MicroEmulator
 *  Copyright (C) 2002-2003 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */
 
package org.microemu.app.ui.swt;

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
	public final static int WARNING = 2;
	public final static int INFORMATION = 3;
	public final static int QUESTION = 4;
	
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


	public static void openMessageDialog(Shell parent, String title, String message, int messageType) 
	{
		SwtMessageDialog dialog = 
				new SwtMessageDialog(parent, title, message, messageType, new String[] {"OK"}, 0);
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
