/*
 * @(#)Main.java  12/12/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.barteo.emulator.app;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.LookAndFeel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import com.barteo.emulator.device.Device;
import com.barteo.emulator.MicroEmulator;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.MIDletEntry;
import com.barteo.emulator.app.launcher.Launcher;
import com.barteo.midp.lcdui.DisplayBridge;
import com.barteo.midp.lcdui.FontManager;
import com.barteo.midp.lcdui.KeyboardComponent;
import com.barteo.midp.lcdui.XYConstraints;
import com.barteo.midp.lcdui.XYLayout;


public class Main extends JFrame implements MicroEmulator
{
  
  Main instance = null;
  
  boolean initialized = false;
  
  JFileChooser fileChooser = null;
  
	SwingDisplayComponent dc;
	KeyboardComponent kc;

  Launcher launcher;
  
  MIDlet midlet;

  ActionListener menuOpenJADFileListener = new ActionListener()
  {

    public void actionPerformed(ActionEvent e)
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
        System.out.println("You chose to open this file: " + 
            fileChooser.getSelectedFile().getName());       
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
  
  
  Main()
  {
    instance = this;
    
    JMenuBar menuBar = new JMenuBar();
    
    JMenu menu = new JMenu("File");
    
    JMenuItem menuItem;
/*    menuItem = new JMenuItem("Open JAD File...");
    menuItem.addActionListener(menuOpenJADFileListener);
    menu.add(menuItem);
    
    menu.addSeparator();*/
    
    menuItem = new JMenuItem("Exit");
    menuItem.addActionListener(menuExitListener);
    menu.add(menuItem);

    menuBar.add(menu);
    setJMenuBar(menuBar);
    
    setTitle("MicroEmulator");
    
    Font defaultFont = new Font("SansSerif", Font.PLAIN, 11);
    setFont(defaultFont);
    FontManager.getInstance().setDefaultFontMetrics(getFontMetrics(defaultFont));

    launcher = new Launcher();
    midlet = launcher;
 
    if (!Device.getInstance().isInitialized()) {
      System.out.println("Cannot initialize device configuration");
      return;
    }
    
    XYLayout xy = new XYLayout();
    getContentPane().setLayout(xy);

    dc = new SwingDisplayComponent(this);
    xy.addLayoutComponent(dc, new XYConstraints(Device.screenRectangle));
    getContentPane().add(dc);

    kc = new KeyboardComponent();
    xy.addLayoutComponent(kc, new XYConstraints(Device.keyboardRectangle));
    getContentPane().add(kc);      

    setSize(Device.deviceRectangle.getSize());
    initialized = true;
  }
  
  
  public void notifyDestroyed()
  {
    launcher.startApp();
  }
  
  
  protected void processWindowEvent(WindowEvent e)
  {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      menuExitListener.actionPerformed(null);
    } else if (e.getID() == WindowEvent.WINDOW_ICONIFIED) {
      MIDletBridge.getAccess(midlet).pauseApp();
    } else if (e.getID() == WindowEvent.WINDOW_DEICONIFIED) {
      try {
        MIDletBridge.getAccess(midlet).startApp();
  		} catch (MIDletStateChangeException ex) {
        System.err.println(ex);
  		}
    }
  }

  
  public void start()
  {
    try {
      MIDletBridge.getAccess(midlet).startApp();
		} catch (MIDletStateChangeException ex) {
      System.err.println(ex);
		}
  }
  
  
  public boolean setMidletClass(String name)
	{
    Class midletClass;
		try {
			midletClass = Class.forName(name);
		} catch (ClassNotFoundException ex) {
			System.out.println("Cannot find " + name + " MIDlet class");
			return false;
		}

    try {
      midlet = (MIDlet) midletClass.newInstance();
    } catch (Exception ex) {
      System.out.println("Cannot initialize " + midletClass + " MIDlet class");
      System.out.println(ex);
      ex.printStackTrace();
      return false;
    }
    
    launcher.addMIDletEntry(new MIDletEntry("MIDlet", midlet));
    
    return true;
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
      UIManager.getDefaults ().put ("Button.font", dialogPlain); 
      UIManager.getDefaults ().put ("ToggleButton.font", dialogPlain); 
      UIManager.getDefaults ().put ("RadioButton.font", dialogPlain); 
      UIManager.getDefaults ().put ("CheckBox.font", dialogPlain); 
      UIManager.getDefaults ().put ("ColorChooser.font", dialogPlain);
      UIManager.getDefaults ().put ("ComboBox.font", dialogPlain); 
      UIManager.getDefaults ().put ("Label.font", dialogPlain); 
      UIManager.getDefaults ().put ("List.font", dialogPlain);
      UIManager.getDefaults ().put ("MenuBar.font", dialogPlain); 
      UIManager.getDefaults ().put ("MenuItem.font", dialogPlain); 
      UIManager.getDefaults ().put ("RadioButtonMenuItem.font", dialogPlain);
      UIManager.getDefaults ().put ("CheckBoxMenuItem.font", dialogPlain); 
      UIManager.getDefaults ().put ("Menu.font", dialogPlain); 
      UIManager.getDefaults ().put ("PopupMenu.font", dialogPlain);
      UIManager.getDefaults ().put ("OptionPane.font", dialogPlain);
      UIManager.getDefaults ().put ("Panel.font", dialogPlain); 
      UIManager.getDefaults ().put ("ProgressBar.font", dialogPlain); 
      UIManager.getDefaults ().put ("ScrollPane.font", dialogPlain); 
      UIManager.getDefaults ().put ("Viewport.font", dialogPlain); 
      UIManager.getDefaults ().put ("TabbedPane.font", dialogPlain); 
      UIManager.getDefaults ().put ("Table.font", dialogPlain); 
      UIManager.getDefaults ().put ("TableHeader.font", dialogPlain); 
      UIManager.getDefaults ().put ("TextField.font", sansSerifPlain); 
      UIManager.getDefaults ().put ("PasswordField.font", monospacedPlain);
      UIManager.getDefaults ().put ("TextArea.font", monospacedPlain); 
      UIManager.getDefaults ().put ("TextPane.font", serifPlain); 
      UIManager.getDefaults ().put ("EditorPane.font", serifPlain); 
      UIManager.getDefaults ().put ("TitledBorder.font", dialogPlain); 
      UIManager.getDefaults ().put ("ToolBar.font", dialogPlain);
      UIManager.getDefaults ().put ("ToolTip.font", sansSerifPlain); 
      UIManager.getDefaults ().put ("Tree.font", dialogPlain); 
    }
    
    Main app = new Main();
    MIDletBridge.setMicroEmulator(app);


    if (args.length > 0) {
      app.setMidletClass(args[0]);
    }
    
    app.start();

    if (app.initialized) {
      app.validate();
      app.setVisible(true);
    }
  }

}
