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

import javax.microedition.midlet.MIDlet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.app.ui.ResponseInterfaceListener;
import com.barteo.emulator.app.ui.StatusBarListener;
import com.barteo.emulator.app.ui.awt.FileChooser;
import com.barteo.emulator.app.ui.swt.SwtDeviceComponent;
import com.barteo.emulator.app.ui.swt.SwtDialogWindow;
import com.barteo.emulator.app.ui.swt.SwtSelectDevicePanel;
import com.barteo.emulator.app.util.DeviceEntry;
import com.barteo.emulator.app.util.ExtensionFileFilter;
import com.barteo.emulator.app.util.ProgressJarClassLoader;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.swt.SwtDevice;


public class Swt
{
	Swt instance = null;
  
	Common common;
  
	boolean initialized = false;
  
	SwtSelectDevicePanel selectDevicePanel = null;
	FileChooser fileChooser = null;
	MenuItem menuOpenJADFile;
	MenuItem menuOpenJADURL;
	MenuItem menuSelectDevice;
	    
	SwtDeviceComponent devicePanel;
	DeviceEntry deviceEntry;

	Label statusBar;
  
	private EmulatorContext emulatorContext = new EmulatorContext()
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
	};
  
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
			if (fileChooser == null) {
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("JAD files");
				fileFilter.addExtension("jad");
//				fileChooser = new FileChooser(instance, "Open JAD File...", FileDialog.LOAD);
				fileChooser.setFilenameFilter(fileFilter);
			}
      
			fileChooser.show();
						
			if (fileChooser.getFile() != null) {
				try {
					common.openJadFile(fileChooser.getSelectedFile().toURL());
				} catch (MalformedURLException ex) {
					System.err.println("Bad URL format " + fileChooser.getSelectedFile().getName());
				}
			}
		} 
	};
  
	Listener menuOpenJADURLListener = new Listener()
	{
		public void handleEvent(Event ev)
		{
/*			String entered = OptionPane.showInputDialog(instance, "Enter JAD URL:");
			if (entered != null) {
				try {
					URL url = new URL(entered);
					common.openJadFile(url);
				} catch (MalformedURLException ex) {
					System.err.println("Bad URL format " + entered);
				}
			}*/
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
			if (SwtDialogWindow.show("Select device...", selectDevicePanel)) {
				if (selectDevicePanel.getSelectedDeviceEntry().equals(getDevice())) {
					return;
				}
				if (MIDletBridge.getCurrentMIDlet() != common.getLauncher()) {
/*					if (JOptionPane.showConfirmDialog(instance, 
							"Changing device needs MIDlet to be restarted. All MIDlet data will be lost. Are you sure?", 
							"Question?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != 0) {
						return;
					}*/
				}
				setDevice(selectDevicePanel.getSelectedDeviceEntry());

				if (MIDletBridge.getCurrentMIDlet() != common.getLauncher()) {
					try {
						MIDlet result = (MIDlet) MIDletBridge.getCurrentMIDlet().getClass().newInstance();
						common.startMidlet(result);
					} catch (Exception ex) {
						System.err.println(ex);
					}
				} else {
					common.startMidlet(common.getLauncher());
				}
			}
		}    
	};
	
	StatusBarListener statusBarListener = new StatusBarListener()
	{
		public void statusBarChanged(String text) 
		{
			statusBar.setText(text);
		}  
	};
  
	ResponseInterfaceListener responseInterfaceListener = new ResponseInterfaceListener()
	{
		public void stateChanged(boolean state) 
		{
			menuOpenJADFile.setEnabled(state);
			menuOpenJADURL.setEnabled(state);
			menuSelectDevice.setEnabled(state);
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
		instance = this;
    
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

		statusBar = new Label(shell, SWT.HORIZONTAL);
		statusBar.setText("Status");

		selectDevicePanel = new SwtSelectDevicePanel();
		setDevice(selectDevicePanel.getSelectedDeviceEntry());
    
		common = new Common(emulatorContext);
		common.setStatusBarListener(statusBarListener);
		common.setResponseInterfaceListener(responseInterfaceListener);
    
		initialized = true;
	}
      
  
	public DeviceEntry getDevice()
	{
		return deviceEntry;
	}
  
  
	public void setDevice(DeviceEntry entry)
	{
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
			DeviceFactory.setDevice(device);
			device.init(emulatorContext);
			this.deviceEntry = entry;
			Image tmpImg = device.getNormalImage();
			Point size = new Point(tmpImg.getBounds().width, tmpImg.getBounds().height);
			size.x += 10;
			size.y += statusBar.computeSize(SWT.DEFAULT, SWT.DEFAULT).y + 55;
//			setSize(size);
//			doLayout();
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
  
  
	public static void main(String args[])
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		shell.setLayout(layout);
		    
		Swt app = new Swt(shell);
		MIDlet m = null;

		if (args.length > 0) {
			Class midletClass;
			try {
				midletClass = Class.forName(args[0]);
				m = app.common.loadMidlet("MIDlet", midletClass);
			} catch (ClassNotFoundException ex) {
				System.out.println("Cannot find " + args[0] + " MIDlet class");
			}
		} else {
			m = app.common.getLauncher();
		}
    
		if (app.initialized) {
			if (m != null) {
				app.common.startMidlet(m);
			}
			
			shell.pack ();
			shell.open ();
			while (!shell.isDisposed ()) {
				if (!display.readAndDispatch ())
					display.sleep ();
			}
			display.dispose ();			
		} else {
			System.exit(0);
		}
	}

}
