package org.microemu.maemo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.microedition.midlet.MIDlet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.microemu.maemo.device.MaemoDevice;

import com.barteo.emulator.app.Swt;
import com.barteo.emulator.device.Device;

public class Main extends Swt
{

	public Main(Shell shell)
	{
		super(shell);
	}
	
	
	protected void setDevice(Device device)
	{
		Device maemoDevice = new MaemoDevice(); 
		
		super.setDevice(maemoDevice);
		
		maemoDevice.init(emulatorContext);
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
	}
	
	
	protected void initInterface(Shell shell)
	{
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		shell.setLayoutData(new GridData(GridData.FILL_BOTH));

		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
    
		MenuItem menuFile = new MenuItem(bar, SWT.CASCADE);
		menuFile.setText("File");
    
		Menu fileSubmenu = new Menu(shell, SWT.DROP_DOWN);
		menuFile.setMenu(fileSubmenu);

		menuOpenJADFile = new MenuItem(fileSubmenu, SWT.PUSH);
		menuOpenJADFile.setText("Open JAD File...");
		menuOpenJADFile.addListener(SWT.Selection, menuOpenJADFileListener);

		menuOpenJADURL = new MenuItem(fileSubmenu, 0);
		menuOpenJADURL.setText("Open JAD URL...");
		menuOpenJADURL.addListener(SWT.Selection, menuOpenJADURLListener);

		new MenuItem(fileSubmenu, SWT.SEPARATOR);
    
		MenuItem menuExit = new MenuItem(fileSubmenu, SWT.PUSH);
		menuExit.setText("Exit");
		menuExit.addListener(SWT.Selection, menuExitListener);
    
		shell.setText("MicroEmulator");
	}


	public static void main(String[] args)
	{
		Display display = new Display();
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		    
		Main app = new Main(shell);
		MIDlet m = null;

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--deviceClass")) {
					i++;
					try {
						Class deviceClass = Class.forName(args[i]);
						app.setDevice((Device) deviceClass.newInstance());
					} catch (ClassNotFoundException ex) {
						System.err.println(ex);          
					} catch (InstantiationException ex) {
						System.err.println(ex);          
					} catch (IllegalAccessException ex) {
						System.err.println(ex);          
					}
					
				} else if (args[i].endsWith(".jad")) {
					try {
						File file = new File(args[i]);
						URL url = file.exists() ? file.toURL() : new URL(args[i]);
						app.openJadFile(url);
					} catch(MalformedURLException exception) {
						System.out.println("Cannot parse " + args[0] + " URL");
					}
				} else {
					Class midletClass;
					try {
						midletClass = Class.forName(args[i]);
						m = app.loadMidlet("MIDlet", midletClass);
					} catch (ClassNotFoundException ex) {
						System.out.println("Cannot find " + args[i] + " MIDlet class");
					}
				}
			}
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

}
