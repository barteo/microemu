/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@it.pl>
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
			  
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.app.ui.swt.SwtDeviceComponent;
import com.barteo.emulator.app.ui.swt.SwtSelectDeviceDialog;
import com.barteo.emulator.app.util.DeviceEntry;
import com.barteo.emulator.app.util.ProgressJarClassLoader;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.swt.SwtDevice;
import com.barteo.emulator.util.JadMidletEntry;


public class EclipseSwt extends Common
{
	public static Shell shell;

	private static SwtDeviceComponent devicePanel;

	private boolean initialized = false;
  
	private SwtSelectDeviceDialog selectDeviceDialog;
	private DeviceEntry deviceEntry;
  
	private KeyListener keyListener = new KeyListener()
	{    
		public void keyTyped(KeyEvent e)
		{
		}
    
		public void keyPressed(KeyEvent e)
		{
//			devicePanel.keyPressed(e);
		}
    
		public void keyReleased(KeyEvent e)
		{
//			devicePanel.keyReleased(e);
		}    
	};
       
	private ShellListener shellListener = new ShellListener()
	{
		public void shellActivated(ShellEvent e) 
		{
		}

		public void shellClosed(ShellEvent e) 
		{
			close();
		}

		public void shellDeactivated(ShellEvent e) 
		{
		}

		public void shellDeiconified(ShellEvent e) 
		{
			try {
				MIDletBridge.getMIDletAccess(getLauncher().getCurrentMIDlet()).startApp();
			} catch (MIDletStateChangeException ex) {
				System.err.println(ex);
			}
		}

		public void shellIconified(ShellEvent e) 
		{
			MIDletBridge.getMIDletAccess(getLauncher().getCurrentMIDlet()).pauseApp();
		}
	};
 
  
	EclipseSwt(Shell shell, Device device, String captureFile)
	{
		super(new EmulatorContext()
		{
			ProgressJarClassLoader loader = new ProgressJarClassLoader();
    
			public ClassLoader getClassLoader()
			{
				return loader;
			}
    
			public DisplayComponent getDisplayComponent()
			{
				return devicePanel.getDisplayComponent();
			}    
		});

		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		shell.setLayoutData(new GridData(GridData.FILL_BOTH));

		shell.setText("Microemu");
//		addWindowListener(windowListener);
		    
		Config.loadConfig("config-swt.xml");
		shell.addKeyListener(keyListener);

		devicePanel = new SwtDeviceComponent(shell);
		devicePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		this.captureFile = captureFile;

		if (device == null) {
			selectDeviceDialog = new SwtSelectDeviceDialog(shell);
			setDevice(selectDeviceDialog.getSelectedDeviceEntry());
		} else {
			setDevice(device);
		}
    
		initialized = true;
	}
      
  
	public DeviceEntry getDevice()
	{
		return deviceEntry;
	}
  
  
	public void setDevice(DeviceEntry entry)
	{
		if (DeviceFactory.getDevice() != null) {
			((SwtDevice) DeviceFactory.getDevice()).dispose();
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
			SwtDevice device = (SwtDevice) deviceClass.newInstance();
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


	protected void setDevice(SwtDevice device)
	{
		super.setDevice(device);

		device.init(emulatorContext);
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
	}
  
  
	protected void loadFromJad(URL jadUrl)
	{		
		try {
			for (Enumeration e = jad.getMidletEntries().elements(); e.hasMoreElements(); ) {
				JadMidletEntry jadEntry = (JadMidletEntry) e.nextElement();
				Class midletClass = emulatorContext.getClassLoader().loadClass(jadEntry.getClassName());
				loadMidlet(jadEntry.getName(), midletClass);
			}
			notifyDestroyed();
		} catch (ClassNotFoundException ex) {
			System.err.println(ex);
		}        
	}


	public static void main(String args[])
	{
		DeviceData data = new DeviceData();
		data.tracking = true;
		
		Display display = new Display(data);
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.ON_TOP);
		    
		MIDlet m = null;

//		Sleak sleak = app.new Sleak (display);
//		sleak.open ();

		Device device = null;
		String captureFile = null;
		URL jadUrl = null;
		Class midletClass = null;

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--deviceClass")) {
					i++;
					try {
						Class deviceClass = Class.forName(args[i]);
						device = (Device) deviceClass.newInstance();
					} catch (ClassNotFoundException ex) {
						System.err.println(ex);          
					} catch (InstantiationException ex) {
						System.err.println(ex);          
					} catch (IllegalAccessException ex) {
						System.err.println(ex);          
					}					
				} else if (args[i].equals("--captureFile")) {
					i++;
					captureFile = args[i];
				} else if (args[i].endsWith(".jad")) {
					try {
						File file = new File(args[i]);
						jadUrl = file.exists() ? file.toURL() : new URL(args[i]);
					} catch(MalformedURLException exception) {
						System.out.println("Cannot parse " + args[0] + " URL");
					}
				} else {
					try {
						midletClass = Class.forName(args[i]);
					} catch (ClassNotFoundException ex) {
						System.out.println("Cannot find " + args[i] + " MIDlet class");
					}
				}
			}
		}

		EclipseSwt app = new EclipseSwt(shell, device, captureFile);
		shell.addShellListener(app.shellListener);
		
		if (jadUrl != null) {
			app.openJadFile(jadUrl);			
		} else if (midletClass != null) {
			m = app.loadMidlet("MIDlet", midletClass);			
		} else {
			m = app.getLauncher();
		}
		
    
		if (app.initialized) {
			if (m != null) {
				app.startMidlet(m);
			}
			
			shell.pack ();
			shell.open ();
			while (!shell.isDisposed ()) {
				if (!display.readAndDispatch ())
					display.sleep ();
			}
			display.dispose ();			
		}

		System.exit(0);
	}	
	
	
	class Sleak {
		Display display;
		Shell shell;
		List list;
		Canvas canvas;
		Button start, stop, check;
		Text text;
		Label label;
	
		Object [] oldObjects = new Object [0];
		Error [] oldErrors = new Error [0];
		Object [] objects = new Object [0];
		Error [] errors = new Error [0];
	
		public Sleak(Display display) 
		{
			this.display = display;
		}

		public void open () {
		display = Display.getCurrent ();
		shell = new Shell (display);
		shell.setText ("S-Leak");
		list = new List (shell, SWT.BORDER | SWT.V_SCROLL);
		list.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				refreshObject ();
			}
		});
		text = new Text (shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		canvas = new Canvas (shell, SWT.BORDER);
		canvas.addListener (SWT.Paint, new Listener () {
			public void handleEvent (Event event) {
				paintCanvas (event);
			}
		});
		check = new Button (shell, SWT.CHECK);
		check.setText ("Stack");
		check.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				toggleStackTrace ();
			}
		});
		start = new Button (shell, SWT.PUSH);
		start.setText ("Snap");
		start.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				refreshAll ();
			}
		});
		stop = new Button (shell, SWT.PUSH);
		stop.setText ("Diff");
		stop.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				refreshDifference ();
			}
		});
		label = new Label (shell, SWT.BORDER);
		label.setText ("0 object(s)");
		shell.addListener (SWT.Resize, new Listener () {
			public void handleEvent (Event e) {
				layout ();
			}
		});
		check.setSelection (false);
		text.setVisible (false);
		Point size = shell.getSize ();
		shell.setSize (size.x / 2, size.y / 2);
		shell.open ();
	}

	void refreshLabel () {
		int colors = 0, cursors = 0, fonts = 0, gcs = 0, images = 0, regions = 0;
		for (int i=0; i<objects.length; i++) {
			Object object = objects [i];
			if (object instanceof Color) colors++;
			if (object instanceof Cursor) cursors++;
			if (object instanceof Font) fonts++;
			if (object instanceof GC) gcs++;
			if (object instanceof Image) images++;
			if (object instanceof Region) regions++;
		}
		String string = "";
		if (colors != 0) string += colors + " Color(s)\n";
		if (cursors != 0) string += cursors + " Cursor(s)\n";
		if (fonts != 0) string += fonts + " Font(s)\n";
		if (gcs != 0) string += gcs + " GC(s)\n";
		if (images != 0) string += images + " Image(s)\n";
		/* Currently regions are not counted. */
//		if (regions != 0) string += regions + " Region(s)\n";
		if (string.length () != 0) {
			string = string.substring (0, string.length () - 1);
		}
		label.setText (string);
	}

	void refreshDifference () {
		DeviceData info = display.getDeviceData ();
		if (!info.tracking) {
			MessageBox dialog = new MessageBox (shell, SWT.ICON_WARNING | SWT.OK);
			dialog.setText (shell.getText ());
			dialog.setMessage ("Warning: Device is not tracking resource allocation");
			dialog.open ();
		}
		Object [] newObjects = info.objects;
		Error [] newErrors = info.errors;
		Object [] diffObjects = new Object [newObjects.length];
		Error [] diffErrors = new Error [newErrors.length];
		int count = 0;
		for (int i=0; i<newObjects.length; i++) {
			int index = 0;
			while (index < oldObjects.length) {
				if (newObjects [i] == oldObjects [index]) break;
				index++;
			}
			if (index == oldObjects.length) {
				diffObjects [count] = newObjects [i];
				diffErrors [count] = newErrors [i];
				count++;
			}
		}
		objects = new Object [count];
		errors = new Error [count];
		System.arraycopy (diffObjects, 0, objects, 0, count);
		System.arraycopy (diffErrors, 0, errors, 0, count);
		list.removeAll ();
		text.setText ("");
		canvas.redraw ();
		for (int i=0; i<objects.length; i++) {
			list.add (objectName (objects [i]));
		}
		refreshLabel ();
		layout ();
	}

	String objectName (Object object) {
		String string = object.toString ();
		int index = string.lastIndexOf ('.');
		if (index == -1) return string;
		return string.substring (index + 1, string.length ());
	}

	void toggleStackTrace () {
		refreshObject ();
		layout ();
	}

	void paintCanvas (Event event) {
		canvas.setCursor (null);
		int index = list.getSelectionIndex ();
		if (index == -1) return;
		GC gc = event.gc;
		Object object = objects [index];
		if (object instanceof Color) {
			if (((Color)object).isDisposed ()) return;
			gc.setBackground ((Color) object);
			gc.fillRectangle (canvas.getClientArea());
			return;
		}
		if (object instanceof Cursor) {
			if (((Cursor)object).isDisposed ()) return;
			canvas.setCursor ((Cursor) object);
			return;
		}
		if (object instanceof Font) {
			if (((Font)object).isDisposed ()) return;
			gc.setFont ((Font) object);
			FontData [] array = gc.getFont ().getFontData ();
			String string = "";
			String lf = text.getLineDelimiter ();
			for (int i=0; i<array.length; i++) {
				FontData data = array [i];
				String style = "NORMAL";
				int bits = data.getStyle ();
				if (bits != 0) {
					if ((bits & SWT.BOLD) != 0) style = "BOLD ";
					if ((bits & SWT.ITALIC) != 0) style += "ITALIC";
				}
				string += data.getName () + " " + data.getHeight () + " " + style + lf;
			}
			gc.drawString (string, 0, 0);
			return;
		}
		//NOTHING TO DRAW FOR GC
//		if (object instanceof GC) {
//			return;
//		}
		if (object instanceof Image) {
			if (((Image)object).isDisposed ()) return;
			gc.drawImage ((Image) object, 0, 0);
			return;
		}
		if (object instanceof Region) {
			if (((Region)object).isDisposed ()) return;
			String string = ((Region)object).getBounds().toString();
			gc.drawString (string, 0, 0);
			return;
		}
	}

	void refreshObject () {
		int index = list.getSelectionIndex ();
		if (index == -1) return;
		if (check.getSelection ()) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream ();
			PrintStream s = new PrintStream (stream);
			errors [index].printStackTrace (s);
			text.setText (stream.toString ());
			text.setVisible (true);
			canvas.setVisible (false);
		} else {
			canvas.setVisible (true);
			text.setVisible (false);
			canvas.redraw ();
		}
	}

	void refreshAll () {
		oldObjects = new Object [0];
		oldErrors = new Error [0];
		refreshDifference ();
		oldObjects = objects;
		oldErrors = errors;
	}

	void layout () {
		Rectangle rect = shell.getClientArea ();
		int width = 0;
		String [] items = list.getItems ();
		GC gc = new GC (list);
		for (int i=0; i<objects.length; i++) {
			width = Math.max (width, gc.stringExtent (items [i]).x);
		}
		gc.dispose ();
		Point size1 = start.computeSize (SWT.DEFAULT, SWT.DEFAULT);
		Point size2 = stop.computeSize (SWT.DEFAULT, SWT.DEFAULT);
		Point size3 = check.computeSize (SWT.DEFAULT, SWT.DEFAULT);
		Point size4 = label.computeSize (SWT.DEFAULT, SWT.DEFAULT);
		width = Math.max (size1.x, Math.max (size2.x, Math.max (size3.x, width)));
		width = Math.max (64, Math.max (size4.x, list.computeSize (width, SWT.DEFAULT).x));
		start.setBounds (0, 0, width, size1.y);
		stop.setBounds (0, size1.y, width, size2.y);
		check.setBounds (0, size1.y + size2.y, width, size3.y);
		label.setBounds (0, rect.height - size4.y, width, size4.y);
		int height = size1.y + size2.y + size3.y;
		list.setBounds (0, height, width, rect.height - height - size4.y);
		text.setBounds (width, 0, rect.width - width, rect.height);
		canvas.setBounds (width, 0, rect.width - width, rect.height);
	}
	}
	
}
