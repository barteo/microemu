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
package org.microemu.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author vlads
 *
 */
public class BuildVersion {

	public static String getVersion() {
		InputStream is = BuildVersion.class.getResourceAsStream("/META-INF/maven/org.microemu/microemu-javase/pom.properties");
		if (is != null) {
			Properties projectProperties = new Properties();
			try {
				projectProperties.load(is);
				String version = projectProperties.getProperty("version");
				if (version != null) {
					return version;
				}
			} catch (IOException ignore) {
			}
		}
		return "n/a";
	}
}
