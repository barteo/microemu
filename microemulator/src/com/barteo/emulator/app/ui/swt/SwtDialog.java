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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

	private boolean resizeHasOccurred = false;
	private Listener resizeListener;
	private Control contents;
	private int shellStyle = SWT.SHELL_TRIM;
	private boolean block = false;
	private int returnCode = OK;


	public SwtDialog(Shell parentShell)
	{
		this.parentShell = parentShell;
		
/*		final Dialog dialog = new Dialog(new Frame(), title, true);
		dialog.setLayout(new BorderLayout());
		dialog.add(panel, BorderLayout.CENTER);

		Panel actionPanel = new Panel();
		actionPanel.add(panel.btOk);
		actionPanel.add(panel.btCancel);
		dialog.add(actionPanel, BorderLayout.SOUTH);

		dialog.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = dialog.getSize();
		dialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

		ActionListener closeListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (event.getSource() == panel.btOk) {
					if (panel.check(true)) {
						panel.state = true;
						dialog.setVisible(false);
						panel.hideNotify();
					}
				} else {
					panel.state = false;
					dialog.setVisible(false);
					panel.hideNotify();
				}
			}
		};
    
		WindowAdapter windowAdapter = new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				panel.state = false;
				panel.hideNotify();
			}
		};

		dialog.addWindowListener(windowAdapter);
		panel.btOk.addActionListener(closeListener);
		panel.btCancel.addActionListener(closeListener);
		panel.showNotify();
		dialog.setVisible(true);
		panel.btOk.removeActionListener(closeListener);
		panel.btCancel.removeActionListener(closeListener);

		return panel.state;*/
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

