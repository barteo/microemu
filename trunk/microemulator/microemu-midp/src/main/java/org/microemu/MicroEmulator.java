/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 *  Contributor(s):
 *    3GLab
 */

package org.microemu;

import java.io.InputStream;

import javax.microedition.io.ConnectionNotFoundException;

import org.microemu.app.launcher.Launcher;

public interface MicroEmulator {

	RecordStoreManager getRecordStoreManager();
	
	Launcher getLauncher();

	String getAppProperty(String key);
	
	InputStream getResourceAsStream(String name);

	void notifyDestroyed(MIDletContext midletContext);

	void destroyMIDletContext(MIDletContext midletContext);
	
	int checkPermission(String permission);
	
	boolean platformRequest(String URL) throws ConnectionNotFoundException;

}
