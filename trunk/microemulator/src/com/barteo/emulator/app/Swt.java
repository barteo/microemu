/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.app;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.microedition.midlet.MIDlet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.app.launcher.Launcher;
import com.barteo.emulator.app.ui.ResponseInterfaceListener;
import com.barteo.emulator.app.ui.StatusBarListener;
import com.barteo.emulator.app.ui.swt.SwtDeviceComponent;
import com.barteo.emulator.app.ui.swt.SwtDialog;
import com.barteo.emulator.app.ui.swt.SwtInputDialog;
import com.barteo.emulator.app.ui.swt.SwtMessageDialog;
import com.barteo.emulator.app.ui.swt.SwtSelectDeviceDialog;
import com.barteo.emulator.app.util.DeviceEntry;
import com.barteo.emulator.app.util.ProgressJarClassLoader;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.swt.SwtDevice;


public class Swt extends Common
{
	public static Shell shell;

	private static SwtDeviceComponent devicePanel;

	Swt instance = null;
    
	boolean initialized = false;
  
	SwtSelectDeviceDialog selectDeviceDialog;
	FileDialog fileDialog = null;
	MenuItem menuOpenJADFile;
	MenuItem menuOpenJADURL;
	MenuItem menuSelectDevice;
	    
	DeviceEntry deviceEntry;

	Label statusBar;
  
	KeyListener keyListener = new KeyListener()
	{    
		public void keyTyped(KeyEvent e)
		{
		}
    
		public void keyPressed(KeyEvent e)
		{
//			devicePanel.keyPressed(e);
		}
    
		public void keyReleased(KeyEvent e)
		{
//			devicePanel.keyReleased(e);
		}    
	};
   
	Listener menuOpenJADFileListener = new Listener()
	{
		public void handleEvent(Event ev)
		{
			if (fileDialog == null) {
				fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setText("Open JAD File...");
				fileDialog.setFilterNames(new String[] {"JAD files"});
				fileDialog.setFilterExtensions(new String[] {"*.jad"});
			}
      
			fileDialog.open();

			if (fileDialog.getFileName().length() > 0) {
				try {
					openJadFile(new File(fileDialog.getFilterPath(), fileDialog.getFileName()).toURL());
				} catch (MalformedURLException ex) {
					System.err.println("Bad URL format " + fileDialog.getFileName());
				}
			}
		} 
	};
  
	Listener menuOpenJADURLListener = new Listener()
	{
		public void handleEvent(Event ev)
		{
			SwtInputDialog inputDialog = new SwtInputDialog(shell, "Open...", "Enter JAD URL:");
			if (inputDialog.open() == SwtDialog.OK) {
				try {
					URL url = new URL(inputDialog.getValue());
					openJadFile(url);
				} catch (MalformedURLException ex) {
					System.err.println("Bad URL format " + inputDialog.getValue());
				}
			}
		}    
	};
  
	Listener menuExitListener = new Listener()
	{    
		public void handleEvent(Event e)
		{
			System.exit(0);
		}    
	};
  
  
	Listener menuSelectDeviceListener = new Listener()
	{    
		public void handleEvent(Event e)
		{
			if (selectDeviceDialog.open() == SwtDialog.OK) {
				if (selectDeviceDialog.getSelectedDeviceEntry().equals(getDevice())) {
					return;
				}
				if (MIDletBridge.getCurrentMIDlet() != getLauncher()) {
					if (!SwtMessageDialog.openQuestion(shell,
							"Question?", "Changing device needs MIDlet to be restarted. All MIDlet data will be lost. Are you sure?")) { 
						return;
					}
				}
				setDevice(selectDeviceDialog.getSelectedDeviceEntry());

				if (MIDletBridge.getCurrentMIDlet() != getLauncher()) {
					try {
						MIDlet result = (MIDlet) MIDletBridge.getCurrentMIDlet().getClass().newInstance();
						startMidlet(result);
					} catch (Exception ex) {
						System.err.println(ex);
					}
				} else {
					startMidlet(getLauncher());
				}
			}
		}    
	};
	
	StatusBarListener statusBarListener = new StatusBarListener()
	{			
		public void statusBarChanged(final String text) 
		{			
			shell.getDisplay().asyncExec(new Runnable()
			{
				public void run() 
				{
					statusBar.setText(text);
				}
			});
		}  
	};
  
	ResponseInterfaceListener responseInterfaceListener = new ResponseInterfaceListener()
	{
		public void stateChanged(final boolean state) 
		{
			shell.getDisplay().asyncExec(new Runnable()
			{
				public void run() 
				{
					menuOpenJADFile.setEnabled(state);
					menuOpenJADURL.setEnabled(state);
					menuSelectDevice.setEnabled(state);
				}
			});
		}  
	};
  
/*	WindowAdapter windowListener = new WindowAdapter()
	{
		public void windowClosing(WindowEvent ev) 
		{
			menuExitListener.actionPerformed(null);
		}
		

		public void windowIconified(WindowEvent ev) 
		{
			MIDletBridge.getMIDletAccess(common.getLauncher().getCurrentMIDlet()).pauseApp();
		}
		
		public void windowDeiconified(WindowEvent ev) 
		{
			try {
				MIDletBridge.getMIDletAccess(common.getLauncher().getCurrentMIDlet()).startApp();
			} catch (MIDletStateChangeException ex) {
				System.err.println(ex);
			}
		}
	};*/  
 
  
	Swt(Shell shell)
	{
		super(new EmulatorContext()
		{
			ProgressJarClassLoader loader = new ProgressJarClassLoader();
    
			public ClassLoader getClassLoader()
			{
				return loader;
			}
    
			public DisplayComponent getDisplayComponent()
			{
				return devicePanel.getDisplayComponent();
			}

			public Launcher getLauncher() 
			{
				return getLauncher();
			}    
		});

		instance = this;
    
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		shell.setLayoutData(new GridData(GridData.FILL_BOTH));

		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
    
		MenuItem menuFile = new MenuItem(bar, SWT.CASCADE);
		menuFile.setText("File");
    
		Menu fileSubmenu = new Menu(shell, SWT.DROP_DOWN);
		menuFile.setMenu(fileSubmenu);

		menuOpenJADFile = new MenuItem(fileSubmenu, SWT.PUSH);
		menuOpenJADFile.setText("Open JAD File...");
		menuOpenJADFile.addListener(SWT.Selection, menuOpenJADFileListener);

		menuOpenJADURL = new MenuItem(fileSubmenu, 0);
		menuOpenJADURL.setText("Open JAD URL...");
		menuOpenJADURL.addListener(SWT.Selection, menuOpenJADURLListener);

		new MenuItem(fileSubmenu, SWT.SEPARATOR);
    
		MenuItem menuExit = new MenuItem(fileSubmenu, SWT.PUSH);
		menuExit.setText("Exit");
		menuExit.addListener(SWT.Selection, menuExitListener);
    
		MenuItem menuOptions = new MenuItem(bar, SWT.CASCADE);
		menuOptions.setText("Options");
    
		Menu optionsSubmenu = new Menu(shell, SWT.DROP_DOWN);
		menuOptions.setMenu(optionsSubmenu);

		menuSelectDevice = new MenuItem(optionsSubmenu, SWT.PUSH);
		menuSelectDevice.setText("Select device...");
		menuSelectDevice.addListener(SWT.Selection, menuSelectDeviceListener);

		shell.setText("MicroEmulator");
//		addWindowListener(windowListener);
		    
		Config.loadConfig("config-swt.xml");
		shell.addKeyListener(keyListener);

		devicePanel = new SwtDeviceComponent(shell);
		devicePanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		statusBar = new Label(shell, SWT.HORIZONTAL);
		statusBar.setText("Status");
		statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		selectDeviceDialog = new SwtSelectDeviceDialog(shell);
		setDevice(selectDeviceDialog.getSelectedDeviceEntry());
    
		setStatusBarListener(statusBarListener);
		setResponseInterfaceListener(responseInterfaceListener);
    
		initialized = true;
	}
      
  
	public DeviceEntry getDevice()
	{
		return deviceEntry;
	}
  
  
	public void setDevice(DeviceEntry entry)
	{
		if (DeviceFactory.getDevice() != null) {
			((SwtDevice) DeviceFactory.getDevice()).dispose();
		}
		
		ProgressJarClassLoader loader = (ProgressJarClassLoader) emulatorContext.getClassLoader();
		try {
			Class deviceClass = null;
			if (entry.getFileName() != null) {
				loader.addRepository(
						new File(Config.getConfigPath(), entry.getFileName()).toURL());
				deviceClass = loader.findClass(entry.getClassName());
			} else {
				deviceClass = Class.forName(entry.getClassName());
			}
			SwtDevice device = (SwtDevice) deviceClass.newInstance();
			this.deviceEntry = entry;
			setDevice(device);
		} catch (MalformedURLException ex) {
			System.err.println(ex);          
		} catch (ClassNotFoundException ex) {
			System.err.println(ex);          
		} catch (InstantiationException ex) {
			System.err.println(ex);          
		} catch (IllegalAccessException ex) {
			System.err.println(ex);          
		}
	}
	
	
	protected void setDevice(SwtDevice device)
	{
		super.setDevice(device);
		
		device.init(emulatorContext);
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
	}
  
  
	public static void main(String args[])
	{
		Display display = new Display();
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		    
		Swt app = new Swt(shell);
		MIDlet m = null;

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--deviceClass")) {
					i++;
					try {
						Class deviceClass = Class.forName(args[i]);
						app.setDevice((SwtDevice) deviceClass.newInstance());
					} catch (ClassNotFoundException ex) {
						System.err.println(ex);          
					} catch (InstantiationException ex) {
						System.err.println(ex);          
					} catch (IllegalAccessException ex) {
						System.err.println(ex);          
					}
					
				} else if (args[i].endsWith(".jad")) {
					try {
						File file = new File(args[i]);
						URL url = file.exists() ? file.toURL() : new URL(args[i]);
						app.openJadFile(url);
					} catch(MalformedURLException exception) {
						System.out.println("Cannot parse " + args[0] + " URL");
					}
				} else {
					Class midletClass;
					try {
						midletClass = Class.forName(args[i]);
						m = app.loadMidlet("MIDlet", midletClass);
					} catch (ClassNotFoundException ex) {
						System.out.println("Cannot find " + args[i] + " MIDlet class");
					}
				}
			}
		} else {
			m = app.getLauncher();
		}
    
		if (app.initialized) {
			if (m != null) {
				app.startMidlet(m);
			}
			
			shell.pack ();
			shell.open ();
			while (!shell.isDisposed ()) {
				if (!display.readAndDispatch ())
					display.sleep ();
			}
			display.dispose ();			
		}

		System.exit(0);
	}

}
