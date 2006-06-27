/*
 *  MicroEmulator
 *  Copyright (C) 2001-2005 Bartek Teodorczyk <barteo@barteo.net>
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

package net.barteo.me.gkey.device;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.microedition.midlet.MIDlet;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.microemu.app.Main;


public class Starter extends Main
{

    public static void main(String[] args)
    {
        Class uiClass = null;
        int uiFontSize = 11;
        try {
            uiClass = Class.forName(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
        }

        if (uiClass != null) {
            try {
                LookAndFeel customUI = (javax.swing.LookAndFeel) uiClass.newInstance();
                UIManager.setLookAndFeel(customUI);
            } catch (Exception e) {
                System.out.println("ERR_UIError");
            }
        } else {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception ex) {
                System.out.println("Failed loading Metal look and feel");
                System.out.println(ex);
                uiFontSize = 11;
            }
        }

        if (uiFontSize > 0) {
            java.awt.Font dialogPlain = new java.awt.Font("Dialog", java.awt.Font.PLAIN, uiFontSize);
            java.awt.Font serifPlain = new java.awt.Font("Serif", java.awt.Font.PLAIN, uiFontSize);
            java.awt.Font sansSerifPlain = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, uiFontSize);
            java.awt.Font monospacedPlain = new java.awt.Font("Monospaced", java.awt.Font.PLAIN, uiFontSize);
            UIManager.getDefaults().put("Button.font", dialogPlain);
            UIManager.getDefaults().put("ToggleButton.font", dialogPlain);
            UIManager.getDefaults().put("RadioButton.font", dialogPlain);
            UIManager.getDefaults().put("CheckBox.font", dialogPlain);
            UIManager.getDefaults().put("ColorChooser.font", dialogPlain);
            UIManager.getDefaults().put("ComboBox.font", dialogPlain);
            UIManager.getDefaults().put("Label.font", dialogPlain);
            UIManager.getDefaults().put("List.font", dialogPlain);
            UIManager.getDefaults().put("MenuBar.font", dialogPlain);
            UIManager.getDefaults().put("MenuItem.font", dialogPlain);
            UIManager.getDefaults().put("RadioButtonMenuItem.font", dialogPlain);
            UIManager.getDefaults().put("CheckBoxMenuItem.font", dialogPlain);
            UIManager.getDefaults().put("Menu.font", dialogPlain);
            UIManager.getDefaults().put("PopupMenu.font", dialogPlain);
            UIManager.getDefaults().put("OptionPane.font", dialogPlain);
            UIManager.getDefaults().put("Panel.font", dialogPlain);
            UIManager.getDefaults().put("ProgressBar.font", dialogPlain);
            UIManager.getDefaults().put("ScrollPane.font", dialogPlain);
            UIManager.getDefaults().put("Viewport.font", dialogPlain);
            UIManager.getDefaults().put("TabbedPane.font", dialogPlain);
            UIManager.getDefaults().put("Table.font", dialogPlain);
            UIManager.getDefaults().put("TableHeader.font", dialogPlain);
            UIManager.getDefaults().put("TextField.font", sansSerifPlain);
            UIManager.getDefaults().put("PasswordField.font", monospacedPlain);
            UIManager.getDefaults().put("TextArea.font", monospacedPlain);
            UIManager.getDefaults().put("TextPane.font", serifPlain);
            UIManager.getDefaults().put("EditorPane.font", serifPlain);
            UIManager.getDefaults().put("TitledBorder.font", dialogPlain);
            UIManager.getDefaults().put("ToolBar.font", dialogPlain);
            UIManager.getDefaults().put("ToolTip.font", sansSerifPlain);
            UIManager.getDefaults().put("Tree.font", dialogPlain);
        }
        
        Starter app = new Starter();
        app.setDevice(new GKeyJ2SEDevice());

        MIDlet m = null;

        for (int i = 0; i < args.length; i++) {
            if (m == null && args[i].endsWith(".jad")) {
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
