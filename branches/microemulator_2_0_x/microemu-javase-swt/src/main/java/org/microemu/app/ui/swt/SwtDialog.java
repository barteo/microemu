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
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;


public abstract class SwtDialog
{
	public static final int OK = 0;
	public static final int CANCEL = 1;

	private Shell parentShell;
	private Shell shell;
	
	protected Control dialogArea;
	protected Control buttonBar;
	
	protected Button btOk;
	protected Button btCancel;

	private boolean resizeHasOccurred = false;
	private Listener resizeListener;
	private Control contents;
	private int shellStyle = SWT.SHELL_TRIM;
	private boolean block = false;
	private int returnCode = OK;


	public SwtDialog(Shell parentShell)
	{
		this.parentShell = parentShell;
		this.block = true;
		this.shellStyle = SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL;
	}
	
	
	public void create() 
	{
		shell = createShell();
		contents = createContents(shell);

		//initialize the bounds of the shell to that appropriate for the contents
		initializeBounds();
	}


	protected final Shell createShell() 
	{
		Shell newShell = new Shell(parentShell, shellStyle);

		resizeListener = new Listener() {
			public void handleEvent(Event e) {
				resizeHasOccurred = true;
			}
		};
	
		newShell.addListener(SWT.Resize,resizeListener);
		newShell.setData(this);

		newShell.addShellListener(getShellListener());

		configureShell(newShell);
		
		return newShell;
	}
	
	
	protected void configureShell(Shell newShell) 
	{
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		newShell.setLayout(layout);
	}


	protected Control createContents(Composite parent) 
	{
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());

		dialogArea = createDialogArea(composite);
		buttonBar = createButtonBar(composite);

		return composite;
	}


	protected Control createDialogArea(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());

		return composite;
	}
	

	protected Control createButtonBar(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		composite.setFont(parent.getFont());

		btOk = new Button(composite, SWT.PUSH);
		btOk.setText("OK");		
		btOk.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
				buttonPressed(OK);
			}
		});
		
		btCancel = new Button(composite, SWT.PUSH);
		btCancel.setText("Cancel");		
		btCancel.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
				buttonPressed(CANCEL);
			}
		});

		return composite;
	}
	

	protected void initializeBounds() 
	{
		if (resizeListener != null) {
			shell.removeListener(SWT.Resize,resizeListener);
		}
	
		if (resizeHasOccurred) { // Check if shell size has been set already.
			return;
		}

		Point size = getInitialSize();
		Point location = getInitialLocation(size);

		shell.setBounds(location.x, location.y, size.x, size.y);
	}
	
	
	protected Point getInitialSize() 
	{
		return shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
	}


	protected Point getInitialLocation(Point initialSize) 
	{
		Composite parentShell = shell.getParent();
		Rectangle containerBounds = (parentShell != null) ? parentShell.getBounds() : shell.getDisplay().getClientArea();
		int x = Math.max(0, containerBounds.x + (containerBounds.width - initialSize.x) / 2);
		int y = Math.max(0, containerBounds.y + (containerBounds.height - initialSize.y) / 3);
		return new Point(x, y);
	}
	
	
	protected void buttonPressed(int buttonId) 
	{
		if (buttonId == OK) {
			okPressed();
		} else if (buttonId == CANCEL) {
			cancelPressed();
		}
	}


	protected void okPressed() 
	{
		setReturnCode(OK);
		close();
	}
	
	
	protected void cancelPressed() {
		setReturnCode(CANCEL);
		close();
	}
	
	
	protected int getReturnCode() 
	{
		return returnCode;
	}
	
	
	protected void setReturnCode(int code) 
	{
		returnCode = code;
	}
	
	
	protected ShellListener getShellListener() 
	{
		return new ShellAdapter() 
		{
			public void shellClosed(ShellEvent event) 
			{
				event.doit= false;	// don't close now
				setReturnCode(CANCEL);
				close();
			}
		};
	}


	public boolean close() 
	{
		if (shell != null || !shell.isDisposed()) {
			shell.dispose();
			shell = null;
			contents = null;
		}

		buttonBar = null;
		dialogArea = null;

		return true;
	}


	public Shell getShell() 
	{
		return shell;
	}


	public int open() 
	{

		if (shell == null) {
			create();
		}

		shell.open();

		if (block) { 
			runEventLoop(shell);
		}	

		return returnCode;
	}


	private void runEventLoop(Shell shell) 
	{
		Display display;
		if(shell == null) {
			 display = Display.getCurrent();
		}	else {
			display = shell.getDisplay();
		}
		
		while (shell != null && ! shell.isDisposed()) {
			try {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} catch (Throwable ex) {
				System.err.println(ex);
			}
		}
		display.update();
	}

}

