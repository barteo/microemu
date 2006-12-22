/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.microemu.EmulatorContext;
import org.microemu.app.Config;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.IOUtils;
import org.microemu.device.Device;


public class SwingSelectDevicePanel extends SwingDialogPanel
{
  private static final long serialVersionUID = 1L;

  private EmulatorContext emulatorContext;
  
  private JScrollPane spDevices;
  private JButton btAdd;
  private JButton btRemove;
  private JButton btDefault;
  private DefaultListModel lsDevicesModel;
  private JList lsDevices;
  
  private ActionListener btAddListener = new ActionListener() {
		private JFileChooser fileChooser = null;

		public void actionPerformed(ActionEvent ev) {
			if (fileChooser == null) {
				fileChooser = new JFileChooser();
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("Device profile (*.jar, *.zip)");
				fileFilter.addExtension("jar");
				fileFilter.addExtension("zip");
				fileChooser.setFileFilter(fileFilter);
			}

			if (fileChooser.showOpenDialog(SwingSelectDevicePanel.this) == JFileChooser.APPROVE_OPTION) {
				String manifestDeviceName = null;
				URL[] urls = new URL[1];
				ArrayList descriptorEntries = new ArrayList();
				JarFile jar = null;
				try {
					jar = new JarFile(fileChooser.getSelectedFile());
					
					Manifest manifest = jar.getManifest();
					if (manifest != null) {
						Attributes attrs = manifest.getMainAttributes();
						manifestDeviceName = attrs.getValue("Device-Name");
					}

					for (Enumeration en = jar.entries(); en.hasMoreElements();) {
						JarEntry entry = (JarEntry) en.nextElement();
						if (entry.getName().toLowerCase().endsWith(".xml")) {
							descriptorEntries.add(entry.getName());
						}
					}
					urls[0] = fileChooser.getSelectedFile().toURL();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(SwingSelectDevicePanel.this,
							"Error reading file: " + fileChooser.getSelectedFile().getName(),
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				} finally {
					if (jar != null) {
						try {
							jar.close();
						} catch (IOException ignore) {
						}
					}
				}
				
				if (descriptorEntries.size() == 0) {
					JOptionPane.showMessageDialog(SwingSelectDevicePanel.this,
							"Cannot find any device profile in file: " + fileChooser.getSelectedFile().getName(),
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (descriptorEntries.size() > 1) {
					manifestDeviceName = null;
				}

				URLClassLoader classLoader = new URLClassLoader(urls);
				HashMap devices = new HashMap();
				for (Iterator it = descriptorEntries.iterator(); it.hasNext();) {
					String entryName = (String) it.next();
					try {
						devices.put(entryName,
								Device.create(emulatorContext, classLoader, entryName));
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(SwingSelectDevicePanel.this,
								"Error parsing device profile: " + ex.getMessage(), 
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				for (Enumeration en = lsDevicesModel.elements(); en.hasMoreElements(); ) {
					DeviceEntry entry = (DeviceEntry) en.nextElement();
					if (devices.containsKey(entry.getDescriptorLocation())) {
						devices.remove(entry.getDescriptorLocation());
					}
				}
				if (devices.size() == 0) {
					JOptionPane.showMessageDialog(SwingSelectDevicePanel.this,
							"Device profile already added", 
							"Info", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				try {
					File deviceFile = File.createTempFile("dev", ".jar", Config.getConfigPath()); 
					IOUtils.copyFile(fileChooser.getSelectedFile(), deviceFile);
					
					DeviceEntry entry = null;
					for (Iterator it = devices.keySet().iterator(); it.hasNext();) {
						String descriptorLocation = (String) it.next();
						Device device = (Device) devices.get(descriptorLocation);
						if (manifestDeviceName != null) {
							entry = new DeviceEntry(manifestDeviceName, deviceFile.getName(), descriptorLocation, false);
						} else {
							entry = new DeviceEntry(device.getName(), deviceFile.getName(), descriptorLocation, false);
						}
						lsDevicesModel.addElement(entry);
						Config.addDeviceEntry(entry);
					}
					lsDevices.setSelectedValue(entry, true);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(SwingSelectDevicePanel.this,
							"Error adding device profile: " + ex.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}				
			}
		}
	};
  
  private ActionListener btRemoveListener = new ActionListener()
  {
    public void actionPerformed(ActionEvent ev)
    {
      DeviceEntry entry = (DeviceEntry) lsDevices.getSelectedValue();
      
      boolean canDeleteFile = true;
      for (Enumeration en = lsDevicesModel.elements(); en.hasMoreElements(); ) {
          DeviceEntry test = (DeviceEntry) en.nextElement();
          if (test != entry && test.getFileName() != null && test.getFileName().equals(entry.getFileName())) {
        	  canDeleteFile = false;
        	  break;
          }
      }      
      if (canDeleteFile) {
	      File deviceFile = new File(Config.getConfigPath(), entry.getFileName());
	      deviceFile.delete();
      }
      
      if (entry.isDefaultDevice()) {
        for (Enumeration en = lsDevicesModel.elements(); en.hasMoreElements(); ) {
          DeviceEntry tmp = (DeviceEntry) en.nextElement();
          if (!tmp.canRemove()) {
            tmp.setDefaultDevice(true);
            break;
          }
        }
      }
      lsDevicesModel.removeElement(entry);
      Config.removeDeviceEntry(entry);
    }
  };
  
  private ActionListener btDefaultListener = new ActionListener()
  {
    public void actionPerformed(ActionEvent ev)
    {
      DeviceEntry entry = (DeviceEntry) lsDevices.getSelectedValue();
      for (Enumeration en = lsDevicesModel.elements(); en.hasMoreElements(); ) {
        DeviceEntry tmp = (DeviceEntry) en.nextElement();
        if (tmp == entry) {
          tmp.setDefaultDevice(true);
        } else {
          tmp.setDefaultDevice(false);
        }
        Config.changeDeviceEntry(tmp);
      }
      lsDevices.repaint();
      btDefault.setEnabled(false);
    }
  };

  ListSelectionListener listSelectionListener = new ListSelectionListener()
  {
    public void valueChanged(ListSelectionEvent ev)
    {
      DeviceEntry entry = (DeviceEntry) lsDevices.getSelectedValue();
      if (entry != null) {
        if (entry.isDefaultDevice()) {
          btDefault.setEnabled(false);
        } else {
          btDefault.setEnabled(true);
        }
        if (entry.canRemove()) {
          btRemove.setEnabled(true);
        } else {
          btRemove.setEnabled(false);
        }
        btOk.setEnabled(true);
      } else {
        btDefault.setEnabled(false);
        btRemove.setEnabled(false);
        btOk.setEnabled(false);
      }
    }
  };
  
  
  public SwingSelectDevicePanel(EmulatorContext emulatorContext) 
  {
	this.emulatorContext = emulatorContext;  
	  
    setLayout(new BorderLayout());
    setBorder(new TitledBorder(new EtchedBorder(), "Installed devices"));

    lsDevicesModel = new DefaultListModel();
    lsDevices = new JList(lsDevicesModel);
    lsDevices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lsDevices.addListSelectionListener(listSelectionListener);
    spDevices = new JScrollPane(lsDevices);
    add(spDevices, BorderLayout.CENTER);
    
    JPanel panel = new JPanel();
    btAdd = new JButton("Add...");
    btAdd.addActionListener(btAddListener);
    btRemove = new JButton("Remove");
    btRemove.addActionListener(btRemoveListener);
    btDefault = new JButton("Set as default");
    btDefault.addActionListener(btDefaultListener);
    panel.add(btAdd);
    panel.add(btRemove);
    panel.add(btDefault);
    
    add(panel, BorderLayout.SOUTH);
    
    for (Enumeration e = Config.getDeviceEntries().elements(); e.hasMoreElements(); ) {
      DeviceEntry entry = (DeviceEntry) e.nextElement();
      lsDevicesModel.addElement(entry);
      if (entry.isDefaultDevice()) {
        lsDevices.setSelectedValue(entry, true);
      }
    }
  }
  
  
  public DeviceEntry getSelectedDeviceEntry()
  {
    return (DeviceEntry) lsDevices.getSelectedValue();
  }
    
}
