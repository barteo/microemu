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

package microemulator.src.com.barteo.emulator.app;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.LookAndFeel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;


public class Main extends JFrame 
{

  ActionListener menuExitListener = new ActionListener()
  {
    
    public void actionPerformed(ActionEvent e)
    {
      System.exit(0);
    }
    
  };
  
  
  Main()
  {
    JMenuBar menuBar = new JMenuBar();
    
    JMenu menu = new JMenu("File");
    
    JMenuItem menuItem = new JMenuItem("Open JAD File...");
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Open JAD URL...");
    menu.add(menuItem);
    
    menu.addSeparator();
    
    menuItem = new JMenuItem("Exit");
    menuItem.addActionListener(menuExitListener);
    menu.add(menuItem);

    menuBar.add(menu);
    setJMenuBar(menuBar);
    
    setTitle("MicroEmulator");
    setSize(200, 400);
  }
  
  
  protected void processWindowEvent(WindowEvent e)
  {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      menuExitListener.actionPerformed(null);
    }
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

    app.validate();
    app.setVisible(true);
  }

}
