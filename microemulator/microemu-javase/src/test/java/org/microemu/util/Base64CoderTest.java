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
package org.microemu.util;

import junit.framework.TestCase;

/**
 * @author vlads
 *
 */
public class Base64CoderTest extends TestCase {


    public void testEncode() {
        assertEquals( "Result of encoding", "QWxhZGRpbjpvcGVuIHNlc2FtZQ==", Base64Coder.encode( "Aladdin:open sesame" ) );
        assertEquals( "Result of encoding", "QWRtaW46Zm9vYmFy",             Base64Coder.encode( "Admin:foobar" ) );
    }


    public void testDecode() {
        assertEquals( "Result of decoding", "Aladdin:open sesame", Base64Coder.decode( "QWxhZGRpbjpvcGVuIHNlc2FtZQ==" ) );
        assertEquals( "Result of decoding", "Admin:foobar",        Base64Coder.decode( "QWRtaW46Zm9vYmFy" ) );
    }

	private void verifyEncodeDecode(byte[] value) {
		char[] chars = Base64Coder.encode(value);
		byte[] decodedValue = Base64Coder.decode(chars);
		assertEquals("Data as expected length", value.length, decodedValue.length);
		for(int i = 0; i < value.length; i++ ) {
			assertEquals("Data as expected", value[i], decodedValue[i]);	
		}
	}
	
	public void testAllBytes() {
		final String message = "Wrong number...";
		int len = message.getBytes().length;
		byte[] data = new byte[len + 256];
		byte b = -127;
		for(int i = len; i < data.length; i++ ) {
			data[i] = b;
			b ++;
		}
		verifyEncodeDecode(data);
	}
}
