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
			throw new RuntimeException("Can acess java.microedition from MIDlet jar using Class.forName" );
		} catch (ClassNotFoundException e) {
			if (verbose) {
				System.out.println("no acess to java.microedition in MIDlet jar OK " + e.toString());
			}
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
