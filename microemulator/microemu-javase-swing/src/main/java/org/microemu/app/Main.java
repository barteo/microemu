/**
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.app.capture.AnimatedGifEncoder;
import org.microemu.app.classloader.MIDletClassLoader;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.ui.swing.DropTransferHandler;
import org.microemu.app.ui.swing.ExtensionFileFilter;
import org.microemu.app.ui.swing.JMRUMenu;
import org.microemu.app.ui.swing.JadUrlPanel;
import org.microemu.app.ui.swing.ResizeDeviceDisplayDialog;
import org.microemu.app.ui.swing.SwingAboutDialog;
import org.microemu.app.ui.swing.SwingDeviceComponent;
import org.microemu.app.ui.swing.SwingDialogWindow;
import org.microemu.app.ui.swing.SwingDisplayComponent;
import org.microemu.app.ui.swing.SwingErrorMessageDialogPanel;
import org.microemu.app.ui.swing.SwingLogConsoleDialog;
import org.microemu.app.ui.swing.SwingSelectDevicePanel;
import org.microemu.app.util.AppletProducer;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.IOUtils;
import org.microemu.app.util.MidletURLReference;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.MutableImage;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.device.j2se.J2SEMutableImage;
import org.microemu.log.Logger;
import org.microemu.log.QueueAppender;
import org.microemu.util.JadMidletEntry;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;

	protected Common common;

	protected SwingSelectDevicePanel selectDevicePanel = null;

	private JadUrlPanel jadUrlPanel = null;

	private JFileChooser saveForWebChooser;

	private JFileChooser fileChooser = null;

	private JFileChooser captureFileChooser = null;

	private JMenuItem menuOpenJADFile;

	private JMenuItem menuOpenJADURL;

	private JMenuItem menuSelectDevice;

	private JMenuItem menuSaveForWeb;

	private JMenuItem menuStartCapture;

	private JMenuItem menuStopCapture;

	private JCheckBoxMenuItem menuMIDletNetworkConnection;

	private JCheckBoxMenuItem menuLogConsole;

	private SwingDeviceComponent devicePanel;

	private SwingLogConsoleDialog logConsoleDialog;

	private QueueAppender logQueueAppender;

	private DeviceEntry deviceEntry;

	private AnimatedGifEncoder encoder;

	private JLabel statusBar = new JLabel("Status");

	private JButton resizeButton = new JButton("Resize");

	private ResizeDeviceDisplayDialog resizeDeviceDisplayDialog = null;

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
				fileChooser.setCurrentDirectory(new File(Config.getRecentDirectory("recentJadDirectory")));
			}

			int returnVal = fileChooser.showOpenDialog(Main.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				Config.setRecentDirectory("recentJadDirectory", fileChooser.getCurrentDirectory().getAbsolutePath());
				String url = IOUtils.getCanonicalFileURL(fileChooser.getSelectedFile());
				Common.openJadUrlSafe(url);
			}
		}
	};

	private ActionListener menuOpenJADURLListener = new ActionListener() {
		public void actionPerformed(ActionEvent ev) {
			if (jadUrlPanel == null) {
				jadUrlPanel = new JadUrlPanel();
			}
			if (SwingDialogWindow.show(Main.this, "Enter JAD URL:", jadUrlPanel, true)) {
				Common.openJadUrlSafe(jadUrlPanel.getText());
			}
		}
	};

	private ActionListener menuCloseMidletListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			common.startLauncher(MIDletBridge.getMIDletContext());
		}
	};

	private ActionListener menuSaveForWebListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (saveForWebChooser == null) {
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("HTML files");
				fileFilter.addExtension("html");
				saveForWebChooser = new JFileChooser();
				saveForWebChooser.setFileFilter(fileFilter);
				saveForWebChooser.setDialogTitle("Save for Web...");
				saveForWebChooser.setCurrentDirectory(new File(Config.getRecentDirectory("recentSaveForWebDirectory")));
			}
			if (saveForWebChooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
				Config.setRecentDirectory("recentSaveForWebDirectory", saveForWebChooser.getCurrentDirectory()
						.getAbsolutePath());
				File pathFile = saveForWebChooser.getSelectedFile().getParentFile();

				String name = saveForWebChooser.getSelectedFile().getName();
				if (!name.toLowerCase().endsWith(".html") && name.indexOf('.') == -1) {
					name = name + ".html";
				}

				// try to get from distribution home location
				String resource = MIDletClassLoader.getClassResourceName(this.getClass().getName());
				URL url = this.getClass().getClassLoader().getResource(resource);
				String path = url.getPath();
				int prefix = path.indexOf(':');
				String mainJarFileName = path.substring(prefix + 1, path.length() - resource.length());
				File appletJarDir = new File(new File(mainJarFileName).getParent(), "lib");
				File appletJarFile = new File(appletJarDir, "microemu-javase-applet.jar");
				if (!appletJarFile.exists()) {
					appletJarFile = null;
				}

				if (appletJarFile == null) {
					// try to get from maven2 repository
					/*
					 * loc/org/microemu/microemulator/2.0.1-SNAPSHOT/microemulator-2.0.1-20070227.080140-1.jar
					 * String version = doRegExpr(mainJarFileName, ); String
					 * basePath = "loc/org/microemu/" appletJarFile = new
					 * File(basePath + "microemu-javase-applet/" + version +
					 * "/microemu-javase-applet" + version + ".jar"); if
					 * (!appletJarFile.exists()) { appletJarFile = null; }
					 */
				}

				if (appletJarFile == null) {
					ExtensionFileFilter fileFilter = new ExtensionFileFilter("JAR packages");
					fileFilter.addExtension("jar");
					JFileChooser appletChooser = new JFileChooser();
					appletChooser.setFileFilter(fileFilter);
					appletChooser.setDialogTitle("Select MicroEmulator applet jar package...");
					appletChooser.setCurrentDirectory(new File(Config.getRecentDirectory("recentAppletJarDirectory")));
					if (appletChooser.showOpenDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
						Config.setRecentDirectory("recentAppletJarDirectory", appletChooser.getCurrentDirectory()
								.getAbsolutePath());
						appletJarFile = appletChooser.getSelectedFile();
					} else {
						return;
					}
				}

				JadMidletEntry jadMidletEntry;
				Iterator it = common.jad.getMidletEntries().iterator();
				if (it.hasNext()) {
					jadMidletEntry = (JadMidletEntry) it.next();
				} else {
					Message.error("MIDlet Suite has no entries");
					return;
				}

				String midletInput = common.jad.getJarURL();
				DeviceEntry deviceInput = selectDevicePanel.getSelectedDeviceEntry();
				if (deviceInput != null && deviceInput.getDescriptorLocation().equals(DeviceImpl.DEFAULT_LOCATION)) {
					deviceInput = null;
				}

				File htmlOutputFile = new File(pathFile, name);
				if (!allowOverride(htmlOutputFile)) {
					return;
				}
				File appletPackageOutputFile = new File(pathFile, "microemu-javase-applet.jar");
				if (!allowOverride(appletPackageOutputFile)) {
					return;
				}
				File midletOutputFile = new File(pathFile, midletInput.substring(midletInput.lastIndexOf("/") + 1));
				if (!allowOverride(midletOutputFile)) {
					return;
				}
				File deviceOutputFile = null;
				String deviceDescriptorLocation = null;
				if (deviceInput != null) {
					deviceOutputFile = new File(pathFile, deviceInput.getFileName());
					if (!allowOverride(deviceOutputFile)) {
						return;
					}
					deviceDescriptorLocation = deviceInput.getDescriptorLocation();
				}

				try {
					AppletProducer.createHtml(htmlOutputFile, DeviceFactory.getDevice(), jadMidletEntry.getClassName(),
							midletOutputFile, appletPackageOutputFile, deviceOutputFile, deviceDescriptorLocation);
					AppletProducer.createMidlet(midletInput, midletOutputFile);
					IOUtils.copyFile(appletJarFile, appletPackageOutputFile);
					if (deviceInput != null) {
						IOUtils.copyFile(new File(Config.getConfigPath(), deviceInput.getFileName()), deviceOutputFile);
					}
				} catch (IOException ex) {
					Logger.error(ex);
				}
			}
		}

		private boolean allowOverride(File file) {
			if (file.exists()) {
				int answer = JOptionPane.showConfirmDialog(Main.this, "Override the file:" + file + "?", "Question?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (answer == 1 /* no */) {
					return false;
				}
			}

			return true;
		}
	};

	private ActionListener menuStartCaptureListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (captureFileChooser == null) {
				ExtensionFileFilter fileFilter = new ExtensionFileFilter("GIF files");
				fileFilter.addExtension("gif");
				captureFileChooser = new JFileChooser();
				captureFileChooser.setFileFilter(fileFilter);
				captureFileChooser.setDialogTitle("Capture to GIF File...");
				captureFileChooser.setCurrentDirectory(new File(Config.getRecentDirectory("recentCaptureDirectory")));
			}

			if (captureFileChooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
				Config.setRecentDirectory("recentCaptureDirectory", captureFileChooser.getCurrentDirectory()
						.getAbsolutePath());
				String name = captureFileChooser.getSelectedFile().getName();
				if (!name.toLowerCase().endsWith(".gif") && name.indexOf('.') == -1) {
					name = name + ".gif";
				}
				File captureFile = new File(captureFileChooser.getSelectedFile().getParentFile(), name);
				if (!allowOverride(captureFile)) {
					return;
				}

				encoder = new AnimatedGifEncoder();
				encoder.start(captureFile.getAbsolutePath());

				menuStartCapture.setEnabled(false);
				menuStopCapture.setEnabled(true);

				emulatorContext.getDisplayComponent().addDisplayRepaintListener(new DisplayRepaintListener() {
					long start = 0;

					public void repaintInvoked(MutableImage image) {
						synchronized (Main.this) {
							if (encoder != null) {
								if (start == 0) {
									start = System.currentTimeMillis();
								} else {
									long current = System.currentTimeMillis();
									encoder.setDelay((int) (current - start));
									start = current;
								}

								encoder.addFrame((BufferedImage) ((J2SEMutableImage) image).getImage());
							}
						}
					}
				});
			}
		}

		private boolean allowOverride(File file) {
			if (file.exists()) {
				int answer = JOptionPane.showConfirmDialog(Main.this, "Override the file:" + file + "?", "Question?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (answer == 1 /* no */) {
					return false;
				}
			}

			return true;
		}
	};

	private ActionListener menuStopCaptureListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			menuStopCapture.setEnabled(false);

			synchronized (Main.this) {
				encoder.finish();
				encoder = null;
			}

			menuStartCapture.setEnabled(true);
		}
	};

	private ActionListener menuMIDletNetworkConnectionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			org.microemu.cldc.http.Connection.setAllowNetworkConnection(menuMIDletNetworkConnection.getState());
		}

	};

	private ActionListener menuLogConsoleListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (logConsoleDialog == null) {
				logConsoleDialog = new SwingLogConsoleDialog(Main.this, Main.this.logQueueAppender);
				logConsoleDialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						menuLogConsole.setState(false);
					}
				});
				logConsoleDialog.pack();
				Rectangle window = Config.getWindow("logConsole", new Rectangle(0, 0, 640, 240));
				logConsoleDialog.setBounds(window.x, window.y, window.width, window.height);
			}
			logConsoleDialog.setVisible(!logConsoleDialog.isVisible());
		}
	};

	private ActionListener menuAboutListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SwingDialogWindow.show(Main.this, "About", new SwingAboutDialog(), false);
		}
	};

	private ActionListener menuExitListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			synchronized (Main.this) {
				if (encoder != null) {
					encoder.finish();
					encoder = null;
				}
			}

			if (logConsoleDialog != null) {
				Config.setWindow("logConsole", new Rectangle(logConsoleDialog.getX(), logConsoleDialog.getY(),
						logConsoleDialog.getWidth(), logConsoleDialog.getHeight()));
			}
			Config.setWindow("main", new Rectangle(Main.this.getX(), Main.this.getY(), Main.this.getWidth(), Main.this
					.getHeight()));

			System.exit(0);
		}
	};

	private ActionListener menuSelectDeviceListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (SwingDialogWindow.show(Main.this, "Select device...", selectDevicePanel, true)) {
				if (selectDevicePanel.getSelectedDeviceEntry().equals(deviceEntry)) {
					return;
				}
				int restartMidlet = 1;
				if (MIDletBridge.getCurrentMIDlet() != common.getLauncher()) {
					restartMidlet = JOptionPane.showConfirmDialog(Main.this,
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
						da.sizeChanged(da.getCurrent().getWidth(), da.getCurrent().getHeight());
						deviceDisplay.repaint(0, 0, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
					}
				}
			}
		}
	};

	private StatusBarListener statusBarListener = new StatusBarListener() {
		public void statusBarChanged(String text) {
			FontMetrics metrics = statusBar.getFontMetrics(statusBar.getFont());
			statusBar.setPreferredSize(new Dimension(metrics.stringWidth(text), metrics.getHeight()));
			statusBar.setText(text);
		}
	};

	private ResponseInterfaceListener responseInterfaceListener = new ResponseInterfaceListener() {
		public void stateChanged(boolean state) {
			menuOpenJADFile.setEnabled(state);
			menuOpenJADURL.setEnabled(state);
			menuSelectDevice.setEnabled(state);

			if (common.jad.getJarURL() != null) {
				menuSaveForWeb.setEnabled(state);
			} else {
				menuSaveForWeb.setEnabled(false);
			}
		}
	};

	private ComponentListener componentListener = new ComponentAdapter() {
		Timer timer;

		int count = 0;

		public void componentResized(ComponentEvent e) {
			count++;
			DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay();
			if (deviceDisplay.isResizable()) {
				deviceDisplay.setDisplayRectangle(new Rectangle(0, 0, devicePanel.getWidth(), devicePanel.getHeight()));
				((SwingDisplayComponent) devicePanel.getDisplayComponent()).init();
				MIDletAccess ma = MIDletBridge.getMIDletAccess();
				if (ma == null) {
					return;
				}
				DisplayAccess da = ma.getDisplayAccess();
				if (da != null) {
					da.sizeChanged(da.getCurrent().getWidth(), da.getCurrent().getHeight());
					deviceDisplay.repaint(0, 0, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
				}
				devicePanel.revalidate();
				statusBarListener.statusBarChanged("New size: " + deviceDisplay.getFullWidth() + "x"
						+ deviceDisplay.getFullHeight());
				synchronized (statusBarListener) {
					if (timer == null) {
						timer = new Timer();
					}
					timer.schedule(new CountTimerTask(count) {
						public void run() {
							if (counter == count) {
								Config.setDeviceEntryDisplaySize(deviceEntry, new Rectangle(0, 0, devicePanel
										.getWidth(), devicePanel.getHeight()));
								statusBarListener.statusBarChanged("");
								timer.cancel();
								timer = null;
							}
						}
					}, 2000);
				}
			}
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

		this.logQueueAppender = new QueueAppender(1024);
		Logger.addAppender(logQueueAppender);

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

		JMRUMenu urlsMRU = new JMRUMenu("Recent MIDlets...");
		urlsMRU.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (event instanceof JMRUMenu.MRUActionEvent) {
					Common.openJadUrlSafe(((MidletURLReference) ((JMRUMenu.MRUActionEvent) event).getSourceMRU())
							.getUrl());
				}
			}
		});

		Config.getUrlsMRU().setListener(urlsMRU);
		menuFile.add(urlsMRU);

		menuFile.addSeparator();

		menuSaveForWeb = new JMenuItem("Save for Web...");
		menuSaveForWeb.addActionListener(menuSaveForWebListener);
		menuFile.add(menuSaveForWeb);

		menuFile.addSeparator();

		JMenuItem menuItem = new JMenuItem("Exit");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(menuExitListener);
		menuFile.add(menuItem);

		JMenu menuOptions = new JMenu("Options");

		menuSelectDevice = new JMenuItem("Select device...");
		menuSelectDevice.addActionListener(menuSelectDeviceListener);
		menuOptions.add(menuSelectDevice);

		menuStartCapture = new JMenuItem("Start capture to GIF...");
		menuStartCapture.addActionListener(menuStartCaptureListener);
		menuOptions.add(menuStartCapture);

		menuStopCapture = new JMenuItem("Stop capture");
		menuStopCapture.setEnabled(false);
		menuStopCapture.addActionListener(menuStopCaptureListener);
		menuOptions.add(menuStopCapture);

		menuMIDletNetworkConnection = new JCheckBoxMenuItem("MIDlet Network access");
		menuMIDletNetworkConnection.setState(true);
		menuMIDletNetworkConnection.addActionListener(menuMIDletNetworkConnectionListener);
		menuOptions.add(menuMIDletNetworkConnection);

		menuLogConsole = new JCheckBoxMenuItem("Log console");
		menuLogConsole.setState(false);
		menuLogConsole.addActionListener(menuLogConsoleListener);
		menuOptions.add(menuLogConsole);

		menuOptions.addSeparator();
		JCheckBoxMenuItem menuShowMouseCoordinates = new JCheckBoxMenuItem("Mouse coordinates");
		menuShowMouseCoordinates.setState(false);
		menuShowMouseCoordinates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				devicePanel.switchShowMouseCoordinates();
			}
		});
		menuOptions.add(menuShowMouseCoordinates);

		JMenu menuHelp = new JMenu("Help");
		JMenuItem menuAbout = new JMenuItem("About");
		menuAbout.addActionListener(menuAboutListener);
		menuHelp.add(menuAbout);

		menuBar.add(menuFile);
		menuBar.add(menuOptions);
		menuBar.add(menuHelp);
		setJMenuBar(menuBar);

		setTitle("MicroEmulator");

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/org/microemu/icon.png")));

		addWindowListener(windowListener);

		Config.loadConfig(defaultDevice, emulatorContext);

		Rectangle window = Config.getWindow("main", new Rectangle(0, 0, 160, 120));
		this.setLocation(window.x, window.y);

		getContentPane().add(createContents(getContentPane()), "Center");

		selectDevicePanel = new SwingSelectDevicePanel(emulatorContext);

		this.common = new Common(emulatorContext);
		this.common.setStatusBarListener(statusBarListener);
		this.common.setResponseInterfaceListener(responseInterfaceListener);

		this.resizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				if (resizeDeviceDisplayDialog == null) {
					resizeDeviceDisplayDialog = new ResizeDeviceDisplayDialog();
				}
				DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay();
				resizeDeviceDisplayDialog.setDeviceDisplaySize(deviceDisplay.getFullWidth(), deviceDisplay
						.getFullHeight());
				if (SwingDialogWindow.show(Main.this, "Enter new size...", resizeDeviceDisplayDialog, true)) {
					deviceDisplay.setDisplayRectangle(new Rectangle(0, 0, resizeDeviceDisplayDialog
							.getDeviceDisplayWidth(), resizeDeviceDisplayDialog.getDeviceDisplayHeight()));
					((SwingDisplayComponent) devicePanel.getDisplayComponent()).init();
					MIDletAccess ma = MIDletBridge.getMIDletAccess();
					if (ma == null) {
						return;
					}
					DisplayAccess da = ma.getDisplayAccess();
					if (da != null) {
						da.sizeChanged(da.getCurrent().getWidth(), da.getCurrent().getHeight());
						deviceDisplay.repaint(0, 0, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
					}
					pack();
				}
			}
		});

		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new BorderLayout());
		statusPanel.add(statusBar, "West");
		statusPanel.add(this.resizeButton, "East");

		getContentPane().add(statusPanel, "South");

		Message.addListener(new SwingErrorMessageDialogPanel(this));

		devicePanel.setTransferHandler(new DropTransferHandler());
	}

	protected Component createContents(Container parent) {
		devicePanel = new SwingDeviceComponent();
		devicePanel.addKeyListener(devicePanel);
		addKeyListener(devicePanel);

		return devicePanel;
	}

	public boolean setDevice(DeviceEntry entry) {
		if (DeviceFactory.getDevice() != null) {
			// ((J2SEDevice) DeviceFactory.getDevice()).dispose();
		}
		final String errorTitle = "Error creating device";
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			if (entry.getFileName() != null) {
				URL[] urls = new URL[1];
				urls[0] = new File(Config.getConfigPath(), entry.getFileName()).toURI().toURL();
				classLoader = Common.createExtensionsClassLoader(urls);
			}

			// TODO font manager have to be moved from emulatorContext into
			// device
			emulatorContext.getDeviceFontManager().init();

			Device device = DeviceImpl.create(emulatorContext, classLoader, entry.getDescriptorLocation());
			this.deviceEntry = entry;

			DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) device.getDeviceDisplay();
			if (deviceDisplay.isResizable()) {
				Rectangle size = Config.getDeviceEntryDisplaySize(entry);
				if (size != null) {
					deviceDisplay.setDisplayRectangle(size);
				}
			}
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
		if (((DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay()).isResizable()) {
			setResizable(true);
			resizeButton.setVisible(true);
		} else {
			setResizable(false);
			resizeButton.setVisible(false);
		}

		pack();
	}

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			Logger.error(ex);
		}

		List params = new ArrayList();
		StringBuffer debugArgs = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			params.add(args[i]);
			if (debugArgs.length() != 0) {
				debugArgs.append(", ");
			}
			debugArgs.append("[").append(args[i]).append("]");
		}
		if (args.length > 0) {
			Logger.debug("arguments", debugArgs.toString());
		}

		Common.initParams(params);
		final Main app = new Main();
		app.common.initDevice(params, app.selectDevicePanel.getSelectedDeviceEntry());
		app.deviceEntry = app.selectDevicePanel.getSelectedDeviceEntry();
		DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl) DeviceFactory.getDevice().getDeviceDisplay();
		if (deviceDisplay.isResizable()) {
			Rectangle size = Config.getDeviceEntryDisplaySize(app.deviceEntry);
			if (size != null) {
				deviceDisplay.setDisplayRectangle(size);
			}
		}
		app.updateDevice();

		app.validate();
		app.setVisible(true);

		app.common.initMIDlet(params, false);

		app.addComponentListener(app.componentListener);

		app.responseInterfaceListener.stateChanged(true);
	}

	private abstract class CountTimerTask extends TimerTask {

		protected int counter;

		public CountTimerTask(int counter) {
			this.counter = counter;
		}

	}

}
