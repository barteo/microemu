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
package org.microemu.device.impl;

import org.microemu.device.impl.DeviceImpl;

import nanoxml.XMLElement;

import junit.framework.TestCase;

/**
 * @author vlads
 *
 */
public class DeviceXMLInheritTest extends TestCase {

	public void verifyXML(String parentXML, String childXML, String expectedXML) {
		XMLElement child = new XMLElement();
		child.parseString(childXML);
		XMLElement parent = new XMLElement();
		parent.parseString(parentXML);
		XMLElement expected = new XMLElement();
		expected.parseString(expectedXML);
		
		XMLElement result = DeviceImpl.inheritXML(parent, child, "/");
		
		//System.out.println("expected:" + expected.toString());
		//System.out.println(" result:" + result.toString());
		
		assertEquals(result.toString(), expected.toString());
	}
	
	public void testValueOverride() {
		verifyXML("<device><display><numcolors>2</numcolors><vp>vpv</vp></display></device>", 
				  "<device><display><numcolors>65536</numcolors><v>v1</v></display></device>", 
				  "<device><display><numcolors>65536</numcolors><vp>vpv</vp><v>v1</v></display></device>");
	}

	public void testValueOverrideByName() {
		verifyXML("<device><v>v1</v><v name=\"2\">v2</v><v name=\"3\">v3</v></device>", 
				  "<device><v>v1m</v><v name=\"3\">v3m</v></device>", 
				  "<device><v>v1m</v><v name=\"2\">v2</v><v name=\"3\">v3m</v></device>");

		verifyXML("<device><v>v1</v><v name=\"2\">v2</v><v name=\"3\">v3</v></device>", 
				  "<device><v name=\"3\">v3m</v><v>v1m</v></device>", 
				  "<device><v>v1m</v><v name=\"2\">v2</v><v name=\"3\">v3m</v></device>");
		
		verifyXML("<device><v>v1</v><v name=\"2\">v2</v><v name=\"3\">v3</v></device>", 
				  "<device><v>v1m</v><v name=\"3\" attrm=\"am\">v3m</v></device>", 
				  "<device><v>v1m</v><v name=\"2\">v2</v><v name=\"3\" attrm=\"am\">v3m</v></device>");
		
		verifyXML("<device><v attrm=\"am\">v1</v><v name=\"2\">v2</v><v name=\"3\">v3</v></device>", 
				  "<device><v>v1m</v><v name=\"3\">v3m</v></device>", 
				  "<device><v attrm=\"am\">v1m</v><v name=\"2\">v2</v><v name=\"3\">v3m</v></device>");
	}
	
	public void testValueOverrideByNameWithCase() {
		verifyXML("<device><v>v1</v><v name=\"a\">v2</v><v name=\"b\">v3</v></device>", 
				  "<device><v>v1m</v><v name=\"B\">v3m</v></device>", 
				  "<device><v>v1m</v><v name=\"a\">v2</v><v name=\"b\">v3</v><v name=\"B\">v3m</v></device>");

	}

	public void testValueOverrideNoName() {
		verifyXML("<device><v name=\"A\">v1</v></device>", 
				  "<device><v name=\"B\">v2</v></device>", 
				  "<device><v name=\"A\">v1</v><v name=\"B\">v2</v></device>");

	}
	
	public void testValueOverrideFonts() {
		verifyXML("<device><fonts>" +
				   "<font face=\"system\" style=\"plain\" size=\"small\">F1</font>" +
				   "<font face=\"system\" style=\"plain\" size=\"medium\">F2</font>" +
				   "</fonts></device>",
				   
				   "<device><fonts>" +
				   "<font face=\"system\" style=\"plain\" size=\"medium\">F2m</font>" +
				   "</fonts></device>",
				   
				   "<device><fonts>" +
				   "<font face=\"system\" style=\"plain\" size=\"small\">F1</font>" +
				   "<font face=\"system\" style=\"plain\" size=\"medium\">F2m</font>" +
				   "</fonts></device>");
	}
	
}
