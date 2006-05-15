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

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.app.launcher.Launcher;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.ui.awt.AwtDeviceComponent;
import org.microemu.app.ui.awt.AwtDialogWindow;
import org.microemu.app.ui.awt.AwtSelectDevicePanel;
import org.microemu.app.ui.awt.FileChooser;
import org.microemu.app.ui.awt.OptionPane;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.ExtensionFileFilter;
import org.microemu.app.util.ProgressJarClassLoader;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.applet.AppletDeviceDisplay;
import org.microemu.device.applet.AppletFontManager;
import org.microemu.device.applet.AppletInputMethod;



public class Awt extends Frame
{
	Awt instance = null;
  
	private Common common;
  
	boolean initialized = false;
  
	AwtSelectDevicePanel selectDevicePanel = null;
	FileChooser fileChooser = null;
	MenuItem menuOpenJADFile;
	MenuItem menuOpenJADURL;
	MenuItem menuSelectDevice;
	    
	AwtDeviceComponent devicePanel;
	DeviceEntry deviceEntry;

	Label statusBar = new Label("Status");
  
	private EmulatorContext emulatorContext = new EmulatorContext()
	{
		private ProgressJarClassLoader loader = new ProgressJarClassLoader();
		
		private InputMethod inputMethod = new AppletInputMethod();
      	    
  	    private DeviceDisplay deviceDisplay = new AppletDeviceDisplay(this); 

		private FontManager fontManager = new AppletFontManager();
  	    
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
			return common.getLauncher();
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
	};
  
	ActionListener menuOpenJADFileListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent ev)
		{
			if (fileChooser == null) {
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("JAD files");
				fileFilter.addExtension("jad");
				fileChooser = new FileChooser(instance, "Open JAD File...", FileDialog.LOAD);
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
  
	ActionListener menuOpenJADURLListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent ev)
		{
			String entered = OptionPane.showInputDialog(instance, "Enter JAD URL:");
			if (entered != null) {
				try {
					URL url = new URL(entered);
					common.openJadFile(url);
				} catch (MalformedURLException ex) {
					System.err.println("Bad URL format " + entered);
				}
			}
		}    
	};
  
	ActionListener menuExitListener = new ActionListener()
	{    
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}    
	};
  
  
	ActionListener menuSelectDeviceListener = new ActionListener()
	{    
		public void actionPerformed(ActionEvent e)
		{
			if (AwtDialogWindow.show("Select device...", selectDevicePanel)) {
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
  
	WindowAdapter windowListener = new WindowAdapter()
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
	};  
 
  
	Awt()
	{
		instance = this;
    
		MenuBar menuBar = new MenuBar();
    
		Menu menuFile = new Menu("File");
    
		menuOpenJADFile = new MenuItem("Open JAD File...");
		menuOpenJADFile.addActionListener(menuOpenJADFileListener);
		menuFile.add(menuOpenJADFile);

		menuOpenJADURL = new MenuItem("Open JAD URL...");
		menuOpenJADURL.addActionListener(menuOpenJADURLListener);
		menuFile.add(menuOpenJADURL);
    
		menuFile.addSeparator();
    
		MenuItem menuItem = new MenuItem("Exit");
		menuItem.addActionListener(menuExitListener);
		menuFile.add(menuItem);
    
		Menu menuOptions = new Menu("Options");
    
		menuSelectDevice = new MenuItem("Select device...");
		menuSelectDevice.addActionListener(menuSelectDeviceListener);
		menuOptions.add(menuSelectDevice);

		menuBar.add(menuFile);
		menuBar.add(menuOptions);
		setMenuBar(menuBar);
    
		setTitle("MicroEmulator");
		addWindowListener(windowListener);
		
    
		Config.loadConfig("config.xml");

		devicePanel = new AwtDeviceComponent();
		selectDevicePanel = new AwtSelectDevicePanel();
    
		common = new Common(emulatorContext);
		common.setStatusBarListener(statusBarListener);
		common.setResponseInterfaceListener(responseInterfaceListener);

		setDevice(selectDevicePanel.getSelectedDeviceEntry());

		add(devicePanel, "Center");
		add(statusBar, "South");    

		initialized = true;
	}
      
  
	public DeviceEntry getDevice()
	{
		return deviceEntry;
	}
  
  
	public void setDevice(DeviceEntry entry)
	{
		if (DeviceFactory.getDevice() != null) {
//			((AppletDevice) DeviceFactory.getDevice()).dispose();
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
			Device device = (Device) deviceClass.newInstance();
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
  
  
	protected void setDevice(Device device) 
	{
		common.setDevice(device);
		
		device.init(emulatorContext);
		devicePanel.init();
		Image tmpImg = device.getNormalImage();
		Dimension size = new Dimension(tmpImg.getWidth(), tmpImg.getHeight());
		size.width += 10;
		size.height += statusBar.getPreferredSize().height + 55;
		setSize(size);
		doLayout();
	}


	public static void main(String args[])
	{    
		Awt app = new Awt();
		MIDlet m = null;

		if (args.length > 0) {
			if (args[0].endsWith(".jad")) {
				try {
					File file = new File(args[0]);
				  URL url = file.exists() ? file.toURL() : new URL(args[0]);
				  app.common.openJadFile(url);
				} catch(MalformedURLException exception) {
				  System.out.println("Cannot parse " + args[0] + " URL");
				}
		  } else {
				Class midletClass;
				try {
				  midletClass = Class.forName(args[0]);
				m = app.common.loadMidlet("MIDlet", midletClass);
				} catch (ClassNotFoundException ex) {
				  System.out.println("Cannot find " + args[0] + " MIDlet class");
				}
		  }
		} else {
			m = app.common.getLauncher();
		}
    
		if (app.initialized) {
			if (m != null) {
				app.common.startMidlet(m);
			}
			app.validate();
			app.setVisible(true);
		} else {
			System.exit(0);
		}
	}

}
