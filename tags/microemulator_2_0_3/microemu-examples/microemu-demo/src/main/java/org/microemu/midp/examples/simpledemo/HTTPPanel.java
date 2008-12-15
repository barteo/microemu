/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id$
 */
package org.microemu.midp.examples.simpledemo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

/**
 * @author vlads
 *
 */
public class HTTPPanel extends BaseExamplesForm {

	static final Command goCommand = new Command("Go", Command.OK, 1);
	
	// See file src/site/resources/test/index.php
	static String demoLocation;
	
	TextField url;
	
	StringItem message;
	
	StringItem status;
	
	Thread requestThread = null;
	
	public HTTPPanel() {
		super("HTTP Connection");
		
		String host = SimpleDemoMIDlet.instance.getAppProperty("microemu.accessible.host");
		if (host == null) {
			demoLocation = "http://www.microemu.org/test/";
		} else {
			demoLocation = "http://" + host + "/test/";
		}
		
		append(url = new TextField("URL:", demoLocation, 128, TextField.URL));
		append(message = new StringItem("Message:", null));
		append(status = new StringItem("Status:", null));
		
		addCommand(goCommand);
	}

	private void makeConnection() {
		if (requestThread == null) {
			requestThread = new Thread(new ConnectionRun());
			requestThread.start();
		} else {
			message.setText("Request still running");  
		}
	}
	
	class ConnectionRun implements Runnable {

		public void run() {
			tryConnect();
		}
	}
	
	private void tryConnect() {
		Connection c = null;
		InputStream is = null;
		try {
			status.setText("Connecting...");
			c = Connector.open(url.getString(), Connector.READ, true);
			HttpConnection hcon = (HttpConnection) c;
			status.setText("http:" + String.valueOf(hcon.getResponseCode()) + " len:" + String.valueOf(hcon.getLength()));
			int length = (int) hcon.getLength();
            byte[] data = null;
            if (length != -1) {
                data = new byte[length];
                is = new DataInputStream(hcon.openInputStream());
                ((DataInputStream)is).readFully(data);
            } else {
            	// If content length is not given, read in chunks.
                int chunkSize = 512;
                is = hcon.openInputStream();
                byte[] readData = new byte[chunkSize];
                int index = 0;
                do {
                    int onebyte = is.read();
                    if (onebyte == -1) {
                        break;
                    }
                    readData[index] = (byte)onebyte;
                    index ++;
                    if (readData.length <= index) {
                        byte[] newData = new byte[index + chunkSize];
                        System.arraycopy(readData, 0, newData, 0, readData.length);
                        readData = newData;
                    }
                } while (index < 1000);
                data = new byte[index];
                System.arraycopy(readData, 0, data, 0, index);
            }
            message.setText(new String(data));            
		} catch (Throwable e) {
			System.out.println(e.toString());
			e.printStackTrace();
			status.setText(e.toString());
			message.setText("");
			Alert alert = new Alert("Error", e.getMessage() + " " + e.toString(), null, AlertType.ERROR);
			alert.setTimeout(Alert.FOREVER);
			SimpleDemoMIDlet.setCurrentDisplayable(alert);
		} finally {
			requestThread = null;
			if (is != null) {
                try {
					is.close();
				} catch (IOException e) {
				}
			}
            if (c != null) {
                try {
					c.close();
				} catch (IOException e) {
				}
            }
		}
	}
	
	public void commandAction(Command c, Displayable d) {
		if (c == goCommand) {
			makeConnection();
		}
		super.commandAction(c, d);
	}
}
