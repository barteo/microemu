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

package org.microemu.app.ui.swt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.microemu.EmulatorContext;
import org.microemu.app.Common;
import org.microemu.app.Config;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.IOUtils;
import org.microemu.device.Device;
import org.microemu.device.impl.DeviceImpl;


public class SwtSelectDeviceDialog extends SwtDialog
{
	private EmulatorContext emulatorContext;
	  
	private Button btAdd;
	private Button btRemove;
	private Button btDefault;
	private List lsDevices;
	private Vector deviceModel;
	private DeviceEntry selectedEntry;
  
	private Listener btAddListener = new Listener()
	{
		private FileDialog fileDialog = null;
    
		public void handleEvent(Event event)
		{
			if (fileDialog == null) {
				fileDialog = new FileDialog(getShell(), SWT.OPEN);
				fileDialog.setText("Open device profile file...");
				fileDialog.setFilterNames(new String[] {"Device profile (*.jar)"});
				fileDialog.setFilterExtensions(new String[] {"*.jar"});
			}
      
			fileDialog.open();
			
			if (fileDialog.getFileName() != null) {
				File file;
				String manifestDeviceName = null;
				URL[] urls = new URL[1];
				ArrayList descriptorEntries = new ArrayList();
				try {
					file = new File(fileDialog.getFilterPath(), fileDialog.getFileName());
					JarFile jar = new JarFile(file);
					
					Manifest manifest = jar.getManifest();
					if (manifest != null) {
						Attributes attrs = manifest.getMainAttributes();
						manifestDeviceName = attrs.getValue("Device-Name");
					}

					for (Enumeration en = jar.entries(); en.hasMoreElements();) {
						String entry = ((JarEntry) en.nextElement()).getName();
						if (entry.toLowerCase().endsWith(".xml") || entry.toLowerCase().endsWith("device.txt")) {
							descriptorEntries.add(entry);
						}
					}
					jar.close();
					urls[0] = file.toURL();
				} catch (IOException ex) {
					SwtMessageDialog.openError(getShell(),
							"Error", 
							"Error reading file: " + fileDialog.getFileName());
					return;
				}
					
				if (descriptorEntries.size() == 0) {
					SwtMessageDialog.openError(getShell(),
							"Error",
							"Cannot find any device profile in file: " + fileDialog.getFileName());
					return;
				}
				
				if (descriptorEntries.size() > 1) {
					manifestDeviceName = null;
				}

				ClassLoader classLoader = Common.createExtensionsClassLoader(urls);
				HashMap devices = new HashMap();
				for (Iterator it = descriptorEntries.iterator(); it.hasNext();) {
					JarEntry entry = (JarEntry) it.next();
					try {
						devices.put(
								entry.getName(),
								DeviceImpl.create(emulatorContext, classLoader, entry.getName()));
					} catch (IOException ex) {
						SwtMessageDialog.openError(getShell(),
								"Error",
								"Error parsing device profile: " + ex.getMessage());
						return;
					}
				}

				for (int i = 0; i < deviceModel.size(); i++) {
					DeviceEntry entry = (DeviceEntry) deviceModel.elementAt(i);
					if (devices.containsKey(entry.getDescriptorLocation())) {
						devices.remove(entry.getDescriptorLocation());
					}
				}
				if (devices.size() == 0) {
					SwtMessageDialog.openError(getShell(),
							"Info", 
							"Device profile already added");
					return;
				}

				try { 
					File deviceFile = File.createTempFile("dev", ".jar", Config.getConfigPath());
					IOUtils.copyFile(file, deviceFile);

					DeviceEntry entry = null;
					for (Iterator it = devices.keySet().iterator(); it.hasNext();) {
						String descriptorLocation = (String) it.next();
						Device device = (Device) devices.get(descriptorLocation);
						if (manifestDeviceName != null) {
							entry = new DeviceEntry(manifestDeviceName, deviceFile.getName(), descriptorLocation, false);
						} else {
							entry = new DeviceEntry(device.getName(), deviceFile.getName(), descriptorLocation, false);
						}
						deviceModel.addElement(entry);
						for (int i = 0; i < deviceModel.size(); i++) {
							if (deviceModel.elementAt(i) == entry) {
								lsDevices.add(entry.getName());
								lsDevices.select(i);
							}
						}
						Config.addDeviceEntry(entry);
					}
					lsDevicesListener.widgetSelected(null);
				} catch (IOException ex) {
					SwtMessageDialog.openError(getShell(),
							"Error",
							"Error adding device profile: " + ex.getMessage());
					return;
				}				
				
			}
		}
	};
  
	private Listener btRemoveListener = new Listener()
	{
		public void handleEvent(Event event)
		{
			DeviceEntry entry = (DeviceEntry) deviceModel.elementAt(lsDevices.getSelectionIndex());
		      
		    boolean canDeleteFile = true;
			for (int i = 0; i < deviceModel.size(); i++) {
				DeviceEntry test = (DeviceEntry) deviceModel.elementAt(i);
				if (test != entry && test.getFileName() != null
						&& test.getFileName().equals(entry.getFileName())) {
					canDeleteFile = false;
					break;
				}
			}
			if (canDeleteFile) {
				File deviceFile = new File(Config.getConfigPath(), entry.getFileName());
				deviceFile.delete();
			}		      

			if (entry.isDefaultDevice()) {
				for (int i = 0; i < deviceModel.size(); i++) {
					DeviceEntry tmp = (DeviceEntry) deviceModel.elementAt(i);
					if (!tmp.canRemove()) {
						tmp.setDefaultDevice(true);
						lsDevices.setItem(i, tmp.getName() + " (default)");						
						break;
					}
				}
			}
			for (int i = 0; i < deviceModel.size(); i++) {
				if (deviceModel.elementAt(i) == entry) {
					deviceModel.removeElementAt(i);
					lsDevices.remove(i);
					break;
				}
			}
			lsDevicesListener.widgetSelected(null);
			Config.removeDeviceEntry(entry);
		}
	};
  
	private Listener btDefaultListener = new Listener()
	{
		public void handleEvent(Event event)
		{
			DeviceEntry entry = (DeviceEntry) deviceModel.elementAt(lsDevices.getSelectionIndex());
			for (int i = 0; i < deviceModel.size(); i++) {
				DeviceEntry tmp = (DeviceEntry) deviceModel.elementAt(i);
				if (tmp == entry) {
					tmp.setDefaultDevice(true);
					lsDevices.setItem(i, tmp.getName() + " (default)");
				} else {
					tmp.setDefaultDevice(false);
					lsDevices.setItem(i, tmp.getName());
				}
				Config.changeDeviceEntry(tmp);
			}
			btDefault.setEnabled(false);
		}
	};
	
	SelectionAdapter lsDevicesListener = new SelectionAdapter()
	{
		public void widgetSelected(SelectionEvent e) 
		{
			int index = lsDevices.getSelectionIndex();
			if (index != -1) {
				selectedEntry = (DeviceEntry) deviceModel.elementAt(index);
				if (selectedEntry.isDefaultDevice()) {
					btDefault.setEnabled(false);
				} else {
					btDefault.setEnabled(true);
				}
				if (selectedEntry.canRemove()) {
					btRemove.setEnabled(true);
				} else {
					btRemove.setEnabled(false);
				}
				btOk.setEnabled(true);
			} else {
				selectedEntry = null;
				btDefault.setEnabled(false);
				btRemove.setEnabled(false);
				btOk.setEnabled(false);
			}
		}
	};


	public SwtSelectDeviceDialog(Shell parent, EmulatorContext emulatorContext)
	{
		super(parent);

		this.emulatorContext = emulatorContext;  
		  
		Vector devs = Config.getDeviceEntries();
		for (int i = 0; i < devs.size(); i++) {
			DeviceEntry entry = (DeviceEntry) devs.elementAt(i);
			if (entry.isDefaultDevice()) {
				selectedEntry = entry;
			}
		}
	}
  
  
	protected void configureShell(Shell shell) 
	{
		super.configureShell(shell);

		shell.setText("Select device...");
	}


	protected Control createDialogArea(Composite composite) 
	{
		GridLayout gridLayout = new GridLayout();
   	gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);

		Group gpDevices = new Group(composite, SWT.NONE);
		gpDevices.setText("Installed devices");
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
	  gpDevices.setLayout(gridLayout);
		gpDevices.setLayoutData(new GridData(GridData.FILL_BOTH));

		lsDevices = new List(gpDevices, SWT.SINGLE | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		gridData.grabExcessVerticalSpace = true;
		Rectangle trim = lsDevices.computeTrim(0, 0, 0, lsDevices.getItemHeight() * 5);
		gridData.heightHint = trim.height;
		lsDevices.setLayoutData(gridData);
		lsDevices.addSelectionListener(lsDevicesListener);
		
		Composite btDevices = new Composite(gpDevices, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		btDevices.setLayout(gridLayout);
		btDevices.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    
		btAdd = new Button(btDevices, SWT.PUSH);
		btAdd.setText("Add...");
		btAdd.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btAdd.addListener(SWT.Selection, btAddListener);
		
		btRemove = new Button(btDevices, SWT.PUSH);
		btRemove.setText("Remove");
		btRemove.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btRemove.addListener(SWT.Selection, btRemoveListener);
		
		btDefault = new Button(btDevices, SWT.PUSH);
		btDefault.setText("Set as default");
		btDefault.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		btDefault.addListener(SWT.Selection, btDefaultListener);
    
		Vector devs = Config.getDeviceEntries();
		deviceModel = new Vector();
		for (int i = 0; i < devs.size(); i++) {
			DeviceEntry entry = (DeviceEntry) devs.elementAt(i);
			deviceModel.addElement(entry);
			if (entry.isDefaultDevice()) {
				lsDevices.add(entry.getName() + " (default)");
				lsDevices.select(i);
			} else {
				lsDevices.add(entry.getName());
			}
		}

		return composite;
	}
  
  
	protected Control createButtonBar(Composite parent) 
	{
		Control control = super.createButtonBar(parent);
		
		lsDevicesListener.widgetSelected(null);

		return control;
	}
	

	public DeviceEntry getSelectedDeviceEntry()
	{
		return selectedEntry;
	}
    
}
