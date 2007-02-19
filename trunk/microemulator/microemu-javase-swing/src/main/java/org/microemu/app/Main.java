/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.ui.swing.DropTransferHandler;
import org.microemu.app.ui.swing.ExtensionFileFilter;
import org.microemu.app.ui.swing.JadUrlPanel;
import org.microemu.app.ui.swing.SwingDeviceComponent;
import org.microemu.app.ui.swing.SwingDialogWindow;
import org.microemu.app.ui.swing.SwingErrorMessageDialogPanel;
import org.microemu.app.ui.swing.SwingSelectDevicePanel;
import org.microemu.app.util.DeviceEntry;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.log.Logger;

public class Main extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private Main instance = null;

	protected Common common;

	protected SwingSelectDevicePanel selectDevicePanel = null;

	private JadUrlPanel jadUrlPanel;

	private JFileChooser fileChooser = null;

	private JMenuItem menuOpenJADFile;

	private JMenuItem menuOpenJADURL;

	private JMenuItem menuSelectDevice;

	private SwingDeviceComponent devicePanel;

	private DeviceEntry deviceEntry;

	private JLabel statusBar = new JLabel("Status");

	protected EmulatorContext emulatorContext = new EmulatorContext() {
		
		private InputMethod inputMethod = new J2SEInputMethod();

		private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);

		private FontManager fontManager = new J2SEFontManager();

		public DisplayComponent getDisplayComponent() {
			return devicePanel.getDisplayComponent();
		}

		public InputMethod getDeviceInputMethod() {
			return inputMethod;
		}

		public DeviceDisplay getDeviceDisplay() {
			return deviceDisplay;
		}

		public FontManager getDeviceFontManager() {
			return fontManager;
		}
	};

	private ActionListener menuOpenJADFileListener = new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			if (fileChooser == null) {
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("JAD files");
				fileFilter.addExtension("jad");
				// TODO- Read manifest in jar
				// fileFilter.addExtension("jar");
				fileChooser = new JFileChooser();
				fileChooser.setFileFilter(fileFilter);
				fileChooser.setDialogTitle("Open JAD File...");
				fileChooser.setCurrentDirectory(new File(Config.getRecentJadDirectory()));
			}

			int returnVal = fileChooser.showOpenDialog(instance);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					Config.setRecentJadDirectory(fileChooser.getCurrentDirectory().getAbsolutePath());
					Config.saveConfig();

					Common.openJadUrl(fileChooser.getSelectedFile().toURL().toString());
				} catch (IOException e) {
					Logger.error("Cannot load", fileChooser.getSelectedFile().getName(), e);
				}
			}
		}
	};

	private ActionListener menuOpenJADURLListener = new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			if (SwingDialogWindow.show(Main.this, "Enter JAD URL:", jadUrlPanel)) {
				try {
					Common.openJadUrl(jadUrlPanel.getText());
				} catch (IOException ex) {
					Logger.error("Cannot load ", jadUrlPanel.getText());
				}
			}
		}
	};

	private ActionListener menuCloseMidletListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (MIDletBridge.getCurrentMIDlet() != common.getLauncher()) {
				common.startLauncher(MIDletBridge.getMIDletAccess());
			}
		}
	};

	private ActionListener menuExitListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Config.setWindowX(Main.this.getX());
			Config.setWindowY(Main.this.getY());
			Config.saveConfig();

			System.exit(0);
		}
	};

	private ActionListener menuSelectDeviceListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (SwingDialogWindow.show(instance, "Select device...", selectDevicePanel)) {
				if (selectDevicePanel.getSelectedDeviceEntry().equals(deviceEntry)) {
					return;
				}
				int restartMidlet = 1;
				if (MIDletBridge.getCurrentMIDlet() != common.getLauncher()) {
					restartMidlet = JOptionPane.showConfirmDialog(instance,
							"Changing device may trigger MIDlet to the unpredictable state and restart of MIDlet is recommended. \n"
									+ "Do you want to restart the MIDlet? All MIDlet data will be lost.", "Question?",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				}
				if (!setDevice(selectDevicePanel.getSelectedDeviceEntry())) {
					return;
				}
				if (restartMidlet == 0) {
					try {
						common.startMidlet(MIDletBridge.getCurrentMIDlet().getClass(), MIDletBridge.getMIDletAccess());
					} catch (Exception ex) {
						System.err.println(ex);
					}
				} else {
					DeviceDisplay deviceDisplay = DeviceFactory.getDevice().getDeviceDisplay();
					DisplayAccess da = MIDletBridge.getMIDletAccess().getDisplayAccess();
					if (da != null) {
						da.sizeChanged(deviceDisplay.getWidth(), deviceDisplay.getHeight());
						deviceDisplay.repaint(0, 0, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
					}
				}
			}
		}
	};

	private StatusBarListener statusBarListener = new StatusBarListener() {
		public void statusBarChanged(String text) {
			statusBar.setText(text);
		}
	};

	private ResponseInterfaceListener responseInterfaceListener = new ResponseInterfaceListener() {
		public void stateChanged(boolean state) {
			menuOpenJADFile.setEnabled(state);
			menuOpenJADURL.setEnabled(state);
			menuSelectDevice.setEnabled(state);
		}
	};

	private WindowAdapter windowListener = new WindowAdapter() {
		public void windowClosing(WindowEvent ev) {
			menuExitListener.actionPerformed(null);
		}

		public void windowIconified(WindowEvent ev) {
			MIDletBridge.getMIDletAccess(MIDletBridge.getCurrentMIDlet()).pauseApp();
		}

		public void windowDeiconified(WindowEvent ev) {
			try {
				MIDletBridge.getMIDletAccess(MIDletBridge.getCurrentMIDlet()).startApp();
			} catch (MIDletStateChangeException ex) {
				System.err.println(ex);
			}
		}
	};

	public Main() {
		this(null);
	}

	public Main(DeviceEntry defaultDevice) {
		instance = this;

		JMenuBar menuBar = new JMenuBar();

		JMenu menuFile = new JMenu("File");

		menuOpenJADFile = new JMenuItem("Open JAD File...");
		menuOpenJADFile.addActionListener(menuOpenJADFileListener);
		menuFile.add(menuOpenJADFile);

		menuOpenJADURL = new JMenuItem("Open JAD URL...");
		menuOpenJADURL.addActionListener(menuOpenJADURLListener);
		menuFile.add(menuOpenJADURL);

		JMenuItem menuItemTmp = new JMenuItem("Close MIDlet");
		menuItemTmp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		menuItemTmp.addActionListener(menuCloseMidletListener);
		menuFile.add(menuItemTmp);

		menuFile.addSeparator();

		JMenuItem menuItem = new JMenuItem("Exit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
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

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/org/microemu/icon.png")));

		addWindowListener(windowListener);

		Config.loadConfig(defaultDevice, emulatorContext);

		this.setLocation(Config.getWindowX(), Config.getWindowY());

		getContentPane().add(createContents(getContentPane()), "Center");

		selectDevicePanel = new SwingSelectDevicePanel(emulatorContext);
		jadUrlPanel = new JadUrlPanel();

		this.common = new Common(emulatorContext);
		this.common.setStatusBarListener(statusBarListener);
		this.common.setResponseInterfaceListener(responseInterfaceListener);

		getContentPane().add(statusBar, "South");

		Message.addListener(new SwingErrorMessageDialogPanel(this));

		devicePanel.setTransferHandler(new DropTransferHandler());
		
		this.setResizable(false);
	}

	protected Component createContents(Container parent) {
		devicePanel = new SwingDeviceComponent();
		devicePanel.addKeyListener(devicePanel);
		addKeyListener(devicePanel);

		return devicePanel;
	}

	public boolean setDevice(DeviceEntry entry) {
		if (DeviceFactory.getDevice() != null) {
			//			((J2SEDevice) DeviceFactory.getDevice()).dispose();
		}
		final String errorTitle = "Error creating device";
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			if (entry.getFileName() != null) {
				URL[] urls = new URL[1];
				urls[0] = new File(Config.getConfigPath(), entry.getFileName()).toURL();
				classLoader = Common.createExtensionsClassLoader(urls);
			}

			// TODO font manager have to be moved from emulatorContext into device
			emulatorContext.getDeviceFontManager().init();

			Device device = Device.create(emulatorContext, classLoader, entry.getDescriptorLocation());
			this.deviceEntry = entry;
			common.setDevice(device);
			updateDevice();
			return true;
		} catch (MalformedURLException e) {
			Message.error(errorTitle, errorTitle + ", " + Message.getCauseMessage(e), e);
		} catch (IOException e) {
			Message.error(errorTitle, errorTitle + ", " + Message.getCauseMessage(e), e);
		} catch (Throwable e) {
			Message.error(errorTitle, errorTitle + ", " + Message.getCauseMessage(e), e);
		}
		return false;
	}

	protected void updateDevice() {
		devicePanel.init();
		pack();
	}

	public static void main(String args[]) {
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
				Logger.error("ERR_UIError", e);
			}
		} else {
			try {
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			} catch (Exception ex) {
				Logger.error("Failed loading Metal look and feel", ex);
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

		List params = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			params.add(args[i]);
		}

		Main app = new Main();
		app.common.initDevice(params, app.selectDevicePanel.getSelectedDeviceEntry());
		app.updateDevice();

		app.validate();
		app.setVisible(true);

		app.common.initMIDlet(params, false);
	}

}
