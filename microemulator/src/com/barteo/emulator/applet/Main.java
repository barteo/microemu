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
 *
 *  Contributor(s):
 *    daniel(at)angrymachine.com.ar
 */

package com.barteo.emulator.applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.MicroEmulator;
import com.barteo.emulator.RecordStoreManager;
import com.barteo.emulator.app.launcher.Launcher;
import com.barteo.emulator.app.ui.awt.AwtDeviceComponent;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.DeviceDisplay;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.FontManager;
import com.barteo.emulator.device.InputMethod;
import com.barteo.emulator.device.applet.AppletDeviceDisplay;
import com.barteo.emulator.device.applet.AppletFontManager;
import com.barteo.emulator.device.applet.AppletInputMethod;
import com.barteo.emulator.util.JadMidletEntry;
import com.barteo.emulator.util.JadProperties;


public class Main extends Applet implements MicroEmulator
{
    MIDlet midlet = null;

    private RecordStoreManager recordStoreManager;

    private JadProperties manifest = new JadProperties();

    Font defaultFont;

    AwtDeviceComponent devicePanel;

    EmulatorContext emulatorContext = new EmulatorContext() 
    {
        private InputMethod inputMethod = new AppletInputMethod();

        private DeviceDisplay deviceDisplay = new AppletDeviceDisplay(this);

        private FontManager fontManager = new AppletFontManager();

        public ClassLoader getClassLoader()
        {
            return getClass().getClassLoader();
        }

        public DisplayComponent getDisplayComponent()
        {
            return devicePanel.getDisplayComponent();
        }

        public Launcher getLauncher()
        {
            return null;
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

    
    public Main()
    {
        recordStoreManager = new AppletRecordStoreManager();
        devicePanel = new AwtDeviceComponent();
    }

    
    public void init()
    {
        System.out.println("Applet::init()");
        if (midlet != null) {
            return;
        }

        MIDletBridge.setMicroEmulator(this);

        setLayout(new BorderLayout());
        add(devicePanel, "Center");

        Device device;
        String deviceClassName = getParameter("device");
        if (deviceClassName == null) {
            device = new Device();
        } else {
            try {
                Class cl = Class.forName(deviceClassName);
                device = (Device) cl.newInstance();
            } catch (ClassNotFoundException ex) {
                System.out.println(ex);
                return;
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
                return;
            } catch (InstantiationException ex) {
                System.out.println(ex);
                return;
            }
        }

        DeviceFactory.setDevice(device);
        device.init(emulatorContext);
        devicePanel.init();

        manifest.clear();
        try {
            URL url = emulatorContext.getClassLoader().getResource(
                    "META-INF/MANIFEST.MF");
            manifest.load(url.openStream());
            if (manifest.getProperty("MIDlet-Name") == null) {
                manifest.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load jad
        String midletClassName = null;
        String jadFile = getParameter("jad");
        if (jadFile != null) {
            InputStream jadInputStream = null;
            try {
                URL jad = new URL(getCodeBase(), jadFile);
                jadInputStream = jad.openStream();
                manifest.load(jadInputStream);
                Vector entries = manifest.getMidletEntries();
                // only load the first (no midlet suite support anyway)
                if (entries.size() > 0) {
                    JadMidletEntry entry = (JadMidletEntry) entries.elementAt(0);
                    midletClassName = entry.getClassName();
                }
            } catch (IOException e) {
            } finally {
                if (jadInputStream != null) {
                    try {
                        jadInputStream.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }

        if (midletClassName == null) {
            midletClassName = getParameter("midlet");
            if (midletClassName == null) {
                System.out.println("There is no midlet parameter");
                return;
            }
        }

        Class midletClass;
        try {
            midletClass = Class.forName(midletClassName);
        } catch (ClassNotFoundException ex) {
            System.out.println("Cannot find " + midletClassName + " MIDlet class");
            return;
        }

        try {
            midlet = (MIDlet) midletClass.newInstance();
        } catch (Exception ex) {
            System.out.println("Cannot initialize " + midletClass + " MIDlet class");
            System.out.println(ex);
            ex.printStackTrace();
            return;
        }

        Image tmpImg = DeviceFactory.getDevice().getNormalImage();
        resize(tmpImg.getWidth(), tmpImg.getHeight());

        return;
    }

    
    public void start()
    {
        devicePanel.requestFocus();
        try {
            MIDletBridge.getMIDletAccess(midlet).startApp();
        } catch (MIDletStateChangeException ex) {
            System.err.println(ex);
        }
    }

    
    public void stop()
    {
        MIDletBridge.getMIDletAccess(midlet).pauseApp();
    }

    
    public void destroy()
    {
        try {
            MIDletBridge.getMIDletAccess(midlet).destroyApp(true);
            notifyDestroyed();
        } catch (MIDletStateChangeException ex) {
            System.err.println(ex);
        }
    }

    
    public RecordStoreManager getRecordStoreManager()
    {
        return recordStoreManager;
    }

    
    public String getAppProperty(String key)
    {
        String value = null;
        if (key.equals("microedition.platform")) {
            value = "MicroEmulator";
        } else if (key.equals("microedition.profile")) {
            value = "MIDP-1.0";
        } else if (key.equals("microedition.configuration")) {
            value = "CLDC-1.0";
        } else if (key.equals("microedition.locale")) {
            value = Locale.getDefault().getLanguage();
        } else if (key.equals("microedition.encoding")) {
            value = System.getProperty("file.encoding");
        } else if (getParameter(key) != null) {
            value = getParameter(key);
        } else {
            value = manifest.getProperty(key);
        }
        
        return value;
    }

    
    public void notifyDestroyed()
    {
        // disable the applet
        super.removeAll();
    }

    
    public void notifySoftkeyLabelsChanged()
    {
    }

    
    public String getAppletInfo()
    {
        return "Title: MicroEmulator \nAuthor: Bartek Teodorczyk, 2001";
    }

    
    public String[][] getParameterInfo()
    {
        String[][] info = { 
                { "midlet", "MIDlet class name", "The MIDlet class name. This field is mandatory." }, 
        };

        return info;
    }

}
