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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.app.launcher.Launcher;
import com.barteo.emulator.app.ui.ResponseInterfaceListener;
import com.barteo.emulator.app.ui.StatusBarListener;
import com.barteo.emulator.app.ui.swing.ExtensionFileFilter;
import com.barteo.emulator.app.ui.swing.SwingDeviceComponent;
import com.barteo.emulator.app.ui.swing.SwingDialogWindow;
import com.barteo.emulator.app.ui.swing.SwingSelectDevicePanel;
import com.barteo.emulator.app.util.DeviceEntry;
import com.barteo.emulator.app.util.ProgressJarClassLoader;
import com.barteo.emulator.device.DeviceDisplay;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.FontManager;
import com.barteo.emulator.device.InputMethod;
import com.barteo.emulator.device.j2se.J2SEDevice;
import com.barteo.emulator.device.j2se.J2SEDeviceDisplay;
import com.barteo.emulator.device.j2se.J2SEFontManager;
import com.barteo.emulator.device.j2se.J2SEInputMethod;


public class Main extends JFrame
{
  Main instance = null;
  
  protected Common common;
  
  protected boolean initialized = false;
  
  SwingSelectDevicePanel selectDevicePanel = null;
  JFileChooser fileChooser = null;
  JMenuItem menuOpenJADFile;
  JMenuItem menuOpenJADURL;
	JMenuItem menuSelectDevice;
	    
  SwingDeviceComponent devicePanel;
  DeviceEntry deviceEntry;

  JLabel statusBar = new JLabel("Status");
  
  private EmulatorContext emulatorContext = new EmulatorContext()
  {
    private ProgressJarClassLoader loader = new ProgressJarClassLoader();
    
    private InputMethod inputMethod = new J2SEInputMethod();
    
    private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);
    
    private FontManager fontManager = new J2SEFontManager();
    
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
      String entered = JOptionPane.showInputDialog(instance, "Enter JAD URL:");
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
      if (SwingDialogWindow.show(instance, "Select device...", selectDevicePanel)) {
        if (selectDevicePanel.getSelectedDeviceEntry().equals(getDevice())) {
          return;
        }
        if (MIDletBridge.getCurrentMIDlet() != common.getLauncher()) {
          if (JOptionPane.showConfirmDialog(instance, 
              "Changing device needs MIDlet to be restarted. All MIDlet data will be lost. Are you sure?", 
              "Question?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != 0) {
            return;
          }
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


  public Main()
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
    
    menuSelectDevice = new JMenuItem("Select device...");
    menuSelectDevice.addActionListener(menuSelectDeviceListener);
    menuOptions.add(menuSelectDevice);

    menuBar.add(menuFile);
    menuBar.add(menuOptions);
    setJMenuBar(menuBar);
    
    setTitle("MicroEmulator");
    addWindowListener(windowListener);
    
    Config.loadConfig("config.xml");
    addKeyListener(keyListener);

    devicePanel = new SwingDeviceComponent();
    selectDevicePanel = new SwingSelectDevicePanel();
    
	common = new Common(emulatorContext);
	common.setStatusBarListener(statusBarListener);
	common.setResponseInterfaceListener(responseInterfaceListener);

	setDevice(selectDevicePanel.getSelectedDeviceEntry());

    getContentPane().add(devicePanel, "Center");
    getContentPane().add(statusBar, "South");    

    initialized = true;
  }
  
  
  public DeviceEntry getDevice()
  {
    return deviceEntry;
  }
  
  
  public void setDevice(DeviceEntry entry)
  {
		if (DeviceFactory.getDevice() != null) {
//			((J2SEDevice) DeviceFactory.getDevice()).dispose();
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
      	J2SEDevice device = (J2SEDevice) deviceClass.newInstance();
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
  
  
	protected void setDevice(J2SEDevice device) 
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
    
    Main app = new Main();
    MIDlet m = null;

    	for (int i = 0; i < args.length; i++) {
    		if (args[i].equals("-d")) {
    			i++;
    			if (i < args.length) {
					try {
						Class deviceClass = Class.forName(args[i]);
						app.setDevice((J2SEDevice) deviceClass.newInstance());
					} catch (ClassNotFoundException ex) {
						ex.printStackTrace();
					} catch (InstantiationException ex) {
						ex.printStackTrace();
					} catch (IllegalAccessException ex) {
						ex.printStackTrace();
					}
    			}
    		} else if (m == null && args[i].endsWith(".jad")) {
				try {
					File file = new File(args[i]);
					URL url = file.exists() ? file.toURL() : new URL(args[0]);
					app.common.openJadFile(url);
				} catch (MalformedURLException exception) {
					System.out.println("Cannot parse " + args[i] + " URL");
				}
			} else {
				Class midletClass;
				try {
					midletClass = Class.forName(args[i]);
					m = app.common.loadMidlet("MIDlet", midletClass);
				} catch (ClassNotFoundException ex) {
					System.out.println("Cannot find " + args[i] + " MIDlet class");
				}
			}
		}
    	
    	if (m == null) {
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
