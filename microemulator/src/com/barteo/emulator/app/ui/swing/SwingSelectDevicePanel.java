/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@it.pl>
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

package com.barteo.emulator.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.Attributes;
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

import com.barteo.emulator.app.Config;
import com.barteo.emulator.app.util.DeviceEntry;
import com.barteo.emulator.app.util.ProgressJarClassLoader;
import com.barteo.emulator.device.Device;


public class SwingSelectDevicePanel extends SwingDialogPanel
{
  private SwingSelectDevicePanel instance;
  
  private JScrollPane spDevices;
  private JButton btAdd;
  private JButton btRemove;
  private JButton btDefault;
  private DefaultListModel lsDevicesModel;
  private JList lsDevices;
  
  private ActionListener btAddListener = new ActionListener()
  {
    private JFileChooser fileChooser = null;
        
    public void actionPerformed(ActionEvent ev)
    {
      if (fileChooser == null) {
        fileChooser = new JFileChooser();
        ExtensionFileFilter fileFilter = new ExtensionFileFilter("Device profile (*.dev)");
        fileFilter.addExtension("dev");
        fileChooser.setFileFilter(fileFilter);
      }
      
      ProgressJarClassLoader loader = new ProgressJarClassLoader();
      
      if (fileChooser.showOpenDialog(instance) == JFileChooser.APPROVE_OPTION) {
        String deviceClassName = null;
        String deviceName = null;
        try {
          JarFile jar = new JarFile(fileChooser.getSelectedFile());
          Manifest manifest = jar.getManifest();
          if (manifest == null) {
            JOptionPane.showMessageDialog(instance,
                "Missing manifest in dev file.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }          
          Attributes attrs = manifest.getMainAttributes();
          
          deviceName = attrs.getValue("Device-Name");
          if (deviceName == null) {
            JOptionPane.showMessageDialog(instance, 
                "Missing Device-Name entry in jar manifest.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          
          deviceClassName = attrs.getValue("Device-Class");
          if (deviceClassName == null) {
            JOptionPane.showMessageDialog(instance, 
                "Missing Device-Class entry in jar manifest.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          
          jar.close();
//          deviceClassName = deviceClassName.replace('.', '/');
//          if (deviceClassName.charAt(0) == '/') {
//            deviceClassName = deviceClassName.substring(1);
//          }
          for (Enumeration e = lsDevicesModel.elements(); e.hasMoreElements(); ) {
            DeviceEntry entry = (DeviceEntry) e.nextElement();
            if (deviceClassName.equals(entry.getClassName())) {
              JOptionPane.showMessageDialog(instance, 
                  "Device is already added.",
                  "Info", JOptionPane.INFORMATION_MESSAGE);
              return;
            }
          }
          
          loader.addRepository(fileChooser.getSelectedFile().toURL());
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(instance, 
              "Error reading " + fileChooser.getSelectedFile().getName() + " file.",
              "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        
        Class deviceClass = null;
        try {
          deviceClass = loader.findClass(deviceClassName);
        } catch (ClassNotFoundException ex) {
          JOptionPane.showMessageDialog(instance, 
              "Cannot find class defined in Device-Class entry in jar manifest.",
              "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
          
        if (!Device.class.isAssignableFrom(deviceClass)) {
          JOptionPane.showMessageDialog(instance, 
              "Class defined in Device-Class entry in jar manifest not superclass of com.barteo.emulator.device.Device.",
              "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        
        try {
          File deviceFile = File.createTempFile("dev", ".dev", Config.getConfigPath());
          FileInputStream fis  = new FileInputStream(fileChooser.getSelectedFile());
          FileOutputStream fos = new FileOutputStream(deviceFile);
          byte[] buf = new byte[1024];
            int i = 0;
            while((i=fis.read(buf))!=-1) {
              fos.write(buf, 0, i);
            }
          fis.close();
          fos.close();
        
          DeviceEntry entry = 
              new DeviceEntry(deviceName, deviceFile.getName(), deviceClassName, false);
          lsDevicesModel.addElement(entry);
          lsDevices.setSelectedValue(entry, true);
        } catch (IOException ex) {
          System.err.println(ex);
        }
      }
    }    
  };
  
  private ActionListener btRemoveListener = new ActionListener()
  {
    public void actionPerformed(ActionEvent ev)
    {
      DeviceEntry entry = (DeviceEntry) lsDevices.getSelectedValue();
      File deviceFile = new File(Config.getConfigPath(), entry.getFileName());
      deviceFile.delete();
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
  
  
  public SwingSelectDevicePanel() 
  {
    instance = this;
    
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
    
    for (Enumeration e = Config.getDevices().elements(); e.hasMoreElements(); ) {
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
  
  
  public void hideNotify()
  {
    Vector devices = Config.getDevices();
    devices.removeAllElements();
    
    for (Enumeration e = lsDevicesModel.elements(); e.hasMoreElements(); ) {
      devices.add(e.nextElement());
    }
    
    Config.saveConfig("config.xml");
  }
    
}
