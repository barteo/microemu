/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.LookAndFeel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.MicroEmulator;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.MIDletEntry;
import com.barteo.emulator.app.launcher.Launcher;
import com.barteo.emulator.app.ui.DeviceEntry;
import com.barteo.emulator.app.ui.DialogWindow;
import com.barteo.emulator.app.ui.SelectDevicePanel;
import com.barteo.emulator.app.util.ProgressEvent;
import com.barteo.emulator.app.util.ProgressJarClassLoader;
import com.barteo.emulator.app.util.ProgressListener;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.j2se.J2SEDevice;
import com.barteo.emulator.util.JadMidletEntry;
import com.barteo.emulator.util.JadProperties;


public class WebStart extends JFrame implements MicroEmulator
{
  static Launcher launcher;
  
  WebStart instance = null;
  
  boolean initialized = false;
  
  SelectDevicePanel selectDevicePanel = null;
  JFileChooser fileChooser = null;
  JMenuItem menuOpenJADFile;
  JMenuItem menuOpenJADURL;
    
  SwingDeviceComponent devicePanel;
  DeviceEntry deviceEntry;

  JLabel statusBar = new JLabel("Status");
  
  JadProperties jad = new JadProperties();
  
  private EmulatorContext emulatorContext = new EmulatorContext()
  {
    ProgressJarClassLoader loader = new ProgressJarClassLoader(WebStart.class.getClassLoader());
    
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
      devicePanel.keyPressed(e);
    }

    
    public void keyReleased(KeyEvent e)
    {
      devicePanel.keyReleased(e);
    }
    
  };
   
  ActionListener menuOpenJADFileListener = new ActionListener()
  {

    public void actionPerformed(ActionEvent ev)
    {
      if (fileChooser == null) {
        ExtensionFileFilter fileFilter = new ExtensionFileFilter("JAD files");
        fileFilter.addExtension("jad");
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setDialogTitle("Open JAD File...");
      }
      
      int returnVal = fileChooser.showOpenDialog(instance);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
          FileInputStream fis = new FileInputStream(fileChooser.getSelectedFile());
          statusBar.setText("Loading...");
          jad.clear();
          jad.load(fis);
          loadFromJad();
        } catch (FileNotFoundException ex) {
          System.err.println("Cannot found file " + fileChooser.getSelectedFile().getName());
        } catch (IOException ex) {
          System.err.println("Cannot open file " + fileChooser.getSelectedFile().getName());
        }
      }
    }
  
  };
  
  ActionListener menuOpenJADURLListener = new ActionListener()
  {

    public void actionPerformed(ActionEvent ev)
    {
      String entered = JOptionPane.showInputDialog("Enter JAD URL:");
      if (entered != null) {
        try {
          URL url = new URL(entered);
          statusBar.setText("Loading...");
          jad.clear();
          jad.load(url.openStream());
          loadFromJad();
        } catch (MalformedURLException ex) {
          System.err.println("Bad URL format " + entered);
        } catch (IOException ex) {
          System.err.println("Cannot open URL " + entered);
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
      if (DialogWindow.show("Select device...", selectDevicePanel)) {
        if (selectDevicePanel.getSelectedDeviceEntry().equals(getDevice())) {
          return;
        }
        if (MIDletBridge.getCurrentMIDlet() != launcher) {
          if (JOptionPane.showConfirmDialog(instance, 
              "Changing device needs MIDlet to be restarted. All MIDlet data will be lost. Are you sure?", 
              "Question?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != 0) {
            return;
          }
        }
        setDevice(selectDevicePanel.getSelectedDeviceEntry());

        if (MIDletBridge.getCurrentMIDlet() != launcher) {
          try {
            MIDlet result = (MIDlet) MIDletBridge.getCurrentMIDlet().getClass().newInstance();
            startMidlet(result);
          } catch (Exception ex) {
            System.err.println(ex);
          }
        } else {
          startMidlet(launcher);
        }
      }
    }
    
  };

  
  ProgressListener progressListener = new ProgressListener()
  {
    int percent = -1;
    

    public void stateChanged(ProgressEvent event)
    {
      int newpercent = (int) ((float) event.getCurrent() / (float) event.getMax() * 100);
      
      if (newpercent != percent) {
        statusBar.setText("Loading... (" + newpercent +" %)");
        percent = newpercent;
      }
    }
    
  };
  
  
  WebStart()
  {
    instance = this;
    
    JMenuBar menuBar = new JMenuBar();
    
    JMenu menuFile = new JMenu("File");
    
    menuOpenJADFile = new JMenuItem("Open JAD File...");
    menuOpenJADFile.addActionListener(menuOpenJADFileListener);
    menuFile.add(menuOpenJADFile);

    menuOpenJADURL = new JMenuItem("Open JAD URL...");
    menuOpenJADURL.addActionListener(menuOpenJADURLListener);
    menuFile.add(menuOpenJADURL);
    
    menuFile.addSeparator();
    
    JMenuItem menuItem = new JMenuItem("Exit");
    menuItem.addActionListener(menuExitListener);
    menuFile.add(menuItem);
    
    JMenu menuOptions = new JMenu("Options");
    
    JMenuItem menuSelectDevice = new JMenuItem("Select device...");
    menuSelectDevice.addActionListener(menuSelectDeviceListener);
    menuOptions.add(menuSelectDevice);

    menuBar.add(menuFile);
    menuBar.add(menuOptions);
    setJMenuBar(menuBar);
    
    setTitle("MicroEmulator");
    
    Config.loadConfig();
    addKeyListener(keyListener);

    devicePanel = new SwingDeviceComponent();
    selectDevicePanel = new SelectDevicePanel();
    setDevice(selectDevicePanel.getSelectedDeviceEntry());
    
    launcher = new Launcher();
    launcher.setCurrentMIDlet(launcher);
     
    getContentPane().add(devicePanel, "Center");
    getContentPane().add(statusBar, "South");    

    initialized = true;
  }
  
  
  public void loadFromJad()
  {
    final ProgressJarClassLoader loader = (ProgressJarClassLoader) emulatorContext.getClassLoader();
    URL url = null;
    try {
      url = new URL(jad.getJarURL());
    } catch (MalformedURLException ex) {
      // it can be just file      
      File f = new File(fileChooser.getSelectedFile().getParent(), jad.getJarURL());
      try {
        url = f.toURL();
      } catch (MalformedURLException ex1) {
        System.err.println(ex1);
      }
    }
    loader.addRepository(url);
    launcher.removeMIDletEntries();
    
    Thread task = new Thread() 
    {
      
      public void run()
      {
        setResponseInterface(false);
        loader.setProgressListener(progressListener);
        try {
          for (Enumeration e = jad.getMidletEntries().elements(); e.hasMoreElements(); ) {
            JadMidletEntry jadEntry = (JadMidletEntry) e.nextElement();
            Class midletClass = loader.loadClass(jadEntry.getClassName());
            loadMidlet(jadEntry.getName(), midletClass);
          }
          notifyDestroyed();
        } catch (ClassNotFoundException ex) {
          System.err.println(ex);
        }        
        loader.setProgressListener(null);
        statusBar.setText("");
        setResponseInterface(true);
      }
      
    };
    
    task.start();
  }
  
  
  public String getAppProperty(String key)
  {
    return jad.getProperty(key);
  }

  
  public void notifyDestroyed()
  {
    startMidlet(launcher);
  }
  

  public void notifySoftkeyLabelsChanged()
  {
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
      J2SEDevice device = (J2SEDevice) deviceClass.newInstance();
      DeviceFactory.setDevice(device);
      device.init(emulatorContext);
      devicePanel.init();
      this.deviceEntry = entry;
      Image tmpImg = device.getNormalImage();
      Dimension size = new Dimension(tmpImg.getWidth(null), tmpImg.getHeight(null));
      size.width += 10;
      size.height += statusBar.getPreferredSize().height + 55;
      setSize(size);
      doLayout();
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
  
  
  public void setResponseInterface(boolean state)
  {
    menuOpenJADFile.setEnabled(state);
    menuOpenJADURL.setEnabled(state);
  }
  
  
  protected void processWindowEvent(WindowEvent e)
  {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      menuExitListener.actionPerformed(null);
    } else if (e.getID() == WindowEvent.WINDOW_ICONIFIED) {
      MIDletBridge.getMIDletAccess(launcher.getCurrentMIDlet()).pauseApp();
    } else if (e.getID() == WindowEvent.WINDOW_DEICONIFIED) {
      try {
        MIDletBridge.getMIDletAccess(launcher.getCurrentMIDlet()).startApp();
  		} catch (MIDletStateChangeException ex) {
        System.err.println(ex);
  		}
    }
  }

  
  public void startMidlet(MIDlet m)
  {
    try {
      MIDletBridge.getMIDletAccess(m).startApp();
		} catch (MIDletStateChangeException ex) {
      System.err.println(ex);
		}
  }
  
  public MIDlet loadMidlet(String name, Class midletClass)
	{
    MIDlet result;
    
    try {
      result = (MIDlet) midletClass.newInstance();
      launcher.addMIDletEntry(new MIDletEntry(name, result));
      launcher.setCurrentMIDlet(result);
    } catch (Exception ex) {
      System.out.println("Cannot initialize " + midletClass + " MIDlet class");
      System.out.println(ex);
      ex.printStackTrace();
      return null;
    }  
    
    return result;
	}

  
  public static void main(String args[])
  {
    Class uiClass = null;
    int uiFontSize = 11;
    try {
      uiClass = Class.forName(UIManager.getSystemLookAndFeelClassName ());
    } catch (ClassNotFoundException e) {}

    if (uiClass != null) {
      try {
        LookAndFeel customUI = (javax.swing.LookAndFeel)uiClass.newInstance();
        UIManager.setLookAndFeel(customUI);
      } catch (Exception e) {
        System.out.println("ERR_UIError");
      }
    } else{
      try {
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      } catch (Exception ex) {
        System.out.println("Failed loading Metal look and feel");
        System.out.println(ex);
        uiFontSize=11;
      }
    }
    
    if(uiFontSize>0) {
      java.awt.Font dialogPlain = new java.awt.Font("Dialog", java.awt.Font.PLAIN, uiFontSize);
      java.awt.Font serifPlain = new java.awt.Font("Serif", java.awt.Font.PLAIN, uiFontSize);
      java.awt.Font sansSerifPlain = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, uiFontSize); 
      java.awt.Font monospacedPlain = new java.awt.Font("Monospaced", java.awt.Font.PLAIN, uiFontSize); 
      UIManager.getDefaults().put ("Button.font", dialogPlain); 
      UIManager.getDefaults().put ("ToggleButton.font", dialogPlain); 
      UIManager.getDefaults().put ("RadioButton.font", dialogPlain); 
      UIManager.getDefaults().put ("CheckBox.font", dialogPlain); 
      UIManager.getDefaults().put ("ColorChooser.font", dialogPlain);
      UIManager.getDefaults().put ("ComboBox.font", dialogPlain); 
      UIManager.getDefaults().put ("Label.font", dialogPlain); 
      UIManager.getDefaults().put ("List.font", dialogPlain);
      UIManager.getDefaults().put ("MenuBar.font", dialogPlain); 
      UIManager.getDefaults().put ("MenuItem.font", dialogPlain); 
      UIManager.getDefaults().put ("RadioButtonMenuItem.font", dialogPlain);
      UIManager.getDefaults().put ("CheckBoxMenuItem.font", dialogPlain); 
      UIManager.getDefaults().put ("Menu.font", dialogPlain); 
      UIManager.getDefaults().put ("PopupMenu.font", dialogPlain);
      UIManager.getDefaults().put ("OptionPane.font", dialogPlain);
      UIManager.getDefaults().put ("Panel.font", dialogPlain); 
      UIManager.getDefaults().put ("ProgressBar.font", dialogPlain); 
      UIManager.getDefaults().put ("ScrollPane.font", dialogPlain); 
      UIManager.getDefaults().put ("Viewport.font", dialogPlain); 
      UIManager.getDefaults().put ("TabbedPane.font", dialogPlain); 
      UIManager.getDefaults().put ("Table.font", dialogPlain); 
      UIManager.getDefaults().put ("TableHeader.font", dialogPlain); 
      UIManager.getDefaults().put ("TextField.font", sansSerifPlain); 
      UIManager.getDefaults().put ("PasswordField.font", monospacedPlain);
      UIManager.getDefaults().put ("TextArea.font", monospacedPlain); 
      UIManager.getDefaults().put ("TextPane.font", serifPlain); 
      UIManager.getDefaults().put ("EditorPane.font", serifPlain); 
      UIManager.getDefaults().put ("TitledBorder.font", dialogPlain); 
      UIManager.getDefaults().put ("ToolBar.font", dialogPlain);
      UIManager.getDefaults().put ("ToolTip.font", sansSerifPlain); 
      UIManager.getDefaults().put ("Tree.font", dialogPlain); 
    }
    
    WebStart app = new WebStart();
    MIDletBridge.setMicroEmulator(app);
    MIDlet m = null;

    if (args.length > 0) {
      Class midletClass;
      try {
        midletClass = Class.forName(args[0]);
        m = app.loadMidlet("MIDlet", midletClass);
      } catch (ClassNotFoundException ex) {
        System.out.println("Cannot find " + args[0] + " MIDlet class");
      }
    } else {
      m = launcher;
    }
    
    if (app.initialized) {
      if (m != null) {
        app.startMidlet(m);
      }
      app.validate();
      app.setVisible(true);
    } else {
      System.exit(0);
    }
  }

}
