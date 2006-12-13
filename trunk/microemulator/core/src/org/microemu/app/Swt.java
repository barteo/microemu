/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@barteo.net>
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
 
package org.microemu.app;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

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
import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.ui.swt.SwtDeviceComponent;
import org.microemu.app.ui.swt.SwtDialog;
import org.microemu.app.ui.swt.SwtInputDialog;
import org.microemu.app.ui.swt.SwtMessageDialog;
import org.microemu.app.ui.swt.SwtSelectDeviceDialog;
import org.microemu.app.util.DeviceEntry;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.swt.SwtDeviceDisplay;
import org.microemu.device.swt.SwtFontManager;
import org.microemu.device.swt.SwtInputMethod;

public class Swt extends Common
{
	public static Shell shell;

	protected static SwtDeviceComponent devicePanel;

	protected MenuItem menuOpenJADFile;
	protected MenuItem menuOpenJADURL;

	private SwtSelectDeviceDialog selectDeviceDialog;
	private FileDialog fileDialog = null;
	private MenuItem menuSelectDevice;
	    
	private DeviceEntry deviceEntry;

	private Label statusBar;
  
	private KeyListener keyListener = new KeyListener()
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
   
	protected Listener menuOpenJADFileListener = new Listener()
	{
		public void handleEvent(Event ev)
		{
			if (fileDialog == null) {
				fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setText("Open JAD File...");
				fileDialog.setFilterNames(new String[] {"JAD files"});
				fileDialog.setFilterExtensions(new String[] {"*.jad"});
				// TODO folder saved in config
			}
      
			fileDialog.open();

			if (fileDialog.getFileName().length() > 0) {
				try {
					openJadUrl(new File(fileDialog.getFilterPath(), fileDialog.getFileName()).toURL().toString());
				} catch (IOException ex) {
					System.err.println("Cannot load " + fileDialog.getFileName());
				}
			}
		} 
	};
  
	protected Listener menuOpenJADURLListener = new Listener()
	{
		public void handleEvent(Event ev)
		{
			// TODO change to JadUrlPanel
			SwtInputDialog inputDialog = new SwtInputDialog(shell, "Open...", "Enter JAD URL:");
			if (inputDialog.open() == SwtDialog.OK) {
				try {
					openJadUrl(inputDialog.getValue());
				} catch (IOException ex) {
					System.err.println("Cannot load " + inputDialog.getValue());
				}
			}
		}    
	};
  
	protected Listener menuExitListener = new Listener()
	{    
		public void handleEvent(Event e)
		{
			Config.setWindowX(shell.getLocation().x);
			Config.setWindowY(shell.getLocation().x);
			Config.saveConfig("config.xml");

			System.exit(0);
		}    
	};
  
  
	private Listener menuSelectDeviceListener = new Listener()
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
						startMidlet(MIDletBridge.getCurrentMIDlet().getClass(), MIDletBridge.getMIDletAccess());
					} catch (Exception ex) {
						System.err.println(ex);
					}
				} else {
					startLauncher(MIDletBridge.getMIDletAccess());
				}
			}
		}    
	};
	
	private StatusBarListener statusBarListener = new StatusBarListener()
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
  
	private ResponseInterfaceListener responseInterfaceListener = new ResponseInterfaceListener()
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
	
	protected Swt(Shell shell)
	{
		this(shell, null);
	}
  
	protected Swt(Shell shell, DeviceEntry defaultDevice)
	{
		super(new EmulatorContext()
		{
			private InputMethod inputMethod = new SwtInputMethod();
			
			private DeviceDisplay deviceDisplay = new SwtDeviceDisplay(this);

			private FontManager fontManager = new SwtFontManager();
			    
			public DisplayComponent getDisplayComponent()
			{
				return devicePanel.getDisplayComponent();
			}

            public InputMethod getDeviceInputMethod()
            {
                return inputMethod;
            }    

            public DeviceDisplay getDeviceDisplay()
            {
                return deviceDisplay;
            }

			public FontManager getDeviceFontManager() 
			{
				return fontManager;
			}    
		});

		initInterface(shell);

//		addWindowListener(windowListener);
		    
		Config.loadConfig("config.xml", null, emulatorContext);
		
		shell.setLocation(Config.getWindowX(), Config.getWindowY());
		
		shell.addKeyListener(keyListener);

		selectDeviceDialog = new SwtSelectDeviceDialog(shell, emulatorContext);
    
		setStatusBarListener(statusBarListener);
		setResponseInterfaceListener(responseInterfaceListener);
	}
	
	
	protected void initInterface(Shell shell)
	{
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

		devicePanel = new SwtDeviceComponent(shell);
		devicePanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		statusBar = new Label(shell, SWT.HORIZONTAL);
		statusBar.setText("Status");
		statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
      
  
	public void setDevice(DeviceEntry entry)
	{
		if (DeviceFactory.getDevice() != null) {
//			((SwtDevice) DeviceFactory.getDevice()).dispose();
		}
		
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			if (entry.getFileName() != null) {
				URL[] urls = new URL[1];
				urls[0] = new File(Config.getConfigPath(), entry.getFileName()).toURL();
				classLoader = new URLClassLoader(urls);
			}
			Device device = Device.create(
					emulatorContext, 
					classLoader, 
					entry.getDescriptorLocation());
			this.deviceEntry = entry;
			setDevice(device);
			updateDevice();
		} catch (MalformedURLException ex) {
			System.err.println(ex);
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}
	
	
	protected void updateDevice()
	{
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
	}
  
  
	public static void main(String args[])
	{
		Display display = new Display();
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		    
		List params = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			params.add(args[i]);
		}

		Swt app = new Swt(shell);
		app.initDevice(params, app.selectDeviceDialog.getSelectedDeviceEntry());
		app.updateDevice();
		
		app.initMIDlet(params, false);
		
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ())
				display.sleep ();
		}
		display.dispose ();			
	}

}
