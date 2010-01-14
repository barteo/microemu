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
package org;

public class TestMain implements Runnable {

	public static boolean verbose = false;
	
	public static void main(String[] args) {
		(new TestMain()).run();
	}

	public void run() {
		
		if (System.getProperty("test.verbose") != null) {
			verbose = true;
		}
		
		if (verbose) {
			System.out.println("ClassLoader " + this.getClass().getClassLoader().hashCode() +  " TestMain");
		}
		
		assertProperty("test.property1", "1");
		assertProperty("microedition.platform", null);
		
		if (verbose) {
			System.out.println("System.getProperty OK");
		}
		
		(new TestResourceLoad()).run();
		(new TestStaticInitializer()).run();
		
		try {
			(new OverrideMicroeditionClient()).run();
			throw new RuntimeException("Can execute OverrideMicroeditionClient");
		} catch (Throwable e) {
			if (e instanceof NoClassDefFoundError) {
				if (verbose) {
					System.out.println("no acess to java.microedition in MIDlet jar OK " + e.toString());
				}
			} else {
				throw new RuntimeException("Can acess java.microedition from MIDlet jar " + e.toString());
			}
		}
		
		try {
			Class.forName("javax.microedition.NotAccessible");
			throw new RuntimeException("Can acess java.microedition from MIDlet jar using Class.forName");
		} catch (ClassNotFoundException e) {
			if (verbose) {
				System.out.println("no acess to java.microedition in MIDlet jar OK " + e.toString());
			}
		} 
		
		String runnerName = "org.DynamicallyLoadedRunner";
		Class drunner;
		try {
			drunner = Class.forName(runnerName);
			if (verbose) {
				System.out.println("Class.forName("+ runnerName +") OK ");
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Can't load "+ runnerName +" from MIDlet jar using Class.forName", e);
		}
		Object r;
		try {
			r = drunner.newInstance();
			if (verbose) {
				System.out.println("Class.forName("+ runnerName +").newInstance() OK ");
			}
		} catch (Exception e) {
			throw new RuntimeException("Can't create "+ runnerName +" from MIDlet jar using Class.forName", e);
		}
		
		((Runnable)r).run();
		if (!DynamicallyLoadedStatus.runnerSuccess) {
			throw new RuntimeException("Can execute DynamicallyLoadedRunner from MIDlet jar" );
		}
		
		
		System.out.println("All tests OK");
	}
	
	private void assertProperty(String key, String expected) {
		String value = System.getProperty(key);
		if (verbose) {
			System.out.println("Got System.getProperty " + key + " value [" + value + "]");
		}
		if (((expected == null) && (value != null)) || ((expected != null) && (!expected.equals(value)))) {
			throw new RuntimeException("Unexpected property value " + value + " expected " + expected);
		}
	}
}
