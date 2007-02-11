/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 MicroEmulator Team.
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
 *
 *  @version $Id$
 */
package org.microemu;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

/**
 * @author vlads
 * 
 */
public class WebStart extends JFrame {

	private static final long serialVersionUID = 1L;

	JTextArea tx;
	
	File recentDirectory;

	public WebStart() throws HeadlessException {
		super("ME2 bytecode Preporcessor");

		Dimension size = new Dimension(200, 200);
		setSize(size);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);

		JMenuItem menuStart = new JMenuItem("Start Internal app");
		menuStart.addActionListener(new MenuStartInternalListener());
		menuFile.add(menuStart);

		JMenuItem menuOpenJADFile = new JMenuItem("Open JAD File...");
		menuOpenJADFile.addActionListener(new MenuOpenJADFileListener());
		menuFile.add(menuOpenJADFile);

		getContentPane().add(tx = new JTextArea(), "Center");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private class MenuStartInternalListener extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent event) {
			tx.setText("Start Internal app...\n");
			runApp(null);
		}
	}

	private class MenuOpenJADFileListener extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ev) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Open JAR File...");
			if (recentDirectory == null) {
				recentDirectory =new File(new File(new File("..").getAbsoluteFile(), "bytecode-test-app"), "target");
			}
			fileChooser.setCurrentDirectory(recentDirectory);

			int returnVal = fileChooser.showOpenDialog(WebStart.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					recentDirectory = fileChooser.getCurrentDirectory().getAbsoluteFile();
					openJar(fileChooser.getSelectedFile().toURL());
				} catch (Throwable e) {
					System.err.println("Cannot load " + fileChooser.getSelectedFile().getName());
				}
			}
		}
	};

	private void openJar(URL path) {
		tx.setText("Open " + path + " \n");
		runApp(path);
	}

	
	public void runApp(URL path) {
		tx.append("Start Class loader test...\n");
		
		System.out.println("ClassLoader " + this.getClass().getClassLoader().hashCode() +  " WebStart");
		
		try {
			SystemProperties.setProperty("microedition.platform", "MicroEmulator-Test");

			PreporcessorClassLoader cl = new PreporcessorClassLoader(PreporcessorTest.class.getClassLoader());
			cl.disableClassLoad(SystemProperties.class);
			cl.disableClassLoad(ResourceLoader.class);
			ResourceLoader.classLoader = cl;
			
			System.out.println("ClassLoader " + cl.hashCode() +  " <-- PreporcessorClassLoader");

			if (path!= null) {
				cl.addURL(path);
			} else {
				cl.addClassURL(PreporcessorTest.TEST_CLASS);
			}

			tx.append("ClassLoader created...\n");

			Class instrumentedClass = cl.loadClass(PreporcessorTest.TEST_CLASS);
			Runnable instrumentedInstance = (Runnable) instrumentedClass.newInstance();
			instrumentedInstance.run();

			tx.append("Looks good!\n");

		} catch (Throwable e) {
			tx.append("Error " + e.toString());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		WebStart app = new WebStart();
		app.validate();
		app.setVisible(true);
	}

}
