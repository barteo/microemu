/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;

import javassist.ClassPool;

public class MIDP2APIDeclarationsTest extends APIDeclarationsTestCase{

	File wtkLibDir;

	protected void setUp() throws Exception {
		super.setUp();
		String wtkHome = System.getProperty("WTK_HOME");
		if (wtkHome == null) {
			throw new Error("System  property WTK_HOME not found");
		}
		wtkLibDir = new File(wtkHome, "lib");
		if (!wtkLibDir.exists() || (!wtkLibDir.isDirectory())) {
			throw new Error("Invalid system  property WTK_HOME = [" + wtkHome + "]");
		}

		reportExtra = false;
		reportOnly = false;
		reportConstructors = false;
	}

	private List getWtkJarURLList(String[] jarNames) throws Exception {
		List files = new Vector();
		for (int i = 0; i < jarNames.length; i++) {
			File jar = new File(wtkLibDir, jarNames[i]);
			if (!jar.exists()) {
				throw new FileNotFoundException(jar.getAbsolutePath());
			}
			files.add(jar.getCanonicalFile().toURI().toURL());
		}
		return files;
	}

	public void testGenericConnectionFrameworkAPI() throws Exception {

		String aPackage = "javax.microedition.io.";
		
		ClassPool wtkClassPool = createClassPool(getWtkJarURLList(new String[] { "midpapi20.jar", "cldcapi11.jar" }));
		ClassPool ourClassPool = createClassPool(aPackage + "Connection");
		appendClassPath(ourClassPool, aPackage + "PushRegistry");
		
		List names = new Vector();

		// Interface
		//names.add(aPackage + "CommConnection");
		names.add(aPackage + "Connection");
		names.add(aPackage + "ContentConnection");
		names.add(aPackage + "Datagram");
		names.add(aPackage + "DatagramConnection");
		names.add(aPackage + "HttpConnection");
		names.add(aPackage + "HttpsConnection");
		names.add(aPackage + "InputConnection");
		names.add(aPackage + "OutputConnection");
		names.add(aPackage + "SecureConnection");
		names.add(aPackage + "SecurityInfo");
		names.add(aPackage + "ServerSocketConnection");
		names.add(aPackage + "SocketConnection");
		names.add(aPackage + "StreamConnection");
		names.add(aPackage + "StreamConnectionNotifier");
		names.add(aPackage + "UDPDatagramConnection");

		// Class
		names.add(aPackage + "Connector");
		names.add(aPackage + "PushRegistry");

		// Exception
		names.add(aPackage + "ConnectionNotFoundException");

		verifyClassList(names, ourClassPool, wtkClassPool);
	}

	public void testLcduiAPI() throws Exception {

		ClassPool wtkClassPool = createClassPool(getWtkJarURLList(new String[] { "midpapi20.jar", "cldcapi11.jar" }));
		ClassPool ourClassPool = createClassPool("javax.microedition.lcdui.Choice");
		
		List names = new Vector();

		String aPackage = "javax.microedition.lcdui.";
		// Interface
		names.add(aPackage + "Choice");
		names.add(aPackage + "CommandListener");
		names.add(aPackage + "ItemCommandListener");
		names.add(aPackage + "ItemStateListener");

		// Class
		names.add(aPackage + "Alert");
		names.add(aPackage + "AlertType");
		names.add(aPackage + "Canvas");
		names.add(aPackage + "ChoiceGroup");
		names.add(aPackage + "Command");
		names.add(aPackage + "CustomItem");
		names.add(aPackage + "DateField");
		names.add(aPackage + "Display");
		names.add(aPackage + "Displayable");
		names.add(aPackage + "Font");
		names.add(aPackage + "Form");
		names.add(aPackage + "Gauge");
		names.add(aPackage + "Graphics");
		names.add(aPackage + "Image");
		names.add(aPackage + "ImageItem");
		names.add(aPackage + "Item");
		names.add(aPackage + "List");
		names.add(aPackage + "Screen");
		names.add(aPackage + "Spacer");
		names.add(aPackage + "StringItem");
		names.add(aPackage + "TextBox");
		names.add(aPackage + "TextField");
		names.add(aPackage + "Ticker");

		// Exception

		verifyClassList(names, ourClassPool, wtkClassPool);
	}

	public void testLcduiGameAPI() throws Exception {

		ClassPool wtkClassPool = createClassPool(getWtkJarURLList(new String[] { "midpapi20.jar", "cldcapi11.jar" }));
		ClassPool ourClassPool = createClassPool("javax.microedition.lcdui.game.GameCanvas");
		
		List names = new Vector();

		String aPackage = "javax.microedition.lcdui.game.";
		// Interface

		// Class
		names.add(aPackage + "GameCanvas");
		names.add(aPackage + "Layer");
		names.add(aPackage + "LayerManager");
		names.add(aPackage + "Sprite");
		names.add(aPackage + "TiledLayer");

		// Exception

		verifyClassList(names, ourClassPool, wtkClassPool);
	}

	public void testMidletAPI() throws Exception {

		ClassPool wtkClassPool = createClassPool(getWtkJarURLList(new String[] { "midpapi20.jar", "cldcapi11.jar" }));
		ClassPool ourClassPool = createClassPool("javax.microedition.midlet.MIDlet");
		
		List names = new Vector();

		String aPackage = "javax.microedition.midlet.";
		// Interface
		// Class
		names.add(aPackage + "MIDlet");
		// Exception
		names.add(aPackage + "MIDletStateChangeException");

		verifyClassList(names, ourClassPool, wtkClassPool);
	}

	public void testRMSAPI() throws Exception {

		ClassPool wtkClassPool = createClassPool(getWtkJarURLList(new String[] { "midpapi20.jar", "cldcapi11.jar" }));
		ClassPool ourClassPool = createClassPool("javax.microedition.rms.RecordComparator");
		
		List names = new Vector();

		String aPackage = "javax.microedition.rms.";
		// Interface
		names.add(aPackage + "RecordComparator");
		names.add(aPackage + "RecordEnumeration");
		names.add(aPackage + "RecordFilter");
		names.add(aPackage + "RecordListener");
		// Class
		names.add(aPackage + "RecordStore");
		// Exception
		names.add(aPackage + "InvalidRecordIDException");
		names.add(aPackage + "RecordStoreException");
		names.add(aPackage + "RecordStoreFullException");
		names.add(aPackage + "RecordStoreNotFoundException");
		names.add(aPackage + "RecordStoreNotOpenException");

		verifyClassList(names, ourClassPool, wtkClassPool);
	}

	public void testCertificatesAPI() throws Exception {

		ClassPool wtkClassPool = createClassPool(getWtkJarURLList(new String[] { "midpapi20.jar", "cldcapi11.jar" }));
		ClassPool ourClassPool = createClassPool("javax.microedition.pki.Certificate");
		
		List names = new Vector();

		String aPackage = "javax.microedition.pki.";
		// Interface
		names.add(aPackage + "Certificate");

		// Class

		// Exception
		names.add(aPackage + "CertificateException");

		verifyClassList(names, ourClassPool, wtkClassPool);
	}


	public void testMediaAPI() throws Exception {

		ClassPool wtkClassPool = createClassPool(getWtkJarURLList(new String[] { "midpapi20.jar", "cldcapi11.jar", "mmapi.jar", "wma20.jar" }));
		ClassPool ourClassPool = createClassPool("javax.microedition.media.Control");
		
		List names = new Vector();

		String aPackage = "javax.microedition.media.";
		// Interface
		names.add(aPackage + "Control");
		names.add(aPackage + "Controllable");
		names.add(aPackage + "Player");
		names.add(aPackage + "PlayerListener");
		// Class
		names.add(aPackage + "Manager");
		// Exception
		names.add(aPackage + "MediaException");

		aPackage = "javax.microedition.media.control.";
		// Interface
		names.add(aPackage + "ToneControl");
		names.add(aPackage + "VolumeControl");

		verifyClassList(names, ourClassPool, wtkClassPool);
	}
	
}
